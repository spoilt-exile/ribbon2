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

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import tk.freaxsoftware.ribbon2.core.data.DirectoryAccessModel;
import tk.freaxsoftware.ribbon2.core.exception.CoreException;
import tk.freaxsoftware.ribbon2.directory.entity.Directory;
import tk.freaxsoftware.ribbon2.directory.entity.GroupEntity;
import tk.freaxsoftware.ribbon2.directory.entity.Permission;
import tk.freaxsoftware.ribbon2.directory.entity.UserEntity;
import tk.freaxsoftware.ribbon2.directory.repo.DirectoryRepository;
import tk.freaxsoftware.ribbon2.directory.repo.GroupRepository;
import tk.freaxsoftware.ribbon2.directory.repo.PermissionRepository;
import tk.freaxsoftware.ribbon2.directory.repo.UserRepository;

/**
 * Auth service unit test.
 *
 * @author Stanislav Nepochatov
 */
public class AuthServiceUnitTest {

    private DirectoryRepository directoryRepository;

    private UserRepository userRespository;

    private GroupRepository groupRepository;

    private PermissionRepository permissionRepository;

    private AuthService authService;

    @Before
    public void init() {
        userRespository = Mockito.mock(UserRepository.class);
        groupRepository = Mockito.mock(GroupRepository.class);
        directoryRepository = Mockito.mock(DirectoryRepository.class);
        permissionRepository = Mockito.mock(PermissionRepository.class);
        authService = new AuthService(directoryRepository, userRespository,
                groupRepository, permissionRepository);
    }

    @Test
    public void grantByUser() {
        Mockito.when(userRespository.findByLogin("test")).thenReturn(new UserEntity(null, "test", Collections.EMPTY_SET));
        Mockito.when(permissionRepository.findByKey("canCreateMessage")).thenReturn(new Permission(null, "canCreateMessage", false, null, null));
        Mockito.when(directoryRepository.findDirByPathsReverse(new String[]{"System", "System.Test"}))
                .thenReturn(Set.of(new Directory(null, "Test", "System.Test", null, 
                        Set.of(new DirectoryAccessModel("test", DirectoryAccessModel.Type.USER, Map.of("canCreateMessage", true))))));
        Boolean result = authService.checkDirAccess("test", "System.Test", "canCreateMessage");
        Assert.assertTrue(result);
    }

    @Test
    public void grantByGroup() {
        Mockito.when(userRespository.findByLogin("test")).thenReturn(new UserEntity(null, "test", Set.of(new GroupEntity(null, "users"))));
        Mockito.when(permissionRepository.findByKey("canCreateMessage")).thenReturn(new Permission(null, "canCreateMessage", false, null, null));
        Mockito.when(directoryRepository.findDirByPathsReverse(new String[]{"System", "System.Test"}))
                .thenReturn(Set.of(new Directory(null, "Test", "System.Test", null, 
                        Set.of(new DirectoryAccessModel("users", DirectoryAccessModel.Type.GROUP, Map.of("canCreateMessage", true))))));
        Boolean result = authService.checkDirAccess("test", "System.Test", "canCreateMessage");
        Assert.assertTrue(result);
    }
    
    @Test
    public void grantBySpecifiedAll() {
        Mockito.when(userRespository.findByLogin("test")).thenReturn(new UserEntity(null, "test", Set.of(new GroupEntity(null, "users"))));
        Mockito.when(permissionRepository.findByKey("canReadMessage")).thenReturn(new Permission(null, "canReadMessage", false, null, null));
        Mockito.when(directoryRepository.findDirByPathsReverse(new String[]{"System", "System.Test"}))
                .thenReturn(Set.of(new Directory(null, "Test", "System.Test", null, 
                        Set.of(new DirectoryAccessModel(null, DirectoryAccessModel.Type.ALL, Map.of("canReadMessage", true))))));
        Boolean result = authService.checkDirAccess("test", "System.Test", "canReadMessage");
        Assert.assertTrue(result);
    }
    
