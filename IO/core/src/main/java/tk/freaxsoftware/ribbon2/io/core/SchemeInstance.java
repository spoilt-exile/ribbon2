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

import java.util.Set;

/**
 * Runtime scheme info, contains status data and list of assigned export directories (if type is export).
 * @author Stanislav Nepochatov
 */
public class SchemeInstance {
    
    /**
     * Current status of scheme.
     */
    private Status status;
    
    /**
     * Error description, null if status is OK.
     */
    private String errorDescription;
    
    /**
     * If current scheme config error handling strategy requires raising admin error (sending service message).
     */
    private Boolean raisingAdminError;
    
    /**
     * List of export directories. Empty if it's an import scheme.
     */
    private Set<String> exportDirectories;

    /**
     * Empty constructor.
     */
    public SchemeInstance() {
        this.status = Status.OK;
    }

    /**
     * Default constructor for import scheme instances.
     * @param raisingAdminError flag enabling admin notification via message on error;
     */
    public SchemeInstance(Boolean raisingAdminError) {
        this();
        this.raisingAdminError = raisingAdminError;
    }

    /**
     * Default constructor for export scheme instances.
     * @param raisingAdminError flag enabling admin notification via message on error;
     * @param exportDirectories list of export directories assigned to scheme;
     */
    public SchemeInstance(Boolean raisingAdminError, Set<String> exportDirectories) {
        this(raisingAdminError);
        this.exportDirectories = exportDirectories;
    }

    /**
     * Full parametric constructor.
     * @param status status of scheme;
     * @param errorDescription raw error description;
     * @param raisingAdminError flag enabling admin notification via message on error;
     * @param exportDirectories list of export directories assigned to scheme;
     */
    public SchemeInstance(Status status, String errorDescription, Boolean raisingAdminError, Set<String> exportDirectories) {
        this.status = status;
        this.errorDescription = errorDescription;
        this.raisingAdminError = raisingAdminError;
        this.exportDirectories = exportDirectories;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
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
    
    public static enum Status {
        OK,
        ERROR,
        DELETED;
    }
    
}
