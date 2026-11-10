/*
 * This file is part of Ribbon2 news message system.
 * Copyright (C) 2026 Freax Software
 */
package tk.freaxsoftware.ribbon2.uix.routes;

import io.javalin.Javalin;
import io.javalin.http.Context;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tk.freaxsoftware.ribbon2.core.data.DirectoryAccessModel;
import tk.freaxsoftware.ribbon2.core.data.DirectoryModel;
import tk.freaxsoftware.ribbon2.core.data.DirectoryPermissionTaggedModel;
import tk.freaxsoftware.ribbon2.core.data.DirectorySecurityModel;
import tk.freaxsoftware.ribbon2.core.data.GroupModel;
import tk.freaxsoftware.ribbon2.core.data.UserModel;
import tk.freaxsoftware.ribbon2.core.data.response.DefaultPage;
import tk.freaxsoftware.ribbon2.core.data.response.DirectoryPage;
import tk.freaxsoftware.ribbon2.io.core.ConfigEntry;
import tk.freaxsoftware.ribbon2.io.core.ConfigEntryType;
import tk.freaxsoftware.ribbon2.io.core.IOScheme;
import tk.freaxsoftware.ribbon2.io.core.ModuleType;
import tk.freaxsoftware.ribbon2.uix.UserSessionModelContext;
import tk.freaxsoftware.ribbon2.uix.model.DirectoryNode;
import tk.freaxsoftware.ribbon2.uix.model.io.IOModuleScheme;
import tk.freaxsoftware.ribbon2.uix.model.io.IOProtocol;
import tk.freaxsoftware.ribbon2.uix.model.io.IOSchemeEditorModel;
import tk.freaxsoftware.ribbon2.uix.rest.GatewayService;

/** Admin-only pages and HTMX actions for directory security and IO management. */
public final class AdminRoutes {
    private static final Logger LOGGER = LoggerFactory.getLogger(AdminRoutes.class);
    private static final int LOOKUP_PAGE_SIZE = 1000;

    private AdminRoutes() {}

