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

import tk.freaxsoftware.ribbon2.core.data.MessageModel;

/**
 * Exporter methods inteface.
 * @author Stanislav Nepochatov
 */
public interface Exporter {
    
    /**
     * Inits export module. Can be also used for reinit module, so 
     * it should also reset it's state.
     * @param scheme import scheme to apply;
     */
    void init(IOScheme scheme);
    
    /**
     * Runs export task for specified message. After export should 
     * add properties to it.
     * @param message message to export;
     */
    void runExport(MessageModel message);
    
}
