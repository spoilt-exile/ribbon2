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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;
import tk.freaxsoftware.ribbon2.io.importer.telegram.TelegramBot;

/**
 * Help command.
 * @author Stanislav Nepochatov
 */
public class HelpCommand extends BotCommand {
    
    private final static Logger LOGGER = LoggerFactory.getLogger(HelpCommand.class);
    
    private final TelegramBot bot;
    
    public static final String HELP_CONTENT = "Ribbon2 news information system importer bot\n\n"
            + "Commands:\n/help - display help information;\n"
            + "/urgentmessage - begin to receive urgent message step-by-step;\n"
            + "/message - begin to recieve message step-by-step;\n"
            + "/status - show status of your messages;\n"
            + "/cancel - cancel receiving message;";
    
    public HelpCommand(TelegramBot bot) {
        super("help", "Display help information");
        this.bot = bot;
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] arguments) {
        LOGGER.info("Command to display help from chat {}", chat.getId());
        String userName = TelegramBot.getUserName(user);
        bot.sendAnswer(absSender, chat.getId(), "/help", userName, HELP_CONTENT);
    }
    
}
