/*
 * This file is part of Ribbon2 news message system.
 * 
 * Copyright (C) 2020-2024 Freax Software
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
package tk.freaxsoftware.ribbon2.uix.rest.impl;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClientBuilder;
import tk.freaxsoftware.extras.bus.bridge.http.util.GsonUtils;
import tk.freaxsoftware.ribbon2.core.data.UserModel;
import static tk.freaxsoftware.ribbon2.core.data.request.PaginationRequest.PARAM_PAGE;
import static tk.freaxsoftware.ribbon2.core.data.request.PaginationRequest.PARAM_SIZE;
import tk.freaxsoftware.ribbon2.core.data.response.DefaultPage;
import tk.freaxsoftware.ribbon2.core.exception.CoreException;
import tk.freaxsoftware.ribbon2.core.exception.RibbonErrorCodes;
import tk.freaxsoftware.ribbon2.uix.rest.UserRestClient;

/**
 * User REST client implementation (admin only),
 * @author Stanislav Nepochatov
 */
public class UserRestClientImpl implements UserRestClient {
    
    private final HttpClientBuilder clientBuilder = HttpClientBuilder.create();
    
    private final Gson gson = GsonUtils.getGson();
    
    private final String baseUrl;

    public UserRestClientImpl(String baseUrl) {
        this.baseUrl = baseUrl + "/api/user";
    }
    
    /**
     * Get all users.
     * @param jwtKey raw JWT key;
     * @return page of users;
     * @throws URISyntaxException
     * @throws IOException 
     */
    public DefaultPage<UserModel> getUsers(String jwtKey, int pageSize, int page) throws URISyntaxException, IOException {
        HttpGet request = new HttpGet(new URIBuilder(baseUrl)
                .addParameter(PARAM_PAGE, String.valueOf(page))
                .addParameter(PARAM_SIZE, String.valueOf(pageSize)).build());
        request.addHeader("x-ribbon2-auth", jwtKey);
        HttpResponse response = clientBuilder.build().execute(request);
        if (response.getStatusLine().getStatusCode() == 200) {
            return gson.fromJson(IOUtils.toString(response.getEntity().getContent(), Charset.defaultCharset()), new TypeToken<DefaultPage<UserModel>>(){}.getType());
        } else {
            throw new CoreException(RibbonErrorCodes.CALL_ERROR, "Messages request failed with status: " + response.getStatusLine().toString());
        }
    }
}
