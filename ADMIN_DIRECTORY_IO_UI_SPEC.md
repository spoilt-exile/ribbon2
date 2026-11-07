# Admin UI for directory security and IO configuration

## Purpose and scope

Add an administrator area to UIX for managing the directory tree, directory access rules, IO module schemes, and export scheme assignments. The UI must use the existing server rendered FreeMarker pages, HTMX requests, and Bootstrap styling. Admin status is determined by membership in the `ADMIN` group (the same `GroupEntity.ADMIN_GROUP` check used by `IORoutes`).

This specification is based on the current implementation: UIX is a Javalin/FreeMarker application (`uix`), gateway REST endpoints proxy calls over the message bus, directory access is stored in `Directory.accessEntries`, and IO protocols/schemes are exposed by `IORoutes`. Existing IO REST operations are already admin guarded. Directory REST operations generally rely on directory permissions, so the admin UI must not mistake a hidden link for authorization.

## Target behavior

1. Non-admins do not see admin navigation and cannot load admin pages or invoke admin UI actions.
2. Admins can browse the complete directory tree, select a directory, inspect/edit its local security configuration, and tell whether effective settings are inherited from an ancestor.
3. Admins can browse IO protocols, create/edit/delete import and export schemes using protocol-specific configuration fields, and see scheme status.
4. Import schemes retain their current module-specific directory behavior and configuration. A shared fixed-target option is deferred with Step 3.
5. Export schemes can be assigned to one or more directories and removed from those directories. A directory's assigned export schemes are visible in its IO tab.
6. Server-side authorization, validation, and existing IO execution semantics remain authoritative.

## Key existing contracts and gaps

- `DirectoryAccessModel` has a principal `Type` of `USER`, `GROUP`, or `ALL`, a `name`, and a `Map<String,Boolean>` of permission values. `Directory.accessEntries == null` currently means no local entries and causes `AuthService.getDirectoryWithAccess` to continue to ancestors. Preserve this inheritance representation. An explicit empty set is different: it is a local policy boundary and falls back to permission defaults; do not silently translate it to inheritance.
- `AuthService.getDirAccess` and `editDirAccess` currently require `canEditDirAccess`; they are not admin-only. Add a server-side admin gate to the admin management endpoints/actions, or define a clearly scoped admin API that performs the same validation. Do not rely on UI route protection alone.
- Existing access API: `GET /api/directory/access/{path}`, `POST /api/directory/access/{path}`, `GET /api/directory/access/permission/all`; group/user listing endpoints are available under `/api/group` and `/api/user` and should be checked for pagination and suitability. Existing directory list endpoint is paged (`GET /api/directory`), so build the full tree by requesting all pages or add an admin tree endpoint.
- Existing IO API in `IORoutes`: `GET /api/io/protocol`, `GET /api/io/scheme`, `GET /api/io/export/scheme/{dirName}`, `GET/POST/DELETE /api/io/scheme...`, plus export assign/dismiss endpoints. These currently call `isAdmin()`.
- `IOProtocol` exposes protocol ID, type, protocol name, and `ConfigEntry` list, but does not expose module display name/description. `ConfigEntry` exposes field name/type/required/default. UI rendering must be driven by those entries, not protocol-specific hard-coded forms.
- `IOScheme` currently contains name, description, id, type, protocol, config. Import target-directory capability is not in the common model. Review each importer's current config before adding a common `generalDirectory` field; prefer a clearly typed optional field on `IOScheme` only if all importers can honor it, otherwise put it in importer config metadata and document which modules support it.
- Export assignment is held by the running scheme instance (`SchemeInstance.exportDirectories`) and updated through IO message-bus topics, not a directory-owned persisted assignment entity. The UI must use those assignment endpoints and treat returned scheme data as current runtime state.
- Existing UIX has login/session context and server-side rendered HTMX fragments, but no general admin page. `MainRoutes` registers `/directories`, `/messages/...`; `index.html` provides the shell and Bootstrap/HTMX assets.

## User interface structure

Add an `Administration` navigation entry for admins. It opens `/admin` (or `/admin/directories`) with two top-level Bootstrap tabs:

- **Directories**: left tree/list of all directories; right detail panel with tabs **Details**, **Security**, and **IO**. Security and IO tabs operate on the selected directory. The tree must support deep paths and show parent/child hierarchy; use `DirectoryModel.parentName()` semantics (dot-separated paths).
- **IO schemes**: system-wide scheme table with filters for Import/Export, protocol, and status; create/edit form; scheme detail with configuration and export assignments. This tab is suitable for scheme lifecycle operations; directory-level IO tab offers a contextual shortcut into the same assignment controls.

