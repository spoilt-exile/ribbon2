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

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.InternetAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tk.freaxsoftware.ribbon2.io.core.IOExceptionCodes;
import tk.freaxsoftware.ribbon2.io.core.IOScheme;
import tk.freaxsoftware.ribbon2.io.core.InputOutputException;
import tk.freaxsoftware.ribbon2.io.core.importer.ImportMessage;
import tk.freaxsoftware.ribbon2.io.core.importer.ImportSource;

/**
 * Mail POP3 message import source.
 * @author Stanislav Nepochatov
 */
public class MailImportSource implements ImportSource {
    
    private final static Logger LOGGER = LoggerFactory.getLogger(MailImportSource.class);
    
    private final IOScheme scheme;
    
    private final MailImportConfig config;
    
    private Folder mailFolder;
    
    private Store mailStore;
    
    private ReportSender reportSender;

    public MailImportSource(IOScheme scheme) {
        this.scheme = scheme;
        this.config = new MailImportConfig(scheme);
        if (config.isSendReportBack()) {
            reportSender = new ReportSender(config);
        }
    }
    
    private void initPop3Connection() throws NoSuchProviderException, MessagingException {
        final Properties mailInit = new Properties();
        if (config.getSecurity() == MailImportConfig.SecurityType.NONE) {
            mailInit.put("mail.store.protocol", "pop3");
        }
        else {
            mailInit.put("mail.store.protocol", "pop3s");
        }

        Session session = Session.getInstance(mailInit);
        session.setDebug(config.getDebug());
        mailStore = session.getStore();
        LOGGER.info("Connecting to POP3 server by address {}, port {}, scheme {}", config.getAddress(), config.getPort(), scheme.getName());
        mailStore.connect(config.getAddress(), config.getLogin(), config.getPassword());

        mailFolder = mailStore.getDefaultFolder().getFolder("INBOX");
        mailFolder.open(Folder.READ_WRITE);
    }
    
    private void closePop3Connection() throws MessagingException {
        LOGGER.info("Closing connection to POP3 server by address {}, port {}, scheme {}", config.getAddress(), config.getPort(), scheme.getName());
        mailFolder.close(true);
        mailStore.close();
        mailFolder = null;
        mailStore = null;
    }

    @Override
    public IOScheme getScheme() {
        return scheme;
    }

    @Override
    public List<ImportMessage> getUnreadMessages() {
        LOGGER.info("Loading unread messages for scheme {}", scheme.getName());
        List<ImportMessage> messages = new ArrayList();
        try {
            Message[] rawMessages = mailFolder.getMessages();
            for (Message rawMessage: rawMessages) {
                InternetAddress fromAddress = getPassedAddress(rawMessage);
                if (fromAddress == null || isSkipped(rawMessage)) {
                    continue;
                }
                MailImportMessage message = new MailImportMessage(config, rawMessage, fromAddress);
                LOGGER.info("New unread message {} with id {}, scheme {}", message.getHeader(), message.getId(), scheme.getName());
                messages.add(message);
            }
        } catch (Exception ex) {
            LOGGER.error("Error during processing messages for scheme {}", scheme.getName());
            LOGGER.error("Error", ex);
            throw new InputOutputException(IOExceptionCodes.IMPORT_CHECK_ERROR, 
                    String.format("Error during processing messages for scheme %s", scheme.getName()));
        }
        return messages;
    }
    
    private Boolean isSkipped(Message message) throws MessagingException {
        return config.getPostAction() == MailImportConfig.PostActionType.MARK && message.getFlags().contains(Flags.Flag.SEEN);
    }
    
    private InternetAddress getPassedAddress(Message message) throws MessagingException {
        if (!config.getReadFromList().isEmpty()) {
            InternetAddress[] addresses = (InternetAddress[]) message.getFrom();
            for (InternetAddress address: addresses) {
                if (config.getReadFromList().contains(address.getAddress())) {
                    return address;
                }
            }
            return null;
        }
        return (InternetAddress) message.getFrom()[0];
    }

    @Override
    public void open() {
        try {
            initPop3Connection();
        } catch (Exception ex) {
            LOGGER.error("Error during opening of POP3 connection for scheme {}", scheme.getName());
            LOGGER.error("Error", ex);
            throw new InputOutputException(IOExceptionCodes.IMPORT_CHECK_ERROR, 
                    String.format("Error during opening of POP3 connection for scheme %s", scheme.getName()));
        }
    }

    @Override
    public void close() {
        try {
            closePop3Connection();
        } catch (Exception ex) {
            LOGGER.error("Error during closing POP3 connection for scheme {}", scheme.getName());
            LOGGER.error("Error", ex);
            throw new InputOutputException(IOExceptionCodes.PROCESSING_ERROR,
                    String.format("Error during closing POP3 connection for scheme %s", scheme.getName()));
        }
    }

    @Override
    public void onSuccess(ImportMessage message, String uid) {
        MailImportMessage mailMessage = (MailImportMessage) message;
        if (reportSender != null) {
            reportSender.sendSuccessReport(mailMessage.getFromAddress().getAddress(), mailMessage.getHeader(), mailMessage.getDirectories() != null ? mailMessage.getDirectories() : Set.of(config.getGeneralDirectory()), uid);
        }
    }

    @Override
    public void onError(ImportMessage message, InputOutputException ex) {
        MailImportMessage mailMessage = (MailImportMessage) message;
        if (reportSender != null) {
            reportSender.sendErrorReport(mailMessage.getFromAddress().getAddress(), config.getAdminAddress(), mailMessage.getHeader(), ex);
        }
    }
    
}
