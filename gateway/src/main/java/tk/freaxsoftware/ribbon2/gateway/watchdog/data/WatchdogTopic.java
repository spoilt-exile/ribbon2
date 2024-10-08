/*
 * This file is part of Ribbon2 news message system.
 * 
 * Copyright (C) 2020-2022 Freax Software
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
package tk.freaxsoftware.ribbon2.gateway.watchdog.data;

import tk.freaxsoftware.ribbon2.gateway.watchdog.WatchdogService;

/**
 * Contains basic info of watched topic.
 * @author Stanislav Nepochatov
 */
public class WatchdogTopic extends WatchdogService.StatusRecord {
    
    private final String topic;
    
    public WatchdogTopic(String topic, WatchdogService.StatusRecord record) {
        super(record.getStatus(), record);
        this.topic = topic;
    }

    public String getTopic() {
        return topic;
    }
}
