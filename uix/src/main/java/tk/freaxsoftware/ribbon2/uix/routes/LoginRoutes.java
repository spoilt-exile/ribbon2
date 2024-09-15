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
package tk.freaxsoftware.ribbon2.uix.routes;

import io.javalin.Javalin;
import java.util.Map;
import tk.freaxsoftware.ribbon2.core.data.UserModel;
import tk.freaxsoftware.ribbon2.uix.Uix;
import tk.freaxsoftware.ribbon2.uix.UserSessionModelContext;
import tk.freaxsoftware.ribbon2.uix.model.UserSessionModel;
import tk.freaxsoftware.ribbon2.uix.rest.GatewayService;

/**
 * Routes and filters for login operations.
 * @author Stanislav Nepochatov
 */
public class LoginRoutes {
    
    private final static String USER_SESSION_KEY = "ribbon2User";
    
    public static void init(Javalin app, GatewayService gatewayService) {
        app.before("/*", ctx -> {
            UserSessionModel logined = null;
            if (ctx.sessionAttribute(USER_SESSION_KEY) != null) {
                logined = ctx.sessionAttribute(USER_SESSION_KEY);
            } else if (ctx.cookie(Uix.config.getHttp().getAuthCookieName()) != null) {
                UserModel user = gatewayService.getAuthRestClient().getAccount(
                        ctx.cookie(Uix.config.getHttp().getAuthCookieName()));
                logined = UserSessionModel.ofUserModelAndJwtKey(user, ctx.cookie(Uix.config.getHttp().getAuthCookieName()));
                if (logined != null) {
                    ctx.sessionAttribute(USER_SESSION_KEY, logined);
                }
            }
            
            if (logined == null) {
                if (!ctx.req().getPathInfo().equals("/login")
                        && !ctx.req().getPathInfo().startsWith("/web/static")) {
                    ctx.redirect("/login");
                }
            } else {
                UserSessionModelContext.setUser(logined);
            }
        });
        
        app.after("/*", ctx -> {
            UserSessionModelContext.setUser(null);
        });
        
        app.get("/login", ctx -> {
            ctx.render("login.html", Map.of());
        });
        
        app.post("/login", ctx -> {
            final String login = ctx.formParam("login");
            final String password = ctx.formParam("password");
            final String token = gatewayService.getAuthRestClient().auth(login, password);
            ctx.cookie(Uix.config.getHttp().getAuthCookieName(), token, Uix.config.getHttp().authTokenMaxAge());
            ctx.header("HX-Redirect", "/");
        });
        
        app.post("/logout", ctx -> {
            ctx.removeCookie(Uix.config.getHttp().getAuthCookieName());
            ctx.req().getSession().invalidate();
            ctx.header("HX-Redirect", "/login");
        });
    }
    
}
