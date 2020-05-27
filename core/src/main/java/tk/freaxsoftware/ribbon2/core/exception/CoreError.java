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
 * Core error is a holder for REST method errors.
 * @author Stanislav Nepochatov
 */
public class CoreError {
    
    private RibbonErrorCodes code;
    
    private String message;
    
    public CoreError() {}
    
    public CoreError(CoreException exception) {
        this.code = exception.getCode();
        this.message = exception.getMessage();
    }

    public RibbonErrorCodes getCode() {
        return code;
    }

    public void setCode(RibbonErrorCodes code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
