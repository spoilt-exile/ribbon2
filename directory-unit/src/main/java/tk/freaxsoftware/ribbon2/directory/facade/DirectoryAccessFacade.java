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

import java.util.List;
import java.util.stream.Collectors;
import tk.freaxsoftware.extras.bus.MessageHolder;
import tk.freaxsoftware.extras.bus.ResponseHolder;
import tk.freaxsoftware.extras.bus.annotation.Receive;
import tk.freaxsoftware.extras.bus.bridge.http.LocalHttpCons;
import tk.freaxsoftware.ribbon2.core.data.DirectoryModel;
import tk.freaxsoftware.ribbon2.core.data.DirectoryPermissionTaggedModel;
import tk.freaxsoftware.ribbon2.core.data.request.DirectoryEditAccessRequest;
import tk.freaxsoftware.ribbon2.core.data.request.DirectoryCheckAccessRequest;
import tk.freaxsoftware.ribbon2.core.utils.MessageUtils;
import tk.freaxsoftware.ribbon2.directory.entity.Directory;
import tk.freaxsoftware.ribbon2.directory.entity.converters.DirectoryConverter;
import tk.freaxsoftware.ribbon2.directory.service.AuthService;

/**
 * Facade for handling messages related to directory access.
 * @author Stanislav Nepochatov
 */
public class DirectoryAccessFacade {
    
    private final DirectoryConverter converter = new DirectoryConverter();
    
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
    
    @Receive(DirectoryModel.CALL_GET_DIRECTORY_BY_PERMISSION)
    public void getDirectoriesByPermission(MessageHolder<String> permissionMessage) {
        String permission = permissionMessage.getContent();
        String userLogin = MessageUtils.getAuthFromHeader(permissionMessage);
        permissionMessage.setResponse(new ResponseHolder());
        permissionMessage.getResponse().getHeaders().put(LocalHttpCons.L_HTTP_NODE_REGISTERED_TYPE_HEADER, DirectoryModel.DIRECTORY_LIST_TYPE_NAME);
        List<Directory> directories = authService.getDirectoriesByPermission(userLogin, permission);
        permissionMessage.getResponse().setContent(directories.stream().map(dir -> converter.convertBack(dir)).collect(Collectors.toList()));
    }
    
    @Receive(DirectoryPermissionTaggedModel.CALL_GET_CURRENT_PERMISSIONS)
    public void getCurrentPermissions(MessageHolder<String> holder) {
        String dirPath = holder.getContent();
        String userLogin = MessageUtils.getAuthFromHeader(holder);
        holder.getResponse().setContent(authService.getCurrentPermissions(userLogin, dirPath).stream().map(perm -> perm.getKey()).collect(Collectors.toSet()));
    }
}
