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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static spark.Spark.delete;
import static spark.Spark.post;
import static spark.Spark.put;
import tk.freaxsoftware.extras.bus.MessageBus;
import tk.freaxsoftware.extras.bus.MessageOptions;
import tk.freaxsoftware.ribbon2.core.data.Group;
import tk.freaxsoftware.ribbon2.gateway.GatewayMain;
import tk.freaxsoftware.ribbon2.gateway.entity.GroupEntity;
import tk.freaxsoftware.ribbon2.gateway.entity.converters.GroupConverter;

/**
 * Routes for CRUD operations with user groups.
 * @author Stanislav Nepochatov
 */
public class GroupRoutes {
    
    private final static Logger LOGGER = LoggerFactory.getLogger(GroupRoutes.class);
    
    public static void init() {
        post("/api/group", (req, res) -> {
            Group group = GatewayMain.gson.fromJson(req.body(), Group.class);
            LOGGER.info("Request to create Group");
            group.setId(null);
            GroupEntity newGroup = new GroupConverter().convertBack(group);
            newGroup.save();
            Group savedGroup = new GroupConverter().convert(newGroup);
            MessageBus.fire(Group.NOTIFICATION_GROUP_CREATED, savedGroup, 
                    MessageOptions.Builder.newInstance().deliveryNotification(5).build());
            return savedGroup;
        }, GatewayMain.gson::toJson);
        
        put("/api/group", (req, res) -> {
            Group group = GatewayMain.gson.fromJson(req.body(), Group.class);
            LOGGER.info("Request to create Group: {}", group.getId());
            GroupEntity updateGroup = new GroupConverter().convertBack(group);
            updateGroup.update();
            Group savedGroup = new GroupConverter().convert(updateGroup);
            MessageBus.fire(Group.NOTIFICATION_GROUP_UPDATED, savedGroup, 
                    MessageOptions.Builder.newInstance().deliveryNotification(5).build());
            return savedGroup;
        }, GatewayMain.gson::toJson);
        
        delete("/api/group/:id", (req, res) -> {
            LOGGER.info("Request to delete Group: {}", req.params("id"));
            GroupEntity entity = DB.getDefault().find(GroupEntity.class).where().idEq(Long.parseLong(req.params("id"))).findOne();
            if (entity != null) {
                DB.getDefault().delete(entity);
                MessageBus.fire(Group.NOTIFICATION_GROUP_DELETED, new GroupConverter().convert(entity), 
                        MessageOptions.Builder.newInstance().deliveryNotification(5).build());
            } else {
                LOGGER.error("Unable to find Group with id {}", req.params("id"));
                res.status(404);
            }
            return "";
        });
    }
    
}
