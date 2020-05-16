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
import static spark.Spark.delete;
import static spark.Spark.post;
import static spark.Spark.put;
import tk.freaxsoftware.extras.bus.MessageBus;
import tk.freaxsoftware.extras.bus.MessageOptions;
import tk.freaxsoftware.ribbon2.core.data.MessageModel;
import tk.freaxsoftware.ribbon2.core.data.UserModel;
import tk.freaxsoftware.ribbon2.gateway.GatewayMain;
import tk.freaxsoftware.ribbon2.gateway.utils.UserContext;

/**
 * Routes for CRUD operations with messages.
 * @author Stanislav Nepochatov
 */
public class MessageRoutes {
    
    private final static Logger LOGGER = LoggerFactory.getLogger(MessageRoutes.class);
    
    public static void init() {
        post("/api/message", (req, res) -> {
            MessageModel model = GatewayMain.gson.fromJson(req.body(), MessageModel.class);
            LOGGER.info("Request to create message {}", model.getHeader());
            MessageModel saved = MessageBus.fireCall(MessageModel.CALL_CREATE_MESSAGE, model, MessageOptions.Builder.newInstance()
                    .header(UserModel.AUTH_HEADER_USERNAME, UserContext.getUser().getLogin())
                    .header(UserModel.AUTH_HEADER_FULLNAME, UserContext.getUser().getFirstname() + " " + UserContext.getUser().getLastname())
                    .deliveryCall().build(), MessageModel.class);
            return saved;
        }, GatewayMain.gson::toJson);
        
        put("/api/message", (req, res) -> {
            MessageModel model = GatewayMain.gson.fromJson(req.body(), MessageModel.class);
            LOGGER.info("Request to update message {}", model.getHeader());
            MessageModel saved = MessageBus.fireCall(MessageModel.CALL_UPDATE_MESSAGE, model, MessageOptions.Builder.newInstance()
                    .header(UserModel.AUTH_HEADER_USERNAME, UserContext.getUser().getLogin())
                    .header(UserModel.AUTH_HEADER_FULLNAME, UserContext.getUser().getFirstname() + " " + UserContext.getUser().getLastname())
                    .deliveryCall().build(), MessageModel.class);
            return saved;
        }, GatewayMain.gson::toJson);
        
        delete("/api/message/:uid", (req, res) -> {
            LOGGER.info("Request to delete message {}", req.params("uid"));
            Boolean deleted = MessageBus.fireCall(MessageModel.CALL_DELETE_MESSAGE, req.params("uid"), MessageOptions.Builder.newInstance()
                    .header(UserModel.AUTH_HEADER_USERNAME, UserContext.getUser().getLogin())
                    .header(UserModel.AUTH_HEADER_FULLNAME, UserContext.getUser().getFirstname() + " " + UserContext.getUser().getLastname())
                    .deliveryCall().build(), Boolean.class);
            if (deleted != null && deleted) {
                res.status(200);
            } else {
                res.status(404);
            }
            return "";
        });
    }
}
