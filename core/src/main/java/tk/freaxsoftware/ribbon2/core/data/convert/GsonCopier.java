/*
 * This file is part of Ribbon2 news message system.
 * 
 * Copyright (C) 2020-2024 Freax Software
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

import com.google.gson.Gson;
import tk.freaxsoftware.extras.bus.bridge.http.util.GsonUtils;

/**
 * Util class with logic to make safe copies of entities tied to db.
 * @author Stanislav Nepochatov
 */
public class GsonCopier {
    
    private final static Gson gson = GsonUtils.getGson();
    
    /**
     * Copies instance of entity through json.
     * @param <T> type of entity;
     * @param instance instance of entity;
     * @return copy of entity untied from db;
     */
    public static <T> T copy(T instance) {
        final Class<T> entityClass = (Class) instance.getClass();
        final String jsonString = gson.toJson(instance);
        return gson.fromJson(jsonString, entityClass);
    }
    
}
