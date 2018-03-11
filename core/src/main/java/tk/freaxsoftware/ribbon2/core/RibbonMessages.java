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

package tk.freaxsoftware.ribbon2.core;

/**
 * Enum which represents all internal Ribbon2 system messages, notifications and strokes;
 * @author Stanisalv Nepochatov
 */
public enum RibbonMessages {
    
    USER_AUTHENTICATED(null, new String[] {"Ribbon.Notification.UserAuthenticated"}, null),
    
    USER_CREATED(null, new String[] {"Ribbon.Notification.UserCreated"}, null),
    
    USER_UPDATED(null, new String[] {"Ribbon.Notification.UserUpdated"}, null),
    
    USER_DELETED(null, new String[] {"Ribbon.Notification.UserDeleted"}, null),
    
    GROUP_CREATED(null, new String[] {"Ribbon.Notification.GroupCreated"}, null),
    
    GROUP_UPDATED(null, new String[] {"Ribbon.Notification.GroupUpdated"}, null),
    
    GROUP_DELETED(null, new String[] {"Ribbon.Notification.GroupDeleted"}, null),
    
    DIRECTORY_CREATE("Ribbon.Directory.Create", new String[] {"Ribbon.Notification.DirectoryCreated"}, null),
    
    DIRECTORY_UPDATE("Ribbon.Directory.Update", new String[] {"Ribbon.Notification.DirectoryUpdated"}, null),
    
    DIRECTORY_DELETE("Ribbon.Directory.Delete", new String[] {"Ribbon.Notification.DirectoryDeleted"}, null),
    
    DIRECTORY_GET_TREE("Ribbon.Directory.GetTree", null, null),
    
    DIRECTORY_GET_BY_NAME("Ribbon.Directory.GetByName", null, null),
    
    MESSAGE_POST("Ribbon.Message.Post", new String[] {"Ribbon.Notification.MessagePosted"}, null),
    
    MESSAGE_EDIT("Ribbon.Message.Edit", new String[] {"Ribbon.Notification.MessageEdited"}, null),
    
    MESSAGE_DELETE("Ribbon.Message.Delete", new String[] {"Ribbon.Notification.MessageDeleted"}, null),
    
    MESSAGE_GET_PAGED("Ribbon.Message.GetPaged", null, null),
    
    MESSAGE_GET_BY_ID("Ribbon.Message.GetById", null, null),
    ;
    
    /**
     * Message id of event.
     */
    private final String messageId;
    
    /**
     * Ids of notifications which should be triggered after processing message (for broadcasting).
     */
    private final String[] notifications;
    
    /**
     * Ids of strokes which should be triggered after processing message (for point-to-point).
     */
    private final String[] strokes;

    private RibbonMessages(String messageId, String[] notifications, String[] strokes) {
        this.messageId = messageId;
        this.notifications = notifications;
        this.strokes = strokes;
    }

    public String getMessageId() {
        return messageId;
    }

    public String[] getNotifications() {
        return notifications;
    }

    public String[] getStrokes() {
        return strokes;
    }
}
