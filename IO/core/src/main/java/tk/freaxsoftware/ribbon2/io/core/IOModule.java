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
package tk.freaxsoftware.ribbon2.io.core;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Main interface for IO modules.
 * @author Stanislav Nepochatov
 */
@Target(value=ElementType.TYPE)
@Retention(value= RetentionPolicy.RUNTIME)
public @interface IOModule {

    /** A single configuration field exposed by this module. */
    @interface ConfigEntry {
        String name();
        ConfigEntryType type();
        boolean required() default false;
        String defaultValue() default "";
    }
    
    /**
     * Id of the module.
     */
    String id();
    
    /**
     * Name of module.
     */
    String name();
    
    /**
     * Name of the module protocol.
     */
    String protocol();
    
    /**
     * Required keys to config this module.
     */
    String[] requiredConfigKeys();

    /** 
     * Configuration form fields. 
     * Annotation arrays are supported by Java at compile time. 
     * */
    ConfigEntry[] configEntries() default {};
    
}
