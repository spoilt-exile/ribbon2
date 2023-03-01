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
package tk.freaxsoftware.ribbon2.exchanger.repository;

import io.ebean.DB;
import java.time.ZonedDateTime;
import java.util.Set;
import tk.freaxsoftware.ribbon2.exchanger.entity.ExportQueue;

/**
 * Export message queue repository.
 * @author Stanislav Nepochatov
 */
public class ExportQueueRepository {
    
    public ExportQueue save(ExportQueue exportMessage) {
        exportMessage.save();
        return exportMessage;
    }
    
    public Set<ExportQueue> findBySchemesAndDate(Set<String> schemes, ZonedDateTime date) {
        return DB.find(ExportQueue.class).where().in("scheme", schemes).and().le("tillDate", date).findSet();
    }
    
}
