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
package tk.freaxsoftware.ribbon2.io.exporter.mail;

import java.util.Properties;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tk.freaxsoftware.ribbon2.io.core.IOExceptionCodes;
import tk.freaxsoftware.ribbon2.io.core.IOModule;
import tk.freaxsoftware.ribbon2.io.core.InputOutputException;
import tk.freaxsoftware.ribbon2.io.core.exporter.ExportMessage;
import tk.freaxsoftware.ribbon2.io.core.exporter.Exporter;

/**
 * Mail message exporter by SMTP protocol.
 * @author Stanislav Nepochatov
 */
@IOModule(id = "export:mail", name = "Mail message exporter by SMTP protocol", 
        protocol = "mail", requiredConfigKeys = {"mailSmtpToList", "mailSmtpAddress", "mailSmtpLogin", "mailSmtpPassword", "mailSmtpFrom"})
public class MailExporter implements Exporter {
    
    private final static Logger LOGGER = LoggerFactory.getLogger(MailExporter.class);

    @Override
    public String export(ExportMessage message) {
        MailExportConfig config = new MailExportConfig(message.getExportScheme());
        Session smtpSession = initSmtpSession(config);
        for (String to: config.getToList()) {
            LOGGER.info("Sending message '{}' by mail '{}' for scheme '{}'", message.getHeader(), to, message.getExportScheme().getName());
            sendEmail(smtpSession, config.getFrom(), to, message.getHeader(), message.getExportContent());
        }
        return message.getHeader();
    }
    
    private Session initSmtpSession(MailExportConfig config) {
        final Properties mailInit = new Properties();
        mailInit.put("mail.smtp.host", config.getAddress());
        mailInit.put("mail.smtp.port", config.getPort());
        if (config.getSecurity() == MailExportConfig.SecurityType.SSL) {
            mailInit.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            mailInit.put("mail.smtp.socketFactory.port", config.getPort());
            mailInit.put("mail.smtp.ssl.enable", true);
        }
        mailInit.put("mail.user", config.getLogin());
        mailInit.put("mail.password", config.getPassword());
        mailInit.put("mail.smtp.auth", "true");
        Session smtpSession = Session.getDefaultInstance(mailInit, new javax.mail.Authenticator() {
                        protected PasswordAuthentication getPasswordAuthentication() {
                                return new PasswordAuthentication(mailInit.getProperty("mail.user"), mailInit.getProperty("mail.password"));
                        }
                });
        smtpSession.setDebug(true);
        return smtpSession;
    }
    
    private void sendEmail(Session session, String from, String to, String subject, String content) {
        try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));
            message.addRecipient(javax.mail.Message.RecipientType.TO, new InternetAddress(to));
            message.setHeader("X-Mailer", "Ribbon2-Mail-Reporter");
            message.setSubject(subject);
            message.setContent(content, "text/plain; charset=UTF-8");
            Transport.send(message);
        } catch (Exception ex) {
            LOGGER.error("Error during exporting message to {} for message {}", to, subject);
            LOGGER.error("Error in detail:", ex);
            throw new InputOutputException(IOExceptionCodes.EXPORT_ERROR, 
                    String.format("Error during exporting message to %s for message %s", to, subject));
        }
    }
}