    @Test
    public void grantByDefaultAll() {
        Mockito.when(userRespository.findByLogin("test")).thenReturn(new UserEntity(null, "test", Set.of(new GroupEntity(null, "users"))));
        Mockito.when(permissionRepository.findByKey("canReadMessage")).thenReturn(new Permission(null, "canReadMessage", true, null, null));
        Mockito.when(directoryRepository.findDirByPathsReverse(new String[]{"System", "System.Test"}))
                .thenReturn(Set.of(new Directory(null, "Test", "System.Test", null, null)));
        Boolean result = authService.checkDirAccess("test", "System.Test", "canReadMessage");
        Assert.assertTrue(result);
    }
    
    @Test
    public void grantByOneGroupFromMultiple() {
        Mockito.when(userRespository.findByLogin("test")).thenReturn(new UserEntity(null, "test", Set.of(new GroupEntity(null, "users"), new GroupEntity(null, "editors"))));
        Mockito.when(permissionRepository.findByKey("canCreateMessage")).thenReturn(new Permission(null, "canCreateMessage", false, null, null));
        Mockito.when(directoryRepository.findDirByPathsReverse(new String[]{"System", "System.Test"}))
                .thenReturn(Set.of(new Directory(null, "Test", "System.Test", null, 
                        Set.of(
                                new DirectoryAccessModel("specialists", DirectoryAccessModel.Type.GROUP, Map.of("canCreateMessage", false)),
                                new DirectoryAccessModel("users", DirectoryAccessModel.Type.GROUP, Map.of("canCreateMessage", false)),
                                new DirectoryAccessModel("editors", DirectoryAccessModel.Type.GROUP, Map.of("canCreateMessage", true))
                        )
                )));
        Boolean result = authService.checkDirAccess("test", "System.Test", "canCreateMessage");
        Assert.assertTrue(result);
    }
    
    @Test
    public void grantByUpperDirectory() {
        Mockito.when(userRespository.findByLogin("test")).thenReturn(new UserEntity(null, "test", Collections.EMPTY_SET));
        Mockito.when(permissionRepository.findByKey("canCreateMessage")).thenReturn(new Permission(null, "canCreateMessage", false, null, null));
        Mockito.when(directoryRepository.findDirByPathsReverse(new String[]{"Root", "Root.System", "Root.System.Test"}))
                .thenReturn(Set.of(
                        new Directory(null, "Root", "Root", null, Set.of(new DirectoryAccessModel("test", DirectoryAccessModel.Type.USER, Map.of("canCreateMessage", true)))),
                        new Directory(null, "System", "Root.System", null, null),
                        new Directory(null, "Test", "Root.System.Test", null, null)
                ));
        Boolean result = authService.checkDirAccess("test", "Root.System.Test", "canCreateMessage");
        Assert.assertTrue(result);
    }
    
    @Test
    public void forbiddenOverridingDefaultAll() {
        Mockito.when(userRespository.findByLogin("test")).thenReturn(new UserEntity(null, "test", Set.of(new GroupEntity(null, "users"))));
        Mockito.when(permissionRepository.findByKey("canReadMessage")).thenReturn(new Permission(null, "canReadMessage", true, null, null));
        Mockito.when(directoryRepository.findDirByPathsReverse(new String[]{"System", "System.Test"}))
                .thenReturn(Set.of(new Directory(null, "Test", "System.Test", null, Set.of(new DirectoryAccessModel("users", DirectoryAccessModel.Type.GROUP, Map.of("canReadMessage", false))))));
        Boolean result = authService.checkDirAccess("test", "System.Test", "canReadMessage");
        Assert.assertFalse(result);
    }

    @Test(expected = CoreException.class)
    public void absentUserTest() {
        authService.checkDirAccess("test", "System.Test", "canCreateMessage");
    }

    @Test(expected = CoreException.class)
    public void absentPermissionTest() {
        Mockito.when(userRespository.findByLogin("test")).thenReturn(new UserEntity(null, "test", null));
        authService.checkDirAccess("test", "System.Test", "canCreatePost");
    }
}
