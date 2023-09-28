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
     * @return rest result with raw JWT token;
     * @throws URISyntaxException
     * @throws IOException 
     */
    public RestResult<String> auth(String login, String password) throws URISyntaxException, IOException {
        HttpPost request = new HttpPost(new URIBuilder(baseUrl + "/auth").addParameter("login", login).addParameter("password", password).build());
        HttpResponse response = clientBuilder.build().execute(request);
        return RestResult.ofResponseRaw(response);
    }
    
    /**
     * Get current account info of user.
     * @param jwtKey raw JWT key;
     * @return rest result with user model;
     * @throws URISyntaxException
     * @throws IOException 
     */
    public RestResult<UserModel> getAccount(String jwtKey) throws URISyntaxException, IOException {
        HttpGet request = new HttpGet(new URIBuilder(baseUrl + "/api/account").build());
        request.addHeader("x-ribbon2-auth", jwtKey);
        HttpResponse response = clientBuilder.build().execute(request);
        return RestResult.ofResponse(response, new TypeToken<UserModel>() {});
    }
    
}
