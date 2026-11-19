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
package tk.freaxsoftware.ribbon2.server.protocol.impl.connection;

import tk.freaxsoftware.ribbon2.server.networking.RibbonChannelHandler.HandlerState;
import tk.freaxsoftware.ribbon2.server.protocol.ConnectionTypes;
import tk.freaxsoftware.ribbon2.server.protocol.ProtocolUtils;
import tk.freaxsoftware.ribbon2.server.protocol.RibbonCommand;

/**
 * RIBBON_NCTL_INIT command implementation. 
 * Registers connection on server, setting charset.
 * @author Stanislav Nepochatov
 */
public class RibbonNctlInit extends RibbonCommand {

    public RibbonNctlInit() {
        super("RIBBON_NCTL_INIT", ConnectionTypes.NEW);
    }

    @Override
    public String exec(HandlerState state, String args) {
        state.setType(ConnectionTypes.CLIENT);
        return ProtocolUtils.OK_RESPONSE;
    }
    
}
