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

import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.BotSession;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import tk.freaxsoftware.ribbon2.io.core.IOExceptionCodes;
import tk.freaxsoftware.ribbon2.io.core.IOScheme;
import tk.freaxsoftware.ribbon2.io.core.InputOutputException;
import tk.freaxsoftware.ribbon2.io.core.importer.ImportMessage;
import tk.freaxsoftware.ribbon2.io.core.importer.ImportSource;

/**
 * Telegram importer source.
 * @author Stanislav Nepochatov
 */
public class TelegramImportSource implements ImportSource {
    
    private final Logger LOGGER = LoggerFactory.getLogger(TelegramImportSource.class);
    
    private final IOScheme scheme;
    
    private TelegramBot bot;
    
    private BotSession botSession;
    
    private List<TelegramImportMessage> messages = new ArrayList<>();

    public TelegramImportSource(IOScheme scheme) {
        this.scheme = scheme;
    }

    @Override
    public void open() {
        if (bot == null && botSession == null) {
            try {
                this.bot = new TelegramBot(this, (String) scheme.getConfig().get("tgBotName"), (String) scheme.getConfig().get("tgBotToken"));
                TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
                botsApi.registerBot(bot);
            } catch (TelegramApiException tex) {
                LOGGER.error("Error during telegram bot registration for scheme {}", scheme.getName());
                LOGGER.error("Error stack trace", tex);
                throw new InputOutputException(IOExceptionCodes.IMPORT_CHECK_ERROR, 
                        String.format("Error during telegram bot registration for scheme %s: %s", scheme.getName(), tex.getMessage()));
            }
        }
    }
    
    public void addCompleteMessage(TelegramImportMessage message) {
        messages.add(message);
    }

    @Override
    public void close() {
        messages.removeIf(message -> message.getStatus() == TelegramImportMessage.Status.TO_DELETE);
    }

    @Override
    public void onSuccess(ImportMessage message, String uid) {
        //Do nothing.
    }

    @Override
    public void onError(ImportMessage message, InputOutputException ex) {
        //Do nothing.
    }

    @Override
    public IOScheme getScheme() {
        return scheme;
    }

    @Override
    public List<ImportMessage> getUnreadMessages() {
        return (List) messages;
    }
    
}
