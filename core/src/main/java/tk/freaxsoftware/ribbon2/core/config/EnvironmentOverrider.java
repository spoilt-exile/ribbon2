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
package tk.freaxsoftware.ribbon2.core.config;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides mechanism to override any config value by environment variable.
 * @author Stanislav Nepochatov
 */
public class EnvironmentOverrider {
    
    private final static Logger LOGGER = LoggerFactory.getLogger(EnvironmentOverrider.class);
    
    private Set<OverrideEntry> entries = new HashSet<>();
    
    /**
     * Registers override entry.
     * @param entry single override entry;
     */
    public void registerOverride(OverrideEntry entry) {
        entries.add(entry);
    }
    
    /**
     * Process config by overriding values in it by environment variable.
     * @param config config to process;
     */
    public void processConfig(Object config) {
        Set<OverrideEntry> filteredSet = entries.stream()
                .filter(entry -> Objects.equals(config.getClass(), entry.getConfigClass()))
                .filter(entry -> isVariableAvailable(entry.getVariable()))
                .collect(Collectors.toSet());
        filteredSet.forEach(en -> {
            LOGGER.info("Overriding param {} by environment", en.variable);
            en.getAction().process(config, System.getenv(en.getVariable()));
        });
    }
    
    private static Boolean isVariableAvailable(String property) {
        return System.getenv().containsKey(property);
    }
    
    /**
     * Entry for overriding config.
     * @param <C> config generic type;
     */
    public static class OverrideEntry<C> {
        
        /**
         * Name of environment variable to override.
         */
        private String variable;
        
        /**
         * Class of the config to override.
         */
        private Class<C> configClass;
        
        /**
         * Action which will be executed on overriding.
         */
        private OverrideAction<C> action;

        /**
         * Default constructor.
         * @param variable name of environment variable;
         * @param configClass config class;
         * @param action action to execute;
         */
        public OverrideEntry(String variable, Class<C> configClass, OverrideAction<C> action) {
            this.variable = variable;
            this.configClass = configClass;
            this.action = action;
        }

        public String getVariable() {
            return variable;
        }

        public void setVariable(String variable) {
            this.variable = variable;
        }

        public Class<C> getConfigClass() {
            return configClass;
        }

        public void setConfigClass(Class<C> configClass) {
            this.configClass = configClass;
        }

        public OverrideAction<C> getAction() {
            return action;
        }

        public void setAction(OverrideAction<C> action) {
            this.action = action;
        }
    }
    
    /**
     * Interface for overriding action.
     * @param <C> config generic type;
     */
    public static interface OverrideAction<C> {
        
        /**
         * Execute override of the config.
         * @param config config to override;
         * @param property value of the property from environment;
         */
        void process(C config, String property);
    }
    
}
