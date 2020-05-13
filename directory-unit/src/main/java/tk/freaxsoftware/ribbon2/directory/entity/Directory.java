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
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.OneToOne;

/**
 * Directory entity.
 * @author Stanislav Nepochatov
 */
@Entity
public class Directory extends Model {
    
    /**
     * Id of directory.
     */
    @Id
    private Long id;
    
    /**
     * Id of parent directory.
     */
    private Long parentId;
    
    /**
     * Name of directory.
     */
    private String name;
    
    /**
     * Full name of directory (path).
     */
    private String fullName;
    
    /**
     * Directory description.
     */
    @Lob
    private String description;
    
    /**
     * Access config of the directory.
     */
    @OneToOne(mappedBy = "directory", fetch = FetchType.EAGER, 
            cascade = javax.persistence.CascadeType.REMOVE)
    private DirectoryAccess access;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public DirectoryAccess getAccess() {
        return access;
    }

    public void setAccess(DirectoryAccess access) {
        this.access = access;
    }
}
