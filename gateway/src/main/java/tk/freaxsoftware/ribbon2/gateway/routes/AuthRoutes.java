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
package tk.freaxsoftware.ribbon2.gateway.routes;

import io.ebean.DB;
import io.jsonwebtoken.Claims;
import java.net.HttpURLConnection;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.QueryParamsMap;
import spark.Spark;
import static spark.Spark.before;
import static spark.Spark.get;
import static spark.Spark.post;
import tk.freaxsoftware.ribbon2.gateway.GatewayMain;
import tk.freaxsoftware.ribbon2.gateway.config.ApplicationConfig;
import tk.freaxsoftware.ribbon2.gateway.entity.UserEntity;
import tk.freaxsoftware.ribbon2.gateway.entity.converters.UserConverter;
import tk.freaxsoftware.ribbon2.gateway.utils.JWTTokenService;
import tk.freaxsoftware.ribbon2.gateway.utils.SHAHash;
import tk.freaxsoftware.ribbon2.gateway.utils.UserContext;

/**
 * Routes for performing auth.
 * @author Stanislav Nepochatov
 */
public class AuthRoutes {
    
    private final static Logger LOGGER = LoggerFactory.getLogger(AuthRoutes.class);
    
    public static void init(ApplicationConfig.HttpConfig httpConfig) {
        before("/api/*", (request, response) -> {
            UserEntity loginedUser = null;
            if (request.headers().contains(httpConfig.getAuthTokenName())) {
                try {
                    Claims userClaims = JWTTokenService.getInstance().decryptToken(request.headers(httpConfig.getAuthTokenName()));
                    loginedUser = DB.getDefault().find(UserEntity.class).where().eq("login", userClaims.getId()).findOne();
                } catch (Exception ex) {
                    LOGGER.error("Unable to finish JWT auth", ex);
                }
            }
            if (loginedUser == null) {
                Spark.halt(HttpURLConnection.HTTP_FORBIDDEN);
            } else if (loginedUser != null && loginedUser.getEnabled()) {
                UserContext.setUser(loginedUser);
            }
        });
        
        post("/auth", (request, response) -> {
            QueryParamsMap map = request.queryMap();
            UserEntity loginedUser = DB.getDefault().find(UserEntity.class).where().eq("login", map.value("login")).findOne();
            if (loginedUser != null 
                    && loginedUser.getEnabled()
                    && Objects.equals(SHAHash.hashPassword(map.value("password")), loginedUser.getPassword())) {
                LOGGER.debug("Proceed JWT auth: " + loginedUser.getLogin());
                JWTTokenService tokenService = JWTTokenService.getInstance();
                String token = tokenService.encryptToken(loginedUser);
                return token;
            } else {
                Spark.halt(HttpURLConnection.HTTP_FORBIDDEN);
            }
            return null;
        });
        
        get("/api/account", (request, response) -> {
            return new UserConverter().convert(UserContext.getUser());
        }, GatewayMain.gson::toJson);
    }
    
}
