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
package tk.freaxsoftware.ribbon2.io.core;

/**
 * Module registration request.
 * @author Stanislav Nepochatov
 */
public class ModuleRegistration {
    
    private String id;
    
    private ModuleType type;
    
    private String protocol;
    
    private String[] requiredConfigKeys;
    
    private String[] schemes;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ModuleType getType() {
        return type;
    }

    public void setType(ModuleType type) {
        this.type = type;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String[] getRequiredConfigKeys() {
        return requiredConfigKeys;
    }

    public void setRequiredConfigKeys(String[] requiredConfigKeys) {
        this.requiredConfigKeys = requiredConfigKeys;
    }

    public String[] getSchemes() {
        return schemes;
    }

    public void setSchemes(String[] schemes) {
        this.schemes = schemes;
    }
    
}
