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

import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tk.freaxsoftware.extras.bus.MessageBus;
import tk.freaxsoftware.extras.bus.MessageOptions;
import tk.freaxsoftware.extras.bus.storage.StorageInterceptor;
import tk.freaxsoftware.ribbon2.core.data.request.DirectoryCheckAccessRequest;
import tk.freaxsoftware.ribbon2.core.exception.CoreException;
import static tk.freaxsoftware.ribbon2.core.exception.RibbonErrorCodes.ACCESS_DENIED;
import static tk.freaxsoftware.ribbon2.core.exception.RibbonErrorCodes.CALL_ERROR;
import static tk.freaxsoftware.ribbon2.core.exception.RibbonErrorCodes.DIRECTORY_NOT_FOUND;
import static tk.freaxsoftware.ribbon2.core.exception.RibbonErrorCodes.MESSAGE_DIRECORIES_REQUIRED;
import static tk.freaxsoftware.ribbon2.core.exception.RibbonErrorCodes.MESSAGE_NOT_FOUND;
import tk.freaxsoftware.ribbon2.message.MessengerUnit;
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
    private final Map<String, Instant> permissionCache;

    public MessageService(DirectoryRepository directoryRepository, MessageRepository messageRepository) {
        this.directoryRepository = directoryRepository;
        this.messageRepository = messageRepository;
        if (MessengerUnit.config.getMessenger().getEnablePermissionCaching()) {
            permissionCache = new ConcurrentHashMap<>();
        } else {
            permissionCache = Collections.EMPTY_MAP;
        }
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
        throw new CoreException(MESSAGE_NOT_FOUND, 
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
        throw new CoreException(MESSAGE_NOT_FOUND, 
                String.format("Message by UID %s not found!", uid));
    }
    
    private void checkDirectoryAccess(String user, Set<String> directories, String permission) {
        LOGGER.info("Checking access for directories {} for user {} by permission.", directories, user, permission);
        DirectoryCheckAccessRequest request = processByCache(new DirectoryCheckAccessRequest(user, permission, directories));
        if (request.getDirectories().isEmpty()) {
            return;
        }
        try {
            Boolean result = MessageBus.fireCall(DirectoryCheckAccessRequest.CALL_CHECK_DIR_ACCESS, 
                    request, MessageOptions.Builder.newInstance().header(StorageInterceptor.IGNORE_STORAGE_HEADER, "true").deliveryCall().build(), Boolean.class);
            if (result) {
                addToCache(request);
                return;
            }
        } catch (Exception ex) {
            throw new CoreException(CALL_ERROR, ex.getMessage());
        }
        throw new CoreException(ACCESS_DENIED, String.format("User %s doesn't have access for current operation.", user));
    }
    
    private DirectoryCheckAccessRequest processByCache(DirectoryCheckAccessRequest request) {
        if (MessengerUnit.config.getMessenger().getEnablePermissionCaching()) {
            Instant now = Instant.now();
            Set<String> processedDirs = new HashSet<>();
            for (String directory: request.getDirectories()) {
                Instant expiry = permissionCache.get(getCacheKey(directory, request.getPermission(), request.getUser()));
                if (expiry == null || (expiry != null && !expiry.isAfter(now))) {
                    processedDirs.add(directory);
                    permissionCache.remove(getCacheKey(directory, request.getPermission(), request.getUser()));
                }
            }
            request.setDirectories(processedDirs);
        }
        return request;
    }
    
    private void addToCache(DirectoryCheckAccessRequest request) {
        if (MessengerUnit.config.getMessenger().getEnablePermissionCaching()) {
            Instant expiry = Instant.now().plus(MessengerUnit.config.getMessenger().getPermissionCacheExpiry(), ChronoUnit.MINUTES);
            for (String directory: request.getDirectories()) {
                String key = getCacheKey(directory, request.getPermission(), request.getUser());
                if (!permissionCache.containsKey(key)) {
                    permissionCache.put(key, expiry);
                }
            }
        }
    }
    
    private String getCacheKey(String directory, String permission, String user) {
        return String.format("%s@%s@%s", user, permission, directory);
    }
    
    private Set<Directory> linkDirectories(Set<String> directoryNames) {
        Set<Directory> directories = new HashSet<>();
        if (directoryNames == null || (directoryNames != null && directoryNames.isEmpty())) {
            throw new CoreException(MESSAGE_DIRECORIES_REQUIRED, "Can't create message without directories.");
        }
        for (String directoryName: directoryNames) {
            Directory finded = directoryRepository.findByFullName(directoryName);
            if (finded != null) {
                directories.add(finded);
            } else {
                throw new CoreException(DIRECTORY_NOT_FOUND, 
                        String.format("Directory %s not found!", directoryName));
            }
        }
        return directories;
    }

    
}
