/*
 * This file is part of Ribbon2 news message system.
 * 
 * Copyright (C) 2020-2024 Freax Software
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
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tk.freaxsoftware.ribbon2.core.data.DirectoryModel;
import tk.freaxsoftware.ribbon2.core.data.MessageModel;
import tk.freaxsoftware.ribbon2.core.exception.CoreException;
import tk.freaxsoftware.ribbon2.core.exception.RibbonErrorCodes;
import tk.freaxsoftware.ribbon2.uix.UserSessionModelContext;
import tk.freaxsoftware.ribbon2.uix.rest.GatewayService;

/**
 * Routes for message editor part.
 * @author Stanislav Nepochatov
 */
public class EditorRoutes {
    
    private final static Logger LOGGER = LoggerFactory.getLogger(EditorRoutes.class);
    
    public static void init(Javalin app, GatewayService gatewayService) {
        app.get("/editor/{mode}", (ctx) -> {
            LOGGER.info("UIX request {}", ctx.path());
            final EditModes mode = EditModes.valueOf(ctx.pathParam("mode"));
            MessageModel editMessage;
            if (mode == EditModes.CREATE) {
                editMessage = new MessageModel();
            } else {
                if (ctx.queryParam("uid") == null) {
                    throw new CoreException(RibbonErrorCodes.INVALID_REQUEST, "No uid specified.");
                }
                if (ctx.queryParam("dir") == null) {
                    throw new CoreException(RibbonErrorCodes.INVALID_REQUEST, "No dir specified.");
                }
                final String uid = ctx.queryParam("uid");
                final String dir = ctx.queryParam("dir");
                editMessage = gatewayService.getMessageRestClient().getMessageByUid(UserSessionModelContext.getUser().getJwtKey(), uid, dir);
            }
            final String dirPermission = mode == EditModes.UPDATE ? "canUpdateMessage" : "canCreateMessage";
            Set<DirectoryModel> directories = gatewayService.getDirectoryRestClient().getDirectoriesByPermission(UserSessionModelContext.getUser().getJwtKey(), dirPermission);
            ctx.render("editor/editor.html", Map.of("mode", mode, "message", editMessage, "directories", directories));
        });
        
        app.post("/editor/submit", (ctx) -> {
            LOGGER.info("UIX request {}", ctx.path());
            LOGGER.info("DIRS: {}", ctx.formParams("directories"));
            ctx.header("HX-Redirect", "/");
        });
    }
    
    /**
     * Enum with all modes for message editor. Mode should be applied as param during editor call.
     */
    public static enum EditModes {
        
        /**
         * Create new message. No uid required. All fields are empty.
         */
        CREATE,
        
        /**
         * Update existing message. Requires uid and directory. All fields filled by message content.
         */
        UPDATE,
        
        /**
         * Re-release existing message. Requires uid and directory. All fields filled by message content except uid.
         */
        RERELEASE;
    }
}
