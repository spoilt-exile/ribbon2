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

import io.javalin.Javalin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import static tk.freaxsoftware.ribbon2.gateway.io.routes.IORoutes.isAdmin;
import tk.freaxsoftware.ribbon2.gateway.utils.UserContext;

/**
 * Routes for CRUD operations with messages.
 * @author Stanislav Nepochatov
 */
public class MessageRoutes {
    
    private final static Logger LOGGER = LoggerFactory.getLogger(MessageRoutes.class);
    
    public static void init(Javalin app) {
        app.post("/api/message", ctx -> {
            MessageModel model = GatewayMain.gson.fromJson(ctx.body(), MessageModel.class);
            LOGGER.info("Request to create message {}", model.getHeader());
            MessageModel saved = MessageBus.fireCall(MessageModel.CALL_CREATE_MESSAGE, model, MessageOptions.Builder.newInstance()
                    .header(UserModel.AUTH_HEADER_USERNAME, UserContext.getUser().getLogin())
                    .header(UserModel.AUTH_HEADER_FULLNAME, UserContext.getUser().getFirstname() + " " + UserContext.getUser().getLastname())
                    .deliveryCall().build(), MessageModel.class);
            ctx.json(saved);
        });
        
        app.put("/api/message", ctx -> {
            MessageModel model = GatewayMain.gson.fromJson(ctx.body(), MessageModel.class);
            LOGGER.info("Request to update message {}", model.getHeader());
            MessageModel saved = MessageBus.fireCall(MessageModel.CALL_UPDATE_MESSAGE, model, MessageOptions.Builder.newInstance()
                    .header(UserModel.AUTH_HEADER_USERNAME, UserContext.getUser().getLogin())
                    .header(UserModel.AUTH_HEADER_FULLNAME, UserContext.getUser().getFirstname() + " " + UserContext.getUser().getLastname())
                    .deliveryCall().build(), MessageModel.class);
            ctx.json(saved);
        });
        
        app.delete("/api/message/{uid}", ctx -> {
            LOGGER.info("Request to delete message {}", ctx.pathParam("uid"));
            Boolean deleted = MessageBus.fireCall(MessageModel.CALL_DELETE_MESSAGE, ctx.pathParam("uid"), MessageOptions.Builder.newInstance()
                    .header(UserModel.AUTH_HEADER_USERNAME, UserContext.getUser().getLogin())
                    .header(UserModel.AUTH_HEADER_FULLNAME, UserContext.getUser().getFirstname() + " " + UserContext.getUser().getLastname())
                    .deliveryCall().build(), Boolean.class);
            if (deleted != null && deleted) {
                ctx.status(200);
            } else {
                ctx.status(404);
            }
        });
        
        app.get("/api/message/{dir}", ctx -> {
            PaginationRequest request = PaginationRequest.ofRequest(ctx.queryParamMap());
            LOGGER.info("Request to get all messages {}", request);
            MessagePage page = MessageBus.fireCall(MessageModel.CALL_GET_MESSAGE_ALL, request, MessageOptions.Builder.newInstance()
                    .header(StorageInterceptor.IGNORE_STORAGE_HEADER, "true")
                    .header(UserModel.AUTH_HEADER_USERNAME, UserContext.getUser().getLogin())
                    .header(UserModel.AUTH_HEADER_FULLNAME, UserContext.getUser().getFirstname() + " " + UserContext.getUser().getLastname())
                    .header(MessageModel.HEADER_MESSAGE_DIR, ctx.pathParam("dir"))
                    .deliveryCall().build(), MessagePage.class);
            ctx.json(page);
        });
        
        app.get("/api/message/{uid}/dir/{dir}", ctx -> {
            LOGGER.info("Request to get message {} by dir {}", ctx.pathParam("uid"));
            MessageModel message = MessageBus.fireCall(MessageModel.CALL_GET_MESSAGE_BY_UID, null, MessageOptions.Builder.newInstance()
                    .header(StorageInterceptor.IGNORE_STORAGE_HEADER, "true")
                    .header(MessageModel.HEADER_MESSAGE_UID, ctx.pathParam("uid"))
                    .header(MessageModel.HEADER_MESSAGE_DIR, ctx.pathParam("dir"))
                    .header(UserModel.AUTH_HEADER_USERNAME, UserContext.getUser().getLogin())
                    .header(UserModel.AUTH_HEADER_FULLNAME, UserContext.getUser().getFirstname() + " " + UserContext.getUser().getLastname())
                    .deliveryCall().build(), MessageModel.class);
            ctx.json(message);
        });
        
        app.get("/api/message/property/all", ctx -> {
            LOGGER.info("Request to get all message property types");
            MessagePropertyTaggedHolder propertyHolder = MessageBus.fireCall(MessagePropertyTagged.CALL_GET_PROPERTIES, null, MessageOptions.Builder.newInstance()
                    .header(StorageInterceptor.IGNORE_STORAGE_HEADER, "true")
                    .header(UserModel.AUTH_HEADER_USERNAME, UserContext.getUser().getLogin())
                    .header(UserModel.AUTH_HEADER_FULLNAME, UserContext.getUser().getFirstname() + " " + UserContext.getUser().getLastname())
                    .deliveryCall().build(), MessagePropertyTaggedHolder.class);
            ctx.json(propertyHolder.getPropertyTypes());
        });
        
        app.post("/api/message/property/{uid}", ctx -> {
            isAdmin();
            MessagePropertyModel model = GatewayMain.gson.fromJson(ctx.body(), MessagePropertyModel.class);
            LOGGER.info("Request to add message property {} with content {}", model.getType(), model.getContent());
            MessagePropertyModel saved = MessageBus.fireCall(MessagePropertyModel.CALL_ADD_PROPERTY, model, MessageOptions.Builder.newInstance()
                    .header(MessageModel.HEADER_MESSAGE_UID, ctx.pathParam("uid"))
                    .header(UserModel.AUTH_HEADER_USERNAME, UserContext.getUser().getLogin())
                    .header(UserModel.AUTH_HEADER_FULLNAME, UserContext.getUser().getFirstname() + " " + UserContext.getUser().getLastname())
                    .deliveryCall().build(), MessagePropertyModel.class);
            ctx.json(saved);
        });
    }
}
