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

import java.io.IOException;
import java.net.URISyntaxException;
import tk.freaxsoftware.ribbon2.core.data.UserModel;

/**
 * Auth resource REST client.
 * @author Stanislav Nepochatov
 */
public interface AuthRestClient {
    
    /**
     * Perform authentication of user.
     * @param login username of user;
     * @param password raw password of user;
     * @return raw JWT token;
     * @throws java.net.URISyntaxException
     * @throws java.io.IOException
     */
    String auth(String login, String password) throws URISyntaxException, IOException;
    
    /**
     * Get current account info of user.
     * @param jwtKey raw JWT key;
     * @return user model;
     * @throws java.net.URISyntaxException
     * @throws java.io.IOException
     */
    UserModel getAccount(String jwtKey) throws URISyntaxException, IOException;
    
}
