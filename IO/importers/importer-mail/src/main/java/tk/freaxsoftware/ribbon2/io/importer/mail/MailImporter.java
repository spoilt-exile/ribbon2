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
package tk.freaxsoftware.ribbon2.io.importer.mail;

import java.util.List;

import tk.freaxsoftware.ribbon2.io.core.IOModule;
import tk.freaxsoftware.ribbon2.io.core.IOScheme;
import tk.freaxsoftware.ribbon2.io.core.importer.ImportSource;
import tk.freaxsoftware.ribbon2.io.core.importer.Importer;

/**
 * Mail message importer by POP3 protocol.
 * @author Stanislav Nepochatov
 */
@IOModule(id = "import:mail", name = "Mail message importer by POP3 protocol", 
        protocol = "mail", requiredConfigKeys = {"mailPop3Address", "mailPop3Login", "mailPop3Password"})
public class MailImporter implements Importer {

    @Override
    public ImportSource createSource(IOScheme scheme) {
        return new MailImportSource(scheme);
    }

    @Override 
    public List<ConfigEntry> getConfigEntries() {
        return List.of(
            new ConfigEntry("mailPop3Address", EntryType.TEXT, true),
            new ConfigEntry("mailPop3Port", EntryType.NUMBER, false),
            new ConfigEntry("mailPop3Login", EntryType.TEXT, true),
            new ConfigEntry("mailPop3Password", EntryType.SECRET, true),
            new ConfigEntry("mailPop3Security", EntryType.TEXT, false),
            new ConfigEntry("mailPop3Debug", EntryType.BOOLEAN, false),
            new ConfigEntry("mailPop3PostAction", EntryType.TEXT, false),
            new ConfigEntry("mailReadFromList", EntryType.TEXT, false),
            new ConfigEntry("mailDirectoryList", EntryType.TEXT, false),
            new ConfigEntry("mailCopyright", EntryType.TEXT, false),
            new ConfigEntry("mailSendReportBack", EntryType.BOOLEAN, false),
            new ConfigEntry("mailReportAdminAddress", EntryType.TEXT, false),
            new ConfigEntry("mailSmtpAddress", EntryType.TEXT, false),
            new ConfigEntry("mailSmtpLogin", EntryType.TEXT, false),
            new ConfigEntry("mailSmtpPassword", EntryType.SECRET, false),
            new ConfigEntry("mailSmtpPort", EntryType.NUMBER, false),
            new ConfigEntry("mailSmtpSecurity", EntryType.TEXT, false),
            new ConfigEntry("mailSmtpFrom", EntryType.TEXT, false),
            new ConfigEntry("generelDirectory", EntryType.DIRECTORY, false),
            new ConfigEntry("generalTimeout", EntryType.NUMBER, false)
        );
    }
    
}
