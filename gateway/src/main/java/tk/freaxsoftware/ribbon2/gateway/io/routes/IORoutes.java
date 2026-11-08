/*
 * This file is part of Ribbon2 news message system.
 * 
 * Copyright (C) 2020 Freax Software
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library.
 */
package tk.freaxsoftware.ribbon2.gateway.io.routes;

import io.javalin.http.Context;
import io.javalin.openapi.HttpMethod;
import io.javalin.openapi.OpenApi;
import io.javalin.openapi.OpenApiContent;
import io.javalin.openapi.OpenApiParam;
import io.javalin.openapi.OpenApiRequestBody;
import io.javalin.openapi.OpenApiResponse;
import io.javalin.openapi.OpenApiSecurity;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tk.freaxsoftware.ribbon2.gateway.io.IOService;
import tk.freaxsoftware.extras.bus.MessageBus;
import tk.freaxsoftware.extras.bus.MessageOptions;
import tk.freaxsoftware.extras.bus.storage.StorageInterceptor;
import tk.freaxsoftware.ribbon2.core.data.UserModel;
import tk.freaxsoftware.ribbon2.core.exception.CoreError;
import tk.freaxsoftware.ribbon2.core.exception.CoreException;
import tk.freaxsoftware.ribbon2.core.exception.RibbonErrorCodes;
import tk.freaxsoftware.ribbon2.gateway.GatewayMain;
import tk.freaxsoftware.ribbon2.gateway.io.data.IOModuleScheme;
import tk.freaxsoftware.ribbon2.gateway.io.data.IOProtocol;
import tk.freaxsoftware.ribbon2.gateway.utils.UserContext;
import tk.freaxsoftware.ribbon2.gateway.utils.AdminAuthorization;
import tk.freaxsoftware.ribbon2.gateway.watchdog.WatchdogRegistry;
import tk.freaxsoftware.ribbon2.io.core.IOLocalIds;
import tk.freaxsoftware.ribbon2.io.core.IOExportAssignment;
import tk.freaxsoftware.ribbon2.io.core.IOScheme;
import tk.freaxsoftware.ribbon2.io.core.ModuleRegistration;
import tk.freaxsoftware.ribbon2.io.core.ModuleType;

/**
 * IO API routes.
 * @author Stanislav Nepochatov
 */
public class IORoutes {
    
    private final static Logger LOGGER = LoggerFactory.getLogger(IORoutes.class);
    
    public static void initWatchdog() {
        WatchdogRegistry.registerPatternTopic(toPattern(IOLocalIds.IO_SCHEME_GET_TOPIC), 
                "Get full IO scheme", WatchdogRegistry.Node.EXCHANGER);
        WatchdogRegistry.registerPatternTopic(toPattern(IOLocalIds.IO_SCHEME_SAVE_TOPIC), 
                "Save IO scheme", WatchdogRegistry.Node.EXCHANGER);
        WatchdogRegistry.registerPatternTopic(toPattern(IOLocalIds.IO_SCHEME_DELETE_TOPIC), 
                "Delete IO scheme", WatchdogRegistry.Node.EXCHANGER);
        WatchdogRegistry.registerPatternTopic(toPattern(IOLocalIds.IO_SCHEME_EXPORT_ASSIGN_TOPIC), 
                "Assign export scheme to directory", WatchdogRegistry.Node.EXCHANGER);
        WatchdogRegistry.registerPatternTopic(toPattern(IOLocalIds.IO_SCHEME_EXPORT_DISMISS_TOPIC), 
                "Dismiss export scheme from directory", WatchdogRegistry.Node.EXCHANGER);
    }
    
    private static String toPattern(String rawTopic) {
        return rawTopic + ".*";
    }
    
