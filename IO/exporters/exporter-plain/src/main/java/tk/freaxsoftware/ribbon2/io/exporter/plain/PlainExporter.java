/*
 * This file is part of Ribbon2 news message system.
 * 
 * Copyright (C) 2021 Freax Software
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
package tk.freaxsoftware.ribbon2.io.exporter.plain;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tk.freaxsoftware.ribbon2.core.data.MessageModel;
import tk.freaxsoftware.ribbon2.io.core.IOExceptionCodes;
import tk.freaxsoftware.ribbon2.io.core.IOModule;
import tk.freaxsoftware.ribbon2.io.core.IOScheme;
import tk.freaxsoftware.ribbon2.io.core.InputOutputException;
import tk.freaxsoftware.ribbon2.io.core.exporter.Exporter;

/**
 * Plain text exporter for debug purposes.
 * @author Stanislav Nepochatov
 */
@IOModule(id = "export:plain", name = "Plain text plain", 
        protocol = "plain", requiredConfigKeys = {"plainFolderPath", "plainFileExtension"})
public class PlainExporter implements Exporter {
    
    private final static Logger LOGGER = LoggerFactory.getLogger(PlainExporter.class);

    @Override
    public String export(MessageModel message, IOScheme scheme) {
        String fileName = String.format("%s.%s", message.getUid(), scheme.getConfig().get("plainFileExtension"));
        LOGGER.info("Writing message {} to file {}", message.getUid(), fileName);
        StringBuffer messageBuffer = new StringBuffer();
        messageBuffer.append(message.getUid());
        messageBuffer.append('\n');
        messageBuffer.append(message.getHeader());
        messageBuffer.append('\n');
        messageBuffer.append('\n');
        messageBuffer.append(message.getContent());
        messageBuffer.append('\n');
        messageBuffer.append(message.getCreated().toString());
        try {
            Files.write(Paths.get(scheme.getConfig().get("plainFolderPath") + "/" + fileName), messageBuffer.toString().getBytes());
        } catch (IOException ioex) {
            LOGGER.error("Error during export of message " + message.getUid(), ioex);
            throw new InputOutputException(IOExceptionCodes.EXPORT_ERROR, String.format("Error during export of message %s: %s", message.getUid(), ioex.getMessage()));
        }
        return fileName;
    }
}