    public static void init(Javalin app, GatewayService gatewayService) {
        app.get("/admin", ctx -> {
            if (!guard(ctx)) return;
            String jwt = UserSessionModelContext.getUser().getJwtKey();
            DirectoryPage dirs = gatewayService.getDirectoryRestClient().getDirectories(jwt);
            Map<String, Object> model = new HashMap<>();
            model.put("user", UserSessionModelContext.getUser());
            model.put("directories", toTree(dirs));
            model.put("protocols", gatewayService.getIoRestClient().getProtocols(jwt));
            model.put("schemes", gatewayService.getIoRestClient().getSchemes(jwt));
            ctx.render("admin.html", model);
        });

        app.get("/admin/directory", ctx -> {
            if (!guard(ctx)) return;
            renderDirectoryDetail(ctx, gatewayService, ctx.queryParam("path"));
        });

        app.post("/admin/directory", ctx -> {
            if (!guard(ctx)) return;
            try {
                DirectoryModel directory = new DirectoryModel();
                directory.setFullName(ctx.formParam("fullName").trim());
                directory.setDescription(ctx.formParam("description"));
                DirectoryModel created = gatewayService.getDirectoryRestClient().createDirectory(
                        UserSessionModelContext.getUser().getJwtKey(), directory);
                ctx.header("X-Directory-Changed", "true");
                renderDirectoryDetail(ctx, gatewayService, created.getFullName());
            } catch (Exception ex) {
                renderError(ctx, "Could not create directory: " + ex.getMessage(), ex);
            }
        });

        app.put("/admin/directory", ctx -> {
            if (!guard(ctx)) return;
            String path = ctx.formParam("path");
            try {
                DirectoryModel directory = new DirectoryModel();
                directory.setFullName(path);
                directory.setDescription(ctx.formParam("description"));
                gatewayService.getDirectoryRestClient().updateDirectory(
                        UserSessionModelContext.getUser().getJwtKey(), directory);
                ctx.header("X-Directory-Changed", "true");
                renderDirectoryDetail(ctx, gatewayService, path);
            } catch (Exception ex) {
                renderError(ctx, "Could not update directory description: " + ex.getMessage(), ex);
            }
        });

        app.delete("/admin/directory", ctx -> {
            if (!guard(ctx)) return;
            String path = ctx.queryParam("path");
            try {
                gatewayService.getDirectoryRestClient().deleteDirectory(
                        UserSessionModelContext.getUser().getJwtKey(), path);
                ctx.header("X-Directory-Changed", "true");
                ctx.render("admin-alert.html", Map.of("message", "Directory and its descendants were deleted."));
            } catch (Exception ex) {
                renderError(ctx, "Could not delete directory: " + ex.getMessage(), ex);
            }
        });

        app.post("/admin/directory/security", ctx -> {
            if (!guard(ctx)) return;
            String path = ctx.formParam("path");
            try {
                Set<DirectoryAccessModel> entries = "inherit".equals(ctx.formParam("mode"))
                        ? null : parseAccessEntries(ctx, gatewayService, path);
                gatewayService.getDirectoryRestClient().saveDirectoryAccess(
                        UserSessionModelContext.getUser().getJwtKey(), path, entries);
                renderDirectoryDetail(ctx, gatewayService, path);
            } catch (Exception ex) {
                LOGGER.warn("Could not save directory security rules for {}", path, ex);
                try {
                    renderDirectoryDetail(ctx, gatewayService, path,
                            "Could not save directory security rules: " + ex.getMessage(),
                            "inherit".equals(ctx.formParam("mode")) ? null : postedAccessRows(ctx, gatewayService));
                } catch (Exception renderFailure) {
                    renderError(ctx, "Could not save directory security rules: " + ex.getMessage(), ex);
                }
            }
        });

        app.get("/admin/io/editor", ctx -> {
            if (!guard(ctx)) return;
            try {
                ctx.render("admin-io-editor.html", editorModel(ctx, gatewayService, null));
            } catch (Exception ex) {
                renderError(ctx, "Could not load IO module metadata: " + ex.getMessage(), ex);
            }
        });

        app.get("/admin/io/scheme", ctx -> {
            if (!guard(ctx)) return;
            try {
                String type = ctx.queryParam("type");
                String protocol = ctx.queryParam("protocol");
                String name = ctx.queryParam("name");
                Optional<IOScheme> scheme = gatewayService.getIoRestClient().getScheme(
                        UserSessionModelContext.getUser().getJwtKey(), type, protocol, name);
                if (scheme.isEmpty()) {
                    ctx.status(200);
                    ctx.render("admin-alert.html", Map.of("message", "IO scheme was not found."));
                    return;
                }
                ctx.render("admin-io-editor.html", editorModel(ctx, gatewayService, scheme.get()));
            } catch (Exception ex) {
                renderError(ctx, "Could not load IO scheme: " + ex.getMessage(), ex);
            }
        });

        app.post("/admin/io/scheme", ctx -> {
            if (!guard(ctx)) return;
            try {
                IOScheme scheme = parseSchemeForm(ctx, gatewayService);
                IOScheme saved = gatewayService.getIoRestClient().saveScheme(
                        UserSessionModelContext.getUser().getJwtKey(), scheme);
                ctx.render("admin-io-schemes.html", Map.of(
                        "schemes", gatewayService.getIoRestClient().getSchemes(UserSessionModelContext.getUser().getJwtKey()),
                        "protocols", gatewayService.getIoRestClient().getProtocols(UserSessionModelContext.getUser().getJwtKey()),
                        "savedScheme", saved));
            } catch (Exception ex) {
                renderError(ctx, "Could not save IO scheme: " + ex.getMessage(), ex);
            }
        });

        app.delete("/admin/io/scheme", ctx -> {
            if (!guard(ctx)) return;
            try {
                gatewayService.getIoRestClient().deleteScheme(UserSessionModelContext.getUser().getJwtKey(),
                        ctx.queryParam("type"), ctx.queryParam("protocol"), ctx.queryParam("name"));
                renderSchemes(ctx, gatewayService, null);
            } catch (Exception ex) {
                renderError(ctx, "Could not delete IO scheme: " + ex.getMessage(), ex);
            }
        });

        app.post("/admin/io/assignment", ctx -> {
            if (!guard(ctx)) return;
            try {
                gatewayService.getIoRestClient().assignExportScheme(UserSessionModelContext.getUser().getJwtKey(),
                        ctx.formParam("protocol"), ctx.formParam("scheme"), ctx.formParam("directory"));
                renderDirectoryDetail(ctx, gatewayService, ctx.formParam("directory"));
            } catch (Exception ex) {
                renderError(ctx, "Could not assign export scheme: " + ex.getMessage(), ex);
            }
        });

        app.delete("/admin/io/assignment", ctx -> {
            if (!guard(ctx)) return;
            try {
                gatewayService.getIoRestClient().dismissExportScheme(UserSessionModelContext.getUser().getJwtKey(),
                        ctx.queryParam("protocol"), ctx.queryParam("scheme"), ctx.queryParam("directory"));
                renderDirectoryDetail(ctx, gatewayService, ctx.queryParam("directory"));
            } catch (Exception ex) {
                renderError(ctx, "Could not dismiss export scheme: " + ex.getMessage(), ex);
            }
        });
    }

