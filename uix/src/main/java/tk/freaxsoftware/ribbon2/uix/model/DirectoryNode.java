/*
 * This file is part of Ribbon2 news message system.
 * 
 * Copyright (C) 2023 Freax Software
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
package tk.freaxsoftware.ribbon2.uix.model;

import java.util.ArrayList;
import java.util.List;
import tk.freaxsoftware.ribbon2.core.data.DirectoryModel;

/**
 * Node for tree nested structure.
 * @author Stanislav Nepochatov
 */
public class DirectoryNode {
    
    private DirectoryModel parentDirectory;
    
    private List<DirectoryNode> directoryChildren;

    public DirectoryNode() {
        directoryChildren = new ArrayList();
    }

    public DirectoryNode(DirectoryModel parentDirectory) {
        this();
        this.parentDirectory = parentDirectory;
    }

    public DirectoryModel getParentDirectory() {
        return parentDirectory;
    }

    public void setParentDirectory(DirectoryModel parentDirectory) {
        this.parentDirectory = parentDirectory;
    }

    public List<DirectoryNode> getDirectoryChildren() {
        return directoryChildren;
    }

    public void setDirectoryChildren(List<DirectoryNode> directoryChildren) {
        this.directoryChildren = directoryChildren;
    }
}
