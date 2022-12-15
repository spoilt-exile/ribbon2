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
import liquibase.util.StringUtils;
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
    
    public static void init(Javalin app) {
        app.post("/api/user", ctx -> {
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
        });
        
        app.put("/api/user", ctx -> {
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
        });
        
        app.delete("/api/user/{id}", ctx -> {
            isAdmin();
            UserEntity entity = DB.getDefault().find(UserEntity.class).where().idEq(Long.parseLong(ctx.pathParam("id"))).findOne();
            LOGGER.info("Request to delete User: {}", ctx.pathParam("id"));
            if (entity != null) {
                DB.getDefault().delete(entity);
                MessageBus.fire(UserModel.NOTIFICATION_USER_DELETED, new UserConverter().convert(entity), 
                        MessageOptions.Builder.newInstance().deliveryNotification(5).build());
            } else {
                throw new CoreException(RibbonErrorCodes.USER_NOT_FOUND, 
                        String.format("Unable to find User with id %s", ctx.pathParam("id")));
            }
        });
        
        app.get("/api/user", ctx -> {
            isAdmin();
            PaginationRequest request = PaginationRequest.ofRequest(ctx.queryParamMap());
            LOGGER.info("Request to get all users {}", request);
            ctx.json(new DefaultConvertablePage(DBUtils.findPaginatedEntity(request, UserEntity.class), new UserConverter()));
        });
        
        app.get("/api/user/{id}", ctx -> {
            isAdmin();
            LOGGER.info("Request to get User: {}", ctx.pathParam("id"));
            UserEntity entity = DB.getDefault().find(UserEntity.class).where().idEq(Long.parseLong(ctx.pathParam("id"))).findOne();
            if (entity == null) {
                throw new CoreException(RibbonErrorCodes.USER_NOT_FOUND, 
                        String.format("Unable to find User with id %s", ctx.pathParam("id")));
            }
            ctx.json(new UserConverter().convert(entity));
        });
    }
    
}
