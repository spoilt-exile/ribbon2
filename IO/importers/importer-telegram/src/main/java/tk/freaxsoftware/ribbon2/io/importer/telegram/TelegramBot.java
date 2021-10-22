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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.extensions.bots.commandbot.TelegramLongPollingCommandBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import tk.freaxsoftware.ribbon2.io.importer.telegram.commands.HelpCommand;
import tk.freaxsoftware.ribbon2.io.importer.telegram.commands.MessageCommand;
import tk.freaxsoftware.ribbon2.io.importer.telegram.commands.StatusCommand;

/**
 * Telegram bot for importing messages in Ribbon2 system.
 * @author Stanislav Nepochatov
 */
public class TelegramBot extends TelegramLongPollingCommandBot {
    
    private final static Logger LOGGER = LoggerFactory.getLogger(TelegramBot.class);
    
    private final String botName;
    
    private final String botToken;
    
    private final TelegramImportSource source;
    
    private final Map<Long, TelegramImportMessage> messageQueue = new HashMap();
    
    private final Set<StatusRecord> statusRecords = new HashSet();

    /**
     * Create new telegram importer bot.
     * @param source import source;
     * @param botName name of the bot;
     * @param botToken bot's token;
     */
    public TelegramBot(TelegramImportSource source, String botName, String botToken) {
        super();
        LOGGER.info("Launching telegram bot {} for scheme {}", botName, source.getScheme().getName());
        this.botName = botName;
        this.botToken = botToken;
        this.source = source;
        
        register(new HelpCommand(this));
        register(new StatusCommand(this));
        register(new MessageCommand(this));
    }

    @Override
    public String getBotUsername() {
        return botName;
    }

    @Override
    public void processNonCommandUpdate(Update update) {
        Long chatId = update.getMessage().getChatId();
        if (messageQueue.containsKey(chatId)) {
            TelegramImportMessage message = messageQueue.get(chatId);
            LOGGER.info("Adding part to message {} with status {} for chat {} on scheme {}", 
                    message.getId(), message.getStatus(), chatId, source.getScheme().getName());
            message.addMessagePart(update.getMessage().getText());
            if (message.getStatus() == TelegramImportMessage.Status.COMPLETE) {
                LOGGER.info("Message {} receiving is complete for chat {} on scheme {}", message.getId(), chatId, source.getScheme().getName());
                source.addCompleteMessage(message);
                messageQueue.remove(chatId);
                StatusRecord statusRecord = new StatusRecord(chatId, message.getId(), message.getHeader());
                statusRecords.add(statusRecord);
                message.setStatusRecord(statusRecord);
            }
            sendAnswerFronNonCommand(chatId, null, message.getStatus().getLabel());
        } else {
            sendAnswerFronNonCommand(chatId, null, HelpCommand.HELP_CONTENT);
        }
    }

    @Override
    public String getBotToken() {
        return botToken;
    }
    
    public void sendAnswer(AbsSender absSender, Long chatId, String commandName, String userName, String text) {
        SendMessage message = new SendMessage();
        message.enableMarkdown(true);
        message.setChatId(chatId.toString());
        message.setText(text);
        try {
            absSender.execute(message);
        } catch (TelegramApiException ex) {
            LOGGER.error("Erorr during executing command {} for user {}: '{}' on scheme {}", 
                    commandName, userName, ex.getMessage(), source.getScheme().getName());
            LOGGER.error("Error stack trace:" , ex);
        }
    }
    
    public void sendAnswerFronNonCommand(Long chatId, String userName, String text) {
        SendMessage message = new SendMessage();
        message.enableMarkdown(true);
        message.setChatId(chatId.toString());
        message.setText(text);
        try {
            execute(message);
        } catch (TelegramApiException ex) {
            LOGGER.error("Erorr during executing nonCommand for user {}: '{}' on scheme {}", 
                    userName, ex.getMessage(), source.getScheme().getName());
            LOGGER.error("Error stack trace:" , ex);
        }
    }
    
    /**
     * Adds message to queue to be filled later.
     * @param message new message;
     * @param chatId id of the chat;
     */
    public void addMessageToQueue(TelegramImportMessage message, Long chatId) {
        LOGGER.info("Adding message {} to queue from chat {}");
        messageQueue.put(chatId, message);
    }
    
    /**
     * Gets all status records for specified chat and removes records of imported messages.
     * @param chatId id of the chat;
     * @return set of status records to return to user;
     */
    public Set<StatusRecord> getAndRemoveStatusRecords(Long chatId) {
        Set<StatusRecord> chatRecords = statusRecords.stream()
                .filter(st -> Objects.equals(st.getChatId(), chatId))
                .collect(Collectors.toSet());
        Set<StatusRecord> toRemove = chatRecords.stream()
                .filter(chr -> chr.getStatus() == StatusType.IMPORTED)
                .collect(Collectors.toSet());
        statusRecords.removeAll(toRemove);
        return chatRecords;
    }
    
    public static String getUserName(Message msg) {
        return getUserName(msg.getFrom());
    }

    public static String getUserName(User user) {
        return (user.getUserName() != null) ? user.getUserName() :
                String.format("%s %s", user.getLastName(), user.getFirstName());
    }
    
    /**
     * Status record of the message. Created after completion of receiving message contnet;
     */
    public static class StatusRecord {
        
        private Long chatId;
        
        private String id;
        
        private String header;
        
        private StatusType status;

        public StatusRecord(Long chatId, String id, String header) {
            this.chatId = chatId;
            this.id = id;
            this.header = header;
            this.status = StatusType.WAITING_IMPORT;
        }

        public Long getChatId() {
            return chatId;
        }

        public void setChatId(Long chatId) {
            this.chatId = chatId;
        }
        
        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getHeader() {
            return header;
        }

        public void setHeader(String header) {
            this.header = header;
        }

        public StatusType getStatus() {
            return status;
        }

        public void setStatus(StatusType status) {
            this.status = status;
        }

        @Override
        public int hashCode() {
            int hash = 5;
            hash = 37 * hash + Objects.hashCode(this.chatId);
            hash = 37 * hash + Objects.hashCode(this.id);
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final StatusRecord other = (StatusRecord) obj;
            if (!Objects.equals(this.id, other.id)) {
                return false;
            }
            if (!Objects.equals(this.chatId, other.chatId)) {
                return false;
            }
            return true;
        }

        @Override
        public String toString() {
            return "Message id='" + id + ", header='" + header + "', is " + status.getLabel();
        }
    }
    
    /**
     * Status type for records.
     */
    public static enum StatusType {
        WAITING_IMPORT("waiting for import."),
        IMPORTED("imported to system.");
        
        private String label;
        
        StatusType(String label) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }
    }
}
