/*
 * This file is part of Ribbon2 news message system.
 * 
 * Copyright (C) 2017 Freax Software
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

import java.time.ZonedDateTime;
import java.util.Set;

/**
 * News message data class.
 * @author Stanislav Nepochatov
 */
public class MessageModel {
    
    public final static String CALL_CREATE_MESSAGE = "Ribbon.Global.CreateMessage";
    public final static String CALL_UPDATE_MESSAGE = "Ribbon.Global.UpdateMessage";
    public final static String CALL_DELETE_MESSAGE = "Ribbon.Global.DeleteMessage";
    public final static String CALL_GET_MESSAGE_ALL = "Ribbon.Global.GetMessageAll";
    public final static String CALL_GET_MESSAGE_BY_UID = "Ribbon.Global.GetMessageByUid";
    
    public final static String NOTIFICATION_MESSAGE_CREATED = "Ribbon.Global.Notification.MessageCreated";
    public final static String NOTIFICATION_MESSAGE_UPDATED = "Ribbon.Global.Notification.MessageUpdated";
    public final static String NOTIFICATION_MESSAGE_DELETED = "Ribbon.Global.Notification.MessageDeleted";
    
    public final static String HEADER_MESSAGE_UID = "Ribbon.Headers.MessageUid";
    public final static String HEADER_MESSAGE_DIR = "Ribbon.Headers.MessageDir";
    
    private Long id;
    
    private String uid;
    
    private String parentUid;
    
    private String createdBy;
    
    private ZonedDateTime created;
    
    private String updatedBy;
    
    private ZonedDateTime updated;
    
    private Set<String> directories;
    
    private String header;
    
    private String content;
    
    private Set<String> tags;
    
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
        return properties;
    }

    public void setProperties(Set<MessagePropertyModel> properties) {
        this.properties = properties;
    }
}
