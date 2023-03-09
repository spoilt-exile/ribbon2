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

import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tk.freaxsoftware.extras.bus.MessageHolder;
import tk.freaxsoftware.extras.bus.annotation.Receive;
import tk.freaxsoftware.extras.bus.bridge.http.TypeResolver;
import tk.freaxsoftware.ribbon2.gateway.io.data.IOModuleScheme;
import tk.freaxsoftware.ribbon2.io.core.IOLocalIds;
import tk.freaxsoftware.ribbon2.io.core.ModuleRegistration;
import tk.freaxsoftware.ribbon2.io.core.SchemeInstance;
import tk.freaxsoftware.ribbon2.io.core.SchemeStatusUpdate;

/**
 * Service for handling IO modules messages (singleton).
 * @author Stanislav Nepochatov
 */
public class IOService {
    
    private static IOService instance;
    
    private final static Logger LOGGER = LoggerFactory.getLogger(IOService.class);
    
    private final Set<ModuleRegistration> registrations = new CopyOnWriteArraySet<>();
    
    private IOService() {
        TypeResolver.register(IOLocalIds.IO_REGISTER_EXPORT_DIRS_TYPE_NAME, IOLocalIds.IO_REGISTER_EXPORT_DIRS_TYPE_TOKEN);
        TypeResolver.register(IOLocalIds.IO_SCHEME_STATUS_UPDATED_TYPE_NAME, IOLocalIds.IO_SCHEME_STATUS_UPDATED_TYPE_TOKEN);
    }
    
    @Receive(IOLocalIds.IO_REGISTER_TOPIC)
    public void registerModule(MessageHolder<ModuleRegistration> registrationHolder) {
        ModuleRegistration registration = registrationHolder.getContent();
        LOGGER.info("Registering {} module for protocol {}", registration.getType(), registration.getProtocol());
        registrations.add(registration);
    }
    
    @Receive(IOLocalIds.IO_SCHEME_STATUS_UPDATED_TOPIC)
    public void schemeStatusUpdated(MessageHolder<Set<SchemeStatusUpdate>> updatesHolder) {
        for (SchemeStatusUpdate statusUpdate: updatesHolder.getContent()) {
            Optional<ModuleRegistration> regOpt = registrations.stream()
                    .filter(reg -> Objects.equals(reg.getId(), statusUpdate.getId()))
                    .findFirst();
            if (regOpt.isPresent()) {
                regOpt.get().getSchemes().put(statusUpdate.getScheme(), statusUpdate.buildInstance());
            } else {
                LOGGER.warn("Skip update status for scheme {}, protocol {}, type {} cause registration not found.", 
                        statusUpdate.getScheme(), statusUpdate.getProtocol(), statusUpdate.getType());
            }
        }
    }

    public Set<ModuleRegistration> getRegistrations() {
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
    
    public Set<IOModuleScheme> getSchemesByExportDirectiry(String directory) {
        Set<IOModuleScheme> schemes = new HashSet();
        for (ModuleRegistration registration: registrations) {
            for (Entry<String, SchemeInstance> entry: registration.getSchemes().entrySet()) {
                if (entry.getValue().getExportDirectories().contains(directory)) {
                    schemes.add(new IOModuleScheme(registration, entry.getKey(), entry.getValue()));
                }
            }
        }
        return schemes;
    }
    
    public static IOService getInstance() {
        if (instance == null) {
            instance = new IOService();
        }
        return instance;
    }
}
