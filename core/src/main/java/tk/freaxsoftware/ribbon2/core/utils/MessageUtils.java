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
package tk.freaxsoftware.ribbon2.core.utils;

import tk.freaxsoftware.extras.bus.MessageHolder;
import tk.freaxsoftware.ribbon2.core.data.UserModel;
import tk.freaxsoftware.ribbon2.core.exception.CoreException;
import static tk.freaxsoftware.ribbon2.core.exception.RibbonErrorCodes.ACCESS_DENIED;

/**
 * Utils methods for working with messages.
 * @author Stanislav Nepochatov
 */
public class MessageUtils {
    
    /**
     * Gets username from message header.
     * @param holder message holder;
     * @return login of current user;
     * @throws CoreException if header not present in message;
     */
    public static String getAuthFromHeader(MessageHolder holder) {
        if (holder.getHeaders().containsKey(UserModel.AUTH_HEADER_USERNAME)) {
            return (String) holder.getHeaders().get(UserModel.AUTH_HEADER_USERNAME);
        }
        throw new CoreException(ACCESS_DENIED, "No auth header provided.");
    }
    
}
