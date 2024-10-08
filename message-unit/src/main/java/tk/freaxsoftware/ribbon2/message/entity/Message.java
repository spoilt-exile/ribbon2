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
package tk.freaxsoftware.ribbon2.message.entity;

import io.ebean.Model;
import io.ebean.annotation.DbArray;
import io.ebean.annotation.DbJsonB;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import tk.freaxsoftware.ribbon2.core.data.MessagePropertyModel;

/**
 * Message entity.
 * @author Stanislav Nepochatov
 */
@Entity
public class Message extends Model {
    
    public final static String PERMISSION_CAN_CREATE_MESSAGE = "canCreateMessage";
    public final static String PERMISSION_CAN_UPDATE_MESSAGE = "canUpdateMessage";
    public final static String PERMISSION_CAN_DELETE_MESSAGE = "canDeleteMessage";
    public final static String PERMISSION_CAN_READ_MESSAGE = "canReadMessage";
    
    @Id
    private Long id;
    
    private String uid;
    
    private String parentUid;
    
    private String createdBy;
    
    private ZonedDateTime created;
    
    private String updatedBy;
    
    private ZonedDateTime updated;
    
    @DbArray
    private Set<String> directories;
    
    private String header;
    
    @Lob
    private String content;
    
    @DbArray
    private Set<String> tags;
    
    @DbJsonB
    private Set<MessagePropertyModel> properties;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getParentUid() {
        return parentUid;
    }

    public void setParentUid(String parentUid) {
        this.parentUid = parentUid;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public ZonedDateTime getCreated() {
        return created;
    }

    public void setCreated(ZonedDateTime created) {
        this.created = created;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public ZonedDateTime getUpdated() {
        return updated;
    }

    public void setUpdated(ZonedDateTime updated) {
        this.updated = updated;
    }

    public Set<String> getDirectories() {
        return directories;
    }

    public void setDirectories(Set<String> directories) {
        this.directories = directories;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Set<String> getTags() {
        return tags;
    }

    public void setTags(Set<String> tags) {
        this.tags = tags;
    }

    public Set<MessagePropertyModel> getProperties() {
        if (properties == null) {
            properties = new HashSet<>();
        }
        return properties;
    }

    public void setProperties(Set<MessagePropertyModel> properties) {
        this.properties = properties;
    }
    
}
