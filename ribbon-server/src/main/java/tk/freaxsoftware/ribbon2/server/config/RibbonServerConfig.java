/*
 * This file is part of Ribbon2 news message system.
 * 
 * Copyright (C) 2026 Freax Software
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
package tk.freaxsoftware.ribbon2.server.config;

import tk.freaxsoftware.ribbon2.core.config.DbConfig;

/**
 * Ribbon server config class.
 * @author Stanislav Nepochatov
 */
public class RibbonServerConfig {
    private Integer port;
    private DbConfig db;
    private AccessConfig access;

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public DbConfig getDb() {
        return db;
    }

    public void setDb(DbConfig db) {
        this.db = db;
    }

    public AccessConfig getAccess() {
        return access;
    }

    public void setAccess(AccessConfig access) {
        this.access = access;
    }

    public static class AccessConfig {
        private Boolean enableSessions;
        private Boolean allowRemote;
        private String remoteGroup;

        public Boolean getEnableSessions() {
            return enableSessions;
        }
        public void setEnableSessions(Boolean enableSessions) {
            this.enableSessions = enableSessions;
        }
        public Boolean getAllowRemote() {
            return allowRemote;
        }
        public void setAllowRemote(Boolean allowRemote) {
            this.allowRemote = allowRemote;
        }
        public String getRemoteGroup() {
            return remoteGroup;
        }
        public void setRemoteGroup(String remoteGroup) {
            this.remoteGroup = remoteGroup;
        }
    }

}
