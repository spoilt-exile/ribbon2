/*
 * This file is part of Ribbon2 news message system.
 * 
 * Copyright (C) 2023 Freax Software
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
package tk.freaxsoftware.ribbon2.uix.routes;

import io.javalin.Javalin;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tk.freaxsoftware.ribbon2.core.data.DirectoryModel;
import tk.freaxsoftware.ribbon2.core.data.MessageModel;
import tk.freaxsoftware.ribbon2.core.data.response.DefaultPage;
import tk.freaxsoftware.ribbon2.core.data.response.DirectoryPage;
import tk.freaxsoftware.ribbon2.uix.UserSessionModelContext;
import tk.freaxsoftware.ribbon2.uix.model.DirectoryNode;
import tk.freaxsoftware.ribbon2.uix.model.PageableUrlWrapper;
import tk.freaxsoftware.ribbon2.uix.rest.GatewayService;

/**
 * Routes for main UI;
 * @author Stanislav Nepochatov
 */
public class MainRoutes {
    
    private final static Logger LOGGER = LoggerFactory.getLogger(MainRoutes.class);
      
    public static void init(Javalin app, GatewayService gatewayService) {
        app.get("/", (ctx) -> {
            ctx.render("index.html", Map.of("user", UserSessionModelContext.getUser()));
        });
        
        app.get("/directories", (ctx) -> {
            final DirectoryPage page = gatewayService.getDirectoryRestClient().getDirectories(UserSessionModelContext.getUser().getJwtKey());
            ctx.render("directories.html", Map.of("directories", convertToTree(page)));
        });
        
        app.get("/messages/{dir}", (ctx) -> {
            final String dir = ctx.pathParam("dir");
            final int page = Integer.parseInt(ctx.queryParam("page"));
            final int pageSize = Integer.parseInt(ctx.queryParam("pageSize"));
            final DefaultPage<MessageModel> messagePage = gatewayService.getMessageRestClient().getMessages(UserSessionModelContext.getUser().getJwtKey(), dir, pageSize, page);
            PageableUrlWrapper<MessageModel> messages = new PageableUrlWrapper(messagePage, String.format("/%s/%s", "messages", dir), pageSize, pageSize);
            ctx.render("messages.html", Map.of("messages", messages));
        });
    }
    
    private static DirectoryNode convertToTree(DirectoryPage dirPage) {
        DirectoryNode root = new DirectoryNode();
        Map<String, DirectoryNode> pathMap = new HashMap<>();
        
        for (DirectoryModel current: dirPage.getContent()) {
            String parentName = current.parentName();
            LOGGER.info("Processing directory {} with parent {}", current.getFullName(), parentName);
            if (parentName.isEmpty()) {
                LOGGER.info("Adding {} directory to ROOT", current.getFullName());
                DirectoryNode insertToRootNode = new DirectoryNode(current);
                root.getDirectoryChildren().add(insertToRootNode);
                pathMap.put(current.getFullName(), insertToRootNode);
            } else {
                if (pathMap.containsKey(current.parentName())) {
                    DirectoryNode parent = pathMap.get(current.parentName());
                    LOGGER.info("Adding {} directory to {}", current.getFullName(), parent.getParentDirectory().getFullName());
                    DirectoryNode insertedInside = new DirectoryNode(current);
                    parent.getDirectoryChildren().add(insertedInside);
                    pathMap.put(current.getFullName(), insertedInside);
                } else {
                    LOGGER.warn("Discarding {} directory", current.getFullName());
                }
            }
        }
        
        return root;
    }
    
}
