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

import io.ebean.DB;
import io.javalin.http.Context;
import io.javalin.openapi.HttpMethod;
import io.javalin.openapi.OpenApi;
import io.javalin.openapi.OpenApiContent;
import io.javalin.openapi.OpenApiParam;
import io.javalin.openapi.OpenApiRequestBody;
import io.javalin.openapi.OpenApiResponse;
import io.javalin.openapi.OpenApiSecurity;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tk.freaxsoftware.extras.bus.MessageBus;
import tk.freaxsoftware.extras.bus.MessageOptions;
import tk.freaxsoftware.ribbon2.core.data.UserModel;
import tk.freaxsoftware.ribbon2.core.data.request.PaginationRequest;
import tk.freaxsoftware.ribbon2.core.data.response.DefaultConvertablePage;
import tk.freaxsoftware.ribbon2.core.exception.CoreException;
import tk.freaxsoftware.ribbon2.core.exception.RibbonErrorCodes;
import tk.freaxsoftware.ribbon2.core.utils.DBUtils;
import tk.freaxsoftware.ribbon2.gateway.GatewayMain;
import tk.freaxsoftware.ribbon2.core.data.UserWithPassword;
import tk.freaxsoftware.ribbon2.core.data.response.DefaultPage;
import tk.freaxsoftware.ribbon2.core.exception.CoreError;
import tk.freaxsoftware.ribbon2.gateway.entity.GroupEntity;
import tk.freaxsoftware.ribbon2.gateway.entity.UserEntity;
import tk.freaxsoftware.ribbon2.gateway.entity.converters.UserConverter;
import tk.freaxsoftware.ribbon2.gateway.entity.converters.UserWithPasswordConverter;
import static tk.freaxsoftware.ribbon2.gateway.io.routes.IORoutes.isAdmin;
import tk.freaxsoftware.ribbon2.gateway.utils.SHAHash;

/**
 * Routes for CRUD operations with users.
 * @author Stanislav Nepochatov
 */
public class UserRoutes {
    
    private final static Logger LOGGER = LoggerFactory.getLogger(UserRoutes.class);
    
    @OpenApi(
        summary = "Create user",
        operationId = "createUser",
        path = "/api/user",
        methods = HttpMethod.POST,
        tags = {"User"},
        security = {
            @OpenApiSecurity(name = "ribbonToken")
        },
        requestBody = @OpenApiRequestBody(required = true, content = {@OpenApiContent(from = UserModel.class)}),
        responses = {
            @OpenApiResponse(status = "200", content = {@OpenApiContent(from = UserModel.class)}),
            @OpenApiResponse(status = "401", content = {@OpenApiContent(from = CoreError.class)})
        }
    )
    public static void createUser(Context ctx) {
        isAdmin();
        UserWithPassword user = GatewayMain.gson.fromJson(ctx.body(), UserWithPassword.class);
        LOGGER.info("Request to create User");
        user.setId(null);
        UserEntity newUser = new UserWithPasswordConverter().convert(user);
        newUser.save();
        UserModel savedUser = new UserConverter().convert(newUser);
        MessageBus.fire(UserModel.NOTIFICATION_USER_CREATED, savedUser, 
                MessageOptions.Builder.newInstance().deliveryNotification(5).build());
        ctx.json(savedUser);
    }
    
