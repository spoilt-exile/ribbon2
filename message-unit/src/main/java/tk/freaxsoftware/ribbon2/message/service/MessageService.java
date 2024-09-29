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

import io.ebean.PagedList;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tk.freaxsoftware.extras.bus.GlobalCons;
import tk.freaxsoftware.extras.bus.MessageBus;
import tk.freaxsoftware.extras.bus.MessageOptions;
import tk.freaxsoftware.extras.bus.storage.StorageInterceptor;
import tk.freaxsoftware.ribbon2.core.data.DirectoryModel;
import tk.freaxsoftware.ribbon2.core.data.MessagePropertyModel;
import tk.freaxsoftware.ribbon2.core.data.convert.GsonCopier;
import tk.freaxsoftware.ribbon2.core.data.request.DirectoryCheckAccessRequest;
import tk.freaxsoftware.ribbon2.core.data.request.PaginationRequest;
import tk.freaxsoftware.ribbon2.core.data.response.DirectoryPage;
import tk.freaxsoftware.ribbon2.core.exception.CoreException;
import static tk.freaxsoftware.ribbon2.core.exception.RibbonErrorCodes.ACCESS_DENIED;
import static tk.freaxsoftware.ribbon2.core.exception.RibbonErrorCodes.CALL_ERROR;
import static tk.freaxsoftware.ribbon2.core.exception.RibbonErrorCodes.DIRECTORY_NOT_FOUND;
import static tk.freaxsoftware.ribbon2.core.exception.RibbonErrorCodes.MESSAGE_NOT_FOUND;
import static tk.freaxsoftware.ribbon2.core.exception.RibbonErrorCodes.PROPERTY_TYPE_NOT_FOUND;
import tk.freaxsoftware.ribbon2.message.MessengerUnit;
import tk.freaxsoftware.ribbon2.message.entity.Directory;
import tk.freaxsoftware.ribbon2.message.entity.Message;
import tk.freaxsoftware.ribbon2.message.entity.PropertyType;
import tk.freaxsoftware.ribbon2.message.repo.DirectoryRepository;
import tk.freaxsoftware.ribbon2.message.repo.MessageRepository;
import tk.freaxsoftware.ribbon2.message.repo.PropertyTypeRepository;
import static tk.freaxsoftware.ribbon2.core.exception.RibbonErrorCodes.MESSAGE_DIRECTORIES_REQUIRED;

/**
 * Message service.
 * @author Stanislav Nepochatov
 */
public class MessageService {
    
    private final static Logger LOGGER = LoggerFactory.getLogger(MessageService.class);
    
    private final DirectoryRepository directoryRepository;
    private final MessageRepository messageRepository;
    private final PropertyTypeRepository propertyTypeRepository;
    private final Map<String, Instant> permissionCache;

    public MessageService(DirectoryRepository directoryRepository, 
            MessageRepository messageRepository, 
            PropertyTypeRepository propertyTypeRepository) {
        this.directoryRepository = directoryRepository;
        this.messageRepository = messageRepository;
        this.propertyTypeRepository = propertyTypeRepository;
        if (MessengerUnit.config.getMessenger().getEnablePermissionCaching()) {
            permissionCache = new ConcurrentHashMap<>();
        } else {
            permissionCache = Collections.EMPTY_MAP;
        }
        MessageBus.addSubscription(GlobalCons.G_SUBSCRIBE_TOPIC, (holder) -> {
            String topic = (String) holder.getHeaders().get(GlobalCons.G_SUBSCRIPTION_DEST_HEADER);
            if (DirectoryModel.CALL_GET_DIRECTORY_ALL.equals(topic)) {
                LOGGER.info("Cross connection to directory unit detected, syncing directories");
                syncDirectories();
            }
        });
    }
    
