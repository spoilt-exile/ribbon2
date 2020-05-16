/*
 * This file is part of Ribbon2 news message system.
 * 
 * Copyright (C) 2020 Freax Software
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
package tk.freaxsoftware.ribbon2.message.service;

import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tk.freaxsoftware.extras.bus.MessageBus;
import tk.freaxsoftware.extras.bus.MessageOptions;
import tk.freaxsoftware.ribbon2.core.data.DirectoryCheckAccessRequest;
import tk.freaxsoftware.ribbon2.core.exception.CoreException;
import tk.freaxsoftware.ribbon2.message.entity.Directory;
import tk.freaxsoftware.ribbon2.message.entity.Message;
import tk.freaxsoftware.ribbon2.message.repo.DirectoryRepository;
import tk.freaxsoftware.ribbon2.message.repo.MessageRepository;

/**
 * Message service.
 * @author Stanislav Nepochatov
 */
public class MessageService {
    
    private final static Logger LOGGER = LoggerFactory.getLogger(MessageService.class);
    
    private final DirectoryRepository directoryRepository;
    private final MessageRepository messageRepository;

    public MessageService(DirectoryRepository directoryRepository, MessageRepository messageRepository) {
        this.directoryRepository = directoryRepository;
        this.messageRepository = messageRepository;
    }
    
    /**
     * Creates message in specified directories.
     * @param message message to create;
     * @param user current user login;
     * @return created message;
     */
    public Message createMessage(Message message, String user) {
        LOGGER.info("Create message {} on directories {} by user {}", 
                message.getHeader(), message.getDirectoryNames(), user);
        message.setUid(UUID.randomUUID().toString());
        message.setCreated(ZonedDateTime.now());
        message.setCreatedBy(user);
        message.setDirectories(linkDirectories(message.getDirectoryNames()));
        checkDirectoryAccess(user, message.getDirectoryNames(), Message.PERMISSION_CAN_CREATE_MESSAGE);
        return messageRepository.save(message);
    }
    
    /**
     * Updates existing message after checking it.
     * @param message message to update;
     * @param user current user login;
     * @return updated message;
     */
    public Message updateMessage(Message message, String user) {
        LOGGER.info("Update message {} on directories {} by user {}", 
                message.getUid(), message.getDirectoryNames(), user);
        Message existingMessage = messageRepository.findByUid(message.getUid());
        if (existingMessage != null) {
            existingMessage.setHeader(message.getHeader());
            existingMessage.setContent(message.getContent());
            existingMessage.setTags(message.getTags());
            existingMessage.setDirectories(linkDirectories(message.getDirectoryNames()));
            existingMessage.setDirectoryNames(message.getDirectoryNames());
            existingMessage.setUpdated(ZonedDateTime.now());
            existingMessage.setCreatedBy(user);
            checkDirectoryAccess(user, message.getDirectoryNames(), Message.PERMISSION_CAN_UPDATE_MESSAGE);
            return messageRepository.save(existingMessage);
        }
        throw new CoreException("MESSAGE_NOT_FOUND", 
                String.format("Message by UID %s not found!", message.getUid()));
    }
    
    public Message deleteMessage(String uid, String user) {
        LOGGER.info("Delete message {} by user {}", uid, user);
        Message existingMessage = messageRepository.findByUid(uid);
        if (existingMessage != null) {
            LOGGER.warn("Deleting message {} from directories: {}", 
                    existingMessage.getUid(), existingMessage.getDirectoryNames());
            checkDirectoryAccess(user, existingMessage.getDirectoryNames(), Message.PERMISSION_CAN_DELETE_MESSAGE);
            existingMessage.delete();
            return existingMessage;
        }
        throw new CoreException("MESSAGE_NOT_FOUND", 
                String.format("Message by UID %s not found!", uid));
    }
    
    private void checkDirectoryAccess(String user, Set<String> directories, String permission) {
        LOGGER.info("Checking access for directories {} for user {} by permission.", directories, user, permission);
        DirectoryCheckAccessRequest request = new DirectoryCheckAccessRequest(user, permission, directories);
        try {
            Boolean result = MessageBus.fireCall(DirectoryCheckAccessRequest.CALL_CHECK_DIR_ACCESS, request, MessageOptions.Builder.newInstance().deliveryCall().build(), Boolean.class);
            if (result) {
                return;
            }
        } catch (Exception ex) {
            throw new CoreException("CALL_ERROR", ex.getMessage());
        }
        throw new CoreException("ILLEGAL_ACCESS", String.format("User %s doesn't have access for current operation.", user));
    }
    
    private Set<Directory> linkDirectories(Set<String> directoryNames) {
        Set<Directory> directories = new HashSet<>();
        if (directoryNames == null || (directoryNames != null && directoryNames.isEmpty())) {
            throw new CoreException("NO_DIR_SPECIFIED", "Can't create message without directories.");
        }
        for (String directoryName: directoryNames) {
            Directory finded = directoryRepository.findByFullName(directoryName);
            if (finded != null) {
                directories.add(finded);
            } else {
                throw new CoreException("DIR_NOT_FOUND", 
                        String.format("Directory %s not found!", directoryName));
            }
        }
        return directories;
    }

    
}
