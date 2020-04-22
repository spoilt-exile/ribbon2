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

import java.util.Map;
import java.util.Set;


/**
 * Message directory data class.
 * @author Stanislav Nepochatov
 */
public class DirectoryModel {
    
    public final static String CALL_CREATE_DIRECTORY = "Ribbon.Global.CreateDirectory";
    public final static String CALL_UPDATE_DIRECTORY = "Ribbon.Global.UpdateDirectory";
    public final static String CALL_DELETE_DIRECTORY = "Ribbon.Global.DeleteDirectory";
    
    public final static String NOTIFICATION_DIRECTORY_CREATED = "Ribbon.Global.Notification.DirectoryCreated";
    public final static String NOTIFICATION_DIRECTORY_UPDATED = "Ribbon.Global.Notification.DirectoryUpdated";
    public final static String NOTIFICATION_DIRECTORY_DELETED = "Ribbon.Global.Notification.DirectoryDeleted";
    
    /**
     * Id of directory.
     */
    private Long id;
    
    /**
     * Id of parent directory.
     */
    private Long parentId;
    
    /**
     * Name of directory.
     */
    private String name;
    
    /**
     * Full name of directory (path).
     */
    private String fullName;
    
    /**
     * Directory description.
     */
    private String description;
    
    /**
     * Access config of the directory.
     */
    private Set<AccessEntry> access;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    
    public static class AccessEntry {
        
        private String name;
        private Type type;
        private Map<String, Boolean> permissions;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Type getType() {
            return type;
        }

        public void setType(Type type) {
            this.type = type;
        }

        public Map<String, Boolean> getPermissions() {
            return permissions;
        }

        public void setPermissions(Map<String, Boolean> permissions) {
            this.permissions = permissions;
        }
        
        public static enum Type {
            ALL,
            USER,
            GROUP;
        }
    }
}
