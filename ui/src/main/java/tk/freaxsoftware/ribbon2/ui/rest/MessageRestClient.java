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
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.Set;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import tk.freaxsoftware.extras.bus.bridge.http.util.GsonUtils;
import tk.freaxsoftware.ribbon2.core.data.MessageModel;
import tk.freaxsoftware.ribbon2.core.data.MessagePropertyTagged;
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
    public MessagePage getMessages(String jwtKey, String directory, int pageSize, int page) throws URISyntaxException, IOException {
        HttpGet request = new HttpGet(new URIBuilder(baseUrl + "/" + directory)
                .addParameter(PARAM_PAGE, String.valueOf(page))
                .addParameter(PARAM_SIZE, String.valueOf(pageSize)).build());
        request.addHeader("x-ribbon2-auth", jwtKey);
        HttpResponse response = clientBuilder.build().execute(request);
        if (response.getStatusLine().getStatusCode() == 200) {
            return gson.fromJson(IOUtils.toString(response.getEntity().getContent(), Charset.defaultCharset()), MessagePage.class);
        } else {
            throw new CoreException(RibbonErrorCodes.CALL_ERROR, "Messages request failed with status: " + response.getStatusLine().toString());
        }
    }
    
    /**
     * Get full message (with content) by uid.
     * @param jwtKey raw JWT key;
     * @param uid message uid;
     * @param directory directory for loading message from;
     * @return message model with content;
     */
    public MessageModel getMessageByUid(String jwtKey, String uid, String directory) throws URISyntaxException, IOException {
        HttpGet request = new HttpGet(new URIBuilder(baseUrl + "/" + uid + "/dir/" + directory).build());
        request.addHeader("x-ribbon2-auth", jwtKey);
        HttpResponse response = clientBuilder.build().execute(request);
        if (response.getStatusLine().getStatusCode() == 200) {
            return gson.fromJson(IOUtils.toString(response.getEntity().getContent(), Charset.defaultCharset()), MessageModel.class);
        } else {
            throw new CoreException(RibbonErrorCodes.CALL_ERROR, "Messages request failed with status: " + response.getStatusLine().toString());
        }
    }
    
    /**
     * Create new message.
     * @param jwtKey raw JWT key;
     * @param message message model;
     * @return fully saved message;
     * @throws URISyntaxException
     * @throws IOException 
     */
    public MessageModel createMessage(String jwtKey, MessageModel message) throws URISyntaxException, IOException {
        HttpPost request = new HttpPost(baseUrl);
        request.addHeader("x-ribbon2-auth", jwtKey);
        request.setEntity(new StringEntity(gson.toJson(message)));
        HttpResponse response = clientBuilder.build().execute(request);
        if (response.getStatusLine().getStatusCode() == 200) {
            return gson.fromJson(IOUtils.toString(response.getEntity().getContent(), Charset.defaultCharset()), MessageModel.class);
        } else {
            throw new CoreException(RibbonErrorCodes.CALL_ERROR, "Create message request failed with status: " + response.getStatusLine().toString());
        }
    }
    
    /**
     * Update existing message.
     * @param jwtKey raw JWT key;
     * @param message message model with id;
     * @return updated message;
     * @throws URISyntaxException
     * @throws IOException 
     */
    public MessageModel updateMessage(String jwtKey, MessageModel message) throws URISyntaxException, IOException {
        HttpPut request = new HttpPut(baseUrl);
        request.addHeader("x-ribbon2-auth", jwtKey);
        request.setEntity(new StringEntity(gson.toJson(message)));
        HttpResponse response = clientBuilder.build().execute(request);
        if (response.getStatusLine().getStatusCode() == 200) {
            return gson.fromJson(IOUtils.toString(response.getEntity().getContent(), Charset.defaultCharset()), MessageModel.class);
        } else {
            throw new CoreException(RibbonErrorCodes.CALL_ERROR, "Update message request failed with status: " + response.getStatusLine().toString());
        }
    }
    
    /**
     * Get all registered property types.
     * @param jwtKey raw JWT key;
     * @return list of property types with tags;
     */
    public Set<MessagePropertyTagged> getAllPropertyTypes(String jwtKey) throws URISyntaxException, IOException {
        HttpGet request = new HttpGet(new URIBuilder(baseUrl + "/property/all").build());
        request.addHeader("x-ribbon2-auth", jwtKey);
        HttpResponse response = clientBuilder.build().execute(request);
        if (response.getStatusLine().getStatusCode() == 200) {
            return gson.fromJson(IOUtils.toString(response.getEntity().getContent(), Charset.defaultCharset()), new TypeToken<Set<MessagePropertyTagged>>(){}.getType());
        } else {
            throw new CoreException(RibbonErrorCodes.CALL_ERROR, "Messages request failed with status: " + response.getStatusLine().toString());
        }
    }
}
