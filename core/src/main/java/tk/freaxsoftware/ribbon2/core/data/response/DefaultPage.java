/*
 * This file is part of Ribbon2 news message system.
 * 
 * Copyright (C) 2022 Freax Software
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
import java.util.stream.Collectors;
import tk.freaxsoftware.ribbon2.core.data.convert.Converter;

/**
 * Default page implementation with built-in conversion of entity.
 * @author Stanislav Nepochatov
 */
public class DefaultPage<T, C> implements Page<C> {
    
    private List<C> content;
    
    private long totalCount;
    
    public DefaultPage(PagedList<T> pagedList, Converter<T, C> converter) {
        content = pagedList.getList().stream().map(r -> converter.convert(r)).collect(Collectors.toList());
        totalCount = pagedList.getTotalCount();
    }

    @Override
    public List<C> getContent() {
        return content;
    }

    @Override
    public long getTotalCount() {
        return totalCount;
    }
    
}
