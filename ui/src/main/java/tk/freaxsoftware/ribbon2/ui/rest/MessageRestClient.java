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
import tk.freaxsoftware.ribbon2.core.data.response.MessagePage;
import tk.freaxsoftware.ribbon2.core.exception.CoreException;
import tk.freaxsoftware.ribbon2.core.exception.RibbonErrorCodes;

/**
 * Message resource REST client.
 * @author Stanislav Nepochatov
 */
public class MessageRestClient {
    
    private final HttpClientBuilder clientBuilder = HttpClientBuilder.create();
    
    private final Gson gson = GsonUtils.getGson();
    
    private final String baseUrl;

    public MessageRestClient(String baseUrl) {
        this.baseUrl = baseUrl + "/api/message";
    }
    
    /**
     * Get all messages.
     * @param jwtKey raw JWT key;
     * @param directory directory for loading messages from;
     * @return page of messages;
     * @throws URISyntaxException
     * @throws IOException 
     */
    public MessagePage getMessages(String jwtKey, String directory) throws URISyntaxException, IOException {
        HttpGet request = new HttpGet(new URIBuilder(baseUrl + "/" + directory).addParameter(PARAM_PAGE, "0").addParameter(PARAM_SIZE, "30").build());
        request.addHeader("x-ribbon2-auth", jwtKey);
        HttpResponse response = clientBuilder.build().execute(request);
        if (response.getStatusLine().getStatusCode() == 200) {
            return gson.fromJson(IOUtils.toString(response.getEntity().getContent()), MessagePage.class);
        } else {
            throw new CoreException(RibbonErrorCodes.CALL_ERROR, "Messages request failed with status: " + response.getStatusLine().toString());
        }
    }
    
}
