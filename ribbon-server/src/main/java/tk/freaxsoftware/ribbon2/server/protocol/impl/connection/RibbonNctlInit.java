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

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

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
        //Return warning if connection is already initalized.
        if (state.getType() != ConnectionTypes.NEW) {
            return "RIBBON_WARNING:З'єднання вже ініціьовано!";
        }
        //Parsing and checning arguments
        String[] parsed = ProtocolUtils.parseBasicArguments(args, 3);
        if (parsed == null) {
            return ProtocolUtils.ERROR_RESPONSE + "Помилка команди!";
        }
        //Type initialization
        Optional<ConnectionTypes> typeOpt = Arrays.stream(ConnectionTypes.values())
            .filter(ct -> Objects.equals(ct.name(), parsed[0])).findAny();
        if (typeOpt.isEmpty()) {
            return ProtocolUtils.ERROR_RESPONSE + "Невідомий тип з'єднання!";
        }
        ConnectionTypes initType = typeOpt.get();
        if (!initType.getAvailableForInit()) {
            return ProtocolUtils.ERROR_RESPONSE + "Помилка команди: тип з'єднання " + parsed[0] + " не можна використовувати!";
        }
        //Protocol init
        if (!ProtocolUtils.RIBBON_PROTOCOL_VERSION.equals(parsed[1])) {
            return ProtocolUtils.ERROR_RESPONSE + "Невідомий ідентефікатор протокола.";
        }
        //Codepage init
        Charset charset = null;
        if (!ProtocolUtils.RIBBON_PROTOCOL_DEFAULT_CHARSET.equals(parsed[2])) {
            if (!Charset.isSupported(parsed[2])) {
                return ProtocolUtils.ERROR_RESPONSE + "Невідомий ідентефікатор кодової сторінки.";
            }
            charset = Charset.forName(parsed[2]);
        }
        state.setType(initType);
        state.setCharset(charset);
        return ProtocolUtils.OK_RESPONSE;
    }
    
}
