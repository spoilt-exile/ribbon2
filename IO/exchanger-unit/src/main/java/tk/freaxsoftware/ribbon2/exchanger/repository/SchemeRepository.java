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
package tk.freaxsoftware.ribbon2.exchanger.repository;

import io.ebean.DB;
import java.util.List;
import tk.freaxsoftware.ribbon2.exchanger.entity.Scheme;

/**
 * Repository for IO schemes.
 * @author Stanislav Nepochatov
 */
public class SchemeRepository {
    
    public List<Scheme> findByModuleId(String moduleId) {
        return DB.find(Scheme.class).where().eq("moduleId", moduleId).findList();
    }
    
}
