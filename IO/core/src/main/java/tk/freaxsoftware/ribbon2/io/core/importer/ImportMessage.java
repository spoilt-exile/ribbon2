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
package tk.freaxsoftware.ribbon2.io.core.importer;

import tk.freaxsoftware.ribbon2.core.data.MessageModel;
import tk.freaxsoftware.ribbon2.io.core.IOMessage;

/**
 * Import message main contract.
 * @author Stanislav Nepochatov
 */
public interface ImportMessage extends IOMessage {
    
    /**
     * Reads message from import source.
     * @return message model;
     */
    MessageModel getMessage();
    
    /**
     * Marks message in the import source as read. 
     * So it shouldn't appear on next run.
     */
    void markAsRead();
    
}
