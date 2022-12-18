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
package tk.freaxsoftware.ribbon2.gateway.routes;

import com.google.gson.reflect.TypeToken;
import io.javalin.http.Context;
import io.javalin.openapi.HttpMethod;
import io.javalin.openapi.OpenApi;
import io.javalin.openapi.OpenApiContent;
import io.javalin.openapi.OpenApiParam;
import io.javalin.openapi.OpenApiRequestBody;
import io.javalin.openapi.OpenApiResponse;
import io.javalin.openapi.OpenApiSecurity;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tk.freaxsoftware.extras.bus.MessageBus;
import tk.freaxsoftware.extras.bus.MessageOptions;
import tk.freaxsoftware.extras.bus.storage.StorageInterceptor;
import tk.freaxsoftware.ribbon2.core.data.DirectoryAccessModel;
import tk.freaxsoftware.ribbon2.core.data.DirectoryModel;
import tk.freaxsoftware.ribbon2.core.data.DirectoryPermissionTaggedModel;
import tk.freaxsoftware.ribbon2.core.data.UserModel;
import tk.freaxsoftware.ribbon2.core.data.request.DirectoryEditAccessRequest;
import tk.freaxsoftware.ribbon2.core.data.request.PaginationRequest;
import tk.freaxsoftware.ribbon2.core.data.response.DirectoryPage;
import tk.freaxsoftware.ribbon2.core.data.response.DirectoryPermissionTaggedHolder;
import tk.freaxsoftware.ribbon2.core.exception.CoreError;
import tk.freaxsoftware.ribbon2.gateway.GatewayMain;
import tk.freaxsoftware.ribbon2.gateway.utils.UserContext;

/**
 * Routes for CRUD operations with directories.
 * @author Stanislav Nepochatov
 */
public class DirectoryRoutes {
    
    private final static Logger LOGGER = LoggerFactory.getLogger(DirectoryRoutes.class);
    
    @OpenApi(
        summary = "Create directory",
        operationId = "createDirectory",
        path = "/api/directory",
        methods = HttpMethod.POST,
        tags = {"Directory"},
        security = {
            @OpenApiSecurity(name = "ribbonToken")
        },
        requestBody = @OpenApiRequestBody(required = true, content = {@OpenApiContent(from = DirectoryModel.class)}),
        responses = {
            @OpenApiResponse(status = "200", content = {@OpenApiContent(from = DirectoryModel.class)}),
            @OpenApiResponse(status = "401", content = {@OpenApiContent(from = CoreError.class)})
        }
    )
    public static void createDirectory(Context ctx) throws Exception {
        DirectoryModel model = GatewayMain.gson.fromJson(ctx.body(), DirectoryModel.class);
        LOGGER.info("Request to create directory {}", model.getFullName());
        DirectoryModel saved = MessageBus.fireCall(DirectoryModel.CALL_CREATE_DIRECTORY, model, MessageOptions.Builder.newInstance()
                .header(UserModel.AUTH_HEADER_USERNAME, UserContext.getUser().getLogin())
                .header(UserModel.AUTH_HEADER_FULLNAME, UserContext.getUser().getFirstname() + " " + UserContext.getUser().getLastname())
                .deliveryCall().build(), DirectoryModel.class);
        model.setId(saved.getId());
        model.setName(saved.getName());
        ctx.json(model);
    }
    
    @OpenApi(
        summary = "Update directory",
        operationId = "updateDirectory",
        path = "/api/directory",
        methods = HttpMethod.PUT,
        tags = {"Directory"},
        security = {
            @OpenApiSecurity(name = "ribbonToken")
        },
        requestBody = @OpenApiRequestBody(required = true, content = {@OpenApiContent(from = DirectoryModel.class)}),
        responses = {
            @OpenApiResponse(status = "200", content = {@OpenApiContent(from = DirectoryModel.class)}),
            @OpenApiResponse(status = "401", content = {@OpenApiContent(from = CoreError.class)})
        }
    )
    public static void updateDirectory(Context ctx) throws Exception {
        DirectoryModel model = GatewayMain.gson.fromJson(ctx.body(), DirectoryModel.class);
        LOGGER.info("Request to update directory {}", model.getFullName());
        DirectoryModel saved = MessageBus.fireCall(DirectoryModel.CALL_UPDATE_DIRECTORY, model, MessageOptions.Builder.newInstance()
                .header(UserModel.AUTH_HEADER_USERNAME, UserContext.getUser().getLogin())
                .header(UserModel.AUTH_HEADER_FULLNAME, UserContext.getUser().getFirstname() + " " + UserContext.getUser().getLastname())
                .deliveryCall().build(), DirectoryModel.class);
        model.setId(saved.getId());
        model.setName(saved.getName());
        model.setDescription(saved.getDescription());
        ctx.json(model);
    }
    
