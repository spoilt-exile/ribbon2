/*
 * This file is part of Ribbon2 news message system.
 * 
 * Copyright (C) 2020-2024 Freax Software
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

import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;
import tk.freaxsoftware.ribbon2.io.importer.telegram.TelegramBot;
import tk.freaxsoftware.ribbon2.io.importer.telegram.TelegramImportMessage;

/**
 * Command to begin receiving urgent message.
 * @author Stanislav Nepochatov
 */
public class UrgentMessageCommand extends BotCommand {
    
    private final static Logger LOGGER = LoggerFactory.getLogger(MessageCommand.class);
    
    private final TelegramBot bot;

    public UrgentMessageCommand(TelegramBot bot) {
        super("urgentmessage", "Begin to recieve urgent message");
        this.bot = bot;
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] arguments) {
        String userName = TelegramBot.getUserName(user);
        LOGGER.info("Start to recieving urgent message from user {} on chat {}", userName, chat.getId());
        TelegramImportMessage message = new TelegramImportMessage(UUID.randomUUID().toString(), userName);
        bot.addMessageToQueue(message, chat.getId());
        bot.sendAnswer(absSender, chat.getId(), "/urgent_message", userName, message.getStatus().getLabel());
    }
}
