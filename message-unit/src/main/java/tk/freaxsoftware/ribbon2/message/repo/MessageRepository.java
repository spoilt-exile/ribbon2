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
package tk.freaxsoftware.ribbon2.message.repo;

import io.ebean.DB;
import io.ebean.PagedList;
import io.ebean.Query;
import tk.freaxsoftware.ribbon2.core.data.request.PaginationRequest;
import tk.freaxsoftware.ribbon2.core.utils.DBUtils;
import tk.freaxsoftware.ribbon2.message.entity.Message;

/**
 * Message entity repository.
 * @author Stanislav Nepochatov
 */
public class MessageRepository {
    
    public Message findByUid(String uid) {
        return DB.getDefault().find(Message.class).where().eq("uid", uid).findOne();
    }
    
    public Message save(Message message) {
        message.save();
        return message;
    }
    
    /**
     * Finds page of messages.
     * @param directory message directory;
     * @param request pagination request;
     * @return paged list;
     */
    public PagedList<Message> findPage(String directory, PaginationRequest request) {
        Query<Message> query = DB.getDefault().find(Message.class).where().arrayContains("directories", directory).query();
        return DBUtils.findPaginatedEntityWithQuery(request, query);
    }
}
