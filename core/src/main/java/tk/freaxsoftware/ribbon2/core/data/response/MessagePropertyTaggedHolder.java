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
package tk.freaxsoftware.ribbon2.core.data.response;

import java.util.List;
import tk.freaxsoftware.ribbon2.core.data.MessagePropertyTagged;

/**
 * Holder for tagged message property type.
 * @author Stanislav Nepochatov
 */
public class MessagePropertyTaggedHolder {
    
    private List<MessagePropertyTagged> propertyTypes;

    public List<MessagePropertyTagged> getPropertyTypes() {
        return propertyTypes;
    }

    public void setPropertyTypes(List<MessagePropertyTagged> propertyTypes) {
        this.propertyTypes = propertyTypes;
    }
    
}
