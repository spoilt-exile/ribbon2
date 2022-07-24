/*
 * This file is part of Ribbon2 news message system.
 * 
 * Copyright (C) 2022 Freax Software
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
 * Directory permission model with tag.
 * @author Stanislav Nepochatov
 */
public class DirectoryPermissionTaggedModel extends DirectoryPermissionModel {
    
    public static final String CALL_GET_PERMISSIONS = "Ribbon.Global.GetPermissions";
    public static final String CALL_GET_CURRENT_PERMISSIONS = "Ribbon.Global.GetCurrentPermissions";
    
    private String tag;

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
    
    @Override
    public String toString() {
        return "{" + "key=" + getKey() + ", description=" + getDescription() + ", defaultValue=" + getDefaultValue() + ", tag=" + getTag() + '}';
    }
    
}
