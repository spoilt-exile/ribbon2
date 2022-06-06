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
import io.javalin.Javalin;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tk.freaxsoftware.extras.bus.annotation.AnnotationUtil;
import tk.freaxsoftware.extras.bus.bridge.http.util.GsonMapper;
import tk.freaxsoftware.extras.bus.bridge.http.util.GsonUtils;
import tk.freaxsoftware.ribbon2.core.config.PropertyConfigProcessor;
import tk.freaxsoftware.ribbon2.core.exception.CoreError;
import tk.freaxsoftware.ribbon2.core.exception.CoreException;
import tk.freaxsoftware.ribbon2.core.exception.RibbonErrorCodes;
import tk.freaxsoftware.ribbon2.gateway.config.ApplicationConfig;
import tk.freaxsoftware.ribbon2.gateway.routes.DirectoryRoutes;
import tk.freaxsoftware.ribbon2.gateway.routes.GroupRoutes;
import tk.freaxsoftware.ribbon2.gateway.routes.MessageRoutes;
import tk.freaxsoftware.ribbon2.gateway.routes.UserRoutes;
import tk.freaxsoftware.extras.bus.exceptions.NoSubscriptionMessageException;
import tk.freaxsoftware.ribbon2.gateway.io.IOService;
import tk.freaxsoftware.ribbon2.gateway.io.routes.IORoutes;
import tk.freaxsoftware.ribbon2.gateway.routes.AuthRoutes;

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
     * Available thread pool for various needs;
     */
    public static ExecutorService executor = Executors.newFixedThreadPool(4);

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        LOGGER.info("\n{}", IOUtils.toString(GatewayMain.class.getClassLoader().getResourceAsStream("header"), Charset.defaultCharset()));
        config = gson.fromJson(IOUtils.toString(GatewayMain.class.getClassLoader().getResourceAsStream("appconfig.json"), Charset.defaultCharset()), ApplicationConfig.class);
        PropertyConfigProcessor.process(config.getDb());
        LOGGER.info("Gateway started, config: {}", config);
        Javalin app = Javalin.create(javalinConfig -> {
            javalinConfig.jsonMapper(new GsonMapper());
        }).start(config.getHttp().getPort());
        Init.init(config);
        AuthRoutes.init(app, config.getHttp());
        UserRoutes.init(app);
        GroupRoutes.init(app);
        DirectoryRoutes.init(app);
        MessageRoutes.init(app);
        
        IOService ioService = new IOService();
        AnnotationUtil.subscribeReceiverInstance(ioService);
        IORoutes.init(app, ioService);
        
        app.exception(CoreException.class, (ex, ctx) -> {
            LOGGER.error("Error occurred:", ex);
            ctx.status(ex.getCode().getHttpCode());
            ctx.json(new CoreError(ex));
        });
        
        app.exception(NoSubscriptionMessageException.class, (ex, ctx) -> {
            LOGGER.error("Call error occurred:", ex);
            CoreError error = new CoreError();
            error.setCode(RibbonErrorCodes.CALL_ERROR);
            error.setMessage(ex.getMessage());
            ctx.status(RibbonErrorCodes.CALL_ERROR.getHttpCode());
            ctx.json(error);
        });
    }
}
