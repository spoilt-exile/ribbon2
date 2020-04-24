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
package tk.freaxsoftware.ribbon2.directory.facade;

import java.util.Set;
import tk.freaxsoftware.extras.bus.MessageHolder;
import tk.freaxsoftware.extras.bus.ResponseHolder;
import tk.freaxsoftware.extras.bus.annotation.Receive;
import tk.freaxsoftware.ribbon2.core.data.DirectoryModel;
import tk.freaxsoftware.ribbon2.directory.entity.Directory;
import tk.freaxsoftware.ribbon2.directory.entity.converters.DirectoryConverter;
import tk.freaxsoftware.ribbon2.directory.service.DirectoryService;

/**
 * Directory service to receive calls from gateway.
 * @author Stanislav Nepochatov
 */
public class DirectoryFacade {
    
    private final DirectoryConverter converter = new DirectoryConverter();
    
    private final DirectoryService directoryService;

    public DirectoryFacade(DirectoryService directoryService) {
        this.directoryService = directoryService;
    }
    
    @Receive(DirectoryModel.CALL_CREATE_DIRECTORY)
    public void createDirectory(MessageHolder<DirectoryModel> createMessage) {
        String userLogin = directoryService.getAuthFromHeader(createMessage);
        Directory directory = converter.convert(createMessage.getContent());
        Directory saved = directoryService.createDirectory(directory, userLogin);
        createMessage.setResponse(new ResponseHolder());
        createMessage.getResponse().setContent(converter.convertBack(saved));
    }
    
    @Receive(DirectoryModel.CALL_UPDATE_DIRECTORY)
    public void updateDirectory(MessageHolder<DirectoryModel> updateMessage) {
        
    }
    
    @Receive(DirectoryModel.CALL_DELETE_DIRECTORY)
    public void deleteDirectory(MessageHolder<Long> deleteMessage) {
        
    }
}
