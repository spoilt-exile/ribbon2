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

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;

/**
 * Module registration request.
 * @author Stanislav Nepochatov
 */
public class ModuleRegistration {
    
    private String id;
    
    private ModuleType type;
    
    private String protocol;
    
    private String[] requiredConfigKeys;
    
    private Map<String, SchemeInstance> schemes;

    public ModuleRegistration() {
    }

    public ModuleRegistration(String id, ModuleType type, String protocol, 
            String[] requiredConfigKeys, Map<String, SchemeInstance> schemes) {
        this.id = id;
        this.type = type;
        this.protocol = protocol;
        this.requiredConfigKeys = requiredConfigKeys;
        this.schemes = schemes;
    }

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

    public Map<String, SchemeInstance> getSchemes() {
        return schemes;
    }

    public void setSchemes(Map<String, SchemeInstance> schemes) {
        this.schemes = schemes;
    }
    
    public String schemeSaveTopic() {
        return String.format("%s.%s.%s", IOLocalIds.IO_SCHEME_SAVE_TOPIC, 
                type.name().toLowerCase(), protocol);
    }
    
    public String schemeGetTopic() {
        return String.format("%s.%s.%s", IOLocalIds.IO_SCHEME_GET_TOPIC, 
                type.name().toLowerCase(), protocol);
    }
    
    public String schemeDeleteTopic() {
        return String.format("%s.%s.%s", IOLocalIds.IO_SCHEME_DELETE_TOPIC, 
                type.name().toLowerCase(), protocol);
    }
    
    public String schemeExportAssignTopic() {
        return String.format("%s.%s", IOLocalIds.IO_SCHEME_EXPORT_ASSIGN_TOPIC, 
                protocol);
    }
    
    public String schemeExportDismissTopic() {
        return String.format("%s.%s", IOLocalIds.IO_SCHEME_EXPORT_DISMISS_TOPIC, 
                protocol);
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 73 * hash + Objects.hashCode(this.id);
        hash = 73 * hash + Objects.hashCode(this.type);
        hash = 73 * hash + Objects.hashCode(this.protocol);
        hash = 73 * hash + Arrays.deepHashCode(this.requiredConfigKeys);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ModuleRegistration other = (ModuleRegistration) obj;
        return Objects.equals(this.id, other.id);
    }
}
