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
package tk.freaxsoftware.ribbon2.message.config;

import java.util.Set;
import tk.freaxsoftware.ribbon2.core.config.DbConfig;
import tk.freaxsoftware.ribbon2.core.data.DirectoryPermissionModel;

/**
 * Config for message unit.
 * @author Stanislav Nepochatov
 */
public class MessengerUnitConfig {
    
    private MessengerConfig messenger;
    private DbConfig db;

    public MessengerConfig getMessenger() {
        return messenger;
    }

    public void setMessenger(MessengerConfig messenger) {
        this.messenger = messenger;
    }

    public DbConfig getDb() {
        return db;
    }

    public void setDb(DbConfig db) {
        this.db = db;
    }

    @Override
    public String toString() {
        return "{" + "messenger=" + messenger + ", db=" + db + '}';
    }
    
    public static class MessengerConfig {
        
        private Set<DirectoryPermissionModel> permissions;
        
        private Boolean enablePermissionCaching;
        
        private Integer permissionCacheExpiry;

        public Set<DirectoryPermissionModel> getPermissions() {
            return permissions;
        }

        public void setPermissions(Set<DirectoryPermissionModel> permissions) {
            this.permissions = permissions;
        }

        public Boolean getEnablePermissionCaching() {
            return enablePermissionCaching;
        }

        public void setEnablePermissionCaching(Boolean enablePermissionCaching) {
            this.enablePermissionCaching = enablePermissionCaching;
        }

        public Integer getPermissionCacheExpiry() {
            return permissionCacheExpiry;
        }

        public void setPermissionCacheExpiry(Integer permissionCacheExpiry) {
            this.permissionCacheExpiry = permissionCacheExpiry;
        }

        @Override
        public String toString() {
            return "{" + "permissions=" + permissions + ", enablePermissionCaching=" + enablePermissionCaching + ", permissionCacheExpiry=" + permissionCacheExpiry + '}';
        }
    }
}