    private static boolean guard(Context ctx) {
        if (UserSessionModelContext.getUser() != null
                && UserSessionModelContext.getUser().getGroups() != null
                && UserSessionModelContext.getUser().getGroups().contains(GroupModel.ADMIN_GROUP)) return true;
        ctx.status(403);
        ctx.render("admin-denied.html", Map.of());
        return false;
    }

    private static void renderDirectoryDetail(Context ctx, GatewayService gateway, String path) throws Exception {
        renderDirectoryDetail(ctx, gateway, path, null, null);
    }

    private static void renderDirectoryDetail(Context ctx, GatewayService gateway, String path,
            String securityError, List<Map<String, Object>> submittedRows) throws Exception {
        String jwt = UserSessionModelContext.getUser().getJwtKey();
        DirectoryPage page = gateway.getDirectoryRestClient().getDirectories(jwt);
        DirectoryModel directory = page.getContent().stream()
                .filter(item -> Objects.equals(item.getFullName(), path)).findFirst().orElse(null);
        if (directory == null) {
            ctx.status(200);
            ctx.render("admin-alert.html", Map.of("message", "Directory was not found."));
            return;
        }
        DirectorySecurityModel security = gateway.getDirectoryRestClient().getDirectorySecurity(jwt, path);
        List<DirectoryPermissionTaggedModel> permissions = gateway.getDirectoryRestClient().getDirectoryPermissionCatalog(jwt);
        List<UserModel> users = loadUsers(gateway, jwt);
        List<GroupModel> groups = loadGroups(gateway, jwt);
        List<IOModuleScheme> assigned = gateway.getIoRestClient().getExportSchemesByDirectory(jwt, path);
        List<IOModuleScheme> allSchemes = gateway.getIoRestClient().getSchemes(jwt);
        List<IOModuleScheme> exportSchemes = allSchemes.stream().filter(s -> s.getType() == ModuleType.EXPORT).toList();
        List<Map<String, Object>> rows = accessRows(security, permissions);
        Map<String, Object> model = new HashMap<>();
        model.put("directory", directory);
        model.put("security", security);
        model.put("permissions", permissions);
        model.put("ruleRows", submittedRows == null ? rows : submittedRows);
        model.put("securityError", securityError);
        model.put("users", users);
        model.put("groups", groups);
        model.put("assigned", assigned);
        model.put("exportSchemes", exportSchemes);
        ctx.render("admin-directory-detail.html", model);
    }

    private static List<Map<String, Object>> accessRows(DirectorySecurityModel security, List<DirectoryPermissionTaggedModel> permissions) {
        List<Map<String, Object>> rows = new ArrayList<>();
        if (security.getAccessEntries() != null) {
            for (DirectoryAccessModel entry : security.getAccessEntries()) {
                rows.add(accessRow(entry, permissions));
            }
        }
        for (int i = 0; i < 3; i++) rows.add(accessRow(null, permissions));
        return rows;
    }

    private static Map<String, Object> accessRow(DirectoryAccessModel entry, List<DirectoryPermissionTaggedModel> permissions) {
        Map<String, Object> row = new HashMap<>();
        row.put("type", entry == null || entry.getType() == null ? "USER" : entry.getType().name());
        row.put("name", entry == null ? "" : Objects.toString(entry.getName(), ""));
        List<String> states = new ArrayList<>();
        for (DirectoryPermissionTaggedModel permission : permissions) {
            Boolean value = entry == null || entry.getPermissions() == null ? null : entry.getPermissions().get(permission.getKey());
            states.add(value == null ? "inherit" : value ? "allow" : "deny");
        }
        row.put("permissionStates", states);
        return row;
    }

