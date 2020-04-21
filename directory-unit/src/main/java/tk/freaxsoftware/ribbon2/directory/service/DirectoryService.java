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
package tk.freaxsoftware.ribbon2.directory.service;

import tk.freaxsoftware.extras.bus.MessageHolder;
import tk.freaxsoftware.extras.bus.annotation.Receive;
import tk.freaxsoftware.ribbon2.core.data.Directory;

/**
 * Directory service to receive calls from gateway.
 * @author Stanislav Nepochatov
 */
public class DirectoryService {
    
    @Receive(Directory.CALL_CREATE_DIRECTORY)
    public void createDirectory(MessageHolder<Directory> createMessage) {
        
    }
    
    @Receive(Directory.CALL_UPDATE_DIRECTORY)
    public void updateDirectory(MessageHolder<Directory> updateMessage) {
        
    }
    
    @Receive(Directory.CALL_DELETE_DIRECTORY)
    public void deleteDirectory(MessageHolder<Long> deleteMessage) {
        
    }
    
}
