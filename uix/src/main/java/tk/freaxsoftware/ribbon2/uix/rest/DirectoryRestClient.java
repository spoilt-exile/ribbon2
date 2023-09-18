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

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.Set;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClientBuilder;
import tk.freaxsoftware.extras.bus.bridge.http.util.GsonUtils;
import tk.freaxsoftware.ribbon2.core.data.DirectoryModel;
import static tk.freaxsoftware.ribbon2.core.data.request.PaginationRequest.PARAM_PAGE;
import static tk.freaxsoftware.ribbon2.core.data.request.PaginationRequest.PARAM_SIZE;
import tk.freaxsoftware.ribbon2.core.data.response.DirectoryPage;
import tk.freaxsoftware.ribbon2.core.exception.CoreException;
import tk.freaxsoftware.ribbon2.core.exception.RibbonErrorCodes;

/**
 * Directory resource REST client.
 * @author Stanislav Nepochatov
 */
public class DirectoryRestClient {
    
    private final HttpClientBuilder clientBuilder = HttpClientBuilder.create();
    
    private final Gson gson = GsonUtils.getGson();
    
    private final String baseUrl;

    public DirectoryRestClient(String baseUrl) {
        this.baseUrl = baseUrl + "/api/directory";
    }
    
    /**
     * Get all directories sorted for tree.
     * @param jwtKey raw JWT key;
     * @return page of directories;
     * @throws URISyntaxException
     * @throws IOException 
     */
    public DirectoryPage getDirectories(String jwtKey) throws URISyntaxException, IOException {
        HttpGet request = new HttpGet(new URIBuilder(baseUrl).addParameter(PARAM_PAGE, "0").addParameter(PARAM_SIZE, "10000").build());
        request.addHeader("x-ribbon2-auth", jwtKey);
        HttpResponse response = clientBuilder.build().execute(request);
        if (response.getStatusLine().getStatusCode() == 200) {
            return gson.fromJson(IOUtils.toString(response.getEntity().getContent(), Charset.defaultCharset()), DirectoryPage.class);
        } else {
            throw new CoreException(RibbonErrorCodes.CALL_ERROR, "Directory request failed with status: " + response.getStatusLine().toString());
        }
    }
    
    /**
     * Get all permissions available to user by directory.
     * @param jwtKey raw JWT key;
     * @param dirPath path to directory;
     * @return list of permission names which current user can use in specified directory;
     * @throws URISyntaxException
     * @throws IOException 
     */
    public Set<String> getDirectoriesPermissions(String jwtKey, String dirPath) throws URISyntaxException, IOException {
        HttpGet request = new HttpGet(new URIBuilder(baseUrl + "/access/permission/current/" + dirPath).build());
        request.addHeader("x-ribbon2-auth", jwtKey);
        HttpResponse response = clientBuilder.build().execute(request);
        if (response.getStatusLine().getStatusCode() == 200) {
            return gson.fromJson(IOUtils.toString(response.getEntity().getContent(), Charset.defaultCharset()), Set.class);
        } else {
            throw new CoreException(RibbonErrorCodes.CALL_ERROR, "Permissions request by directory failed with status: " + response.getStatusLine().toString());
        }
    }
    
    /**
     * Get list of directories which can be accessed by curent user with specified permission.
     * @param jwtKey raw JWT key;
     * @param permission name of the permission;
     * @return list of directories;
     * @throws URISyntaxException
     * @throws IOException 
     */
    public Set<DirectoryModel> getDirectoriesByPermission(String jwtKey, String permission) throws URISyntaxException, IOException {
        HttpGet request = new HttpGet(new URIBuilder(baseUrl + "/permission/" + permission).build());
        request.addHeader("x-ribbon2-auth", jwtKey);
        HttpResponse response = clientBuilder.build().execute(request);
        if (response.getStatusLine().getStatusCode() == 200) {
            return gson.fromJson(IOUtils.toString(response.getEntity().getContent(), Charset.defaultCharset()), new TypeToken<Set<DirectoryModel>>() {}.getType());
        } else {
            throw new CoreException(RibbonErrorCodes.CALL_ERROR, "Directory request by permission failed with status: " + response.getStatusLine().toString());
        }
    }

}
