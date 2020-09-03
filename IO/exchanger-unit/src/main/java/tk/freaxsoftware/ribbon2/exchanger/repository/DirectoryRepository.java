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
import tk.freaxsoftware.ribbon2.exchanger.entity.Directory;

/**
 * Directory repository.
 * @author Stanislav Nepochatov
 */
public class DirectoryRepository {
    
    public Directory findByFullName(String fullName) {
        return DB.getDefault().find(Directory.class).where().eq("fullName", fullName).findOne();
    }
    
    public void deleteByFullname(String fullName) {
        DB.getDefault().find(Directory.class).where().eq("fullName", fullName).delete();
    }
    
    public Directory save(Directory directory) {
        directory.save();
        return directory;
    }
}
