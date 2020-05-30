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

/**
 * Default contract for page of entities.
 * @author Stanislav Nepochatov
 */
public interface Page<T> {
    
    /**
     * Get content of the page.
     * @return list of entities;
     */
    List<T> getContent();
    
    /**
     * Get total count number of entities.
     * @return total count number;
     */
    long getTotalCount();
    
}
