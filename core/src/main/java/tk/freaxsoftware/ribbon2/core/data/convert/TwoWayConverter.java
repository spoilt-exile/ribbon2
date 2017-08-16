/*
 * This file is part of Ribbon2 news message system.
 * 
 * Copyright (C) 2017 Freax Software
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

package tk.freaxsoftware.ribbon2.core.data.convert;

/**
 * Two way converter of data.
 * @author Stanislav Nepochatov
 * @param <S> source type;
 * @param <D> destination type;
 */
public interface TwoWayConverter<S,D> extends Converter<S, D> {
    
    /**
     * Convert destination to source type.
     * @param destination holder of data;
     * @return converted instance;
     */
    S convertBack(D destination);
}
