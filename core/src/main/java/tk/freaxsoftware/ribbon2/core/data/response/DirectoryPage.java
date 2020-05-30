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
package tk.freaxsoftware.ribbon2.core.data.response;

import java.util.List;
import tk.freaxsoftware.ribbon2.core.data.DirectoryModel;

/**
 * Page of directories.
 * @author Stanislav Nepochatov
 */
public class DirectoryPage implements Page<DirectoryModel> {
    
    private List<DirectoryModel> content;
    
    private long totalCount;

    @Override
    public List<DirectoryModel> getContent() {
        return content;
    }

    public void setContent(List<DirectoryModel> directories) {
        this.content = directories;
    }

    @Override
    public long getTotalCount() {
        return totalCount;
    }
    
    public void setTotalCount(long totalCount) {
        this.totalCount = totalCount;
    }
    
}
