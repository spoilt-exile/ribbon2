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
import tk.freaxsoftware.extras.bus.storage.StorageInterceptor;
import tk.freaxsoftware.ribbon2.core.data.MessageModel;
import tk.freaxsoftware.ribbon2.core.data.MessagePropertyModel;
import tk.freaxsoftware.ribbon2.core.data.MessagePropertyTagged;
import tk.freaxsoftware.ribbon2.core.data.UserModel;
import tk.freaxsoftware.ribbon2.core.data.request.PaginationRequest;
import tk.freaxsoftware.ribbon2.core.data.response.MessagePage;
import tk.freaxsoftware.ribbon2.core.data.response.MessagePropertyTaggedHolder;
import tk.freaxsoftware.ribbon2.core.exception.CoreError;
import tk.freaxsoftware.ribbon2.gateway.GatewayMain;
import static tk.freaxsoftware.ribbon2.gateway.io.routes.IORoutes.isAdmin;
import tk.freaxsoftware.ribbon2.gateway.utils.UserContext;

/**
 * Routes for CRUD operations with messages.
 * @author Stanislav Nepochatov
 */
public class MessageRoutes {
    
    private final static Logger LOGGER = LoggerFactory.getLogger(MessageRoutes.class);
    
    @OpenApi(
        summary = "Create message",
        operationId = "createMessage",
        path = "/api/message",
        methods = HttpMethod.POST,
        tags = {"Message"},
        security = {
            @OpenApiSecurity(name = "ribbonToken")
        },
        requestBody = @OpenApiRequestBody(required = true, content = {@OpenApiContent(from = MessageModel.class)}),
        responses = {
            @OpenApiResponse(status = "200", content = {@OpenApiContent(from = MessageModel.class)}),
            @OpenApiResponse(status = "401", content = {@OpenApiContent(from = CoreError.class)})
        }
    )
    public static void createMesage(Context ctx) throws Exception {
        MessageModel model = GatewayMain.gson.fromJson(ctx.body(), MessageModel.class);
        LOGGER.info("Request to create message {}", model.getHeader());
        MessageModel saved = MessageBus.fireCall(MessageModel.CALL_CREATE_MESSAGE, model, MessageOptions.Builder.newInstance()
                .header(UserModel.AUTH_HEADER_USERNAME, UserContext.getUser().getLogin())
                .header(UserModel.AUTH_HEADER_FULLNAME, UserContext.getUser().getFirstname() + " " + UserContext.getUser().getLastname())
                .deliveryCall().build(), MessageModel.class);
        ctx.json(saved);
    }
    
    @OpenApi(
        summary = "Update message",
        operationId = "updateMessage",
        path = "/api/message",
        methods = HttpMethod.PUT,
        tags = {"Message"},
        security = {
            @OpenApiSecurity(name = "ribbonToken")
        },
        requestBody = @OpenApiRequestBody(required = true, content = {@OpenApiContent(from = MessageModel.class)}),
        responses = {
            @OpenApiResponse(status = "200", content = {@OpenApiContent(from = MessageModel.class)}),
            @OpenApiResponse(status = "401", content = {@OpenApiContent(from = CoreError.class)})
        }
    )
    public static void updateMessage(Context ctx) throws Exception {
        MessageModel model = GatewayMain.gson.fromJson(ctx.body(), MessageModel.class);
        LOGGER.info("Request to update message {}", model.getHeader());
        MessageModel saved = MessageBus.fireCall(MessageModel.CALL_UPDATE_MESSAGE, model, MessageOptions.Builder.newInstance()
                .header(UserModel.AUTH_HEADER_USERNAME, UserContext.getUser().getLogin())
                .header(UserModel.AUTH_HEADER_FULLNAME, UserContext.getUser().getFirstname() + " " + UserContext.getUser().getLastname())
                .deliveryCall().build(), MessageModel.class);
        ctx.json(saved);
    }
    
    @OpenApi(
        summary = "Delete message",
        operationId = "deleteMessage",
        path = "/api/message/{uid}",
        methods = HttpMethod.DELETE,
        tags = {"Message"},
        security = {
            @OpenApiSecurity(name = "ribbonToken")
        },
        pathParams = {
            @OpenApiParam(name = "uid", required = true, type = String.class)
        },
        responses = {
            @OpenApiResponse(status = "200", content = {@OpenApiContent(from = Void.class)}),
            @OpenApiResponse(status = "401", content = {@OpenApiContent(from = CoreError.class)})
        }
    )
    public static void deleteMessage(Context ctx) throws Exception {
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
    }
    
    @OpenApi(
        summary = "Get message page",
        operationId = "getMessagePage",
        path = "/api/message/{dir}",
        methods = HttpMethod.GET,
        tags = {"Message"},
        security = {
            @OpenApiSecurity(name = "ribbonToken")
        },
        pathParams = {
            @OpenApiParam(name = "dir", required = true, type = String.class)
        },
        queryParams = {
            @OpenApiParam(name = PaginationRequest.PARAM_PAGE, type = Integer.class),
            @OpenApiParam(name = PaginationRequest.PARAM_SIZE, type = Integer.class),
            @OpenApiParam(name = PaginationRequest.PARAM_ORDER_BY, type = String.class),
            @OpenApiParam(name = PaginationRequest.PARAM_DIRECTION, type = PaginationRequest.Order.class),
        },
        responses = {
            @OpenApiResponse(status = "200", content = {@OpenApiContent(from = MessagePage.class)}),
            @OpenApiResponse(status = "401", content = {@OpenApiContent(from = CoreError.class)})
        }
    )
    public static void getMessagePage(Context ctx) throws Exception {
        PaginationRequest request = PaginationRequest.ofRequest(ctx.queryParamMap());
        LOGGER.info("Request to get all messages {}", request);
        MessagePage page = MessageBus.fireCall(MessageModel.CALL_GET_MESSAGE_ALL, request, MessageOptions.Builder.newInstance()
                .header(StorageInterceptor.IGNORE_STORAGE_HEADER, "true")
                .header(UserModel.AUTH_HEADER_USERNAME, UserContext.getUser().getLogin())
                .header(UserModel.AUTH_HEADER_FULLNAME, UserContext.getUser().getFirstname() + " " + UserContext.getUser().getLastname())
                .header(MessageModel.HEADER_MESSAGE_DIR, ctx.pathParam("dir"))
                .deliveryCall().build(), MessagePage.class);
        ctx.json(page);
    }
    