    @OpenApi(
        summary = "Get IO protocols",
        operationId = "getProtocols",
        path = "/api/io/protocol",
        methods = HttpMethod.GET,
        tags = {"IO"},
        security = {
            @OpenApiSecurity(name = "ribbonToken")
        },
        responses = {
            @OpenApiResponse(status = "200", content = {@OpenApiContent(from = IOProtocol[].class)}),
            @OpenApiResponse(status = "401", content = {@OpenApiContent(from = CoreError.class)})
        }
    )
    public static void getProtocols(Context ctx) {
        AdminAuthorization.requireAdmin();
        LOGGER.info("Request to get IO protocols.");
        ctx.json(IOService.getInstance().getRegistrations().stream()
                .map(reg -> IOProtocol.ofModuleRegistration(reg))
                .collect(Collectors.toList()));
    }
    
    @OpenApi(
        summary = "Get IO schemes",
        operationId = "getSchemes",
        path = "/api/io/scheme",
        methods = HttpMethod.GET,
        tags = {"IO"},
        security = {
            @OpenApiSecurity(name = "ribbonToken")
        },
        responses = {
            @OpenApiResponse(status = "200", content = {@OpenApiContent(from = IOModuleScheme[].class)}),
            @OpenApiResponse(status = "401", content = {@OpenApiContent(from = CoreError.class)})
        }
    )
    public static void getSchemes(Context ctx) {
        AdminAuthorization.requireAdmin();
        LOGGER.info("Request to get IO schemes.");
        List<IOModuleScheme> moduleSchemes = new ArrayList<>();
        IOService.getInstance().getRegistrations().forEach(reg -> {
            moduleSchemes.addAll(IOModuleScheme.ofModuleRegistration(reg));
        });
        ctx.json(moduleSchemes);
    }
    
    @OpenApi(
        summary = "Get IO schemes by directory",
        operationId = "getExportSchemesByDirectory",
        path = "/api/io/export/scheme/{dirName}",
        methods = HttpMethod.GET,
        tags = {"IO"},
        security = {
            @OpenApiSecurity(name = "ribbonToken")
        },
        pathParams = {
            @OpenApiParam(name = "dirName", required = true)
        },
        responses = {
            @OpenApiResponse(status = "200", content = {@OpenApiContent(from = IOModuleScheme[].class)}),
            @OpenApiResponse(status = "401", content = {@OpenApiContent(from = CoreError.class)})
        }
    )
    public static void getExportSchemesByDirectory(Context ctx) {
        AdminAuthorization.requireAdmin();
        String dirName = ctx.pathParam("dirName");
        LOGGER.info("Request to get export IO schemes by directory {}", dirName);
        ctx.json(IOService.getInstance().getSchemesByExportDirectiry(dirName));
    }

    @OpenApi(
        summary = "Get export schemes by directory",
        operationId = "getExportSchemesByDirectoryQuery",
        path = "/api/io/export/schemes",
        methods = HttpMethod.GET,
        tags = {"IO"},
        security = {@OpenApiSecurity(name = "ribbonToken")},
        queryParams = {@OpenApiParam(name = "directory", required = true, type = String.class)},
        responses = {
            @OpenApiResponse(status = "200", content = {@OpenApiContent(from = IOModuleScheme[].class)}),
            @OpenApiResponse(status = "401", content = {@OpenApiContent(from = CoreError.class)})
        }
    )
    public static void getExportSchemesByDirectoryQuery(Context ctx) {
        AdminAuthorization.requireAdmin();
        String directory = requireText(ctx.queryParam("directory"), "directory");
        ctx.json(IOService.getInstance().getSchemesByExportDirectiry(directory));
    }
    
    @OpenApi(
        summary = "Get full IO scheme",
        operationId = "getScheme",
        path = "/api/io/scheme/{type}/{protocol}/{name}",
        methods = HttpMethod.GET,
        tags = {"IO"},
        security = {
            @OpenApiSecurity(name = "ribbonToken")
        },
        pathParams = {
            @OpenApiParam(name = "type", required = true),
            @OpenApiParam(name = "protocol", required = true),
            @OpenApiParam(name = "name", required = true)
        },
        responses = {
            @OpenApiResponse(status = "200", content = {@OpenApiContent(from = IOScheme.class)}),
            @OpenApiResponse(status = "401", content = {@OpenApiContent(from = CoreError.class)})
        }
    )
    public static void getScheme(Context ctx) throws Exception {
        AdminAuthorization.requireAdmin();
        String type = ctx.pathParam("type");
        String protocol = ctx.pathParam("protocol");
        String name = ctx.pathParam("name");
        getScheme(ctx, type, protocol, name);
    }

