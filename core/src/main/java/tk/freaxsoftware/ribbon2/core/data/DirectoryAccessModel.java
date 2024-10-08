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

import com.google.gson.reflect.TypeToken;
import java.util.Map;
import java.util.Set;
import tk.freaxsoftware.extras.bus.bridge.http.TypeResolver;

/**
 * Directory access config entry model.
 * @author Stanislav Nepochatov
 */
public class DirectoryAccessModel {
    
    public static final String CALL_GET_DIR_ACCESS = "Ribbon.Global.GetDirectoryAccess";
    
    public final static String DIRECTORY_ACCESS_MODEL_SET_TYPE_NAME = "DirectoryAccessModelSet";
    public final static TypeToken DIRECTORY_ACCESS_MODEL_SET_TYPE_TOKEN = new TypeToken<Set<DirectoryAccessModel>>() {};
    
    private String name;
    private Type type;
    private Map<String, Boolean> permissions;

    public DirectoryAccessModel() {}

    public DirectoryAccessModel(String name, Type type, Map<String, Boolean> permissions) {
        this.name = name;
        this.type = type;
        this.permissions = permissions;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Map<String, Boolean> getPermissions() {
        return permissions;
    }

    public void setPermissions(Map<String, Boolean> permissions) {
        this.permissions = permissions;
    }

    public static enum Type {
        ALL,
        USER,
        GROUP;
    }
    
    public static void registerSetType() {
        TypeResolver.register(DIRECTORY_ACCESS_MODEL_SET_TYPE_NAME, DIRECTORY_ACCESS_MODEL_SET_TYPE_TOKEN);
    }
}
