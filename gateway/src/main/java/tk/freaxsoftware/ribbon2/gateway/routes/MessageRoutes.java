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
import static spark.Spark.get;
import static spark.Spark.post;
import static spark.Spark.put;
import tk.freaxsoftware.extras.bus.MessageBus;
import tk.freaxsoftware.extras.bus.MessageOptions;
import tk.freaxsoftware.extras.bus.storage.StorageInterceptor;
import tk.freaxsoftware.ribbon2.core.data.MessageModel;
import tk.freaxsoftware.ribbon2.core.data.MessagePropertyModel;
import tk.freaxsoftware.ribbon2.core.data.MessagePropertyTagged;
import tk.freaxsoftware.ribbon2.core.data.UserModel;
import tk.freaxsoftware.ribbon2.core.data.request.PaginationRequest;
import tk.freaxsoftware.ribbon2.core.data.response.MessagePage;
import tk.freaxsoftware.ribbon2.core.data.response.MessagePropertyTaggedHolder;
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
            res.type("application/json");
            return saved;
        }, GatewayMain.gson::toJson);
        
        put("/api/message", (req, res) -> {
            MessageModel model = GatewayMain.gson.fromJson(req.body(), MessageModel.class);
            LOGGER.info("Request to update message {}", model.getHeader());
            MessageModel saved = MessageBus.fireCall(MessageModel.CALL_UPDATE_MESSAGE, model, MessageOptions.Builder.newInstance()
                    .header(UserModel.AUTH_HEADER_USERNAME, UserContext.getUser().getLogin())
                    .header(UserModel.AUTH_HEADER_FULLNAME, UserContext.getUser().getFirstname() + " " + UserContext.getUser().getLastname())
                    .deliveryCall().build(), MessageModel.class);
            res.type("application/json");
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
        
        get("/api/message/:dir", (req, res) -> {
            PaginationRequest request = PaginationRequest.ofRequest(req.queryMap());
            LOGGER.info("Request to get all messages {}", request);
            MessagePage page = MessageBus.fireCall(MessageModel.CALL_GET_MESSAGE_ALL, request, MessageOptions.Builder.newInstance()
                    .header(StorageInterceptor.IGNORE_STORAGE_HEADER, "true")
                    .header(UserModel.AUTH_HEADER_USERNAME, UserContext.getUser().getLogin())
                    .header(UserModel.AUTH_HEADER_FULLNAME, UserContext.getUser().getFirstname() + " " + UserContext.getUser().getLastname())
                    .header(MessageModel.HEADER_MESSAGE_DIR, req.params("dir"))
                    .deliveryCall().build(), MessagePage.class);
            res.type("application/json");
            return page;
        }, GatewayMain.gson::toJson);
        
        get("/api/message/:uid/dir/:dir", (req, res) -> {
            LOGGER.info("Request to get message {} by dir {}", req.params("uid"));
            MessageModel message = MessageBus.fireCall(MessageModel.CALL_GET_MESSAGE_BY_UID, null, MessageOptions.Builder.newInstance()
                    .header(StorageInterceptor.IGNORE_STORAGE_HEADER, "true")
                    .header(MessageModel.HEADER_MESSAGE_UID, req.params("uid"))
                    .header(MessageModel.HEADER_MESSAGE_DIR, req.params("dir"))
                    .header(UserModel.AUTH_HEADER_USERNAME, UserContext.getUser().getLogin())
                    .header(UserModel.AUTH_HEADER_FULLNAME, UserContext.getUser().getFirstname() + " " + UserContext.getUser().getLastname())
                    .deliveryCall().build(), MessageModel.class);
            res.type("application/json");
            return message;
        }, GatewayMain.gson::toJson);
        
        get("/api/message/property", (req, res) -> {
            LOGGER.info("Request to get all message property types {}");
            MessagePropertyTaggedHolder propertyHolder = MessageBus.fireCall(MessagePropertyTagged.CALL_GET_PROPERTIES, null, MessageOptions.Builder.newInstance()
                    .header(StorageInterceptor.IGNORE_STORAGE_HEADER, "true")
                    .header(UserModel.AUTH_HEADER_USERNAME, UserContext.getUser().getLogin())
                    .header(UserModel.AUTH_HEADER_FULLNAME, UserContext.getUser().getFirstname() + " " + UserContext.getUser().getLastname())
                    .deliveryCall().build(), MessagePropertyTaggedHolder.class);
            res.type("application/json");
            return propertyHolder.getPropertyTypes();
        }, GatewayMain.gson::toJson);
        
        post("/api/message/property/:uid", (req, res) -> {
            MessagePropertyModel model = GatewayMain.gson.fromJson(req.body(), MessagePropertyModel.class);
            LOGGER.info("Request to add message property {} with content {}", model.getType(), model.getContent());
            MessagePropertyModel saved = MessageBus.fireCall(MessagePropertyModel.CALL_ADD_PROPERTY, model, MessageOptions.Builder.newInstance()
                    .header(MessageModel.HEADER_MESSAGE_UID, req.params("uid"))
                    .header(UserModel.AUTH_HEADER_USERNAME, UserContext.getUser().getLogin())
                    .header(UserModel.AUTH_HEADER_FULLNAME, UserContext.getUser().getFirstname() + " " + UserContext.getUser().getLastname())
                    .deliveryCall().build(), MessagePropertyModel.class);
            res.type("application/json");
            return saved;
        }, GatewayMain.gson::toJson);
    }
}
