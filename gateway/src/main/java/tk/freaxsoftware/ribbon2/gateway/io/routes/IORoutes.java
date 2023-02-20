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
import java.util.Objects;
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
import tk.freaxsoftware.ribbon2.gateway.entity.GroupEntity;
import tk.freaxsoftware.ribbon2.gateway.io.data.IOModuleScheme;
import tk.freaxsoftware.ribbon2.gateway.io.data.IOProtocol;
import tk.freaxsoftware.ribbon2.gateway.utils.UserContext;
import tk.freaxsoftware.ribbon2.io.core.IOLocalIds;
import tk.freaxsoftware.ribbon2.io.core.IOScheme;
import tk.freaxsoftware.ribbon2.io.core.ModuleRegistration;

/**
 * IO API routes.
 * @author Stanislav Nepochatov
 */
public class IORoutes {
    
    private final static Logger LOGGER = LoggerFactory.getLogger(IORoutes.class);
    
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
        isAdmin();
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
        isAdmin();
        LOGGER.info("Request to get IO schemes.");
        List<IOModuleScheme> moduleSchemes = new ArrayList<>();
        IOService.getInstance().getRegistrations().forEach(reg -> {
            moduleSchemes.addAll(IOModuleScheme.ofModuleRegistration(reg));
        });
        ctx.json(moduleSchemes);
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
        isAdmin();
        String type = ctx.pathParam("type");
        String protocol = ctx.pathParam("protocol");
        String name = ctx.pathParam("name");
        LOGGER.info("Request to get IO scheme {} for protocol {} with type {} with config.", name, protocol, type);
        IOScheme scheme = MessageBus.fireCall(String.format("%s.%s.%s", IOLocalIds.IO_SCHEME_GET_TOPIC, 
            type.toLowerCase(), protocol), name, MessageOptions.Builder.newInstance()
                .header(StorageInterceptor.IGNORE_STORAGE_HEADER, "true")
                .header(UserModel.AUTH_HEADER_USERNAME, UserContext.getUser().getLogin())
                .header(UserModel.AUTH_HEADER_FULLNAME, UserContext.getUser().getFirstname() + " " + UserContext.getUser().getLastname())
                .deliveryCall().build(), IOScheme.class);
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
        isAdmin();
        IOScheme scheme = GatewayMain.gson.fromJson(ctx.body(), IOScheme.class);
        LOGGER.info("Request to save IO scheme {} for protocol {} with type {}.", scheme.getName(), scheme.getProtocol(), scheme.getType());
        IOScheme saved = MessageBus.fireCall(String.format("%s.%s.%s", IOLocalIds.IO_SCHEME_SAVE_TOPIC, 
            scheme.getType().name().toLowerCase(), scheme.getProtocol()), scheme, MessageOptions.Builder.newInstance()
                .header(UserModel.AUTH_HEADER_USERNAME, UserContext.getUser().getLogin())
                .header(UserModel.AUTH_HEADER_FULLNAME, UserContext.getUser().getFirstname() + " " + UserContext.getUser().getLastname())
                .deliveryCall().build(), IOScheme.class);
        ModuleRegistration reg = IOService.getInstance().getById(saved.getId());
        if (reg != null) {
            reg.getSchemes().add(saved.getName());
        }
        ctx.json(reg);
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
        isAdmin();
        String type = ctx.pathParam("type");
        String protocol = ctx.pathParam("protocol");
        String name = ctx.pathParam("name");
        LOGGER.info("Request to delete IO scheme {} for protocol {} with type {}.", name, protocol, type);
        Boolean deleted = MessageBus.fireCall(String.format("%s.%s.%s", IOLocalIds.IO_SCHEME_DELETE_TOPIC, 
            type.toLowerCase(), protocol), name, MessageOptions.Builder.newInstance()
                .header(UserModel.AUTH_HEADER_USERNAME, UserContext.getUser().getLogin())
                .header(UserModel.AUTH_HEADER_FULLNAME, UserContext.getUser().getFirstname() + " " + UserContext.getUser().getLastname())
                .deliveryCall().build(), Boolean.class);
        if (deleted != null && deleted) {
            ctx.status(200);
            ModuleRegistration reg = IOService.getInstance().getById(String.format("%s.%s", type, protocol));
            if (reg != null) {
                reg.getSchemes().remove(name);
            }
        } else {
            ctx.status(404);
        };
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
        isAdmin();
        String dir = ctx.pathParam("dir");
        String protocol = ctx.pathParam("protocol");
        String name = ctx.pathParam("name");
        LOGGER.info("Request to assign directory {} to export scheme {} by protocol {}", dir, name, protocol);
        Boolean assigned = MessageBus.fireCall(String.format("%s.%s", IOLocalIds.IO_SCHEME_EXPORT_ASSIGN_TOPIC, 
            protocol), dir, MessageOptions.Builder.newInstance()
                .header(UserModel.AUTH_HEADER_USERNAME, UserContext.getUser().getLogin())
                .header(UserModel.AUTH_HEADER_FULLNAME, UserContext.getUser().getFirstname() + " " + UserContext.getUser().getLastname())
                .header(IOLocalIds.IO_SCHEME_NAME_HEADER, name)
                .deliveryCall().build(), Boolean.class);
        if (assigned != null && assigned) {
            ctx.status(200);
        } else {
            ctx.status(400);
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
        isAdmin();
        String dir = ctx.pathParam("dir");
        String protocol = ctx.pathParam("protocol");
        String name = ctx.pathParam("name");
        LOGGER.info("Request to dismiss directory {} from export scheme {} by protocol {}", dir, name, protocol);
        Boolean assigned = MessageBus.fireCall(String.format("%s.%s", IOLocalIds.IO_SCHEME_EXPORT_DISMISS_TOPIC, 
            protocol), dir, MessageOptions.Builder.newInstance()
                .header(UserModel.AUTH_HEADER_USERNAME, UserContext.getUser().getLogin())
                .header(UserModel.AUTH_HEADER_FULLNAME, UserContext.getUser().getFirstname() + " " + UserContext.getUser().getLastname())
                .header(IOLocalIds.IO_SCHEME_NAME_HEADER, name)
                .deliveryCall().build(), Boolean.class);
        if (assigned != null && assigned) {
            ctx.status(200);
        } else {
            ctx.status(400);
        }
    }
    
    public static void isAdmin() {
        for (GroupEntity group: UserContext.getUser().getGroups()) {
            if (Objects.equals(group.getName(), GroupEntity.ADMIN_GROUP)) {
                return;
            }
        }
        throw new CoreException(RibbonErrorCodes.ACCESS_DENIED, "User not added to admin group!");
    }
    
}
