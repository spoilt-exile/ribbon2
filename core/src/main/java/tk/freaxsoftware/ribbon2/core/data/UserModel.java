/*
 * This file is part of Ribbon2 news message system.
 * 
 * Copyright (C) 2017 Freax Software
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

package tk.freaxsoftware.ribbon2.core.data;

import java.util.Set;

/**
 * User data class.
 * @author Stanislav Nepochatov
 */
public class UserModel {
    
    public final static String CALL_CHECK_AUTH = "Ribbon.Global.CheckAuth";
    
    public final static String NOTIFICATION_USER_CREATED = "Ribbon.Global.Notification.UserCreated";
    public final static String NOTIFICATION_USER_UPDATED = "Ribbon.Global.Notification.UserUpdated";
    public final static String NOTIFICATION_USER_DELETED = "Ribbon.Global.Notification.UserDeleted";
    
    public final static String AUTH_HEADER_USERNAME = "Auth.Username";
    public final static String AUTH_HEADER_FULLNAME = "Auth.Fullname";
    
    /**
     * Id of the user.
     */
    private Long id;
    
    /**
     * User's login.
     */
    private String login;
    
    /**
     * User's firstname.
     */
    private String firstName;
    
    /**
     * User's lastname.
     */
    private String lastName;
    
    /**
     * User's description for useful info like phones etc.
     */
    private String description;
    
    /**
     * User email.
     */
    private String email;
    
    /**
     * Enabled flag for user.
     */
    private Boolean enabled;
    
    /**
     * Groups of this user.
     */
    private Set<String> groups;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Set<String> getGroups() {
        return groups;
    }

    public void setGroups(Set<String> groups) {
        this.groups = groups;
    }
    
    
}
