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
package tk.freaxsoftware.ribbon2.io.importer.telegram;

import tk.freaxsoftware.ribbon2.io.core.IOModule;
import tk.freaxsoftware.ribbon2.io.core.IOScheme;
import tk.freaxsoftware.ribbon2.io.core.importer.ImportSource;
import tk.freaxsoftware.ribbon2.io.core.importer.Importer;

/**
 * Module for importing messages via telegram bot.
 * @author Stanislav Nepochatov
 */
@IOModule(id = "import:telegram", name = "Telegram bot importer", 
        protocol = "telegram", requiredConfigKeys = {"tgBotName", "tgBotToken", "generalDirectory"})
public class TelegramImporter implements Importer {

    @Override
    public ImportSource createSource(IOScheme scheme) {
        return new TelegramImportSource(scheme);
    }
    
}
