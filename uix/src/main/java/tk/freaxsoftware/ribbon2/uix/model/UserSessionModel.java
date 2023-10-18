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
package tk.freaxsoftware.ribbon2.uix.model;

import tk.freaxsoftware.ribbon2.core.data.UserModel;

/**
 * User session model.
 * @author Stanislav Nepochatov
 */
public class UserSessionModel extends UserModel {
    
    private String jwtKey;

    public String getJwtKey() {
        return jwtKey;
    }

    public void setJwtKey(String jwtKey) {
        this.jwtKey = jwtKey;
    }
    
    public static UserSessionModel ofUserModelAndJwtKey(UserModel user, String jwtKey) {
        UserSessionModel sessionModel = new UserSessionModel();
        sessionModel.setId(user.getId());
        sessionModel.setLogin(user.getLogin());
        sessionModel.setFirstName(user.getFirstName());
        sessionModel.setLastName(user.getLastName());
        sessionModel.setDescription(user.getDescription());
        sessionModel.setEmail(user.getEmail());
        sessionModel.setEnabled(user.getEnabled());
        sessionModel.setGroups(user.getGroups());
        sessionModel.setJwtKey(jwtKey);
        return sessionModel;
    }
    
}
