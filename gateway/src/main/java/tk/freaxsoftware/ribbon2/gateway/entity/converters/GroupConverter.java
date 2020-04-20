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

import tk.freaxsoftware.ribbon2.core.data.Group;
import tk.freaxsoftware.ribbon2.core.data.convert.TwoWayConverter;
import tk.freaxsoftware.ribbon2.gateway.entity.GroupEntity;

/**
 * Group converter.
 * @author Stanislav Nepochatov
 */
public class GroupConverter implements TwoWayConverter<GroupEntity, Group> {

    @Override
    public GroupEntity convertBack(Group destination) {
        GroupEntity entity = new GroupEntity();
        entity.setId(destination.getId());
        entity.setName(destination.getName());
        entity.setDescription(destination.getDescription());
        return entity;
    }

    @Override
    public Group convert(GroupEntity source) {
        Group group = new Group();
        group.setId(source.getId());
        group.setName(source.getName());
        group.setDescription(source.getDescription());
        return group;
    }
    
}
