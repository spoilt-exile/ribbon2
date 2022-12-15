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
package tk.freaxsoftware.ribbon2.ui.managed.adm;

import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tk.freaxsoftware.ribbon2.ui.data.UserLazyPage;
import tk.freaxsoftware.ribbon2.ui.managed.GatewayService;
import tk.freaxsoftware.ribbon2.ui.managed.UserSession;

/**
 * Users admin config bean.
 * @author Stanislav Nepochatov
 */
@Named(value = "admUsers")
@SessionScoped
public class AdmUsers implements Serializable {
    
    private final static Logger LOGGER = LoggerFactory.getLogger(AdmUsers.class);
    
    @Inject
    private UserSession session;
    
    @Inject
    private transient GatewayService gatewayService;
    
    private UserLazyPage userPage;
    
    @PostConstruct
    public void init() {
        userPage = new UserLazyPage(gatewayService, session.getJwtKey());
    }

    public UserLazyPage getUserPage() {
        return userPage;
    }

    public void setUserPage(UserLazyPage userPage) {
        this.userPage = userPage;
    }
    
}
