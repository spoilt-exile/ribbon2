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

import java.util.Set;
import java.util.stream.Collectors;
import tk.freaxsoftware.ribbon2.core.data.UserModel;
import tk.freaxsoftware.ribbon2.core.data.convert.Converter;
import tk.freaxsoftware.ribbon2.gateway.entity.UserEntity;

/**
 * Converter for users;
 * @author Stanislav Nepochatov
 */
public class UserConverter implements Converter<UserEntity, UserModel>{

    @Override
    public UserModel convert(UserEntity source) {
        UserModel user = new UserModel();
        user.setId(source.getId());
        user.setLogin(source.getLogin());
        user.setFirstName(source.getFirstname());
        user.setLastName(source.getLastname());
        user.setEnabled(source.getEnabled());
        user.setEmail(source.getEmail());
        user.setDescription(source.getDescription());
        user.setGroups(source.getGroups().stream().map(gr -> gr.getName()).collect(Collectors.toSet()));
        return user;
    }

}
