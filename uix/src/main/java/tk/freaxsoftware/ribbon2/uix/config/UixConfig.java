/*
 * This file is part of Ribbon2 news message system.
 * 
 * Copyright (C) 2023 Freax Software
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
package tk.freaxsoftware.ribbon2.uix.config;

/**
 * Main config of UIX unit.
 * @author Stanislav Nepochatov
 */
public class UixConfig {
    
    private String gatewayUrl;
    
    private HttpConfig http;

    public String getGatewayUrl() {
        return gatewayUrl;
    }

    public void setGatewayUrl(String gatewayUrl) {
        this.gatewayUrl = gatewayUrl;
    }

    public HttpConfig getHttp() {
        return http;
    }

    public void setHttp(HttpConfig http) {
        this.http = http;
    }

    @Override
    public String toString() {
        return "{" + "gatewayUrl=" + gatewayUrl + ", http=" + http + '}';
    }
    
    public static class HttpConfig {
        
        private Integer port;
        private String authCookieName;
        private Integer authTokenValidDays;

        public Integer getPort() {
            return port;
        }

        public void setPort(Integer port) {
            this.port = port;
        }

        public String getAuthCookieName() {
            return authCookieName;
        }

        public void setAuthCookieName(String authCookieName) {
            this.authCookieName = authCookieName;
        }

        public Integer getAuthTokenValidDays() {
            return authTokenValidDays;
        }

        public void setAuthTokenValidDays(Integer authTokenValidDays) {
            this.authTokenValidDays = authTokenValidDays;
        }

        @Override
        public String toString() {
            return "{" + "port=" + port + ", authCookieName=" + authCookieName + ", authTokenValidDays=" + authTokenValidDays + '}';
        }
    }
}