    @OpenApi(
        summary = "Get directories paged",
        operationId = "getDirectoryPage",
        path = "/api/directory",
        methods = HttpMethod.GET,
        tags = {"Directory"},
        security = {
            @OpenApiSecurity(name = "ribbonToken")
        },
        queryParams = {
            @OpenApiParam(name = PaginationRequest.PARAM_PAGE, type = Integer.class),
            @OpenApiParam(name = PaginationRequest.PARAM_SIZE, type = Integer.class),
            @OpenApiParam(name = PaginationRequest.PARAM_ORDER_BY, type = String.class),
            @OpenApiParam(name = PaginationRequest.PARAM_DIRECTION, type = PaginationRequest.Order.class),
        },
        responses = {
            @OpenApiResponse(status = "200", content = {@OpenApiContent(from = DirectoryPage.class)}),
            @OpenApiResponse(status = "401", content = {@OpenApiContent(from = CoreError.class)})
        }
    )
    public static void getDirectoryPage(Context ctx) throws Exception {
        PaginationRequest request = PaginationRequest.ofRequest(ctx.queryParamMap());
        LOGGER.info("Request to get all directories {}", request);
        DirectoryPage page = MessageBus.fireCall(DirectoryModel.CALL_GET_DIRECTORY_ALL, request, MessageOptions.Builder.newInstance()
                .header(StorageInterceptor.IGNORE_STORAGE_HEADER, "true")
                .header(UserModel.AUTH_HEADER_USERNAME, UserContext.getUser().getLogin())
                .header(UserModel.AUTH_HEADER_FULLNAME, UserContext.getUser().getFirstname() + " " + UserContext.getUser().getLastname())
                .deliveryCall().build(), DirectoryPage.class);
        ctx.json(page);
    }
    
    @OpenApi(
        summary = "Get directory by path",
        operationId = "getDirectory",
        path = "/api/directory/{path}",
        methods = HttpMethod.GET,
        tags = {"Directory"},
        security = {
            @OpenApiSecurity(name = "ribbonToken")
        },
        pathParams = {
            @OpenApiParam(name = "path", required = true, type = String.class)
        },
        responses = {
            @OpenApiResponse(status = "200", content = {@OpenApiContent(from = DirectoryModel.class)}),
            @OpenApiResponse(status = "401", content = {@OpenApiContent(from = CoreError.class)})
        }
    )
    public static void getDirectory(Context ctx) throws Exception {
        LOGGER.info("Request to get directory {}", ctx.pathParam("path"));
        DirectoryModel directory = MessageBus.fireCall(DirectoryModel.CALL_GET_DIRECTORY_BY_PATH, ctx.pathParam("path"), MessageOptions.Builder.newInstance()
                .header(StorageInterceptor.IGNORE_STORAGE_HEADER, "true")
                .header(UserModel.AUTH_HEADER_USERNAME, UserContext.getUser().getLogin())
                .header(UserModel.AUTH_HEADER_FULLNAME, UserContext.getUser().getFirstname() + " " + UserContext.getUser().getLastname())
                .deliveryCall().build(), DirectoryModel.class);
        ctx.json(directory);
    }
    
    @OpenApi(
        summary = "Delete directory by path",
        operationId = "deleteDirectory",
        path = "/api/directory/{path}",
        methods = HttpMethod.DELETE,
        tags = {"Directory"},
        security = {
            @OpenApiSecurity(name = "ribbonToken")
        },
        pathParams = {
            @OpenApiParam(name = "path", required = true, type = String.class)
        },
        responses = {
            @OpenApiResponse(status = "200", content = {@OpenApiContent(from = Void.class)}),
            @OpenApiResponse(status = "401", content = {@OpenApiContent(from = CoreError.class)})
        }
    )
    public static void deleteDirectory(Context ctx) throws Exception {
        LOGGER.info("Request to delete directory {}", ctx.pathParam("path"));
        Boolean deleted = MessageBus.fireCall(DirectoryModel.CALL_DELETE_DIRECTORY, ctx.pathParam("path"), MessageOptions.Builder.newInstance()
                .header(UserModel.AUTH_HEADER_USERNAME, UserContext.getUser().getLogin())
                .header(UserModel.AUTH_HEADER_FULLNAME, UserContext.getUser().getFirstname() + " " + UserContext.getUser().getLastname())
                .deliveryCall().build(), Boolean.class);
        if (deleted != null && deleted) {
            ctx.status(200);
        } else {
            ctx.status(404);
        }
    }
    
    @OpenApi(
        summary = "Edit directory access by path",
        operationId = "editDirectoryAccess",
        path = "/api/directory/access/{path}",
        methods = HttpMethod.POST,
        tags = {"Directory"},
        security = {
            @OpenApiSecurity(name = "ribbonToken")
        },
        pathParams = {
            @OpenApiParam(name = "path", required = true, type = String.class)
        },
        requestBody = @OpenApiRequestBody(required = true, content = {@OpenApiContent(from = DirectoryAccessModel[].class)}),
        responses = {
            @OpenApiResponse(status = "200", content = {@OpenApiContent(from = Boolean.class)}),
            @OpenApiResponse(status = "401", content = {@OpenApiContent(from = CoreError.class)})
        }
    )
    public static void editDirectoryAccess(Context ctx) throws Exception {
        Set<DirectoryAccessModel> entries = GatewayMain.gson.fromJson(ctx.body(), new TypeToken<Set<DirectoryAccessModel>>(){}.getType());
        String path = ctx.pathParam("path");
        DirectoryEditAccessRequest request = new DirectoryEditAccessRequest(path, entries);
        LOGGER.info("Request to edit directory access {}", request.getDirectoryPath());
        Boolean saved = MessageBus.fireCall(DirectoryEditAccessRequest.CALL_EDIT_DIR_ACCESS, request, MessageOptions.Builder.newInstance()
                .header(UserModel.AUTH_HEADER_USERNAME, UserContext.getUser().getLogin())
                .header(UserModel.AUTH_HEADER_FULLNAME, UserContext.getUser().getFirstname() + " " + UserContext.getUser().getLastname())
                .deliveryCall().build(), Boolean.class);
        ctx.json(saved);
    }
    
