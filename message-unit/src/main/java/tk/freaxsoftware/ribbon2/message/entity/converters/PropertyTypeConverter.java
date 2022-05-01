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
package tk.freaxsoftware.ribbon2.message.entity.converters;

import tk.freaxsoftware.ribbon2.core.data.MessagePropertyTagged;
import tk.freaxsoftware.ribbon2.core.data.convert.Converter;
import tk.freaxsoftware.ribbon2.message.entity.PropertyType;

/**
 * Property type converter.
 * @author Stanislav Nepochatov
 */
public class PropertyTypeConverter implements Converter<PropertyType, MessagePropertyTagged> {

    @Override
    public MessagePropertyTagged convert(PropertyType source) {
        MessagePropertyTagged dest = new MessagePropertyTagged();
        dest.setType(source.getType());
        dest.setTag(source.getTag());
        dest.setDescription(source.getDescription());
        return dest;
    }
    
}
