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
package tk.freaxsoftware.ribbon2.directory.facade;

import tk.freaxsoftware.extras.bus.MessageHolder;
import tk.freaxsoftware.extras.bus.ResponseHolder;
import tk.freaxsoftware.extras.bus.annotation.Receive;
import tk.freaxsoftware.ribbon2.core.data.request.DirectoryEditAccessRequest;
import tk.freaxsoftware.ribbon2.core.data.request.DirectoryCheckAccessRequest;
import tk.freaxsoftware.ribbon2.core.utils.MessageUtils;
import tk.freaxsoftware.ribbon2.directory.service.AuthService;

/**
 * Facade for handling messages related to directory access.
 * @author Stanislav Nepochatov
 */
public class DirectoryAccessFacade {
    
    private final AuthService authService;

    public DirectoryAccessFacade(AuthService authService) {
        this.authService = authService;
    }
    
    @Receive(DirectoryCheckAccessRequest.CALL_CHECK_DIR_ACCESS)
    public void checkDirectoriesAccess(MessageHolder<DirectoryCheckAccessRequest> checkCall) {
        DirectoryCheckAccessRequest request = checkCall.getContent();
        Boolean result = authService.checkDirectoryAccess(request.getUser(), request.getDirectories(), request.getPermission());
        checkCall.setResponse(new ResponseHolder());
        checkCall.getResponse().setContent(result);
    }
    
    @Receive(DirectoryEditAccessRequest.CALL_EDIT_DIR_ACCESS)
    public void editDirectoryAccess(MessageHolder<DirectoryEditAccessRequest> editCall) {
        String userLogin = MessageUtils.getAuthFromHeader(editCall);
        DirectoryEditAccessRequest request = editCall.getContent();
        Boolean result = authService.editDirAccess(request, userLogin);
        editCall.setResponse(new ResponseHolder());
        editCall.getResponse().setContent(result);
    }
    
}
