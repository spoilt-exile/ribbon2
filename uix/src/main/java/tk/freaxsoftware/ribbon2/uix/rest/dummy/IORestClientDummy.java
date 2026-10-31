/*
 * This file is part of Ribbon2 news message system.
 * 
 * Copyright (C) 2026 Freax Software
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
import java.util.Optional;

import tk.freaxsoftware.ribbon2.io.core.IOScheme;
import tk.freaxsoftware.ribbon2.io.core.ModuleType;
import tk.freaxsoftware.ribbon2.uix.model.io.IOModuleScheme;
import tk.freaxsoftware.ribbon2.uix.model.io.IOProtocol;
import tk.freaxsoftware.ribbon2.uix.rest.IORestClient;

public class IORestClientDummy implements IORestClient {

    @Override
    public List<IOProtocol> getProtocols(String jwtKey) throws URISyntaxException, IOException {
        return List.of(
            new IOProtocol()
                .setId("import:plain")
                .setProtocol("plain")
                .setType(ModuleType.IMPORT),
            new IOProtocol()
                .setId("import:telex")
                .setProtocol("telex")
                .setType(ModuleType.IMPORT),
            new IOProtocol()
                .setId("export:telex")
                .setProtocol("telex")
                .setType(ModuleType.EXPORT)
        );
    }

    @Override
    public List<IOModuleScheme> getSchemes(String jwtKey) throws URISyntaxException, IOException {
        return List.of();
    }

    @Override
    public List<IOModuleScheme> getExportSchemesByDirectory(String jwtKey, String directory) throws URISyntaxException, IOException {
        return List.of();
    }

    @Override
    public Optional<IOScheme> getScheme(String jwtKey, String type, String protocol, String name) throws URISyntaxException, IOException {
        return Optional.empty();
    }

    @Override
    public IOScheme saveScheme(String jwtKey, IOScheme scheme) throws URISyntaxException, IOException {
        return scheme;
    }

    @Override
    public void deleteScheme(String jwtKey, String type, String protocol, String name) throws URISyntaxException, IOException {}

    @Override
    public void assignExportScheme(String jwtKey, String protocol, String name, String dir) throws URISyntaxException, IOException {}

    @Override
    public void dismissExportScheme(String jwtKey, String protocol, String name, String dir) throws URISyntaxException, IOException {}
    
}
