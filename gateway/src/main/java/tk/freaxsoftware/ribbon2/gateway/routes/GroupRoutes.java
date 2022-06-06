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
import io.javalin.Javalin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tk.freaxsoftware.extras.bus.MessageBus;
import tk.freaxsoftware.extras.bus.MessageOptions;
import tk.freaxsoftware.ribbon2.core.data.GroupModel;
import tk.freaxsoftware.ribbon2.core.data.request.PaginationRequest;
import tk.freaxsoftware.ribbon2.core.data.response.DefaultPage;
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
    
    public static void init(Javalin app) {
        app.post("/api/group", ctx -> {
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
        });
        
        app.put("/api/group", ctx -> {
            isAdmin();
            GroupModel group = GatewayMain.gson.fromJson(ctx.body(), GroupModel.class);
            LOGGER.info("Request to update Group: {}", group.getId());
            GroupEntity updateGroup = new GroupConverter().convertBack(group);
            updateGroup.update();
            GroupModel savedGroup = new GroupConverter().convert(updateGroup);
            MessageBus.fire(GroupModel.NOTIFICATION_GROUP_UPDATED, savedGroup, 
                    MessageOptions.Builder.newInstance().deliveryNotification(5).build());
            ctx.json(savedGroup);
        });
        
        app.delete("/api/group/{id}", ctx -> {
            isAdmin();
            LOGGER.info("Request to delete Group: {}", ctx.pathParam("id"));
            GroupEntity entity = DB.getDefault().find(GroupEntity.class).where().idEq(Long.parseLong(ctx.pathParam("id"))).findOne();
            if (entity != null) {
                DB.getDefault().delete(entity);
                MessageBus.fire(GroupModel.NOTIFICATION_GROUP_DELETED, new GroupConverter().convert(entity), 
                        MessageOptions.Builder.newInstance().deliveryNotification(5).build());
            } else {
                throw new CoreException(RibbonErrorCodes.GROUP_NOT_FOUND, 
                        String.format("Unable to find Group with id %s", ctx.pathParam("id")));
            }
        });
        
        app.get("/api/group", ctx -> {
            isAdmin();
            PaginationRequest request = PaginationRequest.ofRequest(ctx.queryParamMap());
            LOGGER.info("Request to get all groups {}", request);
            ctx.json(new DefaultPage(DBUtils.findPaginatedEntity(request, GroupEntity.class), new GroupConverter()));
        });
        
        app.get("/api/group/{id}", ctx -> {
            isAdmin();
            LOGGER.info("Request to get Group: {}", ctx.pathParam("id"));
            GroupEntity entity = DB.getDefault().find(GroupEntity.class).where().idEq(Long.parseLong(ctx.pathParam("id"))).findOne();
            if (entity == null) {
                throw new CoreException(RibbonErrorCodes.USER_NOT_FOUND, 
                        String.format("Unable to find Group with id %s", ctx.pathParam("id")));
            }
            ctx.json(new GroupConverter().convert(entity));
        });
    }
    
}
