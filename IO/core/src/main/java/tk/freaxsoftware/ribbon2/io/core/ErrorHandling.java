/*
 * This file is part of Ribbon2 news message system.
 * 
 * Copyright (C) 2020-2022 Freax Software
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
package tk.freaxsoftware.ribbon2.io.core;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Enum with options for error handling strategy.
 * @author Stanislav Nepochatov
 */
public enum ErrorHandling {
    
    /**
     * Raises admin level error: gateway notified about new state, 
     * also admin notified by service message to the system. For important 
     * data streams.
     */
    RAISE_ADM_ERROR,
    
    /**
     * Raises normal level error: gateway notified about new state. 
     * No additional messages. Default.
     */
    RAISE_ERROR,
    
    /**
     * No error raised. All queues will be cleared of messages. For tests and debugging.
     */
    DROP_ERROR;
    
    public final static String GENERAL_ERROR_HANDLING_KEY = "generalErrorHandling";
    
    /**
     * Get error handling strategy from config.
     * @param config scheme config;
     * @return error hadnling strategy from config or default value;
     */
    public static ErrorHandling errorHandling(Map<String, Object> config) {
        String rawStrategy = (String) config.getOrDefault(GENERAL_ERROR_HANDLING_KEY, ErrorHandling.RAISE_ERROR.name());
        Set<String> currentStrategies = Arrays.stream(ErrorHandling.values()).map(err -> err.name()).collect(Collectors.toSet());
        return currentStrategies.contains(rawStrategy) ? ErrorHandling.valueOf(rawStrategy) : ErrorHandling.RAISE_ERROR;
    }
    
}
