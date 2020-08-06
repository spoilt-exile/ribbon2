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
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tk.freaxsoftware.ribbon2.io.core.IOScheme;
import tk.freaxsoftware.ribbon2.io.core.importer.ImportMessage;
import tk.freaxsoftware.ribbon2.io.core.importer.ImportSource;

/**
 * Plain text file import source.
 * @author Stanislav Nepochatov
 */
public class PlainImportSource implements ImportSource {
    
    private final static Logger LOGGER = LoggerFactory.getLogger(PlainImportSource.class);
    
    private final IOScheme scheme;

    public PlainImportSource(IOScheme scheme) {
        this.scheme = scheme;
    }

    @Override
    public IOScheme getScheme() {
        return scheme;
    }

    @Override
    public List<ImportMessage> getUnreadMessages() {
        List<ImportMessage> messages = new ArrayList();
        try (DirectoryStream<Path> dirStream = Files.newDirectoryStream(
                Paths.get(scheme.getConfig().get("plainFolderPath").toString()), 
                    scheme.getConfig().get("plainFileMask").toString())) {
            dirStream.forEach(path -> {
                LOGGER.info("Importing file message {}", path);
                messages.add(new PlainImportMessage(path));
            });
        } catch (IOException ex) {
            LOGGER.error("IO exceptio on import", ex);
        }
        return messages;
    }
    
}