    /**
     * Creates message in specified directories.
     * @param message message to create;
     * @param user current user login;
     * @return created message;
     */
    public Message createMessage(Message message, String user) {
        LOGGER.info("Create message {} on directories {} by user {}", 
                message.getHeader(), message.getDirectories(), user);
        if (message.getParentUid() != null && !message.getParentUid().isEmpty()) {
            Message parentMessage = messageRepository.findByUid(message.getParentUid());
            if (parentMessage == null) {
                throw new CoreException(MESSAGE_NOT_FOUND, 
                        String.format("Parent message by UID %s not found!", message.getUid()));
            }
            LOGGER.info("New message is linked to existing message '{}'", parentMessage.getHeader());
        }
        message.setUid(UUID.randomUUID().toString());
        message.setCreated(ZonedDateTime.now());
        message.setCreatedBy(user);
        message.getProperties().forEach(pr -> {
            checkPropertyType(pr.getType());
            pr.setCreated(ZonedDateTime.now());
            pr.setCreatedBy(user);
            pr.setUid(UUID.randomUUID().toString());
        });
        verifyDirectories(message.getDirectories());
        checkDirectoryAccess(user, message.getDirectories(), Message.PERMISSION_CAN_CREATE_MESSAGE);
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
                message.getUid(), message.getDirectories(), user);
        Message existingMessage = messageRepository.findByUid(message.getUid());
        if (existingMessage != null) {
            existingMessage.setHeader(message.getHeader());
            existingMessage.setContent(message.getContent());
            existingMessage.setTags(message.getTags());
            existingMessage.setDirectories(message.getDirectories());
            existingMessage.setUpdated(ZonedDateTime.now());
            existingMessage.setUpdatedBy(user);
            message.getProperties().forEach(pr -> {
                if (!existingMessage.getProperties().contains(pr)) {
                    checkPropertyType(pr.getType());
                    pr.setCreated(ZonedDateTime.now());
                    pr.setCreatedBy(user);
                    pr.setUid(UUID.randomUUID().toString());
                    existingMessage.getProperties().add(pr);
                }
            });
            checkDirectoryAccess(user, message.getDirectories(), Message.PERMISSION_CAN_UPDATE_MESSAGE);
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
                    existingMessage.getUid(), existingMessage.getDirectories());
            checkDirectoryAccess(user, existingMessage.getDirectories(), Message.PERMISSION_CAN_DELETE_MESSAGE);
            final Message existingCopy = GsonCopier.copy(existingMessage);
            existingMessage.delete();
            return existingCopy;
        }
        throw new CoreException(MESSAGE_NOT_FOUND, 
                String.format("Message by UID %s not found!", uid));
    }
    
    /**
     * Finds page of messages by directory.
     * @param user current user login;
     * @param directory name of the directory;
     * @param request pagination request;
     * @return paged list of messages;
     */
    public PagedList<Message> findPage(String user, String directory, PaginationRequest request) {
        LOGGER.info("Find messages paged: directory {}, user {}, request {}", directory, user, request);
        checkDirectoryAccess(user, Set.of(directory), Message.PERMISSION_CAN_READ_MESSAGE);
        return messageRepository.findPage(directory, request);
    }
    
    /**
     * Finds message by uid and directory.
     * @param user current user login;
     * @param directory name of the directory;
     * @param uid message identeficator;
     * @return finded message;
     */
    public Message findMessage(String user, String directory, String uid) {
        LOGGER.info("Find message: directory {}, user {}, uid {}", directory, user, uid);
        checkDirectoryAccess(user, Set.of(directory), Message.PERMISSION_CAN_READ_MESSAGE);
        Message finded = messageRepository.findByUid(uid);
        if (finded == null) {
            throw new CoreException(MESSAGE_NOT_FOUND, 
                    String.format("Message by UID %s not found!", uid));
        }
        return finded;
    }
    
    /**
     * Adds property to the message.
     * @param user current user login;
     * @param uid message uid;
     * @param property message property to add;
     */
    public MessagePropertyModel addMessageProperty(String user, String uid, MessagePropertyModel property) {
        LOGGER.info("Add property to message: property {}, user {}, uid {}", property.getType(), user, uid);
        checkPropertyType(property.getType());
        Message finded = messageRepository.findByUid(uid);
        if (finded == null) {
            throw new CoreException(MESSAGE_NOT_FOUND, 
                    String.format("Message by UID %s not found!", uid));
        }
        property.setCreated(ZonedDateTime.now());
        property.setCreatedBy(user);
        property.setUid(UUID.randomUUID().toString());
        finded.getProperties().add(property);
        messageRepository.save(finded);
        return property;
    }
    
    private void checkPropertyType(String type) {
        PropertyType propType = propertyTypeRepository.findByType(type);
        if (propType == null) {
            throw new CoreException(PROPERTY_TYPE_NOT_FOUND, 
                    String.format("Property type %s not registered!", type));
        }
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
    
    private void verifyDirectories(Set<String> directoryNames) {
        if (directoryNames == null || (directoryNames != null 
                && directoryNames.isEmpty())) {
            throw new CoreException(MESSAGE_DIRECTORIES_REQUIRED, 
                    "Can't create message without directories.");
        }
        Set<Directory> foundDirectories = directoryRepository
                .findAllByFullName(directoryNames);
        Set<String> foundDirNames = foundDirectories.stream()
                .map(dir -> dir.getFullName())
                .collect(Collectors.toSet());
        if (!Objects.equals(directoryNames, foundDirNames)) {
            Set<String> notFoundDirNames = directoryNames.stream()
                    .filter(dirName -> !foundDirNames.contains(dirName))
                    .collect(Collectors.toSet());
            throw new CoreException(DIRECTORY_NOT_FOUND, 
                    String.format("Directories %s not found!", notFoundDirNames));
        }
    }
    
    /**
     * Syncs directory info with directory unit.
     */
    protected void syncDirectories() throws Exception {
        PaginationRequest request = new PaginationRequest();
        request.setPage(0);
        request.setSize(10000); //TODO: raise limit if needed
        request.setOrderBy("id");
        request.setDirection(PaginationRequest.Order.ASC);
        DirectoryPage page = MessageBus.fireCall(DirectoryModel.CALL_GET_DIRECTORY_ALL, request, MessageOptions.Builder.newInstance()
                .header(StorageInterceptor.IGNORE_STORAGE_HEADER, "true")
                .deliveryCall().build(), DirectoryPage.class);
        Set<Directory> existingDirs = directoryRepository.findAll();
        Set<String> existingDirNames = existingDirs.stream().map(dir -> dir.getFullName()).collect(Collectors.toSet());
        Set<String> actualDirNames = page.getContent().stream().map(dir -> dir.getFullName()).collect(Collectors.toSet());
        for (DirectoryModel actualDir: page.getContent()) {
            if (!existingDirNames.contains(actualDir.getFullName())) {
                Directory newDir = new Directory();
                newDir.setFullName(actualDir.getFullName());
                directoryRepository.save(newDir);
            }
        }
        //TODO make clean deletion and relocation of directory
        existingDirs.stream().filter(dir -> !actualDirNames.contains(dir.getFullName())).forEach(delDir -> delDir.delete());
    }
}
