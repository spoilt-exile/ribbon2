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
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tk.freaxsoftware.extras.bus.MessageHolder;
import tk.freaxsoftware.extras.bus.annotation.Receive;
import tk.freaxsoftware.ribbon2.io.core.IOLocalIds;
import tk.freaxsoftware.ribbon2.io.core.ModuleRegistration;

/**
 * Service for handling IO modules messages.
 * @author Stanislav Nepochatov
 */
public class IOService {
    
    private final static Logger LOGGER = LoggerFactory.getLogger(IOService.class);
    
    private final List<ModuleRegistration> registrations = new CopyOnWriteArrayList<>();
    
    @Receive(IOLocalIds.IO_REGISTER_TOPIC)
    public void registerModule(MessageHolder<ModuleRegistration> registrationHolder) {
        ModuleRegistration registration = registrationHolder.getContent();
        LOGGER.info("Registering {} module for protocol {}", registration.getType(), registration.getProtocol());
        registrations.add(registration);
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
}
