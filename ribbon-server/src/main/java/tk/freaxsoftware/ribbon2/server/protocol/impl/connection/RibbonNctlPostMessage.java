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

public class RibbonNctlPostMessage extends RibbonCommand {
    public RibbonNctlPostMessage() {
        super("RIBBON_NCTL_POST_MESSAGE", ConnectionTypes.ANY);
    }

    @Override
    public String exec(HandlerState state, String args) {
        if (state.getCollectionBuffer() == null) {
            collectMessage(state, args);
            return null;
        } else {
            System.out.println(state.getCollectionBuffer().toString());
            resetCollection(state);
            return ProtocolUtils.OK_RESPONSE;
        }
    }
}
