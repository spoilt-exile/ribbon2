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
package tk.freaxsoftware.ribbon2.exchanger.entity;

import io.ebean.Model;
import io.ebean.annotation.DbArray;
import io.ebean.annotation.DbJsonB;
import java.io.Serializable;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import tk.freaxsoftware.ribbon2.io.core.ErrorHandling;
import tk.freaxsoftware.ribbon2.io.core.ModuleType;
import tk.freaxsoftware.ribbon2.io.core.SchemeInstance;

/**
 * IO Scheme DB entity.
 * @author Stanislav Nepochatov
 */
@Entity
@Table(name = "io_scheme")
public class Scheme extends Model implements Serializable {
    
    @Id
    private Long id;
    
    private String name;
    
    private String description;
    
    private String moduleId;
    
    @Enumerated(EnumType.STRING)
    private ModuleType type;
    
    private String protocol;
    
    @DbJsonB
    private Map<String,Object> config;
    
    @DbArray
    private Set<String> exportList;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getModuleId() {
        return moduleId;
    }

    public void setModuleId(String moduleId) {
        this.moduleId = moduleId;
    }

    public ModuleType getType() {
        return type;
    }

    public void setType(ModuleType type) {
        this.type = type;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public Map<String, Object> getConfig() {
        return config;
    }

    public void setConfig(Map<String, Object> config) {
        this.config = config;
    }

    public Set<String> getExportList() {
        return exportList;
    }

    public void setExportList(Set<String> exportList) {
        this.exportList = exportList;
    }
    
    /**
     * Builds scheme runtime data holder with default status but with initialized export directory list and admin error flag.
     * @return new scheme instance;
     */
    public SchemeInstance buildInstance() {
        Boolean raisingAdminError = errorHandling() == ErrorHandling.RAISE_ADM_ERROR;
        return type == ModuleType.EXPORT ? new SchemeInstance(raisingAdminError, new CopyOnWriteArraySet(exportList)) : new SchemeInstance(raisingAdminError);
    }
    
    /**
     * Gets current scheme error handling strategy from config. 
     * @return parsed error handling strategy from config or default value;
     */
    public ErrorHandling errorHandling() {
        return ErrorHandling.errorHandling(config);
    }
}