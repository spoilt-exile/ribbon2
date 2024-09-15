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
package tk.freaxsoftware.ribbon2.uix.rest.dummy;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import tk.freaxsoftware.ribbon2.core.data.MessageModel;
import tk.freaxsoftware.ribbon2.core.data.MessagePropertyModel;
import tk.freaxsoftware.ribbon2.core.data.MessagePropertyTagged;
import tk.freaxsoftware.ribbon2.core.data.response.DefaultPage;
import tk.freaxsoftware.ribbon2.uix.rest.MessageRestClient;

/**
 * Dummy message REST client implementation.
 * @author Stanislav Nepochatov
 */
public class MessageRestClientDummy implements MessageRestClient {
    
    @Override
    public Set<MessagePropertyTagged> getAllPropertyTypes(String jwtKey) throws URISyntaxException, IOException {
        return Set.of(
                createPropertyType("URGENT", "Urgent message mark.", "message"),
                createPropertyType("RELOCATED", "Relocated from directory.", "message"),
                createPropertyType("MARK", "User note or comment.", "message"),
                createPropertyType("EXPORT_PLAIN", "Plain text plain", "exc-export-plain"),
                createPropertyType("EMBARGO", "Embargo message process", "exc-export"),
                createPropertyType("COPYRIGHT", "Copyright on message content.", "message")
        );
    }

    @Override
    public void deleteMessage(String jwtKey, String messageUid) throws URISyntaxException, IOException {
        //Do nothing
    }

    @Override
    public MessageModel updateMessage(String jwtKey, MessageModel message) throws URISyntaxException, IOException {
        return message;
    }

    @Override
    public MessageModel createMessage(String jwtKey, MessageModel message) throws URISyntaxException, IOException {
        message.setUid(UUID.randomUUID().toString());
        message.setId(1000L);
        message.setCreatedBy("root");
        message.setCreated(ZonedDateTime.now());
        return message;
    }

    @Override
    public MessageModel getMessageByUid(String jwtKey, String uid, String directory) throws URISyntaxException, IOException {
        return createMessageModel();
    }

    @Override
    public DefaultPage<MessageModel> getMessages(String jwtKey, String directory, int pageSize, int page) throws URISyntaxException, IOException {
        List<MessageModel> messages = List.of(
                createMessageModel(),
                createMessageModel(),
                createMessageModel(),
                createMessageModel(),
                createMessageModel(),
                createMessageModel(),
                createMessageModel(),
                createMessageModel(),
                createMessageModel(),
                createMessageModel(),
                createMessageModel(),
                createMessageModel(),
                createMessageModel(),
                createMessageModel(),
                createMessageModel(),
                createMessageModel(),
                createMessageModel(),
                createMessageModel(),
                createMessageModel(),
                createMessageModel(),
                createMessageModel(),
                createMessageModel(),
                createMessageModel()
        );
        DefaultPage<MessageModel> messagePage = new DefaultPage(messages, messages.size());
        return messagePage;
    }
    
    private static MessagePropertyTagged createPropertyType(String type, String desc, String tag) {
        MessagePropertyTagged propType = new MessagePropertyTagged();
        propType.setType(type);
        propType.setDescription(desc);
        propType.setTag(tag);
        return propType;
    }
    
    private static MessageModel createMessageModel() {
        final String uid = UUID.randomUUID().toString();
        MessageModel message = new MessageModel();
        message.setId(1000L);
        message.setUid(uid);
        message.setHeader("Message header " + uid);
        message.setCreated(ZonedDateTime.now());
        message.setCreatedBy("root");
        message.setContent("Content " + uid);
        message.setDirectories(Set.of("Dev.Null"));
        message.setTags(Set.of("generated", "random"));
        final MessagePropertyModel copyrightProp = new MessagePropertyModel("COPYRIGHT", "root");
        copyrightProp.setCreatedBy("root");
        copyrightProp.setCreated(ZonedDateTime.now());
        copyrightProp.setUid(uid);
        message.setProperties(Set.of(copyrightProp));
        return message;
    }
}
