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
import tk.freaxsoftware.ribbon2.uix.config.UixConfig;
import tk.freaxsoftware.ribbon2.uix.rest.dummy.AuthRestClientDummy;
import tk.freaxsoftware.ribbon2.uix.rest.dummy.DirectoryRestClientDummy;
import tk.freaxsoftware.ribbon2.uix.rest.dummy.MessageRestClientDummy;
import tk.freaxsoftware.ribbon2.uix.rest.dummy.UserRestClientDummy;
import tk.freaxsoftware.ribbon2.uix.rest.impl.AuthRestClientImpl;
import tk.freaxsoftware.ribbon2.uix.rest.impl.DirectoryRestClientImpl;
import tk.freaxsoftware.ribbon2.uix.rest.impl.MessageRestClientImpl;
import tk.freaxsoftware.ribbon2.uix.rest.impl.UserRestClientImpl;

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

    public GatewayService(UixConfig config) {
        if (!config.getGatewayDummy()) {
            LOGGER.info("Creating Ribbon2 Gateway clients to connect: {}", config.getGatewayUrl());

            this.authRestClient = new AuthRestClientImpl(config.getGatewayUrl());
            this.directoryRestClient = new DirectoryRestClientImpl(config.getGatewayUrl());
            this.messageRestClient = new MessageRestClientImpl(config.getGatewayUrl());
            this.userRestClient = new UserRestClientImpl(config.getGatewayUrl());
        } else {
            LOGGER.info("Creating Ribbon2 Dummy Gateway");

            this.authRestClient = new AuthRestClientDummy();
            this.directoryRestClient = new DirectoryRestClientDummy();
            this.messageRestClient = new MessageRestClientDummy();
            this.userRestClient = new UserRestClientDummy();
        }
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