    @OpenApi(
        summary = "Get full IO scheme by query key",
        operationId = "getSchemeByQuery",
        path = "/api/io/scheme/detail",
        methods = HttpMethod.GET,
        tags = {"IO"},
        security = {@OpenApiSecurity(name = "ribbonToken")},
        queryParams = {
            @OpenApiParam(name = "type", required = true, type = String.class),
            @OpenApiParam(name = "protocol", required = true, type = String.class),
            @OpenApiParam(name = "name", required = true, type = String.class)
        },
        responses = {
            @OpenApiResponse(status = "200", content = {@OpenApiContent(from = IOScheme.class)}),
            @OpenApiResponse(status = "401", content = {@OpenApiContent(from = CoreError.class)}),
            @OpenApiResponse(status = "404", content = {@OpenApiContent(from = CoreError.class)})
        }
    )
    public static void getSchemeByQuery(Context ctx) throws Exception {
        AdminAuthorization.requireAdmin();
        getScheme(ctx, ctx.queryParam("type"), ctx.queryParam("protocol"), ctx.queryParam("name"));
    }

    private static void getScheme(Context ctx, String type, String protocol, String name) throws Exception {
        ModuleType moduleType = parseType(type);
        requireText(protocol, "protocol");
        requireText(name, "name");
        ModuleRegistration registration = getRegistration(moduleType, protocol);
        requireExistingScheme(registration, name);
        LOGGER.info("Request to get IO scheme {} for protocol {} with type {} with config.", name, protocol, type);
        IOScheme scheme = MessageBus.fireCall(String.format("%s.%s.%s", IOLocalIds.IO_SCHEME_GET_TOPIC, 
            moduleType.name().toLowerCase(), protocol), name, MessageOptions.Builder.newInstance()
                .header(StorageInterceptor.IGNORE_STORAGE_HEADER, "true")
                .header(UserModel.AUTH_HEADER_USERNAME, UserContext.getUser().getLogin())
                .header(UserModel.AUTH_HEADER_FULLNAME, UserContext.getUser().getFirstname() + " " + UserContext.getUser().getLastname())
                .deliveryCall().build(), IOScheme.class);
        if (scheme == null) {
            throw new CoreException(RibbonErrorCodes.IO_SCHEME_NOT_FOUND, "Can't find IO scheme " + name);
        }
        ctx.json(scheme);
    }
    
