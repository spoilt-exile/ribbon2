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

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tk.freaxsoftware.ribbon2.core.data.DirectoryAccessModel;
import tk.freaxsoftware.ribbon2.core.data.request.DirectoryEditAccessRequest;
import tk.freaxsoftware.ribbon2.core.exception.CoreException;
import static tk.freaxsoftware.ribbon2.core.exception.RibbonErrorCodes.ACCESS_DENIED;
import static tk.freaxsoftware.ribbon2.core.exception.RibbonErrorCodes.DIRECTORY_NOT_FOUND;
import static tk.freaxsoftware.ribbon2.core.exception.RibbonErrorCodes.GROUP_NOT_FOUND;
import static tk.freaxsoftware.ribbon2.core.exception.RibbonErrorCodes.PERMISSION_NOT_FOUND;
import static tk.freaxsoftware.ribbon2.core.exception.RibbonErrorCodes.PERMISSION_VALIDATION_FAILED;
import static tk.freaxsoftware.ribbon2.core.exception.RibbonErrorCodes.USER_NOT_FOUND;
import tk.freaxsoftware.ribbon2.directory.entity.Directory;
import tk.freaxsoftware.ribbon2.directory.entity.GroupEntity;
import tk.freaxsoftware.ribbon2.directory.entity.Permission;
import tk.freaxsoftware.ribbon2.directory.entity.UserEntity;
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

    public AuthService(DirectoryRepository directoryRepository, UserRepository userRespository, 
            GroupRepository groupRepository, PermissionRepository permissionRepository) {
        this.directoryRepository = directoryRepository;
        this.userRespository = userRespository;
        this.groupRepository = groupRepository;
        this.permissionRepository = permissionRepository;
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
        UserEntity user = userRespository.findByLogin(userLogin);
        if (user == null) {
            throw new CoreException(USER_NOT_FOUND, "Can't find user " + userLogin);
        }
        Permission permissionEntry = permissionRepository.findByKey(permission);
        if (permissionEntry == null) {
            throw new CoreException(PERMISSION_NOT_FOUND, "Can't find permission " + permission);
        }
        Optional<Directory> accessDirectoryOpt = getDirectoryWithAccess(dirFullName);
        Boolean result;
        if (accessDirectoryOpt.isPresent()) {
            LOGGER.info("Checking against directory {}", accessDirectoryOpt.get().getFullName());
            result = checkAccessAgainstDirectoryAccess(accessDirectoryOpt.get(), permissionEntry, user);
        } else {
            LOGGER.info("Checking on default value of permission.");
            result = permissionEntry.getDefaultValue();
        }
        return result;
    }
    
    private Optional<Directory> getDirectoryWithAccess(String dirFullName) {
        return directoryRepository.findDirByPathsReverse(preparePathChunks(dirFullName, ""))
                .stream().filter(dir -> dir.getAccessEntries() != null).findFirst();
    }
    
    private Boolean checkAccessAgainstDirectoryAccess(Directory directory, Permission permission, UserEntity user) {
        DirectoryAccessModel allRecord = null;
        Boolean isForbidden = false;
        Set<String> groups = user.getGroups().stream().map(gr -> gr.getName()).collect(Collectors.toSet());
        for (DirectoryAccessModel accessEntry: directory.getAccessEntries()) {
            switch (accessEntry.getType()) {
                case USER:
                    if (Objects.equals(accessEntry.getName(), user.getLogin())) {
                        if (accessEntry.getPermissions().getOrDefault(permission.getKey(), false)) {
                            LOGGER.info("Granted for user {} to directory {}", accessEntry.getName(), directory.getFullName());
                            return true;
                        } else {
                            isForbidden = accessEntry.getPermissions().containsKey(permission.getKey());
                        }
                    }
                    break;
                case GROUP:
                    if (groups.contains(accessEntry.getName())) {
                        if (accessEntry.getPermissions().getOrDefault(permission.getKey(), false)) {
                            LOGGER.info("Granted for group {} to directory {}", accessEntry.getName(), directory.getFullName());
                            return true;
                        } else {
                            isForbidden = accessEntry.getPermissions().containsKey(permission.getKey());
                        }
                    }
                    break;
                case ALL:
                    allRecord = accessEntry;
                    break;
            }
        }
        return isForbidden ? false : (allRecord != null ? allRecord.getPermissions().getOrDefault(permission.getKey(), false) : permission.getDefaultValue());
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
        if (checkDirAccess(userLogin, request.getDirectoryPath(), DirectoryEditAccessRequest.PERMISSION_CAN_EDIT_DIR_ACCESS)) {
            Directory finded = directoryRepository.findDirectoryByPath(request.getDirectoryPath());
            if (finded == null) {
                throw new CoreException(DIRECTORY_NOT_FOUND, "Can't find directory " + request.getDirectoryPath());
            }
            validateAccessEntries(request.getAccess());
            finded.setAccessEntries(request.getAccess());
            directoryRepository.save(finded);
        } else {
            throw new CoreException(ACCESS_DENIED, "User doesn't have sufficient permission");
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
                        throw new CoreException(PERMISSION_VALIDATION_FAILED, "System allows only single ALL entry in request!");
                    } else {
                        allDetected = true;
                    }
                    LOGGER.warn("Assign to ALL permissions: {}", accessEntry.getPermissions());
                    break;
                case USER:
                    UserEntity findedUser = userRespository.findByLogin(accessEntry.getName());
                    if (findedUser == null) {
                        throw new CoreException(USER_NOT_FOUND, 
                                String.format("Unable to find user %s specified in request.", accessEntry.getName()));
                    }
                    LOGGER.warn("Assign to user {} permissions: {}", findedUser.getLogin(), accessEntry.getPermissions());
                    break;
                case GROUP:
                    GroupEntity findedGroup = groupRepository.findGroupByName(accessEntry.getName());
                    if (findedGroup == null) {
                        throw new CoreException(GROUP_NOT_FOUND, 
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
                throw new CoreException(PERMISSION_NOT_FOUND, 
                        String.format("Can't assign %s permission since it doesn't exist.", accessPermission));
            }
        }
    }
    
    protected String[] preparePathChunks(String dirPath, String currentPath) {
        String[] rawChunks = dirPath.substring(currentPath.length()).trim().split("\\.");
        String[] chunks = new String[rawChunks.length];
        for (int i=0; i<rawChunks.length; i++) {
            if (currentPath.isEmpty()) {
                currentPath = rawChunks[i];
            } else {
                if (!rawChunks[i].isBlank()) {
                    currentPath = currentPath + "." + rawChunks[i];
                }
            }
            chunks[i] = currentPath;
        }
        return chunks;
    }
    
    /**
     * Get list of directories which accessable by specified permission by current user.
     * @param userLogin user login;
     * @param permissionName permission name to check;
     * @return list of the directories which specified user can access by permission.
     */
    public List<Directory> getDirectoriesByPermission(String userLogin, String permissionName) {
        LOGGER.info("Get all directories accessable by user {} with permission {}", userLogin, permissionName);
        UserEntity user = userRespository.findByLogin(userLogin);
        if (user == null) {
            throw new CoreException(USER_NOT_FOUND, "Can't find user " + userLogin);
        }
        Permission permissionEntry = permissionRepository.findByKey(permissionName);
        if (permissionEntry == null) {
            throw new CoreException(PERMISSION_NOT_FOUND, "Can't find permission " + permissionName);
        }
        Set<Directory> allDirectories = directoryRepository.findAllDirectories();
        Map<String, Boolean> checkResultMap = checkDirectoryAccessBatch(allDirectories, user, permissionEntry);
        return allDirectories.stream().filter(dir -> isNestingAccessDir(dir, checkResultMap, permissionEntry)).collect(Collectors.toList());
    }
    
    private Map<String, Boolean> checkDirectoryAccessBatch(Set<Directory> directories, UserEntity user, Permission permission) {
        Map<String, Boolean> checkMap = new HashMap();
        for (Directory dir: directories) {
            if (Objects.equals(user.getLogin(), ROOT_LOGIN)) {
                checkMap.put(dir.getFullName(), true);
            } else if (dir.getAccessEntries() != null) {
                checkMap.put(dir.getFullName(), checkAccessAgainstDirectoryAccess(dir, permission, user));
            }
        }
        return checkMap;
    }
    
    private boolean isNestingAccessDir(Directory dir, Map<String, Boolean> accessMap, Permission permission) {
        if (accessMap.containsKey(dir.getFullName())) {
            return accessMap.get(dir.getFullName());
        } else {
            Optional<String> parentAccessDirName = accessMap.entrySet().stream()
                    .map(entry -> entry.getKey())
                    .filter(dirName -> dir.getFullName().startsWith(dirName))
                    .max(Comparator.comparingInt(String::length));
            return parentAccessDirName.isPresent() ? accessMap.get(parentAccessDirName.get()) : permission.getDefaultValue();
        }
    }
    
    /**
     * Get set of permission which current user can use on specified directory.
     * @param userLogin user login;
     * @param dirPath path to directory;
     * @return set of permissions or empty;
     */
    public Set<Permission> getCurrentPermissions(String userLogin, String dirPath) {
        UserEntity user = userRespository.findByLogin(userLogin);
        if (user == null) {
            throw new CoreException(USER_NOT_FOUND, "Can't find user " + userLogin);
        }
        Optional<Directory> accessDirectoryOpt = getDirectoryWithAccess(dirPath);
        Set<Permission> permissions = permissionRepository.findAllPermissions();
        Set<Permission> result = new HashSet();
        for (Permission permission: permissions) {
            Boolean checkResult = accessDirectoryOpt.isPresent() ? checkAccessAgainstDirectoryAccess(accessDirectoryOpt.get(), permission, user) : permission.getDefaultValue();
            if (checkResult || Objects.equals(user.getLogin(), ROOT_LOGIN)) {
                result.add(permission);
            }
        }
        return result;
    }
}
