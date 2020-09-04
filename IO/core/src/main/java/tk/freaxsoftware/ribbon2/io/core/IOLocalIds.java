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
package tk.freaxsoftware.ribbon2.io.core;

/**
 * IO modules local messages topics.
 * @author Stanislav Nepochatov
 */
public class IOLocalIds {
    
    /**
     * Format for localized message topics.
     */
    public final static String LOCAL_TOPIC_FORMAT = "%s.%s";
    
    /**
     * Topic for registering any IO module on gateway.
     */
    public final static String IO_REGISTER_TOPIC = "Ribbon.IO.Register";
    
    /**
     * Scheme create/update topic.
     */
    public final static String IO_SCHEME_SAVE_TOPIC = "Ribbon.IO.SaveScheme";
    
    /**
     * Scheme get topic.
     */
    public final static String IO_SCHEME_GET_TOPIC = "Ribbon.IO.GetScheme";
    
    /**
     * Scheme delete topic.
     */
    public final static String IO_SCHEME_DELETE_TOPIC = "Ribbon.IO.SchemeDelete";
}