    @OpenApi(
        summary = "Save IO scheme",
        operationId = "saveScheme",
        path = "/api/io/scheme",
        methods = HttpMethod.POST,
        tags = {"IO"},
        security = {
            @OpenApiSecurity(name = "ribbonToken")
        },
        requestBody = @OpenApiRequestBody(required = true, content = {@OpenApiContent(from = IOScheme.class)}),
        responses = {
            @OpenApiResponse(status = "200", content = {@OpenApiContent(from = IOScheme.class)}),
            @OpenApiResponse(status = "401", content = {@OpenApiContent(from = CoreError.class)})
        }
    )
    public static void saveScheme(Context ctx) throws Exception {
        AdminAuthorization.requireAdmin();
        IOScheme scheme = GatewayMain.gson.fromJson(ctx.body(), IOScheme.class);
        if (scheme == null || scheme.getType() == null) {
            throw new CoreException(RibbonErrorCodes.INVALID_REQUEST, "Scheme type is required.");
        }
        requireText(scheme.getProtocol(), "protocol");
        requireText(scheme.getName(), "name");
        ModuleRegistration registration = getRegistration(scheme.getType(), scheme.getProtocol());
        if (registration == null) {
            throw new CoreException(RibbonErrorCodes.IO_SCHEME_NOT_FOUND,
                    "No IO module is registered for " + scheme.getType() + "/" + scheme.getProtocol());
        }
        // The module registration owns the canonical id (for example, "import:rss").
        // Use it even if a client omitted or supplied a stale id.
        scheme.setId(registration.getId());
        LOGGER.info("Request to save IO scheme {} for protocol {} with type {}.", scheme.getName(), scheme.getProtocol(), scheme.getType());
        IOScheme saved = MessageBus.fireCall(String.format("%s.%s.%s", IOLocalIds.IO_SCHEME_SAVE_TOPIC, 
            scheme.getType().name().toLowerCase(), scheme.getProtocol()), scheme, MessageOptions.Builder.newInstance()
                .header(UserModel.AUTH_HEADER_USERNAME, UserContext.getUser().getLogin())
                .header(UserModel.AUTH_HEADER_FULLNAME, UserContext.getUser().getFirstname() + " " + UserContext.getUser().getLastname())
                .deliveryCall().build(), IOScheme.class);
        ctx.json(saved);
    }
    
    @OpenApi(
        summary = "Delete IO scheme",
        operationId = "deleteScheme",
        path = "/api/io/scheme/{type}/{protocol}/{name}",
        methods = HttpMethod.DELETE,
        tags = {"IO"},
        security = {
            @OpenApiSecurity(name = "ribbonToken")
        },
        pathParams = {
            @OpenApiParam(name = "type", required = true),
            @OpenApiParam(name = "protocol", required = true),
            @OpenApiParam(name = "name", required = true)
        },
        responses = {
            @OpenApiResponse(status = "200", content = {@OpenApiContent(from = Void.class)}),
            @OpenApiResponse(status = "401", content = {@OpenApiContent(from = CoreError.class)})
        }
    )
    public static void deleteScheme(Context ctx) throws Exception {
        AdminAuthorization.requireAdmin();
        String type = ctx.pathParam("type");
        String protocol = ctx.pathParam("protocol");
        String name = ctx.pathParam("name");
        deleteScheme(type, protocol, name);
        ctx.status(200);
    }

    @OpenApi(
        summary = "Delete IO scheme by query key",
        operationId = "deleteSchemeByQuery",
        path = "/api/io/scheme",
        methods = HttpMethod.DELETE,
        tags = {"IO"},
        security = {@OpenApiSecurity(name = "ribbonToken")},
        queryParams = {
            @OpenApiParam(name = "type", required = true, type = String.class),
            @OpenApiParam(name = "protocol", required = true, type = String.class),
            @OpenApiParam(name = "name", required = true, type = String.class)
        },
        responses = {
            @OpenApiResponse(status = "200", content = {@OpenApiContent(from = Void.class)}),
            @OpenApiResponse(status = "401", content = {@OpenApiContent(from = CoreError.class)}),
            @OpenApiResponse(status = "404", content = {@OpenApiContent(from = CoreError.class)})
        }
    )
    public static void deleteSchemeByQuery(Context ctx) throws Exception {
        AdminAuthorization.requireAdmin();
        deleteScheme(ctx.queryParam("type"), ctx.queryParam("protocol"), ctx.queryParam("name"));
        ctx.status(200);
    }

    private static void deleteScheme(String type, String protocol, String name) throws Exception {
        ModuleType moduleType = parseType(type);
        requireText(protocol, "protocol");
        requireText(name, "name");
        ModuleRegistration registration = getRegistration(moduleType, protocol);
        requireExistingScheme(registration, name);
        LOGGER.info("Request to delete IO scheme {} for protocol {} with type {}.", name, protocol, type);
        Boolean deleted = MessageBus.fireCall(String.format("%s.%s.%s", IOLocalIds.IO_SCHEME_DELETE_TOPIC, 
            moduleType.name().toLowerCase(), protocol), name, MessageOptions.Builder.newInstance()
                .header(UserModel.AUTH_HEADER_USERNAME, UserContext.getUser().getLogin())
                .header(UserModel.AUTH_HEADER_FULLNAME, UserContext.getUser().getFirstname() + " " + UserContext.getUser().getLastname())
                .deliveryCall().build(), Boolean.class);
        if (deleted == null || !deleted) {
            throw new CoreException(RibbonErrorCodes.IO_SCHEME_NOT_FOUND, "Can't delete IO scheme " + name);
        }
    }
    
