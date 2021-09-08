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

import java.util.List;
import java.util.stream.Collectors;
import tk.freaxsoftware.ribbon2.io.core.ModuleRegistration;
import tk.freaxsoftware.ribbon2.io.core.ModuleType;

/**
 * Module brief info.
 * @author Stanislav Nepochatov
 */
public class IOModuleScheme {
    
    private String id;
    
    private ModuleType type;
    
    private String protocol;
    
    private String scheme;

    public IOModuleScheme() {
    }
    
    public IOModuleScheme(ModuleRegistration reg, String scheme) {
        this.id = reg.getId();
        this.type = reg.getType();
        this.protocol = reg.getProtocol();
        this.scheme = scheme;
    }

    public String getId() {
        return id;
    }

    public IOModuleScheme setId(String id) {
        this.id = id;
        return this;
    }

    public ModuleType getType() {
        return type;
    }

    public IOModuleScheme setType(ModuleType type) {
        this.type = type;
        return this;
    }

    public String getProtocol() {
        return protocol;
    }

    public IOModuleScheme setProtocol(String protocol) {
        this.protocol = protocol;
        return this;
    }

    public String getScheme() {
        return scheme;
    }

    public IOModuleScheme setScheme(String scheme) {
        this.scheme = scheme;
        return this;
    }
    
    public static List<IOModuleScheme> ofModuleRegistration(ModuleRegistration reg) {
        return reg.getSchemes().stream()
                .map(scheme -> new IOModuleScheme(reg, scheme))
                .collect(Collectors.toList());
    }
    
}
