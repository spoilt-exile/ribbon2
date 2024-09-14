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
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import static java.util.Objects.nonNull;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tk.freaxsoftware.ribbon2.core.data.DirectoryModel;
import tk.freaxsoftware.ribbon2.core.data.MessageModel;
import tk.freaxsoftware.ribbon2.core.data.MessagePropertyModel;
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
            final EditModes mode = EditModes.valueOf(ctx.formParam("mode"));
            final String uid = ctx.formParam("uid");
            final String header = ctx.formParam("header");
            final Set<String> tags = Arrays.stream(ctx.formParam("tags").split(",")).map(String::trim).collect(Collectors.toSet());
            final Set<String> directories = ctx.formParams("directories").stream().collect(Collectors.toSet());
            final String content = ctx.formParam("content");
            final Boolean urgent = nonNull(ctx.formParam("urgent"));
            
            String copyright = null;
            if (nonNull(ctx.formParam("copyright_select"))) {
                final String copyrightSelect = ctx.formParam("copyright_select");
                switch (copyrightSelect) {
                    case "ASSIGN_ME":
                        copyright = UserSessionModelContext.getUser().getLogin();
                        break;
                    case "ASSIGN_OTHER": 
                        if (ctx.formParam("copyright_assign").isBlank()) {
                            throw new CoreException(RibbonErrorCodes.INVALID_REQUEST, "Author field is empty!");
                        }
                        copyright = ctx.formParam("copyright_assign");
                        break;
                    default:
                        break;
                }
            }
            
            ZonedDateTime embargo = null;
            if (ctx.formParam("embargo") != null && !ctx.formParam("embargo").isBlank()) {
                final LocalDateTime embargoLocalDateTime = LocalDateTime.parse(ctx.formParam("embargo"));
                final ZoneId serverZoneId = ZoneId.systemDefault();
                embargo = embargoLocalDateTime.atZone(serverZoneId);
            }
            
            final MessageModel message = new MessageModel();
            message.setHeader(header);
            message.setTags(tags);
            message.setDirectories(directories);
            message.setContent(content);
            message.setProperties(new HashSet());
            
            if (copyright != null) {
                addProperty(message, "COPYRIGHT", copyright);
            }
            
            if (urgent) {
                addProperty(message, "URGENT", null);
            }
            
            if (embargo != null) {
                addProperty(message, "EMBARGO", embargo.format(DateTimeFormatter.ISO_ZONED_DATE_TIME));
            }
            
            if (mode == EditModes.UPDATE) {
                message.setUid(uid);
                gatewayService.getMessageRestClient().updateMessage(UserSessionModelContext.getUser().getJwtKey(), message);
            } else {
                if (mode == EditModes.RERELEASE) {
                    message.setParentUid(uid);
                }
                gatewayService.getMessageRestClient().createMessage(UserSessionModelContext.getUser().getJwtKey(), message);
            }
            ctx.header("HX-Redirect", "/");
        });
    }
    
    private static void addProperty(MessageModel message, String type, String content) {
        MessagePropertyModel property = new MessagePropertyModel();
        property.setType(type);
        property.setContent(content);
        message.getProperties().add(property);
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
