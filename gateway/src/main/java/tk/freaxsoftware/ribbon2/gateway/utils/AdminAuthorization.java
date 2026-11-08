/*
 * This file is part of Ribbon2 news message system.
 * 
 * Copyright (C) 2026 Freax Software
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
package tk.freaxsoftware.ribbon2.gateway.utils;

import java.util.Objects;

import tk.freaxsoftware.ribbon2.core.data.GroupModel;
import tk.freaxsoftware.ribbon2.core.exception.CoreException;
import tk.freaxsoftware.ribbon2.core.exception.RibbonErrorCodes;
import tk.freaxsoftware.ribbon2.gateway.entity.UserEntity;

/** Shared authorization checks for gateway operations restricted to administrators. */
public final class AdminAuthorization {

    private AdminAuthorization() {}

    public static boolean isAdmin(UserEntity user) {
        return user != null && user.getGroups() != null && user.getGroups().stream()
                .anyMatch(group -> group != null && Objects.equals(group.getName(), GroupModel.ADMIN_GROUP));
    }

    public static void requireAdmin() {
        if (!isAdmin(UserContext.getUser())) {
            throw new CoreException(RibbonErrorCodes.ACCESS_DENIED, "User not added to admin group!");
        }
    }
}
