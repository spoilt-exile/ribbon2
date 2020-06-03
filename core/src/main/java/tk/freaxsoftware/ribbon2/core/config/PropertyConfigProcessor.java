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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Processing db config by using system properties.
 * @author Stanislav Nepochatov
 */
public class PropertyConfigProcessor {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(PropertyConfigProcessor.class);
    
    public enum Properties {
        
        DB_JDBC_URL("DB_JDBC_URL", (config, property) -> {config.setJdbcUrl(System.getenv(property));}),
        DB_USERNAME("DB_USERNAME", (config, property) -> {config.setJdbcUrl(System.getenv(property));}),
        DB_PASSWORD("DB_PASSWORD", (config, property) -> {config.setJdbcUrl(System.getenv(property));});
        
        private final String propertyId;

        private final PropertyProcessor processor;

        private Properties(String propertyId, PropertyProcessor processor) {
            this.propertyId = propertyId;
            this.processor = processor;
        }

        public String getPropertyId() {
            return propertyId;
        }

        public PropertyProcessor getProcessor() {
            return processor;
        }
    }
    
    public interface PropertyProcessor {
        void processOverride(DbConfig config, String property);
    }
    
    /**
     * Process config and set overrided by properties values to it.
     * @param config message bus config;
     */
    public static void process(DbConfig config) {
        for (Properties overProperty: Properties.values()) {
            if (isPropertyAvailable(overProperty.getPropertyId())) {
                try {
                    LOGGER.info("Overriding value by system property {}", overProperty.getPropertyId());
                    overProperty.getProcessor().processOverride(config, overProperty.getPropertyId());
                } catch (Exception ex) {
                    LOGGER.error("Unable to override property {} by value {}", overProperty.getPropertyId(), System.getenv(overProperty.getPropertyId()), ex);
                }
            }
        }
    }
    
    private static Boolean isPropertyAvailable(String property) {
        return System.getenv().containsKey(property);
    }
}
