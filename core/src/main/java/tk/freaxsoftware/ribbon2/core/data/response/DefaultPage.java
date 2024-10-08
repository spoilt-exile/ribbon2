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
package tk.freaxsoftware.ribbon2.core.data.response;

import io.ebean.PagedList;
import java.util.List;

/**
 * Default page implementation.
 * @author Stanislav Nepochatov
 */
public class DefaultPage<T> implements Page<T> {
    
    private List<T> content;
    
    private long totalCount;

    public DefaultPage(List<T> content, long totalCount) {
        this.content = content;
        this.totalCount = totalCount;
    }
    
    public DefaultPage(PagedList<T> pagedList) {
        this.content = pagedList.getList();
        this.totalCount = pagedList.getTotalCount();
    }

    @Override
    public List<T> getContent() {
        return content;
    }

    @Override
    public long getTotalCount() {
        return totalCount;
    }
    
}
