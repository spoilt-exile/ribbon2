/*
 * This file is part of Ribbon2 news message system.
 * 
 * Copyright (C) 2020-2022 Freax Software
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
package tk.freaxsoftware.ribbon2.ui.managed;

import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tk.freaxsoftware.ribbon2.core.data.UserModel;

/**
 * Login form processing.
 * @author Stanislav Nepochatov
 */
@Named(value = "login")
@RequestScoped
public class Login {
    
    private final static Logger LOGGER = LoggerFactory.getLogger(Login.class);
    
    @Inject
    private UserSession session;
    
    private String login;
    private String pass;
    
    @Inject
    private GatewayService gatewayService;

    public UserSession getSession() {
        return session;
    }

    public void setSession(UserSession session) {
        this.session = session;
    }

    public GatewayService getGatewayService() {
        return gatewayService;
    }

    public void setGatewayService(GatewayService gatewayService) {
        this.gatewayService = gatewayService;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }
    
    public String login() {
        try {
            LOGGER.info("Performing login of {}", login);
            final String jwtToken = gatewayService.getAuthRestClient().auth(login, pass);
            pass = null;
            session.setJwtKey(jwtToken);
            session.setLogin(login);
            final UserModel user = gatewayService.getAuthRestClient().getAccount(jwtToken);
            session.initSession(user);
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("username", session.getLogin());
            if (session.getIsAdmin()) {
                FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("isAdmin", "true");
            }
            return "success";
        } catch (Exception ex) {
            LOGGER.error("Login failed", ex);
            FacesContext.getCurrentInstance().addMessage("login-error", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Login failed, " + ex.getMessage(), ""));
            return "failed";
        }
    }
    
    public String logout() {
        LOGGER.info("Performing log out of {}", session.getLogin());
        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
        return "login.xhtml?faces-redirect=true";
    }
    
}
