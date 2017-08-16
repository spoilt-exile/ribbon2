/*
 * This file is part of Ribbon2 news message system.
 * 
 * Copyright (C) 2017 Freax Software
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
package tk.freaxsoftware.ribbon2.gateway;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.bridge.PermittedOptions;
import io.vertx.ext.stomp.BridgeOptions;
import io.vertx.ext.stomp.StompServer;
import io.vertx.ext.stomp.StompServerHandler;
import spark.Spark;

/**
 * Main class for API gateway.
 *
 * @author Stanislav Nepochatov
 */
public class GatewayMain {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Spark.port(8080);
        
        Vertx vertx = Vertx.vertx();
        StompServer server = StompServer.create(vertx)
                .handler(StompServerHandler.create(vertx)
                        .bridge(new BridgeOptions()
                                .addInboundPermitted(new PermittedOptions())
                                .addOutboundPermitted(new PermittedOptions())
                        )
                )
                .listen();
        Spark.get("/", (req, res) -> {return "OK:200";});
        
        Spark.post("/call", (req,res) -> {
            vertx.eventBus().publish("/test", new JsonObject().put("message", "Hello from gateway!"));
            return "OK";
        });
    }

}
