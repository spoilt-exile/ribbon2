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
import spark.utils.StringUtils;
import tk.freaxsoftware.extras.bus.MessageBus;
import tk.freaxsoftware.extras.bus.MessageOptions;
import tk.freaxsoftware.ribbon2.core.data.User;
import tk.freaxsoftware.ribbon2.gateway.GatewayMain;
import tk.freaxsoftware.ribbon2.gateway.data.UserWithPassword;
import tk.freaxsoftware.ribbon2.gateway.entity.GroupEntity;
import tk.freaxsoftware.ribbon2.gateway.entity.UserEntity;
import tk.freaxsoftware.ribbon2.gateway.entity.converters.UserConverter;
import tk.freaxsoftware.ribbon2.gateway.entity.converters.UserWithPasswordConverter;
import tk.freaxsoftware.ribbon2.gateway.utils.SHAHash;

/**
 * Routes for CRUD operations with users.
 * @author Stanislav Nepochatov
 */
public class UserRoutes {
    
    private final static Logger LOGGER = LoggerFactory.getLogger(UserRoutes.class);
    
    public static void init() {
        post("/api/user", (req, res) -> {
            UserWithPassword user = GatewayMain.gson.fromJson(req.body(), UserWithPassword.class);
            LOGGER.info("Request to create User");
            user.setId(null);
            UserEntity newUser = new UserWithPasswordConverter().convert(user);
            newUser.save();
            User savedUser = new UserConverter().convert(newUser);
            MessageBus.fire(User.NOTIFICATION_USER_CREATED, savedUser, 
                    MessageOptions.Builder.newInstance().deliveryNotification(5).build());
            return savedUser;
        }, GatewayMain.gson::toJson);
        
        put("/api/user", (req, res) -> {
            UserWithPassword user = GatewayMain.gson.fromJson(req.body(), UserWithPassword.class);
            UserEntity updateUser = DB.getDefault().find(UserEntity.class).where().idEq(user.getId()).findOne();
            LOGGER.info("Request to update User: {}", user.getId());
            if (updateUser != null) {
                updateUser.setLogin(user.getLogin());
                if (!StringUtils.isEmpty(user.getPassword())) {
                    updateUser.setPassword(SHAHash.hashPassword(user.getPassword()));
                }
                updateUser.setFirstName(user.getFirstName());
                updateUser.setSecondName(user.getSecondName());
                updateUser.setEmail(user.getEmail());
                updateUser.setDescription(user.getDescription());
                updateUser.getGroups().clear();
                updateUser.getGroups().addAll(DB.getDefault().find(GroupEntity.class).where().in("name", user.getGroups()).findSet());
                updateUser.update();
                User savedUser = new UserConverter().convert(updateUser);
                MessageBus.fire(User.NOTIFICATION_USER_UPDATED, savedUser, 
                        MessageOptions.Builder.newInstance().deliveryNotification(5).build());
                return savedUser;
            } else {
                LOGGER.error("Unable to find User with id {}", user.getId());
                res.status(404);
                return null;
            }
        }, GatewayMain.gson::toJson);
        
        delete("/api/user/:id", (req, res) -> {
            UserEntity entity = DB.getDefault().find(UserEntity.class).where().idEq(Long.parseLong(req.params("id"))).findOne();
            LOGGER.info("Request to delete User: {}", req.params("id"));
            if (entity != null) {
                DB.getDefault().delete(entity);
                MessageBus.fire(User.NOTIFICATION_USER_DELETED, new UserConverter().convert(entity), 
                        MessageOptions.Builder.newInstance().deliveryNotification(5).build());
            } else {
                LOGGER.error("Unable to find User with id {}", req.params("id"));
                res.status(404);
            }
            return "";
        });
    }
    
}