    @OpenApi(
        summary = "Update user",
        operationId = "updateUser",
        path = "/api/user",
        methods = HttpMethod.PUT,
        tags = {"User"},
        security = {
            @OpenApiSecurity(name = "ribbonToken")
        },
        requestBody = @OpenApiRequestBody(required = true, content = {@OpenApiContent(from = UserModel.class)}),
        responses = {
            @OpenApiResponse(status = "200", content = {@OpenApiContent(from = UserModel.class)}),
            @OpenApiResponse(status = "401", content = {@OpenApiContent(from = CoreError.class)})
        }
    )
    public static void updateUser(Context ctx) {
        isAdmin();
        UserWithPassword user = GatewayMain.gson.fromJson(ctx.body(), UserWithPassword.class);
        UserEntity updateUser = DB.getDefault().find(UserEntity.class).where().idEq(user.getId()).findOne();
        LOGGER.info("Request to update User: {}", user.getId());
        if (updateUser != null) {
            updateUser.setLogin(user.getLogin());
            if (!StringUtils.isEmpty(user.getPassword())) {
                updateUser.setPassword(SHAHash.hashPassword(user.getPassword()));
            }
            updateUser.setFirstname(user.getFirstName());
            updateUser.setLastname(user.getLastName());
            updateUser.setEmail(user.getEmail());
            updateUser.setDescription(user.getDescription());
            updateUser.getGroups().clear();
            updateUser.getGroups().addAll(DB.getDefault().find(GroupEntity.class).where().in("name", user.getGroups()).findSet());
            updateUser.update();
            UserModel savedUser = new UserConverter().convert(updateUser);
            MessageBus.fire(UserModel.NOTIFICATION_USER_UPDATED, savedUser, 
                    MessageOptions.Builder.newInstance().deliveryNotification(5).build());
            ctx.json(savedUser);
        } else {
            throw new CoreException(RibbonErrorCodes.USER_NOT_FOUND, 
                    String.format("Unable to find User with id %d", user.getId()));
        }
    }
    
    @OpenApi(
        summary = "Get users paged",
        operationId = "getUserPage",
        path = "/api/user",
        methods = HttpMethod.GET,
        tags = {"User"},
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
            @OpenApiResponse(status = "200", content = {@OpenApiContent(from = DefaultPage.class)}),
            @OpenApiResponse(status = "401", content = {@OpenApiContent(from = CoreError.class)})
        }
    )
    public static void getUserPage(Context ctx) {
        isAdmin();
        PaginationRequest request = PaginationRequest.ofRequest(ctx.queryParamMap());
        LOGGER.info("Request to get all users {}", request);
        ctx.json(new DefaultConvertablePage(DBUtils.findPaginatedEntity(request, UserEntity.class), new UserConverter()));
    }
    
    @OpenApi(
        summary = "Get user by id",
        operationId = "getUser",
        path = "/api/user/{id}",
        methods = HttpMethod.GET,
        tags = {"User"},
        security = {
            @OpenApiSecurity(name = "ribbonToken")
        },
        pathParams = {
            @OpenApiParam(name = "id", required = true, type = Long.class)
        },
        responses = {
            @OpenApiResponse(status = "200", content = {@OpenApiContent(from = UserModel.class)}),
            @OpenApiResponse(status = "401", content = {@OpenApiContent(from = CoreError.class)})
        }
    )
    public static void getUser(Context ctx) {
        isAdmin();
        LOGGER.info("Request to get User: {}", ctx.pathParam("id"));
        UserEntity entity = DB.getDefault().find(UserEntity.class).where().idEq(Long.parseLong(ctx.pathParam("id"))).findOne();
        if (entity == null) {
            throw new CoreException(RibbonErrorCodes.USER_NOT_FOUND, 
                    String.format("Unable to find User with id %s", ctx.pathParam("id")));
        }
        ctx.json(new UserConverter().convert(entity));
    }
    
    @OpenApi(
        summary = "Delete user by id",
        operationId = "deleteUser",
        path = "/api/user/{id}",
        methods = HttpMethod.DELETE,
        tags = {"User"},
        security = {
            @OpenApiSecurity(name = "ribbonToken")
        },
        pathParams = {
            @OpenApiParam(name = "id", required = true, type = Long.class)
        },
        responses = {
            @OpenApiResponse(status = "200", content = {@OpenApiContent(from = Void.class)}),
            @OpenApiResponse(status = "401", content = {@OpenApiContent(from = CoreError.class)})
        }
    )
    public static void deleteUser(Context ctx) {
        isAdmin();
        UserEntity entity = DB.getDefault().find(UserEntity.class).where().idEq(Long.parseLong(ctx.pathParam("id"))).findOne();
        LOGGER.info("Request to delete User: {}", ctx.pathParam("id"));
        if (entity != null) {
            final UserModel deletedUser = new UserConverter().convert(entity);
            DB.getDefault().delete(entity);
            MessageBus.fire(UserModel.NOTIFICATION_USER_DELETED, deletedUser, 
                    MessageOptions.Builder.newInstance().deliveryNotification(5).build());
        } else {
            throw new CoreException(RibbonErrorCodes.USER_NOT_FOUND, 
                    String.format("Unable to find User with id %s", ctx.pathParam("id")));
        }
    }
}
