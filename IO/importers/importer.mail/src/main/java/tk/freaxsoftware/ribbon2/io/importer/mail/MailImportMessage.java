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
import java.util.Set;
import javax.mail.Flags;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.util.SharedByteArrayInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tk.freaxsoftware.ribbon2.core.data.MessageModel;
import tk.freaxsoftware.ribbon2.core.data.MessagePropertyModel;
import tk.freaxsoftware.ribbon2.core.exception.CoreException;
import tk.freaxsoftware.ribbon2.core.exception.RibbonErrorCodes;
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
            parseContent();
            copyright = config.getCopyright() == null ? fromAddress.getPersonal() : config.getCopyright();
        } catch (Exception ex) {
            LOGGER.error("Error during reading of the message", ex);
            throw new CoreException(RibbonErrorCodes.IO_MODULE_ERROR, "Error during reading of the message");
        }
    }
    
    private void parseContent() throws MessagingException, IOException {
        content = readContent();
        if (!config.getDirectoryList().isEmpty()) {
            String header = content.substring(0, content.indexOf('\n'));
            if (header.startsWith("+")) {
                directories = Set.of(header.substring(1).split(","));
                content = content.substring(header.length()).trim();
                if (!config.getDirectoryList().containsAll(directories)) {
                    
                }
            }
        }
    }
    
    private String readContent() throws MessagingException, IOException {
        Object content = mailMessage.getContent();
        if (mailMessage.isMimeType("text/plain")) {
            if (content instanceof SharedByteArrayInputStream || content instanceof QPDecoderStream || content instanceof BASE64DecoderStream) {
                InputStream stream = (InputStream) content;

                BufferedReader reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer contentBuffer = new StringBuffer();
                String currentLine = null;
                while ((currentLine = reader.readLine()) != null) {
                    contentBuffer.append(currentLine);
                    contentBuffer.append("\n");
                }
                return contentBuffer.toString();
            }
        }
        LOGGER.error("Error during reading of the message caust content type is not text/plain");
        throw new CoreException(RibbonErrorCodes.IO_MODULE_ERROR, "Error during reading of the message caust content type is not text/plain");
    }

    @Override
    public MessageModel getMessage() {
        MessageModel message = new MessageModel();
        message.setHeader(header);
        if (!directories.isEmpty()) {
            message.setDirectories(directories);
        }
        message.setProperties(Set.of(new MessagePropertyModel("COPYRIGHT", copyright)));
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
            throw new CoreException(RibbonErrorCodes.IO_MODULE_ERROR, 
                    String.format("Error marking reading of the message %s", id));
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
    
}
