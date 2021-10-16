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
package tk.freaxsoftware.ribbon2.gateway.io.routes;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static spark.Spark.delete;
import tk.freaxsoftware.ribbon2.gateway.io.IOService;
import static spark.Spark.get;
import static spark.Spark.post;
import tk.freaxsoftware.extras.bus.MessageBus;
import tk.freaxsoftware.extras.bus.MessageOptions;
import tk.freaxsoftware.extras.bus.storage.StorageInterceptor;
import tk.freaxsoftware.ribbon2.core.data.UserModel;
import tk.freaxsoftware.ribbon2.core.exception.CoreException;
import tk.freaxsoftware.ribbon2.core.exception.RibbonErrorCodes;
import tk.freaxsoftware.ribbon2.gateway.GatewayMain;
import tk.freaxsoftware.ribbon2.gateway.entity.GroupEntity;
import tk.freaxsoftware.ribbon2.gateway.io.data.IOModuleScheme;
import tk.freaxsoftware.ribbon2.gateway.io.data.IOProtocol;
import tk.freaxsoftware.ribbon2.gateway.utils.UserContext;
import tk.freaxsoftware.ribbon2.io.core.IOLocalIds;
import tk.freaxsoftware.ribbon2.io.core.IOScheme;
import tk.freaxsoftware.ribbon2.io.core.ModuleRegistration;

/**
 * IO API routes.
 * @author Stanislav Nepochatov
 */
public class IORoutes {
    
    private final static Logger LOGGER = LoggerFactory.getLogger(IORoutes.class);
    
    public static void init(IOService ioService) {
        get("/api/io/protocol", (req, res) -> {
            isAccessiableForIO();
            LOGGER.info("Request to get IO protocols.");
            res.type("application/json");
            return ioService.getRegistrations().stream()
                    .map(reg -> IOProtocol.ofModuleRegistration(reg))
                    .collect(Collectors.toList());
        }, GatewayMain.gson::toJson);
        
        get("/api/io/scheme", (req, res) -> {
            isAccessiableForIO();
            LOGGER.info("Request to get IO schemes.");
            List<IOModuleScheme> moduleSchemes = new ArrayList<>();
            ioService.getRegistrations().forEach(reg -> {
                moduleSchemes.addAll(IOModuleScheme.ofModuleRegistration(reg));
            });
            res.type("application/json");
            return moduleSchemes;
        }, GatewayMain.gson::toJson);
        
        get("/api/io/scheme/:type/:protocol/:name", (req, res) -> {
            isAccessiableForIO();
            String type = req.params("type");
            String protocol = req.params("protocol");
            String name = req.params("name");
            LOGGER.info("Request to get IO scheme {} for protocol {} with type {} with config.", name, protocol, type);
            IOScheme scheme = MessageBus.fireCall(String.format("%s.%s.%s", IOLocalIds.IO_SCHEME_GET_TOPIC, 
                type.toLowerCase(), protocol), name, MessageOptions.Builder.newInstance()
                    .header(StorageInterceptor.IGNORE_STORAGE_HEADER, "true")
                    .header(UserModel.AUTH_HEADER_USERNAME, UserContext.getUser().getLogin())
                    .header(UserModel.AUTH_HEADER_FULLNAME, UserContext.getUser().getFirstname() + " " + UserContext.getUser().getLastname())
                    .deliveryCall().build(), IOScheme.class);
            res.type("application/json");
            return scheme;
        }, GatewayMain.gson::toJson);
        
        post("/api/io/scheme/", (req, res) -> {
            isAccessiableForIO();
            IOScheme scheme = GatewayMain.gson.fromJson(req.body(), IOScheme.class);
            LOGGER.info("Request to save IO scheme {} for protocol {} with type {}.", scheme.getName(), scheme.getProtocol(), scheme.getType());
            IOScheme saved = MessageBus.fireCall(String.format("%s.%s.%s", IOLocalIds.IO_SCHEME_SAVE_TOPIC, 
                scheme.getType().name().toLowerCase(), scheme.getProtocol()), scheme, MessageOptions.Builder.newInstance()
                    .header(UserModel.AUTH_HEADER_USERNAME, UserContext.getUser().getLogin())
                    .header(UserModel.AUTH_HEADER_FULLNAME, UserContext.getUser().getFirstname() + " " + UserContext.getUser().getLastname())
                    .deliveryCall().build(), IOScheme.class);
            res.type("application/json");
            ModuleRegistration reg = ioService.getById(saved.getId());
            if (reg != null) {
                reg.getSchemes().add(saved.getName());
            }
            return saved;
        }, GatewayMain.gson::toJson);
        
        delete("/api/io/scheme/:type/:protocol/:name", (req, res) -> {
            isAccessiableForIO();
            String type = req.params("type");
            String protocol = req.params("protocol");
            String name = req.params("name");
            LOGGER.info("Request to delete IO scheme {} for protocol {} with type {}.", name, protocol, type);
            Boolean deleted = MessageBus.fireCall(String.format("%s.%s.%s", IOLocalIds.IO_SCHEME_DELETE_TOPIC, 
                type.toLowerCase(), protocol), name, MessageOptions.Builder.newInstance()
                    .header(UserModel.AUTH_HEADER_USERNAME, UserContext.getUser().getLogin())
                    .header(UserModel.AUTH_HEADER_FULLNAME, UserContext.getUser().getFirstname() + " " + UserContext.getUser().getLastname())
                    .deliveryCall().build(), Boolean.class);
            if (deleted != null && deleted) {
                res.status(200);
                ModuleRegistration reg = ioService.getById(String.format("%s.%s", type, protocol));
                if (reg != null) {
                    reg.getSchemes().remove(name);
                }
            } else {
                res.status(404);
            }
            return "";
        });
        
        post("/api/io/export/scheme/:protocol/:name/assign/:dir", (req, res) -> {
            isAccessiableForIO();
            String dir = req.params("dir");
            String protocol = req.params("protocol");
            String name = req.params("name");
            LOGGER.info("Request to assign directory {} to export scheme {} by protocol {}", dir, name, protocol);
            Boolean assigned = MessageBus.fireCall(String.format("%s.%s", IOLocalIds.IO_SCHEME_EXPORT_ASSIGN_TOPIC, 
                protocol), dir, MessageOptions.Builder.newInstance()
                    .header(UserModel.AUTH_HEADER_USERNAME, UserContext.getUser().getLogin())
                    .header(UserModel.AUTH_HEADER_FULLNAME, UserContext.getUser().getFirstname() + " " + UserContext.getUser().getLastname())
                    .header(IOLocalIds.IO_SCHEME_NAME_HEADER, name)
                    .deliveryCall().build(), Boolean.class);
            if (assigned != null && assigned) {
                res.status(200);
            } else {
                res.status(400);
            }
            return "";
        });
    }
    
    private static void isAccessiableForIO() {
        for (GroupEntity group: UserContext.getUser().getGroups()) {
            if (Objects.equals(group.getName(), GroupEntity.ADMIN_GROUP)) {
                return;
            }
        }
        throw new CoreException(RibbonErrorCodes.ACCESS_DENIED, "User not added to admin group!");
    }
    
}
