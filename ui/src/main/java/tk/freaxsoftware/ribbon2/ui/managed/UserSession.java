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

import java.io.Serializable;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import tk.freaxsoftware.ribbon2.core.data.UserModel;

/**
 * User session bean. Contains data on current user in session.
 * @author Stanislav Nepochatov
 */
@Named(value = "UserSession")
@SessionScoped
public class UserSession implements Serializable {
    
    public static final String ADMIN_GROUP = "Admins";
    
    private String login;
    
    private String rawPassword;
    
    private String jwtKey;
    
    private Boolean isAdmin;
    
    private String firstname;
    
    private String lastname;

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getRawPassword() {
        return rawPassword;
    }

    public void setRawPassword(String rawPassword) {
        this.rawPassword = rawPassword;
    }

    public String getJwtKey() {
        return jwtKey;
    }

    public void setJwtKey(String jwtKey) {
        this.jwtKey = jwtKey;
    }

    public Boolean getIsAdmin() {
        return isAdmin;
    }

    public void setIsAdmin(Boolean isAdmin) {
        this.isAdmin = isAdmin;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }
    
    public void initSession(UserModel user) {
        isAdmin = user.getGroups().contains(ADMIN_GROUP);
        firstname = user.getFirstName();
        lastname = user.getLastName();
    }

    @Override
    public String toString() {
        return "{" + "login=" + login + ", rawPassword=" + rawPassword + ", jwtKey=" + jwtKey + ", isAdmin=" + isAdmin + ", firstname=" + firstname + ", lastname=" + lastname + '}';
    }
    
}
