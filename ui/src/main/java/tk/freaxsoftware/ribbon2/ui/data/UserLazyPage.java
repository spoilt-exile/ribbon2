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
package tk.freaxsoftware.ribbon2.ui.data;

import java.util.List;
import java.util.Map;
import org.primefaces.model.FilterMeta;
import org.primefaces.model.SortMeta;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tk.freaxsoftware.ribbon2.core.data.UserModel;
import tk.freaxsoftware.ribbon2.core.data.response.DefaultPage;
import tk.freaxsoftware.ribbon2.ui.managed.GatewayService;

/**
 * User lazy loading page.
 * @author Stanislav Nepochatov
 */
public class UserLazyPage extends LazyLoadingPage<UserModel> {
    
    private final static Logger LOGGER = LoggerFactory.getLogger(UserLazyPage.class);

    public UserLazyPage(GatewayService gatewayService, String jwtKey) {
        super(gatewayService, jwtKey);
    }
    
    @Override
    public String getRowKey(UserModel object) {
        return object.getId().toString();
    }

    @Override
    public DefaultPage<UserModel> loadCurrentPage(int first, int pageSize, Map<String, SortMeta> sortBy, Map<String, FilterMeta> filterBy) {
//        try {
//            DefaultPage<UserModel> page = gatewayService.getUserRestClient().getUsers(jwtKey, pageSize, first / pageSize);
//            LOGGER.info("Loaded size {}, total {}", page.getContent().size(), page.getTotalCount());
//            return page;
//        } catch (Exception ex) {
//            LOGGER.error("Error on users loading", ex);
//            return null;
//        }

        return new DefaultPage(List.of(
                genUser(1L, "root", null, null, "admin account", "root@localhost", true),
                genUser(2L, "user", null, null, "test user", "user@localhost", true),
                genUser(14L, "john_doe", "John", "Doe", "dead user", "john.doe@dead.inc", false)
        ), 3);
    }
    
    private UserModel genUser(Long id, String login, String firstName, String lastName, String desc, String email, boolean enabled) {
        UserModel user = new UserModel();
        user.setId(id);
        user.setLogin(login);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setDescription(desc);
        user.setEmail(email);
        user.setEnabled(enabled);
        return user;
    }
}