    @OpenApi(
        summary = "Get all directories permissions",
        operationId = "getAllDirectoriesPermissions",
        path = "/api/directory/access/permission/all",
        methods = HttpMethod.GET,
        tags = {"Directory"},
        security = {
            @OpenApiSecurity(name = "ribbonToken")
        },
        responses = {
            @OpenApiResponse(status = "200", content = {@OpenApiContent(from = DirectoryPermissionTaggedModel[].class)}),
            @OpenApiResponse(status = "401", content = {@OpenApiContent(from = CoreError.class)})
        }
    )
    public static void getAllDirectoriesPermissions(Context ctx) throws Exception {
        LOGGER.info("Request to get all access permissions {}");
        DirectoryPermissionTaggedHolder permissionHolder = MessageBus.fireCall(DirectoryPermissionTaggedModel.CALL_GET_PERMISSIONS, null, MessageOptions.Builder.newInstance()
                .header(StorageInterceptor.IGNORE_STORAGE_HEADER, "true")
                .header(UserModel.AUTH_HEADER_USERNAME, UserContext.getUser().getLogin())
                .header(UserModel.AUTH_HEADER_FULLNAME, UserContext.getUser().getFirstname() + " " + UserContext.getUser().getLastname())
                .deliveryCall().build(), DirectoryPermissionTaggedHolder.class);
        ctx.json(permissionHolder.getPermissions());
    }
    
    @OpenApi(
        summary = "Get directories by permissions",
        operationId = "getDirectoriesByPermission",
        path = "/api/directory/permission/{permission}",
        methods = HttpMethod.GET,
        tags = {"Directory"},
        security = {
            @OpenApiSecurity(name = "ribbonToken")
        },
        pathParams = {
            @OpenApiParam(name = "permission", required = true, type = String.class)
        },
        responses = {
            @OpenApiResponse(status = "200", content = {@OpenApiContent(from = DirectoryModel[].class)}),
            @OpenApiResponse(status = "401", content = {@OpenApiContent(from = CoreError.class)})
        }
    )
    public static void getDirectoriesByPermission(Context ctx) throws Exception {
        String permission = ctx.pathParam("permission");
        LOGGER.info("Request to get directories by permission {}", permission);
        List<DirectoryModel> page = MessageBus.fireCall(DirectoryModel.CALL_GET_DIRECTORY_BY_PERMISSION, permission, MessageOptions.Builder.newInstance()
                .header(StorageInterceptor.IGNORE_STORAGE_HEADER, "true")
                .header(UserModel.AUTH_HEADER_USERNAME, UserContext.getUser().getLogin())
                .header(UserModel.AUTH_HEADER_FULLNAME, UserContext.getUser().getFirstname() + " " + UserContext.getUser().getLastname())
                .deliveryCall().build(), List.class);
        ctx.json(page);
    }
    
    @OpenApi(
        summary = "Get current permissions by directory",
        operationId = "getCurrentPermissionsByDirectory",
        path = "/api/directory/access/permission/current/{path}",
        methods = HttpMethod.GET,
        tags = {"Directory"},
        security = {
            @OpenApiSecurity(name = "ribbonToken")
        },
        pathParams = {
            @OpenApiParam(name = "path", required = true, type = String.class)
        },
        responses = {
            @OpenApiResponse(status = "200", content = {@OpenApiContent(from = String[].class)}),
            @OpenApiResponse(status = "401", content = {@OpenApiContent(from = CoreError.class)})
        }
    )
    public static void getCurrentPermissionsByDirectory(Context ctx) throws Exception {
        String dirPath = ctx.pathParam("path");
        LOGGER.info("Request to get permission which can be used by current user on directory {}", dirPath);
        Set<String> permissions = MessageBus.fireCall(DirectoryPermissionTaggedModel.CALL_GET_CURRENT_PERMISSIONS, dirPath, MessageOptions.Builder.newInstance()
                .header(StorageInterceptor.IGNORE_STORAGE_HEADER, "true")
                .header(UserModel.AUTH_HEADER_USERNAME, UserContext.getUser().getLogin())
                .header(UserModel.AUTH_HEADER_FULLNAME, UserContext.getUser().getFirstname() + " " + UserContext.getUser().getLastname())
                .deliveryCall().build(), Set.class);
        ctx.json(permissions);
    }
}
