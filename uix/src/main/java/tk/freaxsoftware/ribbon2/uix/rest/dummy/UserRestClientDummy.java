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
import java.util.List;
import java.util.Set;
import tk.freaxsoftware.ribbon2.core.data.UserModel;
import tk.freaxsoftware.ribbon2.core.data.response.DefaultPage;
import tk.freaxsoftware.ribbon2.uix.rest.UserRestClient;

/**
 * Dummy user REST client implementation (admin only).
 * @author Stanislav Nepochatov
 */
public class UserRestClientDummy implements UserRestClient {
    
    @Override
    public DefaultPage<UserModel> getUsers(String jwtKey, int pageSize, int page) throws URISyntaxException, IOException {
        List<UserModel> users = List.of(
                createUser("root", Set.of("Admins")),
                createUser("user", Set.of("Users"))
        );
        DefaultPage<UserModel> userPage = new DefaultPage(users, users.size());
        return userPage;
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
