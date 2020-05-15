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
package tk.freaxsoftware.ribbon2.message.entity.converters;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import tk.freaxsoftware.ribbon2.core.data.MessageModel;
import tk.freaxsoftware.ribbon2.core.data.convert.TwoWayConverter;
import tk.freaxsoftware.ribbon2.core.exception.CoreException;
import tk.freaxsoftware.ribbon2.message.entity.Directory;
import tk.freaxsoftware.ribbon2.message.entity.Message;
import tk.freaxsoftware.ribbon2.message.repo.DirectoryRepository;

/**
 * Message converter.
 * @author Stanislav Nepochatov
 */
public class MessageConverter implements TwoWayConverter<MessageModel, Message> {
    
    private final DirectoryRepository directoryRepository;

    public MessageConverter(DirectoryRepository directoryRepository) {
        this.directoryRepository = directoryRepository;
    }

    @Override
    public MessageModel convertBack(Message destination) {
        MessageModel model = new MessageModel();
        model.setId(destination.getId());
        model.setUid(destination.getUid());
        model.setParentUid(destination.getParentUid());
        model.setCreated(destination.getCreated());
        model.setCreatedBy(destination.getCreatedBy());
        model.setUpdated(destination.getUpdated());
        model.setUpdatedBy(destination.getUpdatedBy());
        model.setHeader(destination.getHeader());
        model.setContent(destination.getContent());
        model.setTags(destination.getTags());
        model.setDirectories(destination.getDirectories().stream()
                .map(dir -> dir.getFullName()).collect(Collectors.toSet()));
        return model;
    }

    @Override
    public Message convert(MessageModel source) {
        Message message = new Message();
        message.setId(source.getId());
        message.setUid(source.getUid());
        message.setParentUid(source.getParentUid());
        message.setCreated(source.getCreated());
        message.setCreatedBy(source.getCreatedBy());
        message.setUpdated(source.getUpdated());
        message.setUpdatedBy(source.getUpdatedBy());
        message.setHeader(source.getHeader());
        message.setContent(source.getContent());
        message.setTags(source.getTags());
        message.setDirectories(prepareDirectories(source.getDirectories()));
        return message;
    }
    
    private Set<Directory> prepareDirectories(Set<String> directoryNames) {
        Set<Directory> directories = new HashSet<>();
        for (String directoryName: directoryNames) {
            Directory finded = directoryRepository.findByFullName(directoryName);
            if (finded != null) {
                directories.add(finded);
            } else {
                throw new CoreException("NO_DIRECTORY_FOUND", 
                        String.format("Directory %s not found!", directoryName));
            }
        }
        return directories;
    }
}
