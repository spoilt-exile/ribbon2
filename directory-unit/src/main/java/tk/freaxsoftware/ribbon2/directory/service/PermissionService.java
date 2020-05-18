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
package tk.freaxsoftware.ribbon2.directory.service;

import io.ebean.DB;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tk.freaxsoftware.ribbon2.core.data.request.DirectoryPermissionHolder;
import tk.freaxsoftware.ribbon2.directory.entity.Permission;
import tk.freaxsoftware.ribbon2.directory.entity.converters.PermissionConverter;

/**
 * Permission service.
 * @author Stanislav Nepochatov
 */
public class PermissionService {
    
    private final static Logger LOGGER = LoggerFactory.getLogger(PermissionService.class);
    
    private PermissionConverter converter = new PermissionConverter();
    
    public void initPermissions(DirectoryPermissionHolder permissionHolder) {
        LOGGER.info("Removing permission by tag {}", permissionHolder.getTag());
        DB.getDefault().find(Permission.class).where().eq("tag", permissionHolder.getTag()).delete();
        LOGGER.info("Request to init permissions");
        DB.getDefault().saveAll(permissionHolder.getAccess().stream().map(perm -> {
            Permission permission = converter.convertBack(perm);
            permission.setTag(permissionHolder.getTag());
            return permission;
        }).collect(Collectors.toSet()));
    }
    
}
