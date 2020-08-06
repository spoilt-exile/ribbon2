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
 * Exception designed for input/output error handling.
 * @author Stanislav Nepochatov
 */
public class InputOutputException extends RuntimeException {
    
    private final IOExceptionCodes code;

    public InputOutputException(IOExceptionCodes code, String message) {
        super(message);
        this.code = code;
    }

    public InputOutputException(IOExceptionCodes code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    public IOExceptionCodes getCode() {
        return code;
    }
    
}
