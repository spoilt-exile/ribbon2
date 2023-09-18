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
package tk.freaxsoftware.ribbon2.uix.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Rest client service for communicating with gateway;
 * @author Stanislav Nepochatov
 */
public class GatewayService {
    
    private final static Logger LOGGER = LoggerFactory.getLogger(GatewayService.class);
    
    private final AuthRestClient authRestClient;
    private final DirectoryRestClient directoryRestClient;
    private final MessageRestClient messageRestClient;
    private final UserRestClient userRestClient;

    public GatewayService(String gatewayUrl) {
        LOGGER.info("Creating Ribbon2 Gateway clients to connect: {}", gatewayUrl);
        
        this.authRestClient = new AuthRestClient(gatewayUrl);
        this.directoryRestClient = new DirectoryRestClient(gatewayUrl);
        this.messageRestClient = new MessageRestClient(gatewayUrl);
        this.userRestClient = new UserRestClient(gatewayUrl);
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
