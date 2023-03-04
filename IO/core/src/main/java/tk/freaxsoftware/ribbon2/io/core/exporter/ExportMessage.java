/*
 * This file is part of Ribbon2 news message system.
 * 
 * Copyright (C) 2020-2022 Freax Software
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
package tk.freaxsoftware.ribbon2.io.core.exporter;

import tk.freaxsoftware.ribbon2.core.data.MessageModel;
import tk.freaxsoftware.ribbon2.io.core.IOMessage;
import tk.freaxsoftware.ribbon2.io.core.IOScheme;

/**
 * Export message methods interface.
 * @author Stanislav Nepochatov
 */
public interface ExportMessage extends IOMessage {
    
    /**
     * Get export scheme with config.
     * @return export io scheme;
     */
    IOScheme getExportScheme();
    
    /**
     * Get message model instance to export.
     * @return message model to export;
     */
    MessageModel getMessage();
    
    /**
     * Get actual text content to export.
     * @return text content of the message;
     */
    String getExportContent();
    
    /**
     * Returns flag if message content were processed by template.
     * @return true if content were processed / false if export message contains raw content;
     */
    Boolean isContentProcessed();
    
}
