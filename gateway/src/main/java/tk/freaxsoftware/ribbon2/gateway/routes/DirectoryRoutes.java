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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static spark.Spark.post;
import tk.freaxsoftware.extras.bus.MessageBus;
import tk.freaxsoftware.extras.bus.MessageOptions;
import tk.freaxsoftware.ribbon2.core.data.DirectoryModel;
import tk.freaxsoftware.ribbon2.core.data.UserModel;
import tk.freaxsoftware.ribbon2.gateway.GatewayMain;
import tk.freaxsoftware.ribbon2.gateway.utils.UserContext;

/**
 * Routes for CRUD operations with directories.
 * @author Stanislav Nepochatov
 */
public class DirectoryRoutes {
    
    private final static Logger LOGGER = LoggerFactory.getLogger(DirectoryRoutes.class);
    
    public static void init() {
        post("/api/directory", (req, res) -> {
            DirectoryModel model = GatewayMain.gson.fromJson(req.body(), DirectoryModel.class);
            LOGGER.info("Request to create directory {}", model.getFullName());
            MessageBus.fire(DirectoryModel.CALL_CREATE_DIRECTORY, model, MessageOptions.Builder.newInstance()
                    .header(UserModel.AUTH_HEADER_USERNAME, UserContext.getUser().getLogin())
                    .header(UserModel.AUTH_HEADER_FULLNAME, UserContext.getUser().getFirstname() + " " + UserContext.getUser().getLastname())
                    .deliveryCall((resp) -> {
                LOGGER.info("Directory Create: callback");
                DirectoryModel saved = (DirectoryModel) resp.getContent();
                model.setId(saved.getId());
                model.setName(saved.getName());
                model.setParentId(saved.getParentId());
            }).build());
            return model;
        }, GatewayMain.gson::toJson);
    }
    
}
