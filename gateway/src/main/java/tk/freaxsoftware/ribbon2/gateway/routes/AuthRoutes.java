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
import io.javalin.Javalin;
import io.jsonwebtoken.Claims;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tk.freaxsoftware.ribbon2.core.exception.CoreException;
import tk.freaxsoftware.ribbon2.core.exception.RibbonErrorCodes;
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
    
    public static void init(Javalin app, ApplicationConfig.HttpConfig httpConfig) {
        app.before("/api/*", ctx -> {
            UserEntity loginedUser = null;
            if (ctx.headerMap().containsKey(httpConfig.getAuthTokenName())) {
                try {
                    Claims userClaims = JWTTokenService.getInstance().decryptToken(ctx.headerMap().get(httpConfig.getAuthTokenName()));
                    loginedUser = DB.getDefault().find(UserEntity.class).where().eq("login", userClaims.getId()).findOne();
                } catch (Exception ex) {
                    LOGGER.error("Unable to finish JWT auth", ex);
                }
            }
            if (loginedUser == null) {
                throw new CoreException(RibbonErrorCodes.ACCESS_DENIED, "Access denied");
            } else if (loginedUser != null && loginedUser.getEnabled()) {
                UserContext.setUser(loginedUser);
            }
        });
        
        app.post("/auth", ctx -> {
            UserEntity loginedUser = DB.getDefault().find(UserEntity.class).where().eq("login", ctx.queryParam("login")).findOne();
            if (loginedUser != null 
                    && loginedUser.getEnabled()
                    && Objects.equals(SHAHash.hashPassword(ctx.queryParam("password")), loginedUser.getPassword())) {
                LOGGER.debug("Proceed JWT auth: " + loginedUser.getLogin());
                JWTTokenService tokenService = JWTTokenService.getInstance();
                String token = tokenService.encryptToken(loginedUser);
                ctx.result(token);
            } else {
                throw new CoreException(RibbonErrorCodes.ACCESS_DENIED, "Access denied");
            }
        });
        
        app.get("/api/account", ctx -> {
            ctx.json(new UserConverter().convert(UserContext.getUser()));
        });
    }
    
}
