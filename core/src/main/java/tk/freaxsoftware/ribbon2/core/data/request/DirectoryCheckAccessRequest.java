/*
 * This file is part of Ribbon2 news message system.
 * 
 * Copyright (C) 2020 Freax Software
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
package tk.freaxsoftware.ribbon2.core.data.request;

import java.util.Set;

/**
 * Request data model for checking directory access.
 * @author Stanislav Nepochatov
 */
public class DirectoryCheckAccessRequest {
    
    public final static String CALL_CHECK_DIR_ACCESS = "Ribbon.Global.CheckDirectoryAccess";
    
    private String user;
    
    private String permission;
    
    private Set<String> directories;

    public DirectoryCheckAccessRequest() {}

    public DirectoryCheckAccessRequest(String user, String permission, Set<String> directories) {
        this.user = user;
        this.permission = permission;
        this.directories = directories;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    public Set<String> getDirectories() {
        return directories;
    }

    public void setDirectories(Set<String> directories) {
        this.directories = directories;
    }
    
}
