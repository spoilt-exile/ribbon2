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
 * RIBBON_NCTL_LOGIN command implementation.
 * Allows user to authenticate in the system and receive session id.
 * @author Stanislav Nepochatov
 */
public class RibbonNctlLogin extends RibbonCommand {

    public RibbonNctlLogin() {
        super("RIBBON_NCTL_LOGIN", ConnectionTypes.ANY);
    }

    @Override
    public String exec(HandlerState state, String args) {
        // TODO should login user via REST call to gateway service and set it's name to the handler state. Also JWT key from response should be saved as session entity and be returned to the client as OK:SESSION_ID. Session should be valid until JWT expiration. Also to login with connection type CONTROL user should be in Admins group.
        return ProtocolUtils.ERROR_RESPONSE + "Команда не реалізована!";
    }
    
}
