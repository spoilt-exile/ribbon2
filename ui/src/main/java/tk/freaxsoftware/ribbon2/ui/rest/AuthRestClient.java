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
import java.nio.charset.Charset;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClientBuilder;
import tk.freaxsoftware.extras.bus.bridge.http.util.GsonUtils;
import tk.freaxsoftware.ribbon2.core.data.UserModel;
import tk.freaxsoftware.ribbon2.core.exception.CoreException;
import tk.freaxsoftware.ribbon2.core.exception.RibbonErrorCodes;

/**
 * Auth resource REST client.
 * @author Stanislav Nepochatov
 */
public class AuthRestClient {
    
    private final HttpClientBuilder clientBuilder = HttpClientBuilder.create();
    
    private final Gson gson = GsonUtils.getGson();
    
    private final String baseUrl;

    public AuthRestClient(String baseUrl) {
        this.baseUrl = baseUrl;
    }
    
    /**
     * Perform authentication of user.
     * @param login username of user;
     * @param password raw password of user;
     * @return raw JWT token of the user;
     * @throws URISyntaxException
     * @throws IOException 
     */
    public String auth(String login, String password) throws URISyntaxException, IOException {
        HttpPost request = new HttpPost(new URIBuilder(baseUrl + "/auth").addParameter("login", login).addParameter("password", password).build());
        HttpResponse response = clientBuilder.build().execute(request);
        if (response.getStatusLine().getStatusCode() == 200) {
            return IOUtils.toString(response.getEntity().getContent(), Charset.defaultCharset());
        } else {
            throw new CoreException(RibbonErrorCodes.CALL_ERROR, "Auth request failed with status: " + response.getStatusLine().toString());
        }
    }
    
    /**
     * Get current account info of user.
     * @param jwtKey raw JWT key;
     * @return user model;
     * @throws URISyntaxException
     * @throws IOException 
     */
    public UserModel getAccount(String jwtKey) throws URISyntaxException, IOException {
        HttpGet request = new HttpGet(new URIBuilder(baseUrl + "/api/account").build());
        request.addHeader("x-ribbon2-auth", jwtKey);
        HttpResponse response = clientBuilder.build().execute(request);
        if (response.getStatusLine().getStatusCode() == 200) {
            return gson.fromJson(IOUtils.toString(response.getEntity().getContent(), Charset.defaultCharset()), UserModel.class);
        } else {
            throw new CoreException(RibbonErrorCodes.CALL_ERROR, "Auth request failed with status: " + response.getStatusLine().toString());
        }
    }
}
