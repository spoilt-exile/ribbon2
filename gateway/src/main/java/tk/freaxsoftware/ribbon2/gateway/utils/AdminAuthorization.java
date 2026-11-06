/*
 * This file is part of Ribbon2 news message system.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 */
package tk.freaxsoftware.ribbon2.gateway.utils;

import java.util.Objects;
import tk.freaxsoftware.ribbon2.core.data.GroupModel;
import tk.freaxsoftware.ribbon2.core.exception.CoreException;
import tk.freaxsoftware.ribbon2.core.exception.RibbonErrorCodes;
import tk.freaxsoftware.ribbon2.gateway.entity.GroupEntity;
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
