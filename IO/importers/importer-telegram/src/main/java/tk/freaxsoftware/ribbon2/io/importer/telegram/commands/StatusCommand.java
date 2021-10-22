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
package tk.freaxsoftware.ribbon2.io.importer.telegram.commands;

import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;
import tk.freaxsoftware.ribbon2.io.importer.telegram.TelegramBot;

/**
 * Messages status command.
 * @author Stanislav Nepochatov
 */
public class StatusCommand extends BotCommand {
    
    private final static Logger LOGGER = LoggerFactory.getLogger(StatusCommand.class);
    
    private final TelegramBot bot;

    public StatusCommand(TelegramBot bot) {
        super("status", "Show status of your messages");
        this.bot = bot;
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] arguments) {
        String userName = TelegramBot.getUserName(user);
        LOGGER.info("Get messages status for {} on chat {}", userName, chat.getId());
        Set<TelegramBot.StatusRecord> records = bot.getAndRemoveStatusRecords(chat.getId());
        if (records.isEmpty()) {
            bot.sendAnswer(absSender, chat.getId(), "/status", userName, "No messages yet");
        } else {
            String statusMessage = String.join("\n", records.stream().map(r -> r.toString()).collect(Collectors.toSet()));
            LOGGER.info(statusMessage);
            bot.sendAnswer(absSender, chat.getId(), "/status", userName, statusMessage);
        }
    }
}
