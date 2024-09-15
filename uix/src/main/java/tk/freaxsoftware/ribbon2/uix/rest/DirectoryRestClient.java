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
package tk.freaxsoftware.ribbon2.uix.rest;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Set;
import tk.freaxsoftware.ribbon2.core.data.DirectoryModel;
import tk.freaxsoftware.ribbon2.core.data.response.DirectoryPage;

/**
 * Directory resource REST client.
 * @author Stanislav Nepochatov
 */
public interface DirectoryRestClient {
    
    /**
     * Get all directories sorted for tree.
     * @param jwtKey raw JWT key;
     * @return page of directories;
     * @throws java.net.URISyntaxException
     * @throws java.io.IOException
     */
    DirectoryPage getDirectories(String jwtKey) throws URISyntaxException, IOException;
    
    /**
     * Get all permissions available to user by directory.
     * @param jwtKey raw JWT key;
     * @param dirPath path to directory;
     * @return set of permission names which current user can use in specified directory;
     * @throws URISyntaxException
     * @throws IOException 
     */
    Set<String> getDirectoriesPermissions(String jwtKey, String dirPath) throws URISyntaxException, IOException;
    
    /**
     * Get set of directories which can be accessed by curent user with specified permission.
     * @param jwtKey raw JWT key;
     * @param permission name of the permission;
     * @return set of directories;
     * @throws URISyntaxException
     * @throws IOException 
     */
    Set<DirectoryModel> getDirectoriesByPermission(String jwtKey, String permission) throws URISyntaxException, IOException;

}
