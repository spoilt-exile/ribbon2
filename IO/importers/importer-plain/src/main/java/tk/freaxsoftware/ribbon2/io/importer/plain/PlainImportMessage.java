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
package tk.freaxsoftware.ribbon2.io.importer.plain;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tk.freaxsoftware.ribbon2.core.data.MessageModel;
import tk.freaxsoftware.ribbon2.io.core.IOExceptionCodes;
import tk.freaxsoftware.ribbon2.io.core.InputOutputException;
import tk.freaxsoftware.ribbon2.io.core.importer.ImportMessage;

/**
 * Plain text message.
 * @author Stanislav Nepochatov
 */
public class PlainImportMessage implements ImportMessage {
    
    private final static Logger LOGGER = LoggerFactory.getLogger(PlainImportSource.class);
    
    private final Path filePath;

    public PlainImportMessage(Path filePath) {
        this.filePath = filePath;
    }

    @Override
    public MessageModel getMessage() {
        LOGGER.info("Reading content of {} file", filePath.getFileName());
        try {
            MessageModel message = new MessageModel();
            message.setHeader(filePath.getFileName().toString());
            message.setContent(Files.readString(filePath));
            return message;
        } catch (IOException ex) {
            throw new InputOutputException(IOExceptionCodes.IMPORT_ERROR, "Unable to read file from source", ex);
        }
    }

    @Override
    public void markAsRead() {
        LOGGER.info("Deleting file {}", filePath.getFileName());
        try {
            Files.delete(filePath);
        } catch (IOException ex) {
            throw new InputOutputException(IOExceptionCodes.MARK_ERROR, "Unable to delete file from source", ex);
        }
    }

    @Override
    public String getId() {
        return filePath.getFileName().toString();
    }

    @Override
    public String getHeader() {
        return filePath.getFileName().toString();
    }
    
}
