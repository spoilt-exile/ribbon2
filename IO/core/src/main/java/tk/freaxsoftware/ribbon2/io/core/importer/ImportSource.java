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

import java.util.List;
import tk.freaxsoftware.ribbon2.io.core.IOScheme;

/**
 * Represents any import message source. Provides methods for the handling import queue.
 * @author Stanislav Nepochatov
 */
public interface ImportSource {
    
    /**
     * Get current scheme.
     * @return current import scheme;
     */
    IOScheme getScheme();
    
    /**
     * Get list of new unread messages from source.
     * @return list of messages to process;
     */
    List<ImportMessage> getUnreadMessages();
    
}