    @OpenApi(
        summary = "Assign export scheme to directory",
        operationId = "assignExportScheme",
        path = "/api/io/export/scheme/{protocol}/{name}/assign/{dir}",
        methods = HttpMethod.POST,
        tags = {"IO"},
        security = {
            @OpenApiSecurity(name = "ribbonToken")
        },
        pathParams = {
            @OpenApiParam(name = "protocol", required = true),
            @OpenApiParam(name = "name", required = true),
            @OpenApiParam(name = "dir", required = true)
        },
        responses = {
            @OpenApiResponse(status = "200", content = {@OpenApiContent(from = Void.class)}),
            @OpenApiResponse(status = "401", content = {@OpenApiContent(from = CoreError.class)})
        }
    )
    public static void assignExportScheme(Context ctx) throws Exception {
        AdminAuthorization.requireAdmin();
        String dir = ctx.pathParam("dir");
        String protocol = ctx.pathParam("protocol");
        String name = ctx.pathParam("name");
        assignExportScheme(protocol, name, dir, true);
        ctx.status(200);
    }

    @OpenApi(
        summary = "Assign export scheme using a JSON request",
        operationId = "assignExportSchemeRequest",
        path = "/api/io/export/assignment",
        methods = HttpMethod.POST,
        tags = {"IO"},
        security = {@OpenApiSecurity(name = "ribbonToken")},
        requestBody = @OpenApiRequestBody(required = true, content = {@OpenApiContent(from = IOExportAssignment.class)}),
        responses = {
            @OpenApiResponse(status = "200", content = {@OpenApiContent(from = Void.class)}),
            @OpenApiResponse(status = "401", content = {@OpenApiContent(from = CoreError.class)}),
            @OpenApiResponse(status = "404", content = {@OpenApiContent(from = CoreError.class)})
        }
    )
    public static void assignExportSchemeRequest(Context ctx) throws Exception {
        AdminAuthorization.requireAdmin();
        IOExportAssignment assignment = GatewayMain.gson.fromJson(ctx.body(), IOExportAssignment.class);
        if (assignment == null) {
            throw new CoreException(RibbonErrorCodes.INVALID_REQUEST, "Assignment request is required.");
        }
        assignExportScheme(assignment.getProtocol(), assignment.getScheme(), assignment.getDirectory(), true);
        ctx.status(200);
    }

    @OpenApi(
        summary = "Dismiss export scheme using query parameters",
        operationId = "dismissExportSchemeQuery",
        path = "/api/io/export/assignment",
        methods = HttpMethod.DELETE,
        tags = {"IO"},
        security = {@OpenApiSecurity(name = "ribbonToken")},
        queryParams = {
            @OpenApiParam(name = "protocol", required = true, type = String.class),
            @OpenApiParam(name = "scheme", required = true, type = String.class),
            @OpenApiParam(name = "directory", required = true, type = String.class)
        },
        responses = {
            @OpenApiResponse(status = "200", content = {@OpenApiContent(from = Void.class)}),
            @OpenApiResponse(status = "401", content = {@OpenApiContent(from = CoreError.class)}),
            @OpenApiResponse(status = "404", content = {@OpenApiContent(from = CoreError.class)})
        }
    )
    public static void dismissExportSchemeQuery(Context ctx) throws Exception {
        AdminAuthorization.requireAdmin();
        assignExportScheme(ctx.queryParam("protocol"), ctx.queryParam("scheme"), ctx.queryParam("directory"), false);
        ctx.status(200);
    }

