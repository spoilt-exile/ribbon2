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

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import tk.freaxsoftware.ribbon2.core.exception.CoreException;
import tk.freaxsoftware.ribbon2.core.exception.RibbonErrorCodes;
import tk.freaxsoftware.ribbon2.io.core.IOScheme;

/**
 * Config for mail import.
 * @author Stanislav Nepochatov
 */
public class MailImportConfig {
    
    private final String address;
    
    private final Integer port;
    
    private final String login;
    
    private final String password;
    
    private final SecurityType security;
    
    private final Boolean debug;
    
    private final PostActionType postAction;
    
    private final Set<String> readFromList;
    
    private final Set<String> directoryList;
    
    private final String copyright;
    
    private final boolean sendReportBack;
    
    private final String adminAddress;
    
    private final String smtpAddress;
    
    private final String smtpLogin;
    
    private final String smtpPassword;
    
    private final Integer smtpPort;
    
    private final SecurityType smtpSecurity;
    
    private final String smtpFrom;
    
    private final String generalDirectory;
    
    public MailImportConfig(IOScheme scheme) {
        this.address = (String) getOrThrow(scheme.getConfig(), "mailPop3Address");
        this.port = getNumber(scheme.getConfig().getOrDefault("mailPop3Port", 110L));
        this.login = (String) getOrThrow(scheme.getConfig(), "mailPop3Login");
        this.password = (String) getOrThrow(scheme.getConfig(), "mailPop3Password");
        this.security = SecurityType.valueOf((String) scheme.getConfig().getOrDefault("mailPop3Security", SecurityType.NONE.name()));
        this.debug = (Boolean) scheme.getConfig().getOrDefault("mailPop3Debug", Boolean.FALSE);
        this.postAction = PostActionType.valueOf((String) scheme.getConfig().getOrDefault("mailPop3PostAction", PostActionType.DELETE.name()));
        this.readFromList = scheme.getConfig().containsKey("mailReadFromList") ? Set.of(((String) scheme.getConfig().get("mailReadFromList")).split(",")) : Collections.EMPTY_SET;
        this.directoryList = scheme.getConfig().containsKey("mailDirectoryList") ? Set.of(((String) scheme.getConfig().get("mailDirectoryList")).split(",")) : Collections.EMPTY_SET;
        this.copyright = (String) scheme.getConfig().get("mailCopyright");
        this.sendReportBack = (Boolean) scheme.getConfig().getOrDefault("mailSendReportBack", Boolean.FALSE);
        this.adminAddress = (String) scheme.getConfig().get("mailReportAdminAddress");
        this.smtpAddress = sendReportBack ? (String) getOrThrow(scheme.getConfig(), "mailSmtpAddress") : null;
        this.smtpLogin = (String) scheme.getConfig().getOrDefault("mailSmtpLogin", this.login);
        this.smtpPassword = (String) scheme.getConfig().getOrDefault("mailSmtpPassword", this.password);
        this.smtpPort = getNumber(scheme.getConfig().getOrDefault("mailSmtpPort", 25L));
        this.smtpSecurity = SecurityType.valueOf((String) scheme.getConfig().getOrDefault("mailSmtpSecurity", SecurityType.NONE.name()));
        this.smtpFrom = sendReportBack ? (String) getOrThrow(scheme.getConfig(), "mailSmtpFrom") : null;
        this.generalDirectory = directoryList.isEmpty() ? (String) getOrThrow(scheme.getConfig(), "generalDirectory") : null;
    }
    
    private Object getOrThrow(Map<String, Object> config, String key) {
        Object value = config.get(key);
        if (value == null) {
            throw new CoreException(RibbonErrorCodes.IO_SCHEME_CONFIG_ERROR, 
                    String.format("Config key %s is not present!", key));
        }
        return value;
    }
    
    private Integer getNumber(Object number) {
        if (number instanceof Double) {
            Double doubleValue = (Double) number;
            return doubleValue.intValue();
        } else if (number instanceof BigDecimal) {
            BigDecimal bigDecimalValue = (BigDecimal) number;
            return bigDecimalValue.intValue();
        } else {
            Long longValue = (Long) number;
            return longValue.intValue();
        }
    }
    
    public static enum SecurityType {
        NONE,
        SSL;
    }
    
    public static enum PostActionType {
        MARK,
        DELETE;
    }

    public String getAddress() {
        return address;
    }

    public Integer getPort() {
        return port;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public SecurityType getSecurity() {
        return security;
    }

    public Boolean getDebug() {
        return debug;
    }

    public PostActionType getPostAction() {
        return postAction;
    }

    public Set<String> getReadFromList() {
        return readFromList;
    }

    public Set<String> getDirectoryList() {
        return directoryList;
    }

    public String getCopyright() {
        return copyright;
    }

    public boolean isSendReportBack() {
        return sendReportBack;
    }

    public String getAdminAddress() {
        return adminAddress;
    }

    public String getSmtpAddress() {
        return smtpAddress;
    }

    public String getSmtpLogin() {
        return smtpLogin;
    }

    public String getSmtpPassword() {
        return smtpPassword;
    }
    
    public Integer getSmtpPort() {
        return smtpPort;
    }

    public SecurityType getSmtpSecurity() {
        return smtpSecurity;
    }

    public String getSmtpFrom() {
        return smtpFrom;
    }

    public String getGeneralDirectory() {
        return generalDirectory;
    }
    
}
