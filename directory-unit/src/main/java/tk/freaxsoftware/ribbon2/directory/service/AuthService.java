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
package tk.freaxsoftware.ribbon2.directory.service;

import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tk.freaxsoftware.ribbon2.directory.repo.DirectoryRepository;
import tk.freaxsoftware.ribbon2.directory.repo.UserRepository;

/**
 * Authentication service for checking access.
 * @author Stanislav Nepochatov
 */
public abstract class AuthService {
    
    private final static Logger LOGGER = LoggerFactory.getLogger(AuthService.class);
    
    /**
     * Admin login, skips checking of permissions.
     */
    public static final String ROOT_LOGIN = "root";
    
    protected DirectoryRepository directoryRepository;
    
    protected UserRepository userRespository;

    public AuthService(DirectoryRepository directoryRepository, UserRepository userRespository) {
        this.directoryRepository = directoryRepository;
        this.userRespository = userRespository;
    }
    
    /**
     * Checks whether current user has access to perform certain operation withing directory scope.
     * @param userLogin login of current user;
     * @param dirFullName full directory path;
     * @param permission name of the permission;
     * @return result of check: true - access granted, false - access denied;
     */
    public boolean checkDirAccess(String userLogin, String dirFullName, String permission) {
        LOGGER.info("Checking directory {} for user {} by permission {}", dirFullName, userLogin, permission);
        if (Objects.equals(userLogin, ROOT_LOGIN)) {
            return true;
        }
        //TODO: add logic here.
        return false;
    }
    
}
