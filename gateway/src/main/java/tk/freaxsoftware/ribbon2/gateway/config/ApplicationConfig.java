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
package tk.freaxsoftware.ribbon2.gateway.config;

import tk.freaxsoftware.ribbon2.core.config.DbConfig;

/**
 * Main application config.
 * @author Stanislav Nepochatov
 */
public class ApplicationConfig {
    
    private HttpConfig http;
    private DbConfig db;

    public HttpConfig getHttp() {
        return http;
    }

    public void setHttp(HttpConfig http) {
        this.http = http;
    }

    public DbConfig getDb() {
        return db;
    }

    public void setDb(DbConfig db) {
        this.db = db;
    }

    @Override
    public String toString() {
        return "{" + "http=" + http + ", db=" + db + '}';
    }
    
    public static class HttpConfig {
        private Integer port;
        private String url;
        private AuthType authType;
        private String authTokenName;
        private String authTokenSecret;
        private Integer authTokenValidDays;

        public Integer getPort() {
            return port;
        }

        public void setPort(Integer port) {
            this.port = port;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public AuthType getAuthType() {
            return authType;
        }

        public void setAuthType(AuthType authType) {
            this.authType = authType;
        }

        public String getAuthTokenName() {
            return authTokenName;
        }

        public void setAuthTokenName(String authTokenName) {
            this.authTokenName = authTokenName;
        }

        public String getAuthTokenSecret() {
            return authTokenSecret;
        }

        public void setAuthTokenSecret(String authTokenSecret) {
            this.authTokenSecret = authTokenSecret;
        }

        public Integer getAuthTokenValidDays() {
            return authTokenValidDays;
        }

        public void setAuthTokenValidDays(Integer authTokenValidDays) {
            this.authTokenValidDays = authTokenValidDays;
        }

        @Override
        public String toString() {
            return "{" + "port=" + port + ", url=" + url + ", authType=" + authType + ", authTokenName=" + authTokenName + ", authTokenSecret=" + authTokenSecret + ", authTokenValidDays=" + authTokenValidDays + '}';
        }
        
        public static enum AuthType {
            HEADER,
            COOKIE;
        }
    }
}
