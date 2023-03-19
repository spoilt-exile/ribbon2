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
package tk.freaxsoftware.ribbon2.message.facade;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tk.freaxsoftware.extras.bus.MessageHolder;
import tk.freaxsoftware.extras.bus.annotation.Receive;
import tk.freaxsoftware.ribbon2.core.data.DirectoryModel;
import tk.freaxsoftware.ribbon2.message.entity.Directory;
import tk.freaxsoftware.ribbon2.message.entity.converters.DirectoryConverter;
import tk.freaxsoftware.ribbon2.message.repo.DirectoryRepository;

/**
 * Directory facade to receive notification from directory unit.
 * @author Stanislav Nepochatov
 */
public class DirectoryFacade {
    
    private final static Logger LOGGER = LoggerFactory.getLogger(DirectoryFacade.class);
    
    private final DirectoryRepository directoryRepository;
    
    private final DirectoryConverter converter;

    public DirectoryFacade(DirectoryRepository directoryRepository, DirectoryConverter converter) {
        this.directoryRepository = directoryRepository;
        this.converter = converter;
    }
    
    @Receive(DirectoryModel.NOTIFICATION_DIRECTORY_CREATED)
    public void directoryCreated(MessageHolder<DirectoryModel> holder) {
        Directory created = converter.convert(holder.getContent());
        Directory existed = directoryRepository.findByFullName(created.getFullName());
        if (existed != null) {
            return;
        }
        LOGGER.info("Handling notification, directory {} created;", created.getFullName());
        directoryRepository.save(created);
    }
    
    @Receive(DirectoryModel.NOTIFICATION_DIRECTORY_UPDATED)
    public void directoryUpdated(MessageHolder<DirectoryModel> holder) {
        //Do nothing.
    }
    
    @Receive(DirectoryModel.NOTIFICATION_DIRECTORY_DELETED)
    public void directoryDeleted(MessageHolder<DirectoryModel> holder) {
        Directory deleted = converter.convert(holder.getContent());
        LOGGER.info("Handling notification, directory {} deleted;", deleted.getFullName());
        directoryRepository.deleteByFullname(holder.getContent().getFullName());
    }
    
}
