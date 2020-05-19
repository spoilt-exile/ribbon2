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
package tk.freaxsoftware.ribbon2.directory.service;

import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tk.freaxsoftware.ribbon2.core.data.DirectoryAccessModel;
import tk.freaxsoftware.ribbon2.core.data.request.DirectoryEditAccessRequest;
import tk.freaxsoftware.ribbon2.core.exception.CoreException;
import tk.freaxsoftware.ribbon2.directory.entity.Directory;
import tk.freaxsoftware.ribbon2.directory.entity.DirectoryAccess;
import tk.freaxsoftware.ribbon2.directory.entity.GroupEntity;
import tk.freaxsoftware.ribbon2.directory.entity.UserEntity;
import tk.freaxsoftware.ribbon2.directory.repo.DirectoryAccessRepository;
import tk.freaxsoftware.ribbon2.directory.repo.DirectoryRepository;
import tk.freaxsoftware.ribbon2.directory.repo.GroupRepository;
import tk.freaxsoftware.ribbon2.directory.repo.PermissionRepository;
import tk.freaxsoftware.ribbon2.directory.repo.UserRepository;

/**
 * Authentication service for checking access.
 * @author Stanislav Nepochatov
 */
public class AuthService {
    
    private final static Logger LOGGER = LoggerFactory.getLogger(AuthService.class);
    
    /**
     * Admin login, skips checking of permissions.
     */
    public static final String ROOT_LOGIN = "root";
    
    protected DirectoryRepository directoryRepository;
    
    protected UserRepository userRespository;
    
    protected GroupRepository groupRepository;
    
    protected PermissionRepository permissionRepository;
    
    protected DirectoryAccessRepository directoryAccessRepository;

    public AuthService(DirectoryRepository directoryRepository, UserRepository userRespository, 
            GroupRepository groupRepository, PermissionRepository permissionRepository, 
            DirectoryAccessRepository directoryAccessRepository) {
        this.directoryRepository = directoryRepository;
        this.userRespository = userRespository;
        this.groupRepository = groupRepository;
        this.permissionRepository = permissionRepository;
        this.directoryAccessRepository = directoryAccessRepository;
    }
    
    /**
     * Checks whether current user has access to perform certain operation withing directory scope.
     * @param userLogin login of current user;
     * @param dirFullName full directory path;
     * @param permission name of the permission;
     * @return result of check: true - access granted, false - access denied;
     */
    public boolean checkDirAccess(String userLogin, String dirFullName, String permission) {
        LOGGER.info("Checking directory {} for user {} by permission {}", dirFullName, userLogin, permission);
        if (Objects.equals(userLogin, ROOT_LOGIN)) {
            return true;
        }
        //TODO: add logic here.
        return false;
    }
    
    /**
     * Checks if current specified user has access by permission to specified directories.
     * @param userLogin user to check login;
     * @param directories set of directory names;
     * @param permission permission to check;
     */
    public Boolean checkDirectoryAccess(String userLogin, Set<String> directories, String permission) {
        for (String directory: directories) {
            if (!checkDirAccess(userLogin, directory, permission)) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * Edits directory access if user has permission to it.
     * @param request request to edit access;
     * @param userLogin login of current user;
     * @return boolean result of operation;
     */
    public boolean editDirAccess(DirectoryEditAccessRequest request, String userLogin) {
        if (checkDirAccess(userLogin, request.getDirectoryPath(), DirectoryEditAccessRequest.PERMISSION_CAN_CREATE_DIRECTORY)) {
            Directory finded = directoryRepository.findDirectoryByPath(request.getDirectoryPath());
            if (finded == null) {
                throw new CoreException("DIR_NOT_FOUND", "Can't find directory " + request.getDirectoryPath());
            }
            validateAccessEntries(request.getAccess());
            DirectoryAccess access = directoryAccessRepository.findByDirectoryPath(finded.getFullName());
            if (access == null) {
                access = new DirectoryAccess();
                access.setDirectory(finded);
                finded.setAccess(access);
            }
            access.getAccessEntries().clear();
            access.getAccessEntries().addAll(request.getAccess());
            directoryAccessRepository.save(access);
        } else {
            throw new CoreException("NO_PERMISSION", "User doesn't have sufficient permission");
        }
        return true;
    }
    
    private void validateAccessEntries(Set<DirectoryAccessModel> access) {
        Boolean allDetected = false;
        Set<String> permissions = permissionRepository.findAllPermissionNames();
        for (DirectoryAccessModel accessEntry: access) {
            switch (accessEntry.getType()) {
                case ALL:
                    if (allDetected) {
                        throw new CoreException("VALIDATION_FAILED", "System allows only single ALL entry in request!");
                    } else {
                        allDetected = true;
                    }
                    LOGGER.warn("Assign to ALL permissions: {}", accessEntry.getPermissions());
                    break;
                case USER:
                    UserEntity findedUser = userRespository.findByLogin(accessEntry.getName());
                    if (findedUser == null) {
                        throw new CoreException("USER_NOT_FOUND", 
                                String.format("Unable to find user %s specified in request.", accessEntry.getName()));
                    }
                    LOGGER.warn("Assign to user {} permissions: {}", findedUser.getLogin(), accessEntry.getPermissions());
                    break;
                case GROUP:
                    GroupEntity findedGroup = groupRepository.findGroupByName(accessEntry.getName());
                    if (findedGroup == null) {
                        throw new CoreException("GROUP_NOT_FOUND", 
                                String.format("Unable to find group %s specified in request.", accessEntry.getName()));
                    }
                    LOGGER.warn("Assign to group {} permissions: {}", findedGroup.getName(), accessEntry.getPermissions());
                    break;
            }
            validatePermissions(accessEntry.getPermissions(), permissions);
        }
    }
    
    private void validatePermissions(Map<String, Boolean> permissions, Set<String> presentPermissions) {
        Set<String> accessPermissions = permissions.entrySet().stream()
                .map(entry -> entry.getKey()).collect(Collectors.toSet());
        for (String accessPermission: accessPermissions) {
            if (!presentPermissions.contains(accessPermission)) {
                throw new CoreException("PERMISSION_NOT_FOUND", 
                        String.format("Can't assign %s permission since it doesn't exist.", accessPermission));
            }
        }
    }
}
