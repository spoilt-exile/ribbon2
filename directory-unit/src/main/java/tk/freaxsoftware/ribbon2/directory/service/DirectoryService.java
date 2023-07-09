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

import io.ebean.PagedList;
import java.util.Objects;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tk.freaxsoftware.extras.bus.MessageBus;
import tk.freaxsoftware.extras.bus.MessageOptions;
import tk.freaxsoftware.ribbon2.core.data.DirectoryModel;
import tk.freaxsoftware.ribbon2.core.data.request.PaginationRequest;
import tk.freaxsoftware.ribbon2.core.exception.CoreException;
import static tk.freaxsoftware.ribbon2.core.exception.RibbonErrorCodes.ACCESS_DENIED;
import static tk.freaxsoftware.ribbon2.core.exception.RibbonErrorCodes.DIRECTORY_NOT_FOUND;
import tk.freaxsoftware.ribbon2.directory.DirectoryUnit;
import tk.freaxsoftware.ribbon2.directory.entity.Directory;
import tk.freaxsoftware.ribbon2.directory.entity.converters.DirectoryConverter;
import tk.freaxsoftware.ribbon2.directory.repo.DirectoryRepository;
import tk.freaxsoftware.ribbon2.directory.repo.GroupRepository;
import tk.freaxsoftware.ribbon2.directory.repo.PermissionRepository;
import tk.freaxsoftware.ribbon2.directory.repo.UserRepository;

/**
 * Directory service.
 * @author Stanislav Nepochatov
 */
public class DirectoryService extends AuthService {
    
    private final DirectoryConverter converter = new DirectoryConverter();
    
    private static final Logger LOGGER = LoggerFactory.getLogger(DirectoryService.class);

    public DirectoryService(DirectoryRepository directoryRepository, UserRepository userRespository, 
            GroupRepository groupRepository, PermissionRepository permissionRepository) {
        super(directoryRepository, userRespository, groupRepository, permissionRepository);
        checkErrorDir();
    }
    
    private void checkErrorDir() {
        String errorDirName = DirectoryUnit.config.getDirectory().getErrorDir();
        Directory errorDir = directoryRepository.findDirectoryByPath(errorDirName);
        if (errorDir == null && !DirectoryUnit.config.getDirectory().getCreateDirs().contains(errorDirName)) {
            LOGGER.error("Error directory '{}' not found or doesn't exist.", errorDirName);
            throw new IllegalArgumentException(String.format("Error directoty '%s' not found or doesn't exist.", errorDirName));
        }
    }
    
    /**
     * Creats new directory. Also creates chain of autogenerated directories required to complete path.
     * @param directory directory;
     * @param user current user login;
     * @return new created directory;
     */
    public Directory createDirectory(Directory directory, String user) {
        LOGGER.info("Create directory {} by user {}", directory.getFullName(), user);
        Set<Directory> directoryBranch = directoryRepository.findDirByPaths(preparePathChunks(directory.getFullName(), ""));
        String lastCreatedDirPath = getPathOfLastDir(directoryBranch);
        if (checkDirAccess(user, directory.getFullName(), DirectoryModel.PERMISSION_CAN_CREATE_DIRECTORY)) {
            String[] requiredDirs = preparePathChunks(directory.getFullName(), lastCreatedDirPath);
            for (String requiredDir: requiredDirs) {
                if (!Objects.equals(requiredDir, directory.getFullName()) 
                        && !Objects.equals(requiredDir, lastCreatedDirPath)) {
                    LOGGER.info("Creating required directory {}", requiredDir);
                    Directory newDir = new Directory();
                    newDir.setFullName(requiredDir);
                    newDir.setName(getNameFromPath(requiredDir));
                    newDir.setDescription("<AUTOGENERATED>");
                    directoryRepository.save(newDir);
                    MessageBus.fire(DirectoryModel.NOTIFICATION_DIRECTORY_CREATED, converter.convertBack(newDir), 
                            MessageOptions.Builder.newInstance().deliveryNotification(5).build());
                }
            }
            directory.setName(getNameFromPath(directory.getFullName()));
            return directoryRepository.save(directory);
        } else {
            throw new CoreException(ACCESS_DENIED, "User doesn't have sufficient permission");
        }
    }
    
    /**
     * Updates existing directory. In fact allows to change only description of the directory.
     * @param directory directory to update;
     * @param user current user login;
     * @return updated directory;
     */
    public Directory updateDirectory(Directory directory, String user) {
        LOGGER.info("Update directory {} by user {}", directory.getFullName(), user);
        if (!checkDirAccess(user, directory.getFullName(), DirectoryModel.PERMISSION_CAN_UPDATE_DIRECTORY)) {
            throw new CoreException(ACCESS_DENIED, "User doesn't have sufficient permission");
        }
        Directory existingDir = directoryRepository.findDirectoryByPath(directory.getFullName());
        if (existingDir != null) {
            existingDir.setDescription(directory.getDescription());
            return directoryRepository.save(existingDir);
        }
        throw new CoreException(DIRECTORY_NOT_FOUND, "Can't find directory " + directory.getFullName());
    }
    
    /**
     * Deletes existing directory and it's children if it's present.
     * @param fullPath directory path to delete;
     * @param user current user login;
     * @return set of deleted directories;
     */
    public Set<Directory> deleteDirectory(String fullPath, String user) {
        LOGGER.info("Delete directory and it's children {} by user {}", fullPath, user);
        if (!checkDirAccess(user, fullPath, DirectoryModel.PERMISSION_CAN_DELETE_DIRECTORY)) {
            throw new CoreException(ACCESS_DENIED, "User doesn't have sufficient permission");
        }
        Set<Directory> deleteDirs = directoryRepository.findDirectoriesByPath(fullPath);
        if (!deleteDirs.isEmpty()) {
            directoryRepository.delete(deleteDirs);
            return deleteDirs;
        }
        throw new CoreException(DIRECTORY_NOT_FOUND, "Can't find directory " + fullPath);
    }
    
    private String getNameFromPath(String path) {
        String[] splited = path.trim().split("\\.");
        return splited[splited.length - 1];
    }
    
    private String getPathOfLastDir(Set<Directory> directories) {
        String path = "";
        for (Directory directory: directories) {
            if (directory.getFullName().length() > path.length()) {
                path = directory.getFullName();
            }
        }
        return path;
    }
    
    /**
     * Find page of directories.
     * @param request pagination request;
     * @return paged list;
     */
    public PagedList<Directory> findDirectoryPage(PaginationRequest request) {
        LOGGER.info("Find directories paged: {}", request);
        return directoryRepository.findPage(request);
    }
    
    /**
     * Find directory by path.
     * @param path directory path;
     * @return directory or throw exception;
     */
    public Directory findByPath(String path) {
        LOGGER.info("Find directories by path: {}", path);
        Directory found =  directoryRepository.findDirectoryByPath(path);
        if (found == null) {
            throw new CoreException(DIRECTORY_NOT_FOUND, "Can't find directory " + path);
        }
        return found;
    }
}
