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
package tk.freaxsoftware.ribbon2.message.repo;

import io.ebean.DB;
import java.util.List;
import tk.freaxsoftware.ribbon2.message.entity.PropertyType;

/**
 * Property type repository.
 * @author Stanislav Nepochatov
 */
public class PropertyTypeRepository {
    
    public void saveAll(List<PropertyType> types) {
        DB.getDefault().saveAll(types);
    }
    
    public void deleteByTag(String tag) {
        DB.getDefault().find(PropertyType.class).where().eq("tag", tag).delete();
    }
    
    public PropertyType findByType(String type) {
        return DB.getDefault().find(PropertyType.class).where().eq("type", type).findOne();
    }
    
}
