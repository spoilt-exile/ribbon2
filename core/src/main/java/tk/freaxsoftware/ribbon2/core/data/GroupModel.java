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

/**
 * User group data class.
 * @author Stanislav Nepochatov
 */
public class GroupModel {
    
    public final static String NOTIFICATION_GROUP_CREATED = "Ribbon.Global.Notification.GroupCreated";
    public final static String NOTIFICATION_GROUP_UPDATED = "Ribbon.Global.Notification.GroupUpdated";
    public final static String NOTIFICATION_GROUP_DELETED = "Ribbon.Global.Notification.GroupDeleted";
    
    /**
     * Group id.
     */
    private Long id;
    
    /**
     * Name of the group.
     */
    private String name;
    
    /**
     * Group description.
     */
    private String description;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    
}
