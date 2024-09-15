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
import java.util.Set;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import tk.freaxsoftware.extras.bus.bridge.http.util.GsonUtils;
import tk.freaxsoftware.ribbon2.core.data.MessageModel;
import tk.freaxsoftware.ribbon2.core.data.MessagePropertyTagged;
import static tk.freaxsoftware.ribbon2.core.data.request.PaginationRequest.PARAM_PAGE;
import static tk.freaxsoftware.ribbon2.core.data.request.PaginationRequest.PARAM_SIZE;
import tk.freaxsoftware.ribbon2.core.data.response.DefaultPage;
import tk.freaxsoftware.ribbon2.core.exception.CoreException;
import tk.freaxsoftware.ribbon2.core.exception.RibbonErrorCodes;
import tk.freaxsoftware.ribbon2.uix.rest.MessageRestClient;

/**
 * Message REST client implementation.
 * @author Stanislav Nepochatov
 */
public class MessageRestClientImpl implements MessageRestClient {
    
    private final HttpClientBuilder clientBuilder = HttpClientBuilder.create();
    
    private final Gson gson = GsonUtils.getGson();
    
    private final String baseUrl;

    public MessageRestClientImpl(String baseUrl) {
        this.baseUrl = baseUrl + "/api/message";
    }
    
    @Override
    public DefaultPage<MessageModel> getMessages(String jwtKey, String directory, int pageSize, int page) throws URISyntaxException, IOException {
        HttpGet request = new HttpGet(new URIBuilder(baseUrl + "/" + directory)
                .addParameter(PARAM_PAGE, String.valueOf(page))
                .addParameter(PARAM_SIZE, String.valueOf(pageSize)).build());
        request.addHeader("x-ribbon2-auth", jwtKey);
        HttpResponse response = clientBuilder.build().execute(request);
        return ResponseUtil.handleResponse(response, new TypeToken<DefaultPage<MessageModel>>() {});
    }
    
    @Override
    public MessageModel getMessageByUid(String jwtKey, String uid, String directory) throws URISyntaxException, IOException {
        HttpGet request = new HttpGet(new URIBuilder(baseUrl + "/" + uid + "/dir/" + directory).build());
        request.addHeader("x-ribbon2-auth", jwtKey);
        HttpResponse response = clientBuilder.build().execute(request);
        return ResponseUtil.handleResponse(response, new TypeToken<MessageModel>() {});
    }
    
    @Override
    public MessageModel createMessage(String jwtKey, MessageModel message) throws URISyntaxException, IOException {
        HttpPost request = new HttpPost(baseUrl);
        request.addHeader("x-ribbon2-auth", jwtKey);
        request.setEntity(new StringEntity(gson.toJson(message), ContentType.APPLICATION_JSON));
        HttpResponse response = clientBuilder.build().execute(request);
        return ResponseUtil.handleResponse(response, new TypeToken<MessageModel>() {});
    }
    
    @Override
    public MessageModel updateMessage(String jwtKey, MessageModel message) throws URISyntaxException, IOException {
        HttpPut request = new HttpPut(baseUrl);
        request.addHeader("x-ribbon2-auth", jwtKey);
        request.setEntity(new StringEntity(gson.toJson(message), ContentType.APPLICATION_JSON));
        HttpResponse response = clientBuilder.build().execute(request);
        return ResponseUtil.handleResponse(response, new TypeToken<MessageModel>() {});
    }
    
    @Override
    public void deleteMessage(String jwtKey, String messageUid) throws URISyntaxException, IOException {
        HttpDelete request = new HttpDelete(baseUrl + "/" + messageUid);
        request.addHeader("x-ribbon2-auth", jwtKey);
        HttpResponse response = clientBuilder.build().execute(request);
        if (response.getStatusLine().getStatusCode() != 200) {
            throw new CoreException(RibbonErrorCodes.CALL_ERROR, "Delete message request failed with status: " + response.getStatusLine().toString());
        }
    }
    
    @Override
    public Set<MessagePropertyTagged> getAllPropertyTypes(String jwtKey) throws URISyntaxException, IOException {
        HttpGet request = new HttpGet(new URIBuilder(baseUrl + "/property/all").build());
        request.addHeader("x-ribbon2-auth", jwtKey);
        HttpResponse response = clientBuilder.build().execute(request);
        return ResponseUtil.handleResponse(response, new TypeToken<Set<MessagePropertyTagged>>() {});
    }
}
