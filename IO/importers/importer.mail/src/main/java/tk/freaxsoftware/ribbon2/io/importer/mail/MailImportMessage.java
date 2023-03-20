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

import com.sun.mail.util.BASE64DecoderStream;
import com.sun.mail.util.QPDecoderStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;
import javax.mail.Flags;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.util.SharedByteArrayInputStream;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tk.freaxsoftware.ribbon2.core.data.MessageModel;
import tk.freaxsoftware.ribbon2.core.data.MessagePropertyModel;
import tk.freaxsoftware.ribbon2.io.core.IOExceptionCodes;
import tk.freaxsoftware.ribbon2.io.core.InputOutputException;
import tk.freaxsoftware.ribbon2.io.core.importer.ImportMessage;

/**
 * Mail message.
 * @author Stanislav Nepochatov
 */
public class MailImportMessage implements ImportMessage {
    
    private final static Logger LOGGER = LoggerFactory.getLogger(MailImportMessage.class);
    
    private final Message mailMessage;
    
    private final MailImportConfig config;
    
    private final InternetAddress fromAddress;
    
    private String id;
    
    private String header;
    
    private String content;
    
    private Set<String> directories;
    
    private String copyright;

    public MailImportMessage(MailImportConfig config, Message mailMessage, InternetAddress fromAddress) {
        this.config = config;
        this.mailMessage = mailMessage;
        this.fromAddress = fromAddress;
        readMessage();
    }
    
    private void readMessage() {
        try {
            id = mailMessage.getHeader("Message-ID")[0];
            header = mailMessage.getSubject();
            copyright = config.getCopyright() == null ? fromAddress.getPersonal() : config.getCopyright();
        } catch (Exception ex) {
            LOGGER.error("Error during reading of the message", ex);
            throw new InputOutputException(IOExceptionCodes.IMPORT_ERROR, "Error during reading of the message", ex);
        }
    }
    
    private void parseContent() throws MessagingException, IOException {
        content = readContent();
        if (!config.getDirectoryList().isEmpty()) {
            String header = content.substring(0, content.indexOf('\n')).trim();
            if (header.startsWith("+")) {
                directories = new HashSet(Set.of(header.substring(1).split(",")));
                content = content.substring(header.length()).trim();
                if (!config.getDirectoryList().containsAll(directories)) {
                    directories.removeIf(dir -> config.getDirectoryList().contains(dir));
                    LOGGER.error("Directories {} not allowed to import by config", directories);
                    throw new InputOutputException(IOExceptionCodes.IMPORT_ERROR, 
                            String.format("Directories %s not allowed to import by config", directories));
                }
            } else {
                LOGGER.error("Unable to determine directories to post message {} from {}", id, fromAddress.toString());
                throw new InputOutputException(IOExceptionCodes.IMPORT_ERROR, 
                        String.format("Unable to determine directories to post message %s from %s", id, fromAddress.toString()));
            }
        }
    }
    
    private String readContent() throws MessagingException, IOException {
        LOGGER.info("Reading contnet of the message {}, type {}", id, mailMessage.getContentType());
        Object content = mailMessage.getContent();
        if (mailMessage.isMimeType("text/plain")) {
            return (String) content;
        } else if (mailMessage.isMimeType("text/html")) {
            return Jsoup.parse((String) content).wholeText();
        }
        LOGGER.error("Error during reading of the message caust content type is not text/plain or text/html");
        throw new InputOutputException(IOExceptionCodes.IMPORT_ERROR, "Error during reading of the message caust content type is not text/plain or text/html");
    }

    @Override
    public MessageModel getMessage() {
        try {
            parseContent();
        } catch (Exception ex) {
            LOGGER.error("Error during reading of the message", ex);
            throw new InputOutputException(IOExceptionCodes.IMPORT_ERROR, "Error during reading of the message", ex);
        }
        MessageModel message = new MessageModel();
        message.setHeader(header);
        if (directories != null && !directories.isEmpty()) {
            message.setDirectories(directories);
        } else {
            message.setDirectories(Set.of(config.getGeneralDirectory()));
        }
        message.setProperties(new HashSet(Set.of(new MessagePropertyModel("COPYRIGHT", copyright))));
        message.setContent(content);
        return message;
    }

    @Override
    public void markAsRead() {
        try {
            switch (config.getPostAction()) {
                case DELETE:
                    mailMessage.setFlag(Flags.Flag.DELETED, true);
                    break;
                case MARK:
                    mailMessage.setFlag(Flags.Flag.SEEN, true);
                    break;
            }
        } catch (Exception ex) {
            LOGGER.error("Error during marking of the message " + id, ex);
            throw new InputOutputException(IOExceptionCodes.MARK_ERROR, 
                    String.format("Error marking reading of the message %s", id), ex);
        }
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getHeader() {
        return header;
    }

    public InternetAddress getFromAddress() {
        return fromAddress;
    }
    
    public Set<String> getDirectories() {
        return directories;
    }
    
}
