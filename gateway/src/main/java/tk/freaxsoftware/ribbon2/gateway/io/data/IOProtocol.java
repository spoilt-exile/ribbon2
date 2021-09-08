/*
 * This file is part of Ribbon2 news message system.
 * 
 * Copyright (C) 2020 Freax Software
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
package tk.freaxsoftware.ribbon2.gateway.io.data;

import tk.freaxsoftware.ribbon2.io.core.ModuleRegistration;
import tk.freaxsoftware.ribbon2.io.core.ModuleType;

/**
 * IO protocol data class.
 * @author Stanislav Nepochatov
 */
public class IOProtocol {
    
    private String id;
    
    private ModuleType type;
    
    private String protocol;
    
    private String[] requiredConfigKeys;

    public String getId() {
        return id;
    }

    public IOProtocol setId(String id) {
        this.id = id;
        return this;
    }

    public ModuleType getType() {
        return type;
    }

    public IOProtocol setType(ModuleType type) {
        this.type = type;
        return this;
    }

    public String getProtocol() {
        return protocol;
    }

    public IOProtocol setProtocol(String protocol) {
        this.protocol = protocol;
        return this;
    }

    public String[] getRequiredConfigKeys() {
        return requiredConfigKeys;
    }

    public IOProtocol setRequiredConfigKeys(String[] requiredConfigKeys) {
        this.requiredConfigKeys = requiredConfigKeys;
        return this;
    }
    
    public static IOProtocol ofModuleRegistration(ModuleRegistration registration) {
        return new IOProtocol()
                .setId(registration.getId())
                .setProtocol(registration.getProtocol())
                .setType(registration.getType())
                .setRequiredConfigKeys(registration.getRequiredConfigKeys());
    }
    
}
