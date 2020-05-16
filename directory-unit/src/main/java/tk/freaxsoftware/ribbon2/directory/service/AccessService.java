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

import java.util.Set;
import tk.freaxsoftware.ribbon2.directory.repo.DirectoryRepository;
import tk.freaxsoftware.ribbon2.directory.repo.UserRepository;

/**
 * Access service provides methods to check directory permissions of user.
 * @author Stanislav Nepochatov
 */
public class AccessService extends AuthService {

    public AccessService(DirectoryRepository directoryRepository, UserRepository userRespository) {
        super(directoryRepository, userRespository);
    }
    
    /**
     * Checks if current specified user has access by permission to specified directories.
     * @param userLogin user to check login;
     * @param directories set of directory names;
     * @param permission permission to check;
     */
    public Boolean checkDirectoryAccess(String userLogin, Set<String> directories, String permission) {
        for (String directory: directories) {
            if (!checkDirAccess(userLogin, directory, permission)) {
                return false;
            }
        }
        return true;
    }
    
}
