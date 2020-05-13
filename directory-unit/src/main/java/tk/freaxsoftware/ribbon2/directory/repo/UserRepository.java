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
package tk.freaxsoftware.ribbon2.directory.repo;

import io.ebean.DB;
import tk.freaxsoftware.ribbon2.directory.entity.UserEntity;

/**
 * Persistance repository for users.
 * @author Stanislav Nepochatov
 */
public class UserRepository {
    
    public UserEntity save(UserEntity user) {
        user.save();
        return user;
    }
    
    public UserEntity findByLogin(String login) {
        return DB.getDefault().find(UserEntity.class).where().eq("login", login).findOne();
    }
    
    public void deleteByLogin(String login) {
        DB.getDefault().find(UserEntity.class).where().eq("login", login).delete();
    } 
}