Use HTMX for loading fragments, saving forms, deleting schemes, and assigning/dismissing exports. Return focused HTML fragments for successful HTMX actions and a Bootstrap alert fragment for validation/API errors. Preserve entered form values on validation errors. Include confirmation for destructive scheme deletion and make the effect of deleting a scheme with assigned directories explicit.

### Directory security editor

- Show directory path and local mode: `Inherited from <nearest ancestor>` when access entries are null; `Custom rules` when entries are non-null, including an empty set.
- Offer an explicit **Inherit from parent** action that submits JSON `null` (or a dedicated `inherit=true` request) and an **Use custom rules** action. The latter may start from the currently effective rules for convenience, but must not save until the admin confirms.
- Rule editor rows select principal type (`User`, `Group`, `All`), principal, and permission values. Only one `ALL` row is allowed. USER/GROUP principals are selected from API-backed searchable lists. Render every permission returned by `/api/directory/access/permission/all`, grouped by its tag if useful, with description and boolean allow/deny controls.
- Keep the current permission semantics visible: for an explicit principal rule an absent permission key means no override; explicit `false` denies. For matching USER/GROUP rows, current service logic grants on any matching `true` and denies when a matching explicit `false` is present; explain this in helper text and avoid implying ordered ACL evaluation.
- Show an effective-permissions preview for a selected user/group only if backed by a server calculation; do not calculate inheritance/overrides differently in JavaScript.

### IO scheme editor

- Keep the UIX IO client and editor models protocol-neutral: select modules from `IOProtocol` metadata and address a scheme by `(type, protocol, name)`. Do not add protocol-specific methods or route branches for individual modules.
- List scheme identity (`type`, `protocol`, `name`), status, error/admin alert state, and assigned export directories.
- On create, choose module type and protocol from `GET /api/io/protocol`; then render fields from its `configEntries`. Apply type-aware inputs (text, secret/password, boolean, number, etc. according to `ConfigEntryType`), required flags, defaults, and server-side validation messages.
- On edit, fetch full `IOScheme` using the existing detail endpoint, populate module config, and submit the complete scheme through the existing save API.
- For imports, render only the configuration supported by the current module. Do not add or imply a shared fixed target directory option in the prototype; import directory behavior remains module-specific.
- For exports, allow selecting multiple directories from the admin tree and save assignment changes via existing assign/dismiss endpoints. Keep assignment identity keyed by `(type, protocol, scheme name, directory path)`; do not key by display text alone.
- Do not expose secret config values in list responses or prefill secrets into HTML if the API masks them. If current APIs return secret values, add a masked/secret-safe response and require re-entry or preserve server value on unchanged edit.

## Implementation plan (large steps)

### Step 1 — Confirm and formalize server-side admin authorization

1. Reuse one gateway admin authorization helper based on `ADMIN_GROUP`; avoid duplicating slightly different checks in `IORoutes` and directory admin routes.
2. Apply the check to every new admin-facing endpoint and to existing directory access management endpoints if they are used by the admin console. Keep ordinary directory CRUD/read authorization behavior intact.
3. Ensure unauthorized requests return the standard 403/access-denied response, and the UIX admin routes handle a non-admin response by showing an access-denied page rather than a blank fragment.
4. Add authorization coverage for admin and non-admin users for directory access read/write and IO scheme operations.

### Step 2 — Complete the admin directory data/API contract

1. Add an admin tree/list API that returns all directories in stable parent-before-child order, or make UIX page through `GET /api/directory` until `totalCount` is satisfied. Prefer an explicit tree endpoint if pagination behavior makes reliable completion awkward.
2. Add/extend admin security endpoints to expose, for a directory, local access mode (`INHERIT` vs `CUSTOM`), local entries, nearest ancestor providing entries, and effective permissions only where a user context is requested. Keep `null` vs empty-set semantics intact.
3. Ensure security management loads the permission catalog plus users and groups. Add paginated/search endpoints for principals if existing `/api/user` or `/api/group` routes cannot support a large list.
4. Make security writes accept an explicit inherit operation and validate directory existence, principal type/name, duplicate principals, duplicate `ALL`, and permission keys. Return validation errors in a stable response shape usable by the UI.
5. Add server-side transactional handling for a full access-entry replacement so a failed validation cannot partially update rules.
6. Document whether admins bypass access checks for this administrative operation; do not confuse admin capability with the `root` login's permission bypass in `AuthService`.

