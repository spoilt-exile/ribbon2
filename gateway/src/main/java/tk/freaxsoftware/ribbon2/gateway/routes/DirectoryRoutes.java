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

import com.google.gson.reflect.TypeToken;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static spark.Spark.delete;
import static spark.Spark.post;
import static spark.Spark.put;
import static spark.Spark.get;
import tk.freaxsoftware.extras.bus.MessageBus;
import tk.freaxsoftware.extras.bus.MessageOptions;
import tk.freaxsoftware.extras.bus.storage.StorageInterceptor;
import tk.freaxsoftware.ribbon2.core.data.DirectoryAccessModel;
import tk.freaxsoftware.ribbon2.core.data.DirectoryModel;
import tk.freaxsoftware.ribbon2.core.data.DirectoryPermissionTaggedModel;
import tk.freaxsoftware.ribbon2.core.data.UserModel;
import tk.freaxsoftware.ribbon2.core.data.request.DirectoryEditAccessRequest;
import tk.freaxsoftware.ribbon2.core.data.request.PaginationRequest;
import tk.freaxsoftware.ribbon2.core.data.response.DirectoryPage;
import tk.freaxsoftware.ribbon2.core.data.response.DirectoryPermissionTaggedHolder;
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
            DirectoryModel saved = MessageBus.fireCall(DirectoryModel.CALL_CREATE_DIRECTORY, model, MessageOptions.Builder.newInstance()
                    .header(UserModel.AUTH_HEADER_USERNAME, UserContext.getUser().getLogin())
                    .header(UserModel.AUTH_HEADER_FULLNAME, UserContext.getUser().getFirstname() + " " + UserContext.getUser().getLastname())
                    .deliveryCall().build(), DirectoryModel.class);
            model.setId(saved.getId());
            model.setName(saved.getName());
            res.type("application/json");
            return model;
        }, GatewayMain.gson::toJson);
        
        put("/api/directory", (req, res) -> {
            DirectoryModel model = GatewayMain.gson.fromJson(req.body(), DirectoryModel.class);
            LOGGER.info("Request to update directory {}", model.getFullName());
            DirectoryModel saved = MessageBus.fireCall(DirectoryModel.CALL_UPDATE_DIRECTORY, model, MessageOptions.Builder.newInstance()
                    .header(UserModel.AUTH_HEADER_USERNAME, UserContext.getUser().getLogin())
                    .header(UserModel.AUTH_HEADER_FULLNAME, UserContext.getUser().getFirstname() + " " + UserContext.getUser().getLastname())
                    .deliveryCall().build(), DirectoryModel.class);
            model.setId(saved.getId());
            model.setName(saved.getName());
            model.setDescription(saved.getDescription());
            res.type("application/json");
            return model;
        }, GatewayMain.gson::toJson);
        
        delete("/api/directory/:path", (req, res) -> {
            LOGGER.info("Request to delete directory {}", req.params("path"));
            Boolean deleted = MessageBus.fireCall(DirectoryModel.CALL_DELETE_DIRECTORY, req.params("path"), MessageOptions.Builder.newInstance()
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
        
        post("/api/directory/access/:path", (req, res) -> {
            Set<DirectoryAccessModel> entries = GatewayMain.gson.fromJson(req.body(), new TypeToken<Set<DirectoryAccessModel>>(){}.getType());
            String path = req.params("path");
            DirectoryEditAccessRequest request = new DirectoryEditAccessRequest(path, entries);
            LOGGER.info("Request to edit directory access {}", request.getDirectoryPath());
            Boolean saved = MessageBus.fireCall(DirectoryEditAccessRequest.CALL_EDIT_DIR_ACCESS, request, MessageOptions.Builder.newInstance()
                    .header(UserModel.AUTH_HEADER_USERNAME, UserContext.getUser().getLogin())
                    .header(UserModel.AUTH_HEADER_FULLNAME, UserContext.getUser().getFirstname() + " " + UserContext.getUser().getLastname())
                    .deliveryCall().build(), Boolean.class);
            return saved;
        }, GatewayMain.gson::toJson);
        
        get("/api/directory/access/permission", (req, res) -> {
            LOGGER.info("Request to get all access permissions {}");
            DirectoryPermissionTaggedHolder permissionHolder = MessageBus.fireCall(DirectoryPermissionTaggedModel.CALL_GET_PERMISSIONS, null, MessageOptions.Builder.newInstance()
                    .header(StorageInterceptor.IGNORE_STORAGE_HEADER, "true")
                    .header(UserModel.AUTH_HEADER_USERNAME, UserContext.getUser().getLogin())
                    .header(UserModel.AUTH_HEADER_FULLNAME, UserContext.getUser().getFirstname() + " " + UserContext.getUser().getLastname())
                    .deliveryCall().build(), DirectoryPermissionTaggedHolder.class);
            res.type("application/json");
            return permissionHolder.getPermissions();
        }, GatewayMain.gson::toJson);
        
        get("/api/directory", (req, res) -> {
            PaginationRequest request = PaginationRequest.ofRequest(req.queryMap());
            LOGGER.info("Request to get all directories {}", request);
            DirectoryPage page = MessageBus.fireCall(DirectoryModel.CALL_GET_DIRECTORY_ALL, request, MessageOptions.Builder.newInstance()
                    .header(StorageInterceptor.IGNORE_STORAGE_HEADER, "true")
                    .header(UserModel.AUTH_HEADER_USERNAME, UserContext.getUser().getLogin())
                    .header(UserModel.AUTH_HEADER_FULLNAME, UserContext.getUser().getFirstname() + " " + UserContext.getUser().getLastname())
                    .deliveryCall().build(), DirectoryPage.class);
            res.type("application/json");
            return page;
        }, GatewayMain.gson::toJson);
        
        get("/api/directory/:path", (req, res) -> {
            LOGGER.info("Request to get directory {}", req.params("path"));
            DirectoryModel directory = MessageBus.fireCall(DirectoryModel.CALL_GET_DIRECTORY_BY_PATH, req.params("path"), MessageOptions.Builder.newInstance()
                    .header(StorageInterceptor.IGNORE_STORAGE_HEADER, "true")
                    .header(UserModel.AUTH_HEADER_USERNAME, UserContext.getUser().getLogin())
                    .header(UserModel.AUTH_HEADER_FULLNAME, UserContext.getUser().getFirstname() + " " + UserContext.getUser().getLastname())
                    .deliveryCall().build(), DirectoryModel.class);
            res.type("application/json");
            return directory;
        }, GatewayMain.gson::toJson);
    }
    
}
