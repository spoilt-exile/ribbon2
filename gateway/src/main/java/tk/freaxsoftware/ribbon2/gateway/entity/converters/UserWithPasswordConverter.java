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
package tk.freaxsoftware.ribbon2.gateway.entity.converters;

import io.ebean.DB;
import java.util.Set;
import liquibase.util.StringUtils;
import tk.freaxsoftware.ribbon2.core.data.convert.Converter;
import tk.freaxsoftware.ribbon2.gateway.data.UserWithPassword;
import tk.freaxsoftware.ribbon2.gateway.entity.GroupEntity;
import tk.freaxsoftware.ribbon2.gateway.entity.UserEntity;
import tk.freaxsoftware.ribbon2.gateway.utils.SHAHash;

/**
 * User converter for create/update operations.
 * @author Stanislav Nepochaotv
 */
public class UserWithPasswordConverter implements Converter<UserWithPassword, UserEntity>{

    @Override
    public UserEntity convert(UserWithPassword source) {
        UserEntity user = new UserEntity();
        user.setId(source.getId());
        user.setLogin(source.getLogin());
        user.setFirstname(source.getFirstName());
        user.setLastname(source.getLastName());
        user.setEnabled(source.getEnabled());
        user.setEmail(source.getEmail());
        user.setDescription(source.getDescription());
        Set<GroupEntity> groups = DB.getDefault().find(GroupEntity.class).where().in("name", source.getGroups()).findSet();
        user.setGroups(groups);
        if (!StringUtils.isEmpty(source.getPassword())) {
            user.setPassword(SHAHash.hashPassword(source.getPassword()));
        }
        return user;
    }
}
