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
package tk.freaxsoftware.ribbon2.io.core;

/**
 * Exception codes for IO exception.
 * @author Stanislav Nepochatov
 */
public enum IOExceptionCodes {
    
    /**
     * Error happend during extracting content from the source.
     */
    IMPORT_ERROR,
    
    /**
     * Error happend during checking for unread messages in the source.
     */
    IMPORT_CHECK_ERROR,
    
    /**
     * Error happend on processing messages after import.
     */
    PROCESSING_ERROR,
    
    /**
     * Error happend on marking messages as read.
     */
    MARK_ERROR,
    
    /**
     * Error happend during exporting content to external system.
     */
    EXPORT_ERROR;
    
}
