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

import tk.freaxsoftware.ribbon2.io.core.ConfigEntryType;
import tk.freaxsoftware.ribbon2.io.core.IOModule;
import tk.freaxsoftware.ribbon2.io.core.IOScheme;
import tk.freaxsoftware.ribbon2.io.core.importer.ImportSource;
import tk.freaxsoftware.ribbon2.io.core.importer.Importer;

/**
 * Mail message importer by POP3 protocol.
 * @author Stanislav Nepochatov
 */
@IOModule(id = "import:mail", name = "Mail message importer by POP3 protocol", 
        protocol = "mail",
        configEntries = {
            @IOModule.ConfigEntry(name = "mailPop3Address", type = ConfigEntryType.TEXT, required = true),
            @IOModule.ConfigEntry(name = "mailPop3Port", type = ConfigEntryType.NUMBER),
            @IOModule.ConfigEntry(name = "mailPop3Login", type = ConfigEntryType.TEXT, required = true),
            @IOModule.ConfigEntry(name = "mailPop3Password", type = ConfigEntryType.SECRET, required = true),
            @IOModule.ConfigEntry(name = "mailPop3Security", type = ConfigEntryType.TEXT, defaultValue = "NONE"),
            @IOModule.ConfigEntry(name = "mailPop3Debug", type = ConfigEntryType.BOOLEAN, defaultValue = "false"),
            @IOModule.ConfigEntry(name = "mailPop3PostAction", type = ConfigEntryType.TEXT, defaultValue = "DELETE"),
            @IOModule.ConfigEntry(name = "mailReadFromList", type = ConfigEntryType.TEXT),
            @IOModule.ConfigEntry(name = "mailDirectoryList", type = ConfigEntryType.TEXT),
            @IOModule.ConfigEntry(name = "mailCopyright", type = ConfigEntryType.TEXT),
            @IOModule.ConfigEntry(name = "mailSendReportBack", type = ConfigEntryType.BOOLEAN, defaultValue = "false"),
            @IOModule.ConfigEntry(name = "mailReportAdminAddress", type = ConfigEntryType.TEXT),
            @IOModule.ConfigEntry(name = "mailSmtpAddress", type = ConfigEntryType.TEXT),
            @IOModule.ConfigEntry(name = "mailSmtpLogin", type = ConfigEntryType.TEXT),
            @IOModule.ConfigEntry(name = "mailSmtpPassword", type = ConfigEntryType.SECRET),
            @IOModule.ConfigEntry(name = "mailSmtpPort", type = ConfigEntryType.NUMBER),
            @IOModule.ConfigEntry(name = "mailSmtpSecurity", type = ConfigEntryType.TEXT, defaultValue = "NONE"),
            @IOModule.ConfigEntry(name = "mailSmtpFrom", type = ConfigEntryType.TEXT),
            @IOModule.ConfigEntry(name = "generalDirectory", type = ConfigEntryType.DIRECTORY),
            @IOModule.ConfigEntry(name = "generalTimeout", type = ConfigEntryType.NUMBER, defaultValue = "5")
        })
public class MailImporter implements Importer {

    @Override
    public ImportSource createSource(IOScheme scheme) {
        return new MailImportSource(scheme);
    }

}
