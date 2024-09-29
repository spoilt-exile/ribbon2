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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tk.freaxsoftware.extras.bus.MessageBus;
import tk.freaxsoftware.extras.bus.MessageOptions;
import tk.freaxsoftware.ribbon2.core.data.GroupModel;
import tk.freaxsoftware.ribbon2.core.data.request.PaginationRequest;
import tk.freaxsoftware.ribbon2.core.data.response.DefaultConvertablePage;
import tk.freaxsoftware.ribbon2.core.data.response.DefaultPage;
import tk.freaxsoftware.ribbon2.core.exception.CoreError;
import tk.freaxsoftware.ribbon2.core.exception.CoreException;
import tk.freaxsoftware.ribbon2.core.exception.RibbonErrorCodes;
import tk.freaxsoftware.ribbon2.core.utils.DBUtils;
import tk.freaxsoftware.ribbon2.gateway.GatewayMain;
import tk.freaxsoftware.ribbon2.gateway.entity.GroupEntity;
import tk.freaxsoftware.ribbon2.gateway.entity.converters.GroupConverter;
import static tk.freaxsoftware.ribbon2.gateway.io.routes.IORoutes.isAdmin;

/**
 * Routes for CRUD operations with user groups.
 * @author Stanislav Nepochatov
 */
public class GroupRoutes {
    
    private final static Logger LOGGER = LoggerFactory.getLogger(GroupRoutes.class);
    
    @OpenApi(
        summary = "Create group",
        operationId = "createGroup",
        path = "/api/group",
        methods = HttpMethod.POST,
        tags = {"Group"},
        security = {
            @OpenApiSecurity(name = "ribbonToken")
        },
        requestBody = @OpenApiRequestBody(required = true, content = {@OpenApiContent(from = GroupModel.class)}),
        responses = {
            @OpenApiResponse(status = "200", content = {@OpenApiContent(from = GroupModel.class)}),
            @OpenApiResponse(status = "401", content = {@OpenApiContent(from = CoreError.class)})
        }
    )
    public static void createGroup(Context ctx) {
        isAdmin();
        GroupModel group = GatewayMain.gson.fromJson(ctx.body(), GroupModel.class);
        LOGGER.info("Request to create Group");
        group.setId(null);
        GroupEntity newGroup = new GroupConverter().convertBack(group);
        newGroup.save();
        GroupModel savedGroup = new GroupConverter().convert(newGroup);
        MessageBus.fire(GroupModel.NOTIFICATION_GROUP_CREATED, savedGroup, 
                MessageOptions.Builder.newInstance().deliveryNotification(5).build());
        ctx.json(savedGroup);
    }
    
    @OpenApi(
        summary = "Update group",
        operationId = "updateGroup",
        path = "/api/group",
        methods = HttpMethod.PUT,
        tags = {"Group"},
        security = {
            @OpenApiSecurity(name = "ribbonToken")
        },
        requestBody = @OpenApiRequestBody(required = true, content = {@OpenApiContent(from = GroupModel.class)}),
        responses = {
            @OpenApiResponse(status = "200", content = {@OpenApiContent(from = GroupModel.class)}),
            @OpenApiResponse(status = "401", content = {@OpenApiContent(from = CoreError.class)})
        }
    )
    public static void updateGroup(Context ctx) {
        isAdmin();
        GroupModel group = GatewayMain.gson.fromJson(ctx.body(), GroupModel.class);
        LOGGER.info("Request to update Group: {}", group.getId());
        GroupEntity updateGroup = new GroupConverter().convertBack(group);
        updateGroup.update();
        GroupModel savedGroup = new GroupConverter().convert(updateGroup);
        MessageBus.fire(GroupModel.NOTIFICATION_GROUP_UPDATED, savedGroup, 
                MessageOptions.Builder.newInstance().deliveryNotification(5).build());
        ctx.json(savedGroup);
    }
    
    @OpenApi(
        summary = "Get group paged",
        operationId = "getGroupPage",
        path = "/api/group",
        methods = HttpMethod.GET,
        tags = {"Group"},
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
    public static void getGroupPage(Context ctx) {
        isAdmin();
        PaginationRequest request = PaginationRequest.ofRequest(ctx.queryParamMap());
        LOGGER.info("Request to get all groups {}", request);
        ctx.json(new DefaultConvertablePage(DBUtils.findPaginatedEntity(request, GroupEntity.class), new GroupConverter()));
    }
    
    @OpenApi(
        summary = "Get group by id",
        operationId = "getGroup",
        path = "/api/group/{id}",
        methods = HttpMethod.GET,
        tags = {"Group"},
        security = {
            @OpenApiSecurity(name = "ribbonToken")
        },
        pathParams = {
            @OpenApiParam(name = "id", required = true, type = Long.class)
        },
        responses = {
            @OpenApiResponse(status = "200", content = {@OpenApiContent(from = GroupModel.class)}),
            @OpenApiResponse(status = "401", content = {@OpenApiContent(from = CoreError.class)})
        }
    )
    public static void getGroup(Context ctx) {
        isAdmin();
        LOGGER.info("Request to get Group: {}", ctx.pathParam("id"));
        GroupEntity entity = DB.getDefault().find(GroupEntity.class).where().idEq(Long.parseLong(ctx.pathParam("id"))).findOne();
        if (entity == null) {
            throw new CoreException(RibbonErrorCodes.GROUP_NOT_FOUND, 
                    String.format("Unable to find Group with id %s", ctx.pathParam("id")));
        }
        ctx.json(new GroupConverter().convert(entity));
    }
    
    @OpenApi(
        summary = "Delete group by id",
        operationId = "deleteGroup",
        path = "/api/group/{id}",
        methods = HttpMethod.DELETE,
        tags = {"Group"},
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
    public static void deleteGroup(Context ctx) {
        isAdmin();
        LOGGER.info("Request to delete Group: {}", ctx.pathParam("id"));
        GroupEntity entity = DB.getDefault().find(GroupEntity.class).where().idEq(Long.parseLong(ctx.pathParam("id"))).findOne();
        if (entity != null) {
            final GroupModel deletedGroup = new GroupConverter().convert(entity);
            DB.getDefault().delete(entity);
            MessageBus.fire(GroupModel.NOTIFICATION_GROUP_DELETED, deletedGroup, 
                    MessageOptions.Builder.newInstance().deliveryNotification(5).build());
        } else {
            throw new CoreException(RibbonErrorCodes.GROUP_NOT_FOUND, 
                    String.format("Unable to find Group with id %s", ctx.pathParam("id")));
        }
    }
}
