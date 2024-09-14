/*
 * This file is part of Ribbon2 news message system.
 * 
 * Copyright (C) 2024 Freax Software
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
import tk.freaxsoftware.ribbon2.core.data.DirectoryModel;
import tk.freaxsoftware.ribbon2.core.data.MessageModel;
import tk.freaxsoftware.ribbon2.core.data.MessagePropertyModel;
import tk.freaxsoftware.ribbon2.core.data.MessagePropertyTagged;
import tk.freaxsoftware.ribbon2.core.data.UserModel;
import tk.freaxsoftware.ribbon2.core.data.response.DefaultPage;
import tk.freaxsoftware.ribbon2.core.data.response.DirectoryPage;
import tk.freaxsoftware.ribbon2.uix.rest.AuthRestClient;
import tk.freaxsoftware.ribbon2.uix.rest.DirectoryRestClient;
import tk.freaxsoftware.ribbon2.uix.rest.GatewayService;
import tk.freaxsoftware.ribbon2.uix.rest.MessageRestClient;
import tk.freaxsoftware.ribbon2.uix.rest.UserRestClient;

/**
 * Dummy gateway service allows to mock data from system.
 * @author Stanislav Nepochatov
 */
public class GatewayServiceDummy extends GatewayService {
    
    private final UserRestClient dummyUserClient;
    private final MessageRestClient dummyMessageClient;
    private final DirectoryRestClient dummyDirectoryClient;
    private final AuthRestClient dummyAuthClient;
    
    public GatewayServiceDummy(String gatewayUrl) {
        super(gatewayUrl);
        this.dummyUserClient = new UserRestClientDummy(gatewayUrl);
        this.dummyMessageClient = new MessageRestClientDummy(gatewayUrl);
        this.dummyDirectoryClient = new DirectoryRestClientDummy(gatewayUrl);
        this.dummyAuthClient = new AuthRestClientDummy(gatewayUrl);
    }

    @Override
    public UserRestClient getUserRestClient() {
        return dummyUserClient;
    }

    @Override
    public MessageRestClient getMessageRestClient() {
        return dummyMessageClient;
    }

    @Override
    public DirectoryRestClient getDirectoryRestClient() {
        return dummyDirectoryClient;
    }

    @Override
    public AuthRestClient getAuthRestClient() {
        return dummyAuthClient;
    }
    
    private final static class UserRestClientDummy extends UserRestClient {
        
        public UserRestClientDummy(String baseUrl) {
            super(baseUrl);
        }

        @Override
        public DefaultPage<UserModel> getUsers(String jwtKey, int pageSize, int page) throws URISyntaxException, IOException {
            List<UserModel> users = List.of(
                    createUser("root", Set.of("Admins")),
                    createUser("user", Set.of("Users"))
            );
            DefaultPage<UserModel> userPage = new DefaultPage(users, users.size());
            return userPage;
        }
    }
    
    private final static class MessageRestClientDummy extends MessageRestClient {
        
        public MessageRestClientDummy(String baseUrl) {
            super(baseUrl);
        }

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
    }
    
    private final static class DirectoryRestClientDummy extends DirectoryRestClient {
        
        public DirectoryRestClientDummy(String baseUrl) {
            super(baseUrl);
        }

        @Override
        public Set<DirectoryModel> getDirectoriesByPermission(String jwtKey, String permission) throws URISyntaxException, IOException {
            return Set.of(createDirecotry("System.Test", "Test"), createDirecotry("Dev.Null", "Null"));
        }

        @Override
        public Set<String> getDirectoriesPermissions(String jwtKey, String dirPath) throws URISyntaxException, IOException {
            return Set.of(
                    "canUpdateMessage",
                    "canUpdateDir",
                    "canReadMessage",
                    "canEditDirAccess",
                    "canDeleteMessage",
                    "canDeleteDir",
                    "canCreateMessage",
                    "canCreateDir",
                    "canAssignExport"
            );
        }

        @Override
        public DirectoryPage getDirectories(String jwtKey) throws URISyntaxException, IOException {
            final DirectoryPage dirPage = new DirectoryPage();
            dirPage.setContent(List.of(
                    createDirecotry("System", "System"),
                    createDirecotry("System.Test", "Test"),
                    createDirecotry("System.Error", "Error"),
                    createDirecotry("Edit", "Edit"),
                    createDirecotry("Edit.Release", "Release"),
                    createDirecotry("Edit.Release.EN", "EN"),
                    createDirecotry("Edit.Release.UA", "UA")
            ));
            return dirPage;
        }
    }
    
    private final static class AuthRestClientDummy extends AuthRestClient {
        
        public AuthRestClientDummy(String baseUrl) {
            super(baseUrl);
        }

        @Override
        public UserModel getAccount(String jwtKey) throws URISyntaxException, IOException {
            return createUser("root", Set.of("Admins"));
        }

        @Override
        public String auth(String login, String password) throws URISyntaxException, IOException {
            return "TOKEN";
        }
    }
    
    private static UserModel createUser(String login, Set<String> groups) {
        UserModel user = new UserModel();
        user.setLogin(login);
        user.setEnabled(true);
        user.setGroups(groups);
        user.setId(1000L);
        user.setEmail(login + "@freaksoftware.tk");
        user.setFirstName("FName");
        user.setLastName("LName");
        return user;
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
        final MessagePropertyModel copyrightProp = new MessagePropertyModel("MARK", "System.Test");
        copyrightProp.setCreatedBy("root");
        copyrightProp.setCreated(ZonedDateTime.now());
        copyrightProp.setUid(uid);
        message.setProperties(Set.of(copyrightProp));
        return message;
    }
    
    private static DirectoryModel createDirecotry(String path, String name) {
        DirectoryModel dir = new DirectoryModel();
        dir.setId(1000L);
        dir.setName(name);
        dir.setFullName(path);
        dir.setDescription("GENERATED");
        return dir;
    }
}
