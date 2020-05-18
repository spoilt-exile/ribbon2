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
import io.ebean.annotation.DbJsonB;
import java.util.Set;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import tk.freaxsoftware.ribbon2.core.data.DirectoryAccessModel;

/**
 * Access entry entity contains permissions for directory.
 * @author Stanislav Nepochatov
 */
@Entity
public class DirectoryAccess extends Model {
    
    /**
     * Entity id.
     */
    @Id
    private Long id;
    
    /**
     * Parent directory.
     */
    @OneToOne
    @JoinColumn(name = "directory_id", referencedColumnName = "id")
    private Directory directory;
    
    /**
     * Set of access entries.
     */
    @DbJsonB
    private Set<DirectoryAccessModel> accessEntries;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Directory getDirectory() {
        return directory;
    }

    public void setDirectory(Directory directory) {
        this.directory = directory;
    }

    public Set<DirectoryAccessModel> getAccessEntries() {
        return accessEntries;
    }

    public void setAccessEntries(Set<DirectoryAccessModel> accessEntries) {
        this.accessEntries = accessEntries;
    }
}
