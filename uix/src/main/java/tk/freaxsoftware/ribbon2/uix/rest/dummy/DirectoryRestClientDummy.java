/*
 * This file is part of Ribbon2 news message system.
 * 
 * Copyright (C) 2020-2024 Freax Software
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
package tk.freaxsoftware.ribbon2.uix.rest.dummy;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Set;
import tk.freaxsoftware.ribbon2.core.data.DirectoryModel;
import tk.freaxsoftware.ribbon2.core.data.response.DirectoryPage;
import tk.freaxsoftware.ribbon2.uix.rest.DirectoryRestClient;

/**
 * Dummy directory REST client implementation.
 * @author Stanislav Nepochatov
 */
public class DirectoryRestClientDummy implements DirectoryRestClient {
    @Override
    public Set<DirectoryModel> getDirectoriesByPermission(String jwtKey, String permission) throws URISyntaxException, IOException {
        return Set.of(createDirecotry("System.Test", "Test"), createDirecotry("Dev.Null", "Null"));
    }

    @Override
    public Set<String> getDirectoriesPermissions(String jwtKey, String dirPath) throws URISyntaxException, IOException {
        return Set.of(
                "canUpdateMessage",
                "canUpdateDir",
                "canReadMessage",
                "canEditDirAccess",
                "canDeleteMessage",
                "canDeleteDir",
                "canCreateMessage",
                "canCreateDir",
                "canAssignExport"
        );
    }

    @Override
    public DirectoryPage getDirectories(String jwtKey) throws URISyntaxException, IOException {
        final DirectoryPage dirPage = new DirectoryPage();
        dirPage.setContent(List.of(
                createDirecotry("System", "System"),
                createDirecotry("System.Test", "Test"),
                createDirecotry("System.Error", "Error"),
                createDirecotry("Edit", "Edit"),
                createDirecotry("Edit.Release", "Release"),
                createDirecotry("Edit.Release.EN", "EN"),
                createDirecotry("Edit.Release.UA", "UA")
        ));
        return dirPage;
    }
    
    private static DirectoryModel createDirecotry(String path, String name) {
        DirectoryModel dir = new DirectoryModel();
        dir.setId(1000L);
        dir.setName(name);
        dir.setFullName(path);
        dir.setDescription("GENERATED");
        return dir;
    }
}
