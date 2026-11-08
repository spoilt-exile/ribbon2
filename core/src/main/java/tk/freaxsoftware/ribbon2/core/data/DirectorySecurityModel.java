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
package tk.freaxsoftware.ribbon2.core.data;

import java.util.Set;

/** Local and inherited access configuration for a directory. */
public class DirectorySecurityModel {

    public static final String CALL_GET_DIRECTORY_SECURITY = "Ribbon.Global.GetDirectorySecurity";

    public enum AccessMode {
        INHERIT,
        CUSTOM
    }

    private String directoryPath;
    private AccessMode accessMode;
    private Set<DirectoryAccessModel> accessEntries;
    private String accessSourcePath;

    public String getDirectoryPath() {
        return directoryPath;
    }

    public void setDirectoryPath(String directoryPath) {
        this.directoryPath = directoryPath;
    }

    public AccessMode getAccessMode() {
        return accessMode;
    }

    public void setAccessMode(AccessMode accessMode) {
        this.accessMode = accessMode;
    }

    public Set<DirectoryAccessModel> getAccessEntries() {
        return accessEntries;
    }

    public void setAccessEntries(Set<DirectoryAccessModel> accessEntries) {
        this.accessEntries = accessEntries;
    }

    /** Nearest ancestor (or this directory) with custom rules; null when none exists. */
    public String getAccessSourcePath() {
        return accessSourcePath;
    }

    public void setAccessSourcePath(String accessSourcePath) {
        this.accessSourcePath = accessSourcePath;
    }
}
