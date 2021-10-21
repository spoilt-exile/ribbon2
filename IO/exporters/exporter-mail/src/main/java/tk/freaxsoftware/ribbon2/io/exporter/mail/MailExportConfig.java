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

import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;
import tk.freaxsoftware.ribbon2.io.core.IOExceptionCodes;
import tk.freaxsoftware.ribbon2.io.core.IOScheme;
import tk.freaxsoftware.ribbon2.io.core.InputOutputException;

/**
 * Mail exporter config.
 * @author Stanislav Nepochatov
 */
public class MailExportConfig {
    
    private final Set<String> toList;
    
    private final String address;
    
    private final String login;
    
    private final String password;
    
    private final Integer port;
    
    private final SecurityType security;
    
    private final String from;
    
    private final Boolean debug;

    public MailExportConfig(IOScheme scheme) {
        this.toList = Set.of(((String) getOrThrow(scheme.getConfig(), "mailSmtpToList")).split(","));
        this.address = (String) getOrThrow(scheme.getConfig(), "mailSmtpAddress");
        this.login = (String) getOrThrow(scheme.getConfig(), "mailSmtpLogin");
        this.password = (String) getOrThrow(scheme.getConfig(), "mailSmtpPassword");
        this.port = getNumber(scheme.getConfig().getOrDefault("mailSmtpPort", 25L));
        this.security = SecurityType.valueOf((String) scheme.getConfig().getOrDefault("mailSmtpSecurity", SecurityType.NONE.name()));
        this.from = (String) getOrThrow(scheme.getConfig(), "mailSmtpFrom");
        this.debug = (Boolean) scheme.getConfig().getOrDefault("mailSmtpDebug", Boolean.FALSE);
    }
    
    public static enum SecurityType {
        NONE,
        SSL;
    }
    
    private Object getOrThrow(Map<String, Object> config, String key) {
        Object value = config.get(key);
        if (value == null) {
            throw new InputOutputException(IOExceptionCodes.EXPORT_ERROR, 
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

    public Set<String> getToList() {
        return toList;
    }

    public String getAddress() {
        return address;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public Integer getPort() {
        return port;
    }

    public SecurityType getSecurity() {
        return security;
    }

    public String getFrom() {
        return from;
    }

    public Boolean getDebug() {
        return debug;
    }
    
}
