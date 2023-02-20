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
package tk.freaxsoftware.ribbon2.gateway.io;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tk.freaxsoftware.extras.bus.MessageHolder;
import tk.freaxsoftware.extras.bus.annotation.Receive;
import tk.freaxsoftware.extras.bus.bridge.http.TypeResolver;
import tk.freaxsoftware.ribbon2.io.core.IOLocalIds;
import tk.freaxsoftware.ribbon2.io.core.ModuleRegistration;

/**
 * Service for handling IO modules messages (singleton).
 * @author Stanislav Nepochatov
 */
public class IOService {
    
    private static IOService instance;
    
    private final static Logger LOGGER = LoggerFactory.getLogger(IOService.class);
    
    private final List<ModuleRegistration> registrations = new CopyOnWriteArrayList<>();
    
    private final Map<String, Set<String>> exportDirectories = new ConcurrentHashMap();
    
    private IOService() {
        TypeResolver.register(IOLocalIds.IO_REGISTER_EXPORT_DIRS_TYPE_NAME, IOLocalIds.IO_REGISTER_EXPORT_DIRS_TYPE_TOKEN);
    }
    
    @Receive(IOLocalIds.IO_REGISTER_TOPIC)
    public void registerModule(MessageHolder<ModuleRegistration> registrationHolder) {
        ModuleRegistration registration = registrationHolder.getContent();
        LOGGER.info("Registering {} module for protocol {}", registration.getType(), registration.getProtocol());
        registrations.add(registration);
    }
    
    @Receive(IOLocalIds.IO_REGISTER_EXPORT_DIRS)
    public void registerExportDirs(MessageHolder<Map<String, Set<String>>> dirsHolder) {
        Map<String, Set<String>> exportDirMap = dirsHolder.getContent();
        for (Entry<String, Set<String>> dirEntry: exportDirMap.entrySet()) {
            LOGGER.info("Adding export record for dir {} on schemes {}", dirEntry.getKey(), dirEntry.getValue());
            if (!exportDirectories.containsKey(dirEntry.getKey())) {
                exportDirectories.put(dirEntry.getKey(), new CopyOnWriteArraySet());
            }
            exportDirectories.get(dirEntry.getKey()).addAll(dirEntry.getValue());
        }
    }

    public List<ModuleRegistration> getRegistrations() {
        return registrations;
    }
    
    public ModuleRegistration getById(String id) {
        ModuleRegistration registration = null;
        for (ModuleRegistration module: registrations) {
            if (Objects.equals(module.getId(), id)) {
                registration = module;
                break;
            }
        }
        return registration;
    }
    
    public static IOService getInstance() {
        if (instance == null) {
            instance = new IOService();
        }
        return instance;
    }
}
