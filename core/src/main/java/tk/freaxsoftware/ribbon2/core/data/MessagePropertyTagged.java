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
package tk.freaxsoftware.ribbon2.core.data;

import tk.freaxsoftware.ribbon2.core.data.request.MessagePropertyRegistrationRequest;

/**
 * Tagged version of message property registration.
 * @author Stanislav Nepochatov
 */
public class MessagePropertyTagged extends MessagePropertyRegistrationRequest.Entry {
    
    public static final String CALL_GET_PROPERTIES = "Ribbon.Global.GetProperties";
    
    private String tag;

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
    
}