### Step 3 — Deferred

Skip this step for the prototype. Keep the current module metadata and import behavior; do not add a shared import target field or capability metadata. A later iteration can add fixed import targets after the module-specific behavior is better understood.

### Step 4 — Harden and round out the IO REST API

1. Keep existing scheme CRUD, protocol, and assignment operations. Use the existing paginated directory API for assignment pickers, and refresh assignment lists from the existing scheme list data.
2. Add only basic request checks for required scheme identity fields and registered modules/schemes. Pass directory values through to the exchanger, which owns directory validation. Leave detailed module config validation and duplicate-name policy to the IO modules.
3. Add query/body route variants for directory paths and scheme keys, since dot-separated directories and reserved characters are fragile in path segments. Keep old routes available for compatibility and use the safe variants in UIX.
4. Keep errors on the existing `CoreError` response format and update OpenAPI annotations for added routes. Do not add a larger validation/error framework for the prototype.

### Step 5 — Add UIX REST clients and typed UI models

1. Implement or complete `IORestClient` production/dummy implementations for every required operation; inspect the existing implementations and align URL encoding, response parsing, and error handling.
2. Add directory-admin API methods for tree data, access mode/entries, permission catalog, and user/group lookup. Do not call gateway REST APIs from browser JavaScript; UIX server routes should attach the logged-in user's JWT as current clients do.
3. Add UI models/view models for directory tree nodes, security editor state, protocol/config metadata, and scheme list/detail. Avoid passing secret config values to templates unless required and explicitly masked.
4. Centralize API exception translation into actionable Bootstrap alerts while retaining diagnostic logs server-side.

### Step 6 — Build the HTMX/Bootstrap admin pages

1. Add an admin route class and register it in `Uix.main`; add `/admin` access gating based on current account groups. Gate both full pages and fragment/action routes.
2. Add navigation in `index.html` only for admins and an `/admin` entry point. Keep existing message workspace unchanged for non-admins.
3. Build the directory tree and directory detail fragments; wire the **Security** tab to fetch/edit rule fragments and submit replacements/inherit actions.
4. Build the system-wide **IO schemes** table, protocol picker, metadata-driven config form, create/edit/delete actions, and status/error presentation.
5. Build export assignment controls with a directory tree picker and live assigned-directory list. Provide the same controls from a selected directory's **IO** tab.
6. Use Bootstrap accessible tabs, labels, keyboard focus, confirmation dialogs, loading indicators, empty states, and inline errors. Add minimal JavaScript only for interactions HTMX cannot express (e.g. row cloning or confirmation); keep persistence/server decisions in the server.

### Step 7 — Verify end-to-end behavior and rollout

1. Unit-test access mode serialization (`null` inheritance versus empty custom set), validation, `ALL` uniqueness, matching principal behavior, and export assignment refresh.
2. Integration-test admin/non-admin access, all CRUD flows, assignment/dismiss, and UIX route rendering with gateway dummy and a real gateway integration profile.
3. Verify existing import schemes retain their current module-specific behavior.
4. Verify export assignment causes newly created messages in the selected directory to flow through the scheme, and dismissal stops subsequent exports as expected.
5. Update user/admin documentation with permissions required, inheritance rules, import directory behavior, scheme lifecycle, and operational status meanings.

## Acceptance criteria

- Only members of the `Admins` group can access admin pages or admin-specific directory/IO management operations; enforcement is server-side.
- Admin can navigate the complete directory tree and inspect a directory's local access mode without confusing inherited rules with custom empty rules.
- Admin can assign multiple USER/GROUP principals and the single ALL wildcard any supported permissions, save, reload, and see the same entries.
- Switching a directory to inheritance clears local access entries to `null`; a custom empty policy remains representable as an empty set.
- Protocol-specific IO forms are generated from module metadata and validate required fields.
- Import directory behavior remains module-specific; a shared fixed-target feature is deferred.
- Admin can create/edit/delete schemes, view status/error, and assign/dismiss export schemes on directories; results refresh from server state.
- Existing non-admin message browsing/editing and existing IO consumers continue to work.

## Suggested delivery order

Deliver Steps 1–2 and 4 as the backend/API foundation, Steps 5–6 as the admin UI, then Step 7 verification and documentation. Step 3 is intentionally deferred for the prototype.
