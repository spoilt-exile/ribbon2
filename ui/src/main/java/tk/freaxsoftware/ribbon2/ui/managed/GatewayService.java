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
    
    private final static String BASE_URL = "http://127.0.0.1:9000";
    
    private final AuthRestClient authRestClient = new AuthRestClient(BASE_URL);
    private final DirectoryRestClient directoryRestClient = new DirectoryRestClient(BASE_URL);
    private final MessageRestClient messageRestClient = new MessageRestClient(BASE_URL);
    private final UserRestClient userRestClient = new UserRestClient(BASE_URL);

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
