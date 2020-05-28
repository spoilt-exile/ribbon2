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

import java.util.Objects;
import tk.freaxsoftware.extras.bus.GlobalCons;
import tk.freaxsoftware.extras.bus.MessageBus;
import tk.freaxsoftware.extras.bus.ResponseHolder;
import tk.freaxsoftware.extras.bus.exceptions.MessageExceptionCallback;

/**
 * Ribbon2 message exception callback for message bus.
 * @author Stanislav Nepochatov
 */
public class RibbonMessageExceptionCallback implements MessageExceptionCallback {

    @Override
    public void callback(ResponseHolder response) throws Exception {
        if (!MessageBus.isSuccessful(response.getHeaders())) {
            RibbonErrorCodes errCode = getErrorCode((String) response.getHeaders().get(CoreException.HEADER_ERROR_CODE));
            throw new CoreException(errCode, (String) response.getHeaders().get(GlobalCons.G_EXCEPTION_MESSAGE_HEADER));
        }
    }
    
    private RibbonErrorCodes getErrorCode(String codeStr) {
        if (codeStr == null) {
            return RibbonErrorCodes.UNREGISTERED;
        }
        for (RibbonErrorCodes code: RibbonErrorCodes.values()) {
            if (Objects.equals(codeStr, code.name())) {
                return code;
            }
        }
        return RibbonErrorCodes.UNREGISTERED;
    }
    
}
