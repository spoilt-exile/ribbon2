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
package tk.freaxsoftware.ribbon2.directory.entity;

import io.ebean.Model;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;

/**
 * Permission entity class.
 * @author Stanislav Nepochatov
 */
@Entity
public class Permission extends Model {
    
    /**
     * Id of the permission.
     */
    @Id
    private String key;
    
    /**
     * Default value used for ALL type access check.
     */
    private Boolean defaultValue;
    
    /**
     * Detail description of the permission.
     */
    @Lob
    private String description;
    
    /**
     * Tag displays from which unit this permission came from.
     */
    private String tag;

    public Permission() {}

    public Permission(Long id, String key, Boolean defaultValue, String description, String tag) {
        this.key = key;
        this.defaultValue = defaultValue;
        this.description = description;
        this.tag = tag;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Boolean getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(Boolean defaultValue) {
        this.defaultValue = defaultValue;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
}
