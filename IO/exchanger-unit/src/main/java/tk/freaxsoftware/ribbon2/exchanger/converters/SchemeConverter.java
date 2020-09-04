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
package tk.freaxsoftware.ribbon2.exchanger.converters;

import tk.freaxsoftware.ribbon2.core.data.convert.TwoWayConverter;
import tk.freaxsoftware.ribbon2.exchanger.entity.Scheme;
import tk.freaxsoftware.ribbon2.io.core.IOScheme;

/**
 * Scheme converter.
 * @author Stanislav Nepochatov
 */
public class SchemeConverter implements TwoWayConverter<Scheme, IOScheme> {

    @Override
    public Scheme convertBack(IOScheme destination) {
        Scheme scheme = new Scheme();
        scheme.setModuleId(destination.getId());
        scheme.setName(destination.getName());
        scheme.setDescription(destination.getDescription());
        scheme.setProtocol(destination.getProtocol());
        scheme.setType(destination.getType());
        scheme.setConfig(destination.getConfig());
        return scheme;
    }

    @Override
    public IOScheme convert(Scheme source) {
        IOScheme scheme = new IOScheme();
        scheme.setId(source.getModuleId());
        scheme.setName(source.getName());
        scheme.setDescription(source.getDescription());
        scheme.setProtocol(source.getProtocol());
        scheme.setType(source.getType());
        scheme.setConfig(source.getConfig());
        return scheme;
    }
    
}
