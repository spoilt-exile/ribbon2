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
import java.io.IOException;
import java.nio.charset.Charset;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tk.freaxsoftware.extras.bus.bridge.http.util.GsonUtils;
import tk.freaxsoftware.ribbon2.core.config.EnvironmentOverrider;
import tk.freaxsoftware.ribbon2.uix.config.UixConfig;
import tk.freaxsoftware.ribbon2.uix.rest.GatewayService;

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
    }
    
    private static void processConfig(UixConfig config) {
        EnvironmentOverrider overrider = new EnvironmentOverrider();
        overrider.registerOverride(new EnvironmentOverrider.OverrideEntry<UixConfig>("GATEWAY_URL", 
                UixConfig.class, (conf, property) -> conf.setGatewayUrl(property)));
        overrider.processConfig(config);
    }
}
