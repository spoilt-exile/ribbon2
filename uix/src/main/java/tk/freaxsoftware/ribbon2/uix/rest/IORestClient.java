/*
 * This file is part of Ribbon2 news message system.
 * 
 * Copyright (C) 2026 Freax Software
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
import java.util.List;
import java.util.Optional;

import tk.freaxsoftware.ribbon2.io.core.IOScheme;
import tk.freaxsoftware.ribbon2.uix.model.io.IOModuleScheme;
import tk.freaxsoftware.ribbon2.uix.model.io.IOProtocol;

/**
 * REST client for system IO resources.
 * @author Stanislav Nepochatov
 */
public interface IORestClient {
    
    /**
     * Get IO protocols available for system's input and output operations. 
     * Same protocol type can be used for input and output. 
     * @param jwtKey key of current user (user should be admin for IO API);
     * @return list of protocols;
     * @throws java.net.URISyntaxException
     * @throws java.io.IOException
     */
    List<IOProtocol> getProtocols(String jwtKey) throws URISyntaxException, IOException;

    /**
     * Get list of active IO schemes across whole system.
     * @param jwtKey key of current user (user should be admin for IO API);
     * @return list of all registered IO schemes;
     * @throws java.net.URISyntaxException
     * @throws java.io.IOException
     */
    List<IOModuleScheme> getSchemes(String jwtKey) throws URISyntaxException, IOException;

    /**
     * Get list of export IO schemes assigned to specified directory.
     * @param jwtKey key of current user (user should be admin for IO API);
     * @param directory full path of directory;
     * @return list of export IO schemes or empty list;
     * @throws java.net.URISyntaxException
     * @throws java.io.IOException
     */
    List<IOModuleScheme> getExportSchemesByDirectory(String jwtKey, String directory) throws URISyntaxException, IOException;

    /**
     * Get full IO scheme with config values.
     * @param jwtKey key of current user (user should be admin for IO API);
     * @param type type of of the scheme (import or export);
     * @param protocol protocol of scheme;
     * @param name name of the scheme;
     * @return optional value of scheme;
     * @throws java.net.URISyntaxException
     * @throws java.io.IOException
     */
    Optional<IOScheme> getScheme(String jwtKey, String type, String protocol, String name) throws URISyntaxException, IOException;

    /**
     * Save IO scheme into the system.
     * @param jwtKey key of current user (user should be admin for IO API);
     * @param scheme scheme to save;
     * @return saved scheme (should be identical to sent one);
     * @throws java.net.URISyntaxException
     * @throws java.io.IOException
     */
    IOScheme saveScheme(String jwtKey, IOScheme scheme) throws URISyntaxException, IOException;

    /**
     * Delete IO scheme for the system.
     * @param jwtKey key of current user (user should be admin for IO API);
     * @param type type of of the scheme (import or export);
     * @param protocol protocol of scheme;
     * @param name name of the scheme;
     * @throws java.net.URISyntaxException
     * @throws java.io.IOException
     */
    void deleteScheme(String jwtKey, String type, String protocol, String name) throws URISyntaxException, IOException;

    /**
     * Assign export scheme to the directory. Messages after that will be exported by scheme.
     * @param jwtKey key of current user (user should be admin for IO API);
     * @param protocol protocol of scheme;
     * @param name name of the scheme;
     * @param dir name of directory to assign to;
     * @throws java.net.URISyntaxException
     * @throws java.io.IOException
     */
    void assignExportScheme(String jwtKey, String protocol, String name, String dir) throws URISyntaxException, IOException;

    /**
     * Dismiss export scheme from the directory. 
     * Export of the messages from this directory by this scheme will be stopped after that.
     * @param jwtKey key of current user (user should be admin for IO API);
     * @param protocol protocol of scheme;
     * @param name name of the scheme;
     * @param dir name of directory to dismiss from;
     * @throws java.net.URISyntaxException
     * @throws java.io.IOException
     */
    void dismissExportScheme(String jwtKey, String protocol, String name, String dir) throws URISyntaxException, IOException;
}
