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
package tk.freaxsoftware.ribbon2.directory.entity.converters;

import tk.freaxsoftware.ribbon2.core.data.DirectoryPermissionModel;
import tk.freaxsoftware.ribbon2.core.data.convert.TwoWayConverter;
import tk.freaxsoftware.ribbon2.directory.entity.Permission;

/**
 * Permission converter.
 * @author Stanislav Nepochatov
 */
public class PermissionConverter implements TwoWayConverter<Permission, DirectoryPermissionModel>{

    @Override
    public Permission convertBack(DirectoryPermissionModel destination) {
        Permission permission = new Permission();
        permission.setKey(destination.getKey());
        permission.setDescription(destination.getDescription());
        permission.setDefaultValue(destination.getDefaultValue());
        return permission;
    }

    @Override
    public DirectoryPermissionModel convert(Permission source) {
        DirectoryPermissionModel model = new DirectoryPermissionModel();
        model.setKey(source.getKey());
        return model;
    }
    
}