    private static Set<DirectoryAccessModel> parseAccessEntries(Context ctx, GatewayService gateway, String path) throws Exception {
        List<String> types = ctx.formParams("ruleType");
        List<String> names = ctx.formParams("ruleName");
        List<DirectoryPermissionTaggedModel> catalog = gateway.getDirectoryRestClient()
                .getDirectoryPermissionCatalog(UserSessionModelContext.getUser().getJwtKey());
        Set<DirectoryAccessModel> entries = new HashSet<>();
        for (int row = 0; row < types.size(); row++) {
            String typeValue = types.get(row);
            String name = row < names.size() ? names.get(row).trim() : "";
            if (name.isEmpty() && !"ALL".equals(typeValue)) continue;
            DirectoryAccessModel.Type type = DirectoryAccessModel.Type.valueOf(typeValue);
            Map<String, Boolean> rules = new LinkedHashMap<>();
            for (int index = 0; index < catalog.size(); index++) {
                String value = ctx.formParam("perm_" + row + "_" + index);
                if ("allow".equals(value)) rules.put(catalog.get(index).getKey(), true);
                if ("deny".equals(value)) rules.put(catalog.get(index).getKey(), false);
            }
            entries.add(new DirectoryAccessModel(type == DirectoryAccessModel.Type.ALL ? "*" : name, type, rules));
        }
        return entries;
    }

    private static List<Map<String, Object>> postedAccessRows(Context ctx, GatewayService gateway) throws Exception {
        List<DirectoryPermissionTaggedModel> catalog = gateway.getDirectoryRestClient()
                .getDirectoryPermissionCatalog(UserSessionModelContext.getUser().getJwtKey());
        List<String> types = ctx.formParams("ruleType");
        List<String> names = ctx.formParams("ruleName");
        List<Map<String, Object>> rows = new ArrayList<>();
        for (int rowIndex = 0; rowIndex < types.size(); rowIndex++) {
            Map<String, Object> row = new HashMap<>();
            row.put("type", types.get(rowIndex));
            row.put("name", rowIndex < names.size() ? names.get(rowIndex) : "");
            List<String> states = new ArrayList<>();
            for (int permissionIndex = 0; permissionIndex < catalog.size(); permissionIndex++) {
                String state = ctx.formParam("perm_" + rowIndex + "_" + permissionIndex);
                states.add(Set.of("allow", "deny").contains(state) ? state : "inherit");
            }
            row.put("permissionStates", states);
            rows.add(row);
        }
        return rows;
    }

    private static Map<String, Object> editorModel(Context ctx, GatewayService gateway, IOScheme source) throws Exception {
        String jwt = UserSessionModelContext.getUser().getJwtKey();
        List<IOProtocol> protocols = gateway.getIoRestClient().getProtocols(jwt);
        String typeValue = source == null ? ctx.queryParam("type") : source.getType().name();
        String protocolValue = source == null ? ctx.queryParam("protocol") : source.getProtocol();
        ModuleType type = typeValue == null ? null : ModuleType.valueOf(typeValue.toUpperCase());
        IOProtocol protocol = protocols.stream().filter(item -> item.getType() == type
                && Objects.equals(item.getProtocol(), protocolValue)).findFirst().orElse(null);
        IOSchemeEditorModel editor = new IOSchemeEditorModel(protocol, source);
        Map<String, Object> model = new HashMap<>();
        model.put("protocols", protocols);
        model.put("selectedType", type);
        model.put("selectedProtocol", protocolValue);
        model.put("editor", editor);
        model.put("editing", source != null);
        model.put("directoryPaths", gateway.getDirectoryRestClient().getDirectories(jwt).getContent().stream()
                .map(DirectoryModel::getFullName).toList());
        Map<String, Set<String>> selectedDirectoryLists = new HashMap<>();
        if (protocol != null && protocol.getConfigEntries() != null) {
            for (ConfigEntry entry : protocol.getConfigEntries()) {
                if (entry.getType() != ConfigEntryType.DIRECTORY_LIST) continue;
                Object configured = source == null || source.getConfig() == null ? null : source.getConfig().get(entry.getName());
                Set<String> selected = new HashSet<>();
                if (configured instanceof String value && !value.isBlank()) selected.addAll(List.of(value.split("\\s*,\\s*")));
                else if (configured instanceof Iterable<?> values) for (Object value : values) selected.add(Objects.toString(value, ""));
                selectedDirectoryLists.put(entry.getName(), selected);
            }
        }
        model.put("selectedDirectoryLists", selectedDirectoryLists);
        return model;
    }

