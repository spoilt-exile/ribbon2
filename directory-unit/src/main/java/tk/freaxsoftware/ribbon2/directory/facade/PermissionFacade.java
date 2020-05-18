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
package tk.freaxsoftware.ribbon2.directory.facade;

import tk.freaxsoftware.extras.bus.MessageHolder;
import tk.freaxsoftware.extras.bus.annotation.Receive;
import tk.freaxsoftware.ribbon2.core.data.request.DirectoryPermissionHolder;
import tk.freaxsoftware.ribbon2.core.data.DirectoryPermissionModel;
import tk.freaxsoftware.ribbon2.directory.service.PermissionService;

/**
 * Permission facade.
 * @author Stanislav Nepochatov
 */
public class PermissionFacade {
    
    private final PermissionService permissionService;

    public PermissionFacade(PermissionService permissionService) {
        this.permissionService = permissionService;
    }
    
    /**
     * Init permissions by message.
     * @param holder holder with new or updated permissions;
     */
    @Receive(DirectoryPermissionModel.CALL_INIT_PERMISSIONS)
    public void initPermissions(MessageHolder<DirectoryPermissionHolder> holder) {
        permissionService.initPermissions(holder.getContent());
    }
    
}
