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
package tk.freaxsoftware.ribbon2.uix;

import com.google.gson.Gson;
import freemarker.template.Configuration;
import static freemarker.template.Configuration.VERSION_2_3_32;
import io.javalin.Javalin;
import io.javalin.http.staticfiles.Location;
import io.javalin.rendering.JavalinRenderer;
import io.javalin.rendering.template.JavalinFreemarker;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Map;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tk.freaxsoftware.extras.bus.bridge.http.util.GsonUtils;
import tk.freaxsoftware.ribbon2.core.config.EnvironmentOverrider;
import tk.freaxsoftware.ribbon2.uix.config.UixConfig;
import tk.freaxsoftware.ribbon2.uix.config.UixConfig.HttpConfig;
import tk.freaxsoftware.ribbon2.uix.rest.GatewayService;
import tk.freaxsoftware.ribbon2.uix.routes.LoginRoutes;
import tk.freaxsoftware.ribbon2.uix.routes.MainRoutes;

/**
 * UIX unit main class.
 * @author Stanislav Nepochatov
 */
public class Uix {
    
    private final static Logger LOGGER = LoggerFactory.getLogger(Uix.class);
    
    /**
     * Current application config;
     */
    public static UixConfig config;
    
    /**
     * Gson instance.
     */
    public static final Gson gson = GsonUtils.getGson();

    public static void main(String[] args) throws IOException {
        LOGGER.info("\n{}", IOUtils.toString(
                Uix.class.getClassLoader().getResourceAsStream("header"), 
                Charset.defaultCharset()));
        config = gson.fromJson(
                IOUtils.toString(
                        Uix.class.getClassLoader().getResourceAsStream("uixconfig.json"), 
                        Charset.defaultCharset()), 
                UixConfig.class);
        processConfig(config);
        LOGGER.info("UIX started, config: {}", config);
        
        GatewayService gatewayService = new GatewayService(config.getGatewayUrl());
        
        Javalin app = Javalin.create(javalinConfig -> {
            javalinConfig.staticFiles.add((statConfig) -> {
                statConfig.hostedPath = "/web";
                statConfig.directory = "web";
                statConfig.location = Location.EXTERNAL;
            });
        });
        
        Configuration freeMarkerConfiguration = new Configuration(VERSION_2_3_32);
        freeMarkerConfiguration.setDirectoryForTemplateLoading(new File("web"));
        
        JavalinRenderer.register(new JavalinFreemarker(freeMarkerConfiguration), ".html");
        
        LoginRoutes.init(app, gatewayService);
        MainRoutes.init(app, gatewayService);
        
        app.start(config.getHttp().getPort());
    }
    
    private static void processConfig(UixConfig config) {
        EnvironmentOverrider overrider = new EnvironmentOverrider();
        overrider.registerOverride(new EnvironmentOverrider.OverrideEntry<UixConfig>("GATEWAY_URL", 
                UixConfig.class, (conf, property) -> conf.setGatewayUrl(property)));
        overrider.registerOverride(new EnvironmentOverrider.OverrideEntry<HttpConfig>("HTTP_PORT", 
                UixConfig.HttpConfig.class, (conf, property) -> conf.setPort(Integer.parseInt(property))));
        overrider.registerOverride(new EnvironmentOverrider.OverrideEntry<HttpConfig>("HTTP_AUTH_COOKIE_NAME", 
                UixConfig.HttpConfig.class, (conf, property) -> conf.setAuthCookieName(property)));
        overrider.registerOverride(new EnvironmentOverrider.OverrideEntry<HttpConfig>("HTTP_AUTH_COOKIE_VALID_DAYS", 
                UixConfig.HttpConfig.class, (conf, property) -> conf.setAuthTokenValidDays(Integer.parseInt(property))));
        overrider.processConfig(config);
    }
}
