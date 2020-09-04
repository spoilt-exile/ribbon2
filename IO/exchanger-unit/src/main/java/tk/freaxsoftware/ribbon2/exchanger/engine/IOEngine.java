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
package tk.freaxsoftware.ribbon2.exchanger.engine;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tk.freaxsoftware.ribbon2.io.core.IOModule;
import tk.freaxsoftware.ribbon2.io.core.ModuleType;
import tk.freaxsoftware.ribbon2.io.core.exporter.Exporter;
import tk.freaxsoftware.ribbon2.io.core.importer.Importer;

/**
 * Engine for IO operations. Prepares list of modules by specified module classes.
 * @author Stanislav Nepochatov
 * @param <T> type of the IO module;
 */
public abstract class IOEngine<T> {
    
    private final static Logger LOGGER = LoggerFactory.getLogger(IOEngine.class);
    
    protected List<ModuleWrapper<T>> modules  = new ArrayList<>();
    
    /**
     * Default constructor.
     * @param type IO operation type;
     * @param classes list of module classes from config;
     */
    public IOEngine(ModuleType type, String[] classes) {
        for (String moduleClass: classes) {
            try {
                processModuleClass(type, moduleClass);
            } catch (Exception ex) {
                LOGGER.error("Error on processing module class {}", moduleClass);
                LOGGER.error("Stacktrace:", ex);
            }
        }
    }
    
    /**
     * Starts IO engine.
     */
    public abstract void start();
    
    /**
     * Process module class.
     * @param type type of current IO operation;
     * @param moduleClass full name of module class;
     * @throws ClassNotFoundException
     * @throws NoSuchMethodException
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws InvocationTargetException 
     */
    private void processModuleClass(ModuleType type, String moduleClass) throws ClassNotFoundException, 
            NoSuchMethodException, InstantiationException, IllegalAccessException, IllegalArgumentException, 
            InvocationTargetException {
        Class moduleClassInstance = Class.forName(moduleClass);
        IOModule moduleAnnotation = (IOModule) moduleClassInstance.getAnnotation(IOModule.class);
        checkModuleType(moduleClassInstance, type);
        T moduleInstance = (T) moduleClassInstance.getConstructor().newInstance();
        modules.add(new ModuleWrapper<>(moduleAnnotation, moduleInstance));
    }
    
    /**
     * Check if class of the module compatible with current type of IO operation.
     * @param moduleClass class to check;
     * @param type type of IO;
     */
    private void checkModuleType(Class moduleClass, ModuleType type) {
        if ((Objects.equals(type, ModuleType.IMPORT) && !(Importer.class.isAssignableFrom(moduleClass))) || 
                (Objects.equals(type, ModuleType.EXPORT) && !(Exporter.class.isAssignableFrom(moduleClass)))) {
            throw new IllegalArgumentException(
                    String.format("Specified module [%s] not compatible with current mode [%s]", 
                            moduleClass.getName(), type));
        }
    }
    
    protected Optional<ModuleWrapper<T>> getModule(String moduleId) {
        return modules.stream().filter(m -> Objects.equals(m.getModuleData().id(), moduleId)).findFirst();
    }
    
    /**
     * Wrapper for modules.
     * @param <T> type of module;
     */
    protected static class ModuleWrapper<T> {
        
        private IOModule moduleData;
        
        private T moduleInstance;

        public ModuleWrapper() {
        }

        public ModuleWrapper(IOModule moduleData, T moduleInstance) {
            this.moduleData = moduleData;
            this.moduleInstance = moduleInstance;
        }

        public IOModule getModuleData() {
            return moduleData;
        }

        public void setModuleData(IOModule moduleData) {
            this.moduleData = moduleData;
        }

        public T getModuleInstance() {
            return moduleInstance;
        }

        public void setModuleInstance(T moduleInstance) {
            this.moduleInstance = moduleInstance;
        }
        
    }
}
