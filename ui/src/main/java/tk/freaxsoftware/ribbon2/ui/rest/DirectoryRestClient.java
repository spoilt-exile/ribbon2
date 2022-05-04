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
package tk.freaxsoftware.ribbon2.ui.rest;

import com.google.gson.Gson;
import java.io.IOException;
import java.net.URISyntaxException;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClientBuilder;
import spark.utils.IOUtils;
import tk.freaxsoftware.extras.bus.bridge.http.util.GsonUtils;
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
            return gson.fromJson(IOUtils.toString(response.getEntity().getContent()), DirectoryPage.class);
        } else {
            throw new CoreException(RibbonErrorCodes.CALL_ERROR, "Directory request failed with status: " + response.getStatusLine().toString());
        }
    }
    
}
