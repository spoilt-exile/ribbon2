/*
 * This file is part of Ribbon2 news message system.
 * 
 * Copyright (C) 2026 Freax Software
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

package tk.freaxsoftware.ribbon2.uix.model.io;

import java.util.Set;

import tk.freaxsoftware.ribbon2.io.core.ModuleRegistration;
import tk.freaxsoftware.ribbon2.io.core.ModuleType;
import tk.freaxsoftware.ribbon2.io.core.SchemeInstance;

/**
 * Copy of IOModuleScheme from gateway.
 * @author Stanislav Nepochatov
 */
public class IOModuleScheme {

    private String id;
    
    private ModuleType type;
    
    private String protocol;
    
    private String scheme;
    
    private SchemeInstance.Status status;
    
    private String errorDescription;
    
    private Boolean raisingAdminError;
    
    private Set<String> exportDirectories;

    public IOModuleScheme() {
    }
    
    public IOModuleScheme(ModuleRegistration reg, String scheme, SchemeInstance instance) {
        this.id = reg.getId();
        this.type = reg.getType();
        this.protocol = reg.getProtocol();
        this.scheme = scheme;
        this.status = instance.getStatus();
        this.errorDescription = instance.getErrorDescription();
        this.raisingAdminError = instance.getRaisingAdminError();
        this.exportDirectories = instance.getExportDirectories();
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

    public SchemeInstance.Status getStatus() {
        return status;
    }

    public void setStatus(SchemeInstance.Status status) {
        this.status = status;
    }

    public String getErrorDescription() {
        return errorDescription;
    }

    public void setErrorDescription(String errorDescription) {
        this.errorDescription = errorDescription;
    }

    public Boolean getRaisingAdminError() {
        return raisingAdminError;
    }

    public void setRaisingAdminError(Boolean raisingAdminError) {
        this.raisingAdminError = raisingAdminError;
    }

    public Set<String> getExportDirectories() {
        return exportDirectories;
    }

    public void setExportDirectories(Set<String> exportDirectories) {
        this.exportDirectories = exportDirectories;
    }
}
