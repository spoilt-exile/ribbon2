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

import java.util.HashSet;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tk.freaxsoftware.ribbon2.core.data.MessageModel;
import tk.freaxsoftware.ribbon2.core.data.MessagePropertyModel;
import tk.freaxsoftware.ribbon2.io.core.IOExceptionCodes;
import tk.freaxsoftware.ribbon2.io.core.InputOutputException;
import tk.freaxsoftware.ribbon2.io.core.importer.ImportMessage;

/**
 * Telegram import message. It has internal status to help filling it up step-by-step.
 * @author Stanislav Nepochatov
 */
public class TelegramImportMessage implements ImportMessage {
    
    private final static Logger LOGGER = LoggerFactory.getLogger(TelegramImportMessage.class);
    
    public final static String MARK_READ_TOPIC_FORMAT = "Internal.MarkRead";
    
    private Status status = Status.HEADER_REQUIRED;
    private String id;
    private String userName;
    private String header;
    private Set<String> tags;
    private String content;
    private TelegramBot.StatusRecord statusRecord;

    public TelegramImportMessage(String id, String userName) {
        this.id = id;
        this.userName = userName;
    }

    @Override
    public MessageModel getMessage() {
        checkStatus();
        MessageModel message = new MessageModel();
        message.setHeader(header);
        message.setTags(tags);
        message.setContent(content);
        message.setProperties(new HashSet(Set.of(new MessagePropertyModel("COPYRIGHT", userName))));
        return message;
    }

    @Override
    public void markAsRead() {
        status = Status.TO_DELETE;
        statusRecord.setStatus(TelegramBot.StatusType.IMPORTED);
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getHeader() {
        checkStatus();
        return header;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatusRecord(TelegramBot.StatusRecord statusRecord) {
        this.statusRecord = statusRecord;
    }
    
    /**
     * Adds part of the message according to status.
     * @param part part of the message;
     */
    public Status addMessagePart(String part) {
        switch(status) {
            case HEADER_REQUIRED:
                header = part;
                status = Status.TAGS_REQUIRED;
                break;
            case TAGS_REQUIRED:
                tags = Set.of(part.split(","));
                status = Status.CONTENT_REQUIRED;
                break;
            case CONTENT_REQUIRED:
                content = part;
                status = Status.COMPLETE;
                break;
        }
        return status;
    }
    
    /**
     * Checks if current status of the message allows it's processing by engine. Throws exeption if not.
     */
    private void checkStatus() {
        if (status != Status.COMPLETE && status != Status.TO_DELETE) {
            LOGGER.error("Message {} can't be processed, illegal status {}", id, status.name());
            throw new InputOutputException(IOExceptionCodes.IMPORT_ERROR, 
                    String.format("Message %s can't be processed, illegal status %s", id, status.name()));
        }
    }
    
    /**
     * Status enumeration types.
     */
    public static enum Status {
        HEADER_REQUIRED("Please send header of the message"),
        TAGS_REQUIRED("Please send tags separated by ','"),
        CONTENT_REQUIRED("Please send content of the message"),
        COMPLETE("Message complete"),
        TO_DELETE("Message about to be deleted");
        
        private final String label;
        
        Status(String label) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }
    }
    
}
