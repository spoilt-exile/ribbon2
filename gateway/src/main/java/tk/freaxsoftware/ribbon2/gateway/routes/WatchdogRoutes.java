/*
 * This file is part of Ribbon2 news message system.
 * 
 * Copyright (C) 2020-2022 Freax Software
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
import io.javalin.openapi.OpenApiResponse;
import io.javalin.openapi.OpenApiSecurity;
import java.util.Optional;
import tk.freaxsoftware.ribbon2.gateway.io.routes.IORoutes;
import tk.freaxsoftware.ribbon2.gateway.watchdog.WatchdogService;
import tk.freaxsoftware.ribbon2.gateway.watchdog.data.WatchdogSystemStatus;
import tk.freaxsoftware.ribbon2.gateway.watchdog.data.WatchdogTopic;

/**
 * Routes for getting status of the system.
 * @author Stanislav Nepochatov
 */
public class WatchdogRoutes {
    
    private static WatchdogService watchdogService;
    
    public static void init(WatchdogService watchdogService) {
        WatchdogRoutes.watchdogService = watchdogService;
    }
    
    @OpenApi(
        summary = "Get watch status by topic",
        operationId = "getWatchByTopic",
        path = "/api/watchdog/statusByTopic/{topic}",
        methods = HttpMethod.GET,
        tags = {"Watchdog"},
        security = {
            @OpenApiSecurity(name = "ribbonToken")
        },
        pathParams = {
            @OpenApiParam(name = "topic", required = true, type = String.class)
        },
        responses = {
            @OpenApiResponse(status = "200", content = {@OpenApiContent(from = WatchdogTopic.class)}),
            @OpenApiResponse(status = "404")
        }
    )
    public static void getWatchByTopic(Context ctx) throws Exception {
        IORoutes.isAdmin();
        String topic = ctx.pathParam("topic");
        Optional<WatchdogTopic> topicOpt = watchdogService.getWatchByTopic(topic);
        if (topicOpt.isPresent()) {
            ctx.json(topicOpt.get());
        } else {
            ctx.status(404);
        }
    }
    
    @OpenApi(
        summary = "Get watch status of system",
        operationId = "getWatch",
        path = "/api/watchdog/status",
        methods = HttpMethod.GET,
        tags = {"Watchdog"},
        security = {
            @OpenApiSecurity(name = "ribbonToken")
        },
        responses = {
            @OpenApiResponse(status = "200", content = {@OpenApiContent(from = WatchdogSystemStatus.class)})
        }
    )
    public static void getWatch(Context ctx) throws Exception {
        IORoutes.isAdmin();
        ctx.json(watchdogService.getWatchdogStatus());
    }
}