    private static void assignExportScheme(String protocol, String name, String directory, boolean assign) throws Exception {
        requireText(protocol, "protocol");
        requireText(name, "scheme");
        requireText(directory, "directory");
        ModuleRegistration registration = getRegistration(ModuleType.EXPORT, protocol);
        requireExistingScheme(registration, name);
        LOGGER.info("Request to {} directory {} on export scheme {} by protocol {}",
                assign ? "assign" : "dismiss", directory, name, protocol);
        String topic = assign ? IOLocalIds.IO_SCHEME_EXPORT_ASSIGN_TOPIC : IOLocalIds.IO_SCHEME_EXPORT_DISMISS_TOPIC;
        Boolean result = MessageBus.fireCall(String.format("%s.%s", topic, protocol), directory, MessageOptions.Builder.newInstance()
                .header(UserModel.AUTH_HEADER_USERNAME, UserContext.getUser().getLogin())
                .header(UserModel.AUTH_HEADER_FULLNAME, UserContext.getUser().getFirstname() + " " + UserContext.getUser().getLastname())
                .header(IOLocalIds.IO_SCHEME_NAME_HEADER, name)
                .deliveryCall().build(), Boolean.class);
        if (result == null || !result) {
            throw new CoreException(RibbonErrorCodes.IO_SCHEME_NOT_FOUND,
                    String.format("Unable to %s scheme '%s' for directory '%s'.", assign ? "assign" : "dismiss", name, directory));
        }
    }
    
    @OpenApi(
        summary = "Dismiss export scheme from directory",
        operationId = "dismissExportScheme",
        path = "/api/io/export/scheme/{protocol}/{name}/dismiss/{dir}",
        methods = HttpMethod.DELETE,
        tags = {"IO"},
        security = {
            @OpenApiSecurity(name = "ribbonToken")
        },
        pathParams = {
            @OpenApiParam(name = "protocol", required = true),
            @OpenApiParam(name = "name", required = true),
            @OpenApiParam(name = "dir", required = true)
        },
        responses = {
            @OpenApiResponse(status = "200", content = {@OpenApiContent(from = Void.class)}),
            @OpenApiResponse(status = "401", content = {@OpenApiContent(from = CoreError.class)})
        }
    )
    public static void dismissExportScheme(Context ctx) throws Exception {
        AdminAuthorization.requireAdmin();
        String dir = ctx.pathParam("dir");
        String protocol = ctx.pathParam("protocol");
        String name = ctx.pathParam("name");
        assignExportScheme(protocol, name, dir, false);
        ctx.status(200);
    }

    private static ModuleType parseType(String rawType) {
        requireText(rawType, "type");
        try {
            return ModuleType.valueOf(rawType.toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new CoreException(RibbonErrorCodes.INVALID_REQUEST, "Unknown IO scheme type: " + rawType);
        }
    }

    private static ModuleRegistration getRegistration(ModuleType type, String protocol) {
        ModuleRegistration registration = IOService.getInstance().getByTypeAndProtocol(type, protocol);
        if (registration == null) {
            throw new CoreException(RibbonErrorCodes.IO_SCHEME_NOT_FOUND,
                    "No IO module is registered for " + type + "/" + protocol);
        }
        return registration;
    }

    private static void requireExistingScheme(ModuleRegistration registration, String name) {
        if (registration.getSchemes() == null || !registration.getSchemes().containsKey(name)) {
            throw new CoreException(RibbonErrorCodes.IO_SCHEME_NOT_FOUND, "Can't find IO scheme " + name);
        }
    }

    private static String requireText(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new CoreException(RibbonErrorCodes.INVALID_REQUEST, "Missing required field: " + field);
        }
        return value;
    }
    
    /** Kept for existing callers; new code should use AdminAuthorization directly. */
    public static void isAdmin() {
        AdminAuthorization.requireAdmin();
    }
    
}
