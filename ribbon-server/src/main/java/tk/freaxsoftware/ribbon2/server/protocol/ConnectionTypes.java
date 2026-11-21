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

/**
 * Enumeration of available connection types. 
 * Contains a flag if connection type can be used on init operaiton.
 * @author Stanislav Nepochatov
 */
public enum ConnectionTypes {

    /**
     * New connection which is not initiated properly. Can't be specified on init.
     */
    NEW(false),

    /**
     * Client connection.
     */
    CLIENT(true),

    /**
     * Control application connection (not implemented).
     */
    CONTROL(true),

    /**
     * Any type of connection (wildcard). Can't be specified on init.
     */
    ANY(false);

    private Boolean availableForInit = false;

    ConnectionTypes(Boolean availableForInit) {
        this.availableForInit = availableForInit;
    }

    public Boolean getAvailableForInit() {
        return availableForInit;
    }
}
