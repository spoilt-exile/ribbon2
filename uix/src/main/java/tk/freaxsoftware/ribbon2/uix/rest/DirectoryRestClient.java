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
import java.util.List;
import tk.freaxsoftware.ribbon2.core.data.DirectoryAccessModel;
import tk.freaxsoftware.ribbon2.core.data.DirectoryModel;
import tk.freaxsoftware.ribbon2.core.data.DirectoryPermissionTaggedModel;
import tk.freaxsoftware.ribbon2.core.data.DirectorySecurityModel;
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

    /** Create a directory. The service derives its name from the full path. */
    DirectoryModel createDirectory(String jwtKey, DirectoryModel directory) throws URISyntaxException, IOException;

    /** Update only the description of an existing directory. */
    DirectoryModel updateDirectory(String jwtKey, DirectoryModel directory) throws URISyntaxException, IOException;

    /** Delete a directory and all of its descendants. */
    void deleteDirectory(String jwtKey, String dirPath) throws URISyntaxException, IOException;

    /** Read the directory's local access mode and inheritance source (admin API). */
    DirectorySecurityModel getDirectorySecurity(String jwtKey, String dirPath) throws URISyntaxException, IOException;

    /** Get the permission catalog used by directory access rules (admin API). */
    List<DirectoryPermissionTaggedModel> getDirectoryPermissionCatalog(String jwtKey) throws URISyntaxException, IOException;

    /** Replace local access rules; null entries mean inherit from parent (admin API). */
    Boolean saveDirectoryAccess(String jwtKey, String dirPath, Set<DirectoryAccessModel> entries) throws URISyntaxException, IOException;
    
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