    private static IOScheme parseSchemeForm(Context ctx, GatewayService gateway) throws Exception {
        String jwt = UserSessionModelContext.getUser().getJwtKey();
        ModuleType type = ModuleType.valueOf(ctx.formParam("type").toUpperCase());
        String protocolName = ctx.formParam("protocol");
        String name = ctx.formParam("name");
        String originalName = ctx.formParam("originalName");
        IOProtocol protocol = gateway.getIoRestClient().getProtocols(jwt).stream()
                .filter(item -> item.getType() == type && Objects.equals(item.getProtocol(), protocolName))
                .findFirst().orElseThrow(() -> new IllegalArgumentException("Selected IO module is unavailable."));
        IOScheme scheme = new IOScheme();
        scheme.setId(protocol.getId());
        scheme.setType(type);
        scheme.setProtocol(protocolName);
        scheme.setName(name);
        scheme.setDescription(ctx.formParam("description"));
        Map<String, Object> config = new LinkedHashMap<>();
        Optional<IOScheme> existing = originalName == null || originalName.isBlank() ? Optional.empty()
                : gateway.getIoRestClient().getScheme(jwt, type.name(), protocolName, originalName);
        if (existing.isPresent() && existing.get().getConfig() != null) config.putAll(existing.get().getConfig());
        if (protocol.getConfigEntries() != null) {
            for (ConfigEntry entry : protocol.getConfigEntries()) {
                String raw = ctx.formParam("config_" + entry.getName());
                List<String> multiple = ctx.formParams("config_" + entry.getName());
                if (entry.getType() == ConfigEntryType.SECRET && (raw == null || raw.isBlank()) && config.containsKey(entry.getName())) continue;
                config.put(entry.getName(), parseConfigValue(entry, raw, multiple));
            }
        }
        scheme.setConfig(config);
        return scheme;
    }

    private static Object parseConfigValue(ConfigEntry entry, String raw, List<String> multiple) {
        if (raw == null || raw.isBlank()) return null;
        return switch (entry.getType()) {
            case BOOLEAN -> Boolean.valueOf(raw);
            case NUMBER -> {
                try { yield Long.valueOf(raw); }
                catch (NumberFormatException ignored) { yield Double.valueOf(raw); }
            }
            case DIRECTORY_LIST -> multiple == null ? "" : String.join(",", multiple);
            default -> raw;
        };
    }

    private static void renderSchemes(Context ctx, GatewayService gateway, IOScheme saved) throws Exception {
        Map<String, Object> model = new HashMap<>();
        model.put("schemes", gateway.getIoRestClient().getSchemes(UserSessionModelContext.getUser().getJwtKey()));
        model.put("protocols", gateway.getIoRestClient().getProtocols(UserSessionModelContext.getUser().getJwtKey()));
        model.put("savedScheme", saved);
        ctx.render("admin-io-schemes.html", model);
    }

    private static void renderError(Context ctx, String message, Exception ex) {
        LOGGER.warn("Admin UI operation failed: {}", message, ex);
        // HTMX swaps successful responses by default; return the alert fragment for inline display.
        ctx.status(200);
        ctx.render("admin-alert.html", Map.of("message", message == null ? "Request failed." : message));
    }

    private static List<UserModel> loadUsers(GatewayService gateway, String jwt) throws Exception {
        List<UserModel> users = new ArrayList<>();
        int page = 0;
        long total;
        do {
            DefaultPage<UserModel> result = gateway.getUserRestClient().getUsers(jwt, LOOKUP_PAGE_SIZE, page++);
            if (result.getContent() == null || result.getContent().isEmpty()) break;
            users.addAll(result.getContent());
            total = result.getTotalCount();
        } while (users.size() < total);
        return users;
    }

    private static List<GroupModel> loadGroups(GatewayService gateway, String jwt) throws Exception {
        List<GroupModel> groups = new ArrayList<>();
        int page = 0;
        long total;
        do {
            DefaultPage<GroupModel> result = gateway.getGroupRestClient().getGroups(jwt, LOOKUP_PAGE_SIZE, page++);
            if (result.getContent() == null || result.getContent().isEmpty()) break;
            groups.addAll(result.getContent());
            total = result.getTotalCount();
        } while (groups.size() < total);
        return groups;
    }

    private static DirectoryNode toTree(DirectoryPage page) {
        DirectoryNode root = new DirectoryNode();
        Map<String, DirectoryNode> nodes = new LinkedHashMap<>();
        if (page.getContent() == null) return root;
        for (DirectoryModel directory : page.getContent()) {
            DirectoryNode node = new DirectoryNode(directory);
            String parent = directory.parentName();
            if (parent == null || parent.isEmpty()) root.getDirectoryChildren().add(node);
            else if (nodes.containsKey(parent)) nodes.get(parent).getDirectoryChildren().add(node);
            else {
                LOGGER.warn("Directory tree page is not parent-first; omitting orphan directory {}", directory.getFullName());
                continue;
            }
            nodes.put(directory.getFullName(), node);
        }
        return root;
    }
}
