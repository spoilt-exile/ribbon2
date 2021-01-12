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
package tk.freaxsoftware.ribbon2.core.data.request;

import java.util.List;

/**
 * Model of request to register message properties.
 * @author Stanislav Nepochatov
 */
public class MessagePropertyRegistrationRequest {
    
    public static final String CALL_REGISTER_PROPERTY = "Ribbon.Property.Register";
    
    private String tag;
    
    private List<Entry> propertyTypes;

    public MessagePropertyRegistrationRequest() {}

    public MessagePropertyRegistrationRequest(String tag, List<Entry> propertyTypes) {
        this.tag = tag;
        this.propertyTypes = propertyTypes;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public List<Entry> getPropertyTypes() {
        return propertyTypes;
    }

    public void setPropertyTypes(List<Entry> propertyTypes) {
        this.propertyTypes = propertyTypes;
    }
    
    /**
     * Entry contains data for registration of single property type.
     */
    public static class Entry {
        
        private String type;
        
        private String description;
        
        public Entry() {}

        public Entry(String type, String description) {
            this.type = type;
            this.description = description;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        @Override
        public String toString() {
            return "{" + "type=" + type + ", description=" + description + '}';
        }
    }
}
