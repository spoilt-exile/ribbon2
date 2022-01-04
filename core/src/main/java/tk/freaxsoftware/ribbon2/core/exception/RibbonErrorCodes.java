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
package tk.freaxsoftware.ribbon2.core.exception;

/**
 * Unified enum for all system error codes.
 * @author Stanislav Nepochatov
 */
public enum RibbonErrorCodes {
    
    UNREGISTERED(500),
    
    CALL_ERROR(504),
    
    ACCESS_DENIED(401),
    
    USER_NOT_FOUND(404),
    
    GROUP_NOT_FOUND(404),
    
    DIRECTORY_NOT_FOUND(404),
    
    PERMISSION_NOT_FOUND(404),
    PERMISSION_VALIDATION_FAILED(400),
    
    MESSAGE_NOT_FOUND(404),
    MESSAGE_DIRECTORIES_REQUIRED(400),
    
    PROPERTY_TYPE_NOT_FOUND(404),
    
    IO_SCHEME_NOT_FOUND(404),
    IO_SCHEME_CONFIG_ERROR(400);
    
    private int httpCode;
    
    private RibbonErrorCodes() {}
    
    private RibbonErrorCodes(int httpCode) {
        this.httpCode = httpCode;
    }
    
    public int getHttpCode() {
        return this.httpCode;
    }
    
}
