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
import tk.freaxsoftware.ribbon2.core.data.MessageModel;
import tk.freaxsoftware.ribbon2.core.data.MessagePropertyTagged;
import tk.freaxsoftware.ribbon2.core.data.response.DefaultPage;

/**
 * Message resource REST client.
 * @author Stanislav Nepochatov
 */
public interface MessageRestClient {
    
    /**
     * Get messages paged.
     * @param jwtKey raw JWT key;
     * @param directory directory for loading messages from;
     * @param pageSize size of page;
     * @param page number of page;
     * @return page of messages;
     * @throws java.net.URISyntaxException
     * @throws java.io.IOException
     */
    DefaultPage<MessageModel> getMessages(String jwtKey, String directory, int pageSize, int page) throws URISyntaxException, IOException;
    
    /**
     * Get full message (with content) by uid.
     * @param jwtKey raw JWT key;
     * @param uid message uid;
     * @param directory directory for loading message from;
     * @return rest result with message model with content;
     * @throws java.net.URISyntaxException
     * @throws java.io.IOException
     */
    MessageModel getMessageByUid(String jwtKey, String uid, String directory) throws URISyntaxException, IOException;
    
    /**
     * Create new message.
     * @param jwtKey raw JWT key;
     * @param message message model;
     * @return fully saved message;
     * @throws URISyntaxException
     * @throws IOException 
     */
    MessageModel createMessage(String jwtKey, MessageModel message) throws URISyntaxException, IOException;
    
    /**
     * Update existing message.
     * @param jwtKey raw JWT key;
     * @param message message model with id;
     * @return updated message;
     * @throws URISyntaxException
     * @throws IOException 
     */
    MessageModel updateMessage(String jwtKey, MessageModel message) throws URISyntaxException, IOException;
    
    /**
     * Delete message by uid.
     * @param jwtKey raw JWT key;
     * @param messageUid message uid;
     * @throws URISyntaxException
     * @throws IOException 
     */
    void deleteMessage(String jwtKey, String messageUid) throws URISyntaxException, IOException;
    
    /**
     * Get all registered property types.
     * @param jwtKey raw JWT key;
     * @return list of property types with tags;
     * @throws java.net.URISyntaxException
     * @throws java.io.IOException
     */
    Set<MessagePropertyTagged> getAllPropertyTypes(String jwtKey) throws URISyntaxException, IOException;

}
