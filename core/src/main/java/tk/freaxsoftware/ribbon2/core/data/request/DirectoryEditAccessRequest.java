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
package tk.freaxsoftware.ribbon2.core.data.request;

import java.util.Set;
import tk.freaxsoftware.ribbon2.core.data.DirectoryAccessModel;

/**
 * Model class for request to edit directory access entires.
 * @author Stanilav Nepochatov
 */
public class DirectoryEditAccessRequest {
    
    public static final String CALL_EDIT_DIR_ACCESS = "Ribbon.Global.EditDirectoryAccess";
    
    public final static String PERMISSION_CAN_EDIT_DIR_ACCESS = "canEditDirAccess";
    
    private String directoryPath;
    
    private Set<DirectoryAccessModel> access;

    public DirectoryEditAccessRequest() {}

    public DirectoryEditAccessRequest(String directoryPath, Set<DirectoryAccessModel> access) {
        this.directoryPath = directoryPath;
        this.access = access;
    }
    
    public String getDirectoryPath() {
        return directoryPath;
    }

    public void setDirectoryPath(String directoryPath) {
        this.directoryPath = directoryPath;
    }

    public Set<DirectoryAccessModel> getAccess() {
        return access;
    }

    public void setAccess(Set<DirectoryAccessModel> access) {
        this.access = access;
    }
    
}
