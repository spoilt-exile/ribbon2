/*
 * This file is part of Ribbon2 news message system.
 * 
 * Copyright (C) 2020-2022 Freax Software
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

import java.util.Objects;
import java.util.Set;

/**
 * Scheme status update notification.
 * @author Stanislav Nepochatov
 */
public class SchemeStatusUpdate {
    
    private String id;
    
    private ModuleType type;
    
    private String protocol;
    
    private String scheme;
    
    private SchemeInstance.Status status;
    
    private String errorDescription;
    
    private Boolean raisingAdminError;
    
    private Set<String> exportDirectories;

    public SchemeStatusUpdate() {
    }

    public SchemeStatusUpdate(String id, 
            ModuleType type, 
            String protocol, 
            String scheme, 
            SchemeInstance.Status status, 
            String errorDescription, 
            Boolean raisingAdminError, 
            Set<String> exportDirectories) {
        this.id = id;
        this.type = type;
        this.protocol = protocol;
        this.scheme = scheme;
        this.status = status;
        this.errorDescription = errorDescription;
        this.raisingAdminError = raisingAdminError;
        this.exportDirectories = exportDirectories;
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

    public String getScheme() {
        return scheme;
    }

    public void setScheme(String scheme) {
        this.scheme = scheme;
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
    
    public SchemeInstance buildInstance() {
        return new SchemeInstance(status, errorDescription, raisingAdminError, exportDirectories);
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 61 * hash + Objects.hashCode(this.id);
        hash = 61 * hash + Objects.hashCode(this.type);
        hash = 61 * hash + Objects.hashCode(this.protocol);
        hash = 61 * hash + Objects.hashCode(this.scheme);
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
        final SchemeStatusUpdate other = (SchemeStatusUpdate) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        return Objects.equals(this.scheme, other.scheme);
    }
}
