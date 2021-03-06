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
package tk.freaxsoftware.ribbon2.core.data;

/**
 * Directory permission metadata.
 * @author Stanislav Nepochatov
 */
public class DirectoryPermissionModel {
    
    public static final String CALL_INIT_PERMISSIONS = "Ribbon.Global.InitPermissions";
    
    /**
     * Id of the permission.
     */
    private String key;
    
    /**
     * Detail description of the permission.
     */
    private String description;
    
    /**
     * Default value for permission.
     */
    private Boolean defaultValue;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    
    public Boolean getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(Boolean defaultValue) {
        this.defaultValue = defaultValue;
    }

    @Override
    public String toString() {
        return "{" + "key=" + key + ", description=" + description + ", defaultValue=" + defaultValue + '}';
    }
}
