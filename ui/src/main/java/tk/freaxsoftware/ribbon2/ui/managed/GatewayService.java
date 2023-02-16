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
package tk.freaxsoftware.ribbon2.ui.managed;

import javax.inject.Named;
import javax.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tk.freaxsoftware.ribbon2.ui.rest.AuthRestClient;
import tk.freaxsoftware.ribbon2.ui.rest.DirectoryRestClient;
import tk.freaxsoftware.ribbon2.ui.rest.MessageRestClient;
import tk.freaxsoftware.ribbon2.ui.rest.UserRestClient;

/**
 * Gateway REST service.
 * @author Stanislav Nepochatov
 */
@Named(value = "GatewayService")
@Singleton
public class GatewayService {
    
    private final static Logger LOGGER = LoggerFactory.getLogger(GatewayService.class);
    
    private final static String DEFAULT_BASE_URL = "http://127.0.0.1:9000";
    private final static String GATEWAY_URL_PARAM = "GATEWAY_URL";
    
    private final AuthRestClient authRestClient;
    private final DirectoryRestClient directoryRestClient;
    private final MessageRestClient messageRestClient;
    private final UserRestClient userRestClient;

    public GatewayService() {
        String baseUrl = System.getenv().containsKey(GATEWAY_URL_PARAM) 
                ? System.getenv(GATEWAY_URL_PARAM) 
                : DEFAULT_BASE_URL;
        LOGGER.info("Creating Ribbon2 Gateway clients to connect: {}", baseUrl);
        authRestClient = new AuthRestClient(baseUrl);
        directoryRestClient = new DirectoryRestClient(baseUrl);
        messageRestClient = new MessageRestClient(baseUrl);
        userRestClient = new UserRestClient(baseUrl);
    }

    public AuthRestClient getAuthRestClient() {
        return authRestClient;
    }

    public DirectoryRestClient getDirectoryRestClient() {
        return directoryRestClient;
    }

    public MessageRestClient getMessageRestClient() {
        return messageRestClient;
    }

    public UserRestClient getUserRestClient() {
        return userRestClient;
    }
}
