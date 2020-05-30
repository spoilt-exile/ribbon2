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
package tk.freaxsoftware.ribbon2.core.utils;

import io.ebean.DB;
import io.ebean.Model;
import io.ebean.PagedList;
import io.ebean.Query;
import tk.freaxsoftware.ribbon2.core.data.request.PaginationRequest;

/**
 * Various method for DB optimization.
 * @author Stanislav Nepochatov
 */
public class DBUtils {
    
    /**
     * Finds page of any entity by page request.
     * @param <T> generic type of entity;
     * @param pageRequest page request;
     * @param entityClass class of the entity;
     * @return paged list;
     */
    public static <T extends Model> PagedList<T> findPaginatedEntity(PaginationRequest pageRequest, Class<T> entityClass) {
        Query<T> query = DB.getDefault().find(entityClass).setFirstRow(pageRequest.getPage() * pageRequest.getSize())
                .setMaxRows(pageRequest.getSize());
        
        if (pageRequest.getDirection() == PaginationRequest.Order.ASC) {
            query = query.order().asc(pageRequest.getOrderBy());
        } else {
            query = query.order().desc(pageRequest.getOrderBy());
        }
        
        return query.findPagedList();
    }
}