    @OpenApi(
        summary = "Get message by dir and uid",
        operationId = "getMessage",
        path = "/api/message/{uid}/dir/{dir}",
        methods = HttpMethod.GET,
        tags = {"Message"},
        security = {
            @OpenApiSecurity(name = "ribbonToken")
        },
        pathParams = {
            @OpenApiParam(name = "uid", required = true, type = String.class),
            @OpenApiParam(name = "dir", required = true, type = String.class)
        },
        responses = {
            @OpenApiResponse(status = "200", content = {@OpenApiContent(from = MessageModel.class)}),
            @OpenApiResponse(status = "401", content = {@OpenApiContent(from = CoreError.class)})
        }
    )
    public static void getMessage(Context ctx) throws Exception {
        LOGGER.info("Request to get message {} by dir {}", ctx.pathParam("uid"), ctx.pathParam("dir"));
        MessageModel message = MessageBus.fireCall(MessageModel.CALL_GET_MESSAGE_BY_UID, null, MessageOptions.Builder.newInstance()
                .header(StorageInterceptor.IGNORE_STORAGE_HEADER, "true")
                .header(MessageModel.HEADER_MESSAGE_UID, ctx.pathParam("uid"))
                .header(MessageModel.HEADER_MESSAGE_DIR, ctx.pathParam("dir"))
                .header(UserModel.AUTH_HEADER_USERNAME, UserContext.getUser().getLogin())
                .header(UserModel.AUTH_HEADER_FULLNAME, UserContext.getUser().getFirstname() + " " + UserContext.getUser().getLastname())
                .deliveryCall().build(), MessageModel.class);
        ctx.json(message);
    }
    
    @OpenApi(
        summary = "Get all message properties",
        operationId = "getMessageProperties",
        path = "/api/message/property/all",
        methods = HttpMethod.GET,
        tags = {"Message"},
        security = {
            @OpenApiSecurity(name = "ribbonToken")
        },
        responses = {
            @OpenApiResponse(status = "200", content = {@OpenApiContent(from = MessagePropertyTagged[].class)}),
            @OpenApiResponse(status = "401", content = {@OpenApiContent(from = CoreError.class)})
        }
    )
    public static void getMessageProperties(Context ctx) throws Exception {
        LOGGER.info("Request to get all message property types");
        MessagePropertyTaggedHolder propertyHolder = MessageBus.fireCall(MessagePropertyTagged.CALL_GET_PROPERTIES, null, MessageOptions.Builder.newInstance()
                .header(StorageInterceptor.IGNORE_STORAGE_HEADER, "true")
                .header(UserModel.AUTH_HEADER_USERNAME, UserContext.getUser().getLogin())
                .header(UserModel.AUTH_HEADER_FULLNAME, UserContext.getUser().getFirstname() + " " + UserContext.getUser().getLastname())
                .deliveryCall().build(), MessagePropertyTaggedHolder.class);
        ctx.json(propertyHolder.getPropertyTypes());
    }
    
    @OpenApi(
        summary = "Add message property",
        operationId = "addMessageProperty",
        path = "/api/message/property/{uid}",
        methods = HttpMethod.POST,
        tags = {"Message"},
        security = {
            @OpenApiSecurity(name = "ribbonToken")
        },
        pathParams = {
            @OpenApiParam(name = "uid", required = true, type = String.class)
        },
        requestBody = @OpenApiRequestBody(required = true, content = {@OpenApiContent(from = MessagePropertyModel.class)}),
        responses = {
            @OpenApiResponse(status = "200", content = {@OpenApiContent(from = MessagePropertyModel.class)}),
            @OpenApiResponse(status = "401", content = {@OpenApiContent(from = CoreError.class)})
        }
    )
    public static void addMessageProperty(Context ctx) throws Exception {
        isAdmin();
        MessagePropertyModel model = GatewayMain.gson.fromJson(ctx.body(), MessagePropertyModel.class);
        LOGGER.info("Request to add message property {} with content {}", model.getType(), model.getContent());
        MessagePropertyModel saved = MessageBus.fireCall(MessagePropertyModel.CALL_ADD_PROPERTY, model, MessageOptions.Builder.newInstance()
                .header(MessageModel.HEADER_MESSAGE_UID, ctx.pathParam("uid"))
                .header(UserModel.AUTH_HEADER_USERNAME, UserContext.getUser().getLogin())
                .header(UserModel.AUTH_HEADER_FULLNAME, UserContext.getUser().getFirstname() + " " + UserContext.getUser().getLastname())
                .deliveryCall().build(), MessagePropertyModel.class);
        ctx.json(saved);
    }
}
