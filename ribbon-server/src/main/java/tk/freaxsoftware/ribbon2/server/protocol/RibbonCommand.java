/*
 * This file is part of Ribbon2 news message system.
 * 
 * Copyright (C) 2026 Freax Software
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
package tk.freaxsoftware.ribbon2.server.protocol;

import tk.freaxsoftware.ribbon2.server.networking.RibbonChannelHandler.HandlerState;

/**
 * Ribbon command abstract class. Defines name of command and connection type.
 * @author Stanislav Nepochatov
 */
public abstract class RibbonCommand {
    private final String name;
    private final ConnectionTypes type;

    public RibbonCommand(String name, ConnectionTypes type) {
        this.name = name;
        this.type = type;
    }
    
    public String getName() {
        return name;
    }

    public ConnectionTypes getType() {
        return type;
    }

    /**
     * Execute command of Ribbon protocol. 
     * @param state state of handler to persist any changes between invokation;
     * @param args arguments of protocol command in raw form;
     * @return string response to write it as response to client, may be null if there is nothing to return at this point;
     */
    public abstract String exec(HandlerState state, String args);

    /**
     * Setup handler state to collect message.
     * @param state state of handler;
     * @param args arguments of command;
     */
    protected void collectMessage(HandlerState state, String args) {
        state.setCurrentCommand(this);
        state.setCurrentCommandArgs(args);
        state.setCollectionBuffer(new StringBuffer());
    }
}
