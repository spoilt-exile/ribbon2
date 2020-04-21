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

import com.google.gson.Gson;
import java.io.IOException;
import java.time.ZonedDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Spark;
import spark.utils.IOUtils;
import tk.freaxsoftware.extras.bus.MessageBus;
import tk.freaxsoftware.extras.bus.MessageOptions;
import tk.freaxsoftware.extras.bus.ResponseHolder;
import tk.freaxsoftware.extras.bus.bridge.http.util.GsonUtils;
import tk.freaxsoftware.ribbon2.core.data.Message;
import tk.freaxsoftware.ribbon2.gateway.config.ApplicationConfig;
import tk.freaxsoftware.ribbon2.gateway.routes.GroupRoutes;
import tk.freaxsoftware.ribbon2.gateway.routes.UserRoutes;

/**
 * Main class for API gateway.
 *
 * @author Stanislav Nepochatov
 */
public class GatewayMain {
    
    private final static Logger LOGGER = LoggerFactory.getLogger(GatewayMain.class);
    
    /**
     * Gson instance.
     */
    public static final Gson gson = GsonUtils.getGson();
    
    /**
     * Current application config;
     */
    public static ApplicationConfig config;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        LOGGER.info("\n{}", IOUtils.toString(GatewayMain.class.getClassLoader().getResourceAsStream("header")));
        config = gson.fromJson(IOUtils.toString(GatewayMain.class.getClassLoader().getResourceAsStream("appconfig.json")), ApplicationConfig.class);
        LOGGER.info("System started, config: {}", config);
        Init.init(config);
        UserRoutes.init();
        GroupRoutes.init();
        
        Spark.get("/", (req, res) -> {return "OK:200";});
        Spark.post("/api/call", (req,res) -> {
            Message message = new Message();
            message.setId(Long.MIN_VALUE);
            message.setHeader("Test message to module!");
            message.setContent("Hello there!");
            message.setCreated(ZonedDateTime.now());
            ResponseHolder receivedHolder = new ResponseHolder();
            MessageBus.fire(Message.CALL_CREATE_MESSAGE, message, MessageOptions.Builder.newInstance().deliveryNotification(5).build());
            return "OK";
        }, gson::toJson);
    }
}