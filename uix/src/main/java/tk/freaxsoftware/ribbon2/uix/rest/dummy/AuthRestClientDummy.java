/*
 * This file is part of Ribbon2 news message system.
 * 
 * Copyright (C) 2020-2024 Freax Software
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
package tk.freaxsoftware.ribbon2.uix.rest.dummy;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Set;
import tk.freaxsoftware.ribbon2.core.data.UserModel;
import tk.freaxsoftware.ribbon2.uix.rest.AuthRestClient;

/**
 * Dummy auth REST client implementation.
 * @author Stanislav Nepochatov
 */
public class AuthRestClientDummy implements AuthRestClient {
    
    @Override
    public UserModel getAccount(String jwtKey) throws URISyntaxException, IOException {
        return createUser("root", Set.of("Admins"));
    }

    @Override
    public String auth(String login, String password) throws URISyntaxException, IOException {
        return "TOKEN";
    }
    
    private static UserModel createUser(String login, Set<String> groups) {
        UserModel user = new UserModel();
        user.setLogin(login);
        user.setEnabled(true);
        user.setGroups(groups);
        user.setId(1000L);
        user.setEmail(login + "@freaksoftware.tk");
        user.setFirstName("FName");
        user.setLastName("LName");
        return user;
    }
}
