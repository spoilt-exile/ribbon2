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

import java.util.Properties;
import java.util.Set;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tk.freaxsoftware.ribbon2.io.core.InputOutputException;

/**
 * Sends reports for success and error cases.
 * @author Stanislav Nepochatov
 */
public class ReportSender {
    
    private final static Logger LOGGER = LoggerFactory.getLogger(ReportSender.class);
    
    private final MailImportConfig config;
    
    private Session smtpSession;

    public ReportSender(MailImportConfig config) {
        this.config = config;
    }
    
    private void initSmtpSession() {
        final Properties mailInit = new Properties();
        mailInit.put("mail.smtp.host", config.getSmtpAddress());
        mailInit.put("mail.smtp.port", config.getSmtpPort());
        if (config.getSmtpSecurity() == MailImportConfig.SecurityType.SSL) {
            mailInit.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            mailInit.put("mail.smtp.socketFactory.port", config.getSmtpPort());
            mailInit.put("mail.smtp.ssl.enable", true);
        }
        mailInit.put("mail.user", config.getSmtpLogin());
        mailInit.put("mail.password", config.getSmtpPassword());
        mailInit.put("mail.smtp.auth", "true");
        smtpSession = Session.getDefaultInstance(mailInit, new javax.mail.Authenticator() {
                        protected PasswordAuthentication getPasswordAuthentication() {
                                return new PasswordAuthentication(mailInit.getProperty("mail.user"), mailInit.getProperty("mail.password"));
                        }
                });
        smtpSession.setDebug(true);
    }
    
    public void sendSuccessReport(String address, String importedSubject, Set<String> directories, String uid) {
        String content = String.format("Hello!\n\nYour message '%s' were successfully imported to Ribbon2 system by uid %s to following directories:\n%s\n\nRibbon2 Mail Reporter", 
                importedSubject, uid, directories);
        String subject = String.format("[Ribbon2] Message %s imported", uid);
        sendEmail(address, subject, content);
    }
    
    public void sendErrorReport(String address, String adminAddress, String errorSubject, InputOutputException ex) {
        String content = String.format("Hello!\n\nError occurred during import of your message %s. By now it's not imported yet. %s\n\nRibbon2 Mail Reporter", 
                errorSubject, adminAddress != null ? "Administrator informed.\n" : "");
        String subject = String.format("[Ribbon2] Message %s failed to import", errorSubject);
        String adminContent = String.format("Hello!\n\nError occurred during import message %s. Error with code %s and message %s.\n\nRibbon2 Mail Reporter", errorSubject, ex.getCode().name(), ex.getMessage());
        sendEmail(address, subject, content);
        sendEmail(adminAddress, subject, adminContent);
    }
    
    private void sendEmail(String to, String subject, String content) {
        try {
            initSmtpSession();
            MimeMessage message = new MimeMessage(smtpSession);
            message.setFrom(new InternetAddress(config.getSmtpFrom()));
            message.addRecipient(javax.mail.Message.RecipientType.TO, new InternetAddress(to));
            message.setHeader("X-Mailer", "Ribbon2-Mail-Reporter");
            message.setSubject(subject);
            message.setContent(content, "text/plain; charset=UTF-8");
            Transport.send(message);
        } catch (Exception ex) {
            LOGGER.error("Error during sending success report back to {} for message {}", to, subject);
            LOGGER.error("Error in detail:", ex);
        }
    }
    
}
