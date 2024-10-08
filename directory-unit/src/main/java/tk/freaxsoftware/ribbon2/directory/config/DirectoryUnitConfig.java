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
package tk.freaxsoftware.ribbon2.directory.config;

import java.util.Set;
import tk.freaxsoftware.ribbon2.core.config.DbConfig;
import tk.freaxsoftware.ribbon2.core.data.DirectoryPermissionModel;

/**
 * Directory unit config.
 * @author Stanislav Nepochatov
 */
public class DirectoryUnitConfig {
    
    private DbConfig db;
    private DirectoryConfig directory;

    public DbConfig getDb() {
        return db;
    }

    public void setDb(DbConfig db) {
        this.db = db;
    }

    public DirectoryConfig getDirectory() {
        return directory;
    }

    public void setDirectory(DirectoryConfig directory) {
        this.directory = directory;
    }

    @Override
    public String toString() {
        return "{" + "db=" + db + ", directory=" + directory + '}';
    }
    
    public static class DirectoryConfig {
        private Set<String> createDirs;
        private Set<DirectoryPermissionModel> permissions;
        private String errorDir;

        public Set<String> getCreateDirs() {
            return createDirs;
        }

        public void setCreateDirs(Set<String> createDirs) {
            this.createDirs = createDirs;
        }

        public Set<DirectoryPermissionModel> getPermissions() {
            return permissions;
        }

        public void setPermissions(Set<DirectoryPermissionModel> permissions) {
            this.permissions = permissions;
        }

        public String getErrorDir() {
            return errorDir;
        }

        public void setErrorDir(String errorDir) {
            this.errorDir = errorDir;
        }

        @Override
        public String toString() {
            return "{" + "createDirs=" + createDirs + ", permissions=" + permissions + ", errorDirectory=" + errorDir + '}';
        }
    }
    
}
