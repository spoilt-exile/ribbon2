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
        return messageRepository.save(message);
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
