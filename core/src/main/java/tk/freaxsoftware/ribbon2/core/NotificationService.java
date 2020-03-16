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

import java.util.Collections;
import java.util.Map;
import tk.freaxsoftware.extras.bus.MessageBus;
import tk.freaxsoftware.extras.bus.MessageOptions;
import tk.freaxsoftware.extras.bus.annotation.Receive;

/**
 * Notification service for system needs.
 * @author Stanislav Nepochatov
 */
public class NotificationService {
    
    private static final MessageOptions notificationOptions = MessageOptions.Builder.newInstance()
            .async()
            .broadcast()
            .build();
    
    private static final MessageOptions strokeOptions = MessageOptions.Builder.newInstance()
            .async()
            .pointToPoint()
            .build();
    
    private static final Map dummyHeaders = Collections.EMPTY_MAP;
    
    /**
     * Sends notification and strokes based on system message destination.
     * @param messageDestination system defined destination for messageing;
     * @param content any object with content to send;
     */
    public static void notify(RibbonMessages messageDestination, Object content) {
        if (messageDestination != null && messageDestination.getStrokes() != null) {
            for (String strokeId: messageDestination.getStrokes()) {
                MessageBus.fire(strokeId, content, strokeOptions);
            }
        }
        
        if (messageDestination != null && messageDestination.getStrokes() != null) {
            for (String notificationId: messageDestination.getNotifications()) {
                MessageBus.fire(notificationId, content, notificationOptions);
            }
        }
    }
    
    
    public void test() {
        
    }
    
}
