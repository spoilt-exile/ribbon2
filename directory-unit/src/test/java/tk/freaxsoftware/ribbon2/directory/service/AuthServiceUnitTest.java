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
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
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
    public void shouldCheckDirAccessGrantByUser() {
        Mockito.when(userRespository.findByLogin("test")).thenReturn(new UserEntity(null, "test", Collections.EMPTY_SET));
        Mockito.when(permissionRepository.findByKey("canCreateMessage")).thenReturn(new Permission(null, "canCreateMessage", false, null, null));
        Mockito.when(directoryRepository.findDirByPathsReverse(new String[]{"System", "System.Test"}))
                .thenReturn(Set.of(new Directory(null, "Test", "System.Test", null, 
                        Set.of(new DirectoryAccessModel("test", DirectoryAccessModel.Type.USER, Map.of("canCreateMessage", true))))));
        Boolean result = authService.checkDirAccess("test", "System.Test", "canCreateMessage");
        Assert.assertTrue(result);
    }

    @Test
    public void shouldCheckDirAccessGrantByGroup() {
        Mockito.when(userRespository.findByLogin("test")).thenReturn(new UserEntity(null, "test", Set.of(new GroupEntity(null, "users"))));
        Mockito.when(permissionRepository.findByKey("canCreateMessage")).thenReturn(new Permission(null, "canCreateMessage", false, null, null));
        Mockito.when(directoryRepository.findDirByPathsReverse(new String[]{"System", "System.Test"}))
                .thenReturn(Set.of(new Directory(null, "Test", "System.Test", null, 
                        Set.of(new DirectoryAccessModel("users", DirectoryAccessModel.Type.GROUP, Map.of("canCreateMessage", true))))));
        Boolean result = authService.checkDirAccess("test", "System.Test", "canCreateMessage");
        Assert.assertTrue(result);
    }
    
    @Test
    public void shouldCheckDirAccessGrantBySpecifiedAll() {
        Mockito.when(userRespository.findByLogin("test")).thenReturn(new UserEntity(null, "test", Set.of(new GroupEntity(null, "users"))));
        Mockito.when(permissionRepository.findByKey("canReadMessage")).thenReturn(new Permission(null, "canReadMessage", false, null, null));
        Mockito.when(directoryRepository.findDirByPathsReverse(new String[]{"System", "System.Test"}))
                .thenReturn(Set.of(new Directory(null, "Test", "System.Test", null, 
                        Set.of(new DirectoryAccessModel(null, DirectoryAccessModel.Type.ALL, Map.of("canReadMessage", true))))));
        Boolean result = authService.checkDirAccess("test", "System.Test", "canReadMessage");
        Assert.assertTrue(result);
    }
    
    @Test
    public void shouldCheckDirAccessGrantByDefaultAll() {
        Mockito.when(userRespository.findByLogin("test")).thenReturn(new UserEntity(null, "test", Set.of(new GroupEntity(null, "users"))));
        Mockito.when(permissionRepository.findByKey("canReadMessage")).thenReturn(new Permission(null, "canReadMessage", true, null, null));
        Mockito.when(directoryRepository.findDirByPathsReverse(new String[]{"System", "System.Test"}))
                .thenReturn(Set.of(new Directory(null, "Test", "System.Test", null, null)));
        Boolean result = authService.checkDirAccess("test", "System.Test", "canReadMessage");
        Assert.assertTrue(result);
    }
    
    @Test
    public void shouldCheckDirAccessGrantByOneGroupFromMultiple() {
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
    public void shouldCheckDirAccessGrantByUpperDirectory() {
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
    public void shouldFailCheckDirAccessForbiddenOverridingDefaultAll() {
        Mockito.when(userRespository.findByLogin("test")).thenReturn(new UserEntity(null, "test", Set.of(new GroupEntity(null, "users"))));
        Mockito.when(permissionRepository.findByKey("canReadMessage")).thenReturn(new Permission(null, "canReadMessage", true, null, null));
        Mockito.when(directoryRepository.findDirByPathsReverse(new String[]{"System", "System.Test"}))
                .thenReturn(Set.of(new Directory(null, "Test", "System.Test", null, Set.of(new DirectoryAccessModel("users", DirectoryAccessModel.Type.GROUP, Map.of("canReadMessage", false))))));
        Boolean result = authService.checkDirAccess("test", "System.Test", "canReadMessage");
        Assert.assertFalse(result);
    }

    @Test(expected = CoreException.class)
    public void shouldFailCheckDirAccessAbsentUser() {
        authService.checkDirAccess("test", "System.Test", "canCreateMessage");
    }

    @Test(expected = CoreException.class)
    public void shouldFailCheckDirAccessAbsentPermission() {
        Mockito.when(userRespository.findByLogin("test")).thenReturn(new UserEntity(null, "test", null));
        authService.checkDirAccess("test", "System.Test", "canCreatePost");
    }
    
    @Test
    public void shouldGetDirectoriesByPermission() {
        Mockito.when(userRespository.findByLogin("test")).thenReturn(new UserEntity(null, "test", Set.of(new GroupEntity(null, "users"))));
        Mockito.when(permissionRepository.findByKey("canReadMessage")).thenReturn(new Permission(null, "canReadMessage", true, null, null));
        
        Directory testRoot = new Directory(null, "Test", "Test", null, Set.of(new DirectoryAccessModel("users", DirectoryAccessModel.Type.GROUP, Map.of("canReadMessage", false))));
        Directory testGroupAccess = new Directory(null, "GroupAccess", "Test.GroupAccess", null, Set.of(new DirectoryAccessModel("users", DirectoryAccessModel.Type.GROUP, Map.of("canReadMessage", true))));
        Directory testUserAccess = new Directory(null, "UserAccess", "Test.UserAccess", null, Set.of(new DirectoryAccessModel("test", DirectoryAccessModel.Type.USER, Map.of("canReadMessage", true))));
        Directory testAllAccess = new Directory(null, "AllAccess", "Test.AllAccess", null, Set.of(new DirectoryAccessModel(null, DirectoryAccessModel.Type.ALL, Map.of("canReadMessage", true))));
        Directory testNotAccessable = new Directory(null, "NotAccessable", "Test.NotAccessable", null, null);
        Directory defaultRoot = new Directory(null, "Default", "Default", null, null);
        Directory defaultAccessable = new Directory(null, "Accessable", "Default.Accessable", null, null);
        
        Mockito.when(directoryRepository.findAllDirectories()).thenReturn(Set.of(testRoot, testGroupAccess, testUserAccess, testAllAccess, testNotAccessable, defaultRoot, defaultAccessable));
        List<Directory> accessDirs = authService.getDirectoriesByPermission("test", "canReadMessage");
        Assert.assertFalse(accessDirs.isEmpty());
        Set<String> accessDirNames = accessDirs.stream().map(dir -> dir.getFullName()).collect(Collectors.toSet());
        Assert.assertTrue(accessDirNames.contains("Test.GroupAccess"));
        Assert.assertTrue(accessDirNames.contains("Test.UserAccess"));
        Assert.assertTrue(accessDirNames.contains("Test.AllAccess"));
        Assert.assertTrue(accessDirNames.contains("Default"));
        Assert.assertTrue(accessDirNames.contains("Default.Accessable"));
        Assert.assertFalse(accessDirNames.contains("Test"));
        Assert.assertFalse(accessDirNames.contains("Test.NotAccessable"));
    }
    
    @Test(expected = CoreException.class)
    public void shouldFailGetDirectoriesByPermissionAbsentUser() {
        authService.getDirectoriesByPermission("test", "canReadMessage");
    }

    @Test(expected = CoreException.class)
    public void shouldFailGetDirectoriesByPermissionAbsentPermission() {
        Mockito.when(userRespository.findByLogin("test")).thenReturn(new UserEntity(null, "test", null));
        authService.getDirectoriesByPermission("test", "canReadMessage");
    }
    
    @Test
    public void shouldGetCurrentPermissions() {
        Mockito.when(userRespository.findByLogin("test")).thenReturn(new UserEntity(null, "test", Set.of(new GroupEntity(null, "users"))));
        Permission canReadMessage = new Permission(null, "canReadMessage", true, null, null);
        Permission canDeleteMessage = new Permission(null, "canDeleteMessage", false, null, null);
        Permission canCreateMessage = new Permission(null, "canCreateMessage", false, null, null);
        Permission canUpdateMessage = new Permission(null, "canUpdateMessage", false, null, null);
        
        Mockito.when(permissionRepository.findAllPermissions()).thenReturn(Set.of(canReadMessage, canDeleteMessage, canCreateMessage, canUpdateMessage));
        
        Set<Permission> accessPermissions = authService.getCurrentPermissions("test", "Test.Edit");
        Assert.assertFalse(accessPermissions.isEmpty());
        Set<String> accessPermNames = accessPermissions.stream().map(perm -> perm.getKey()).collect(Collectors.toSet());
        Assert.assertTrue(accessPermNames.contains("canReadMessage"));
        
        Directory testRoot = new Directory(null, "Test", "Test", null, 
                Set.of(new DirectoryAccessModel("users", DirectoryAccessModel.Type.GROUP, 
                        Map.of("canReadMessage", true, "canCreateMessage", true, "canUpdateMessage", true, "canDeleteMessage", false))));
        Directory testEdit = new Directory(null, "Edit", "Test.Edit", null, null);
        
        Mockito.when(directoryRepository.findDirByPathsReverse(new String[]{"Test", "Test.Edit"}))
                .thenReturn(Set.of(testEdit, testRoot));
        
        Set<Permission> accessPermissions2 = authService.getCurrentPermissions("test", "Test.Edit");
        Assert.assertFalse(accessPermissions2.isEmpty());
        Set<String> accessPermNames2 = accessPermissions2.stream().map(perm -> perm.getKey()).collect(Collectors.toSet());
        Assert.assertTrue(accessPermNames2.contains("canReadMessage"));
        Assert.assertTrue(accessPermNames2.contains("canCreateMessage"));
        Assert.assertTrue(accessPermNames2.contains("canUpdateMessage"));
        
    }
}
