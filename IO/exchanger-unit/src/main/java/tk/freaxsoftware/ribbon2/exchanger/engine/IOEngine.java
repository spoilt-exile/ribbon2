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
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tk.freaxsoftware.extras.bus.MessageBus;
import tk.freaxsoftware.extras.bus.MessageOptions;
import tk.freaxsoftware.extras.bus.bridge.http.LocalHttpCons;
import tk.freaxsoftware.extras.bus.storage.StorageInterceptor;
import tk.freaxsoftware.ribbon2.core.data.request.DirectoryCheckAccessRequest;
import tk.freaxsoftware.ribbon2.core.data.request.MessagePropertyRegistrationRequest;
import tk.freaxsoftware.ribbon2.core.exception.CoreException;
import static tk.freaxsoftware.ribbon2.core.exception.RibbonErrorCodes.ACCESS_DENIED;
import static tk.freaxsoftware.ribbon2.core.exception.RibbonErrorCodes.CALL_ERROR;
import tk.freaxsoftware.ribbon2.core.utils.MessageUtils;
import tk.freaxsoftware.ribbon2.exchanger.ExchangerUnit;
import tk.freaxsoftware.ribbon2.io.core.IOLocalIds;
import tk.freaxsoftware.ribbon2.io.core.IOModule;
import tk.freaxsoftware.ribbon2.io.core.IOScheme;
import tk.freaxsoftware.ribbon2.io.core.ModuleRegistration;
import tk.freaxsoftware.ribbon2.io.core.ModuleType;
import tk.freaxsoftware.ribbon2.io.core.SchemeInstance;
import tk.freaxsoftware.ribbon2.io.core.SchemeStatusUpdate;
import tk.freaxsoftware.ribbon2.io.core.exporter.Exporter;
import tk.freaxsoftware.ribbon2.io.core.importer.Importer;

/**
 * Engine for IO operations. Prepares list of modules by specified module classes.
 * @author Stanislav Nepochatov
 * @param <T> type of the IO module;
 */
public abstract class IOEngine<T> {
    
    public static final String GENERAL_ERROR_HANDLING_KEY = "generalErrorHandling";
    
    private final static Logger LOGGER = LoggerFactory.getLogger(IOEngine.class);
    
    protected List<ModuleWrapper<T>> modules  = new ArrayList<>();
    
    private final Map<String, Instant> permissionCache;
    
    /**
     * Default constructor.
     * @param type IO operation type;
     * @param classes list of module classes from config;
     */
    public IOEngine(ModuleType type, String[] classes) {
        if (ExchangerUnit.config.getExchanger().getEnablePermissionCaching()) {
            permissionCache = new ConcurrentHashMap<>();
        } else {
            permissionCache = Collections.EMPTY_MAP;
        }
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
     * Creates or updates existing scheme. 
     * Also launch new task or restart existing if present. 
     * Effectivly only description and config can be updated.
     * @param scheme scheme to save;
     * @param username name of user for further permission check;
     * @return same scheme;
     */
    public abstract IOScheme saveScheme(IOScheme scheme, String username);
    
    /**
     * Get scheme by name.
     * @param name name of scheme to get;
     * @return scheme or throws exception if not found;
     */
    public abstract IOScheme getScheme(String name);
    
    /**
     * Deletes scheme by name and stops it's task.
     * @param name name of the scheme to delete;
     * @return result of deletion as boolean value;
     */
    public abstract Boolean deleteScheme(String name);
    
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
    
    /**
     * Get module by string id (type:protocol);
     * @param moduleId string form of module id;
     * @return optional of the module;
     */
    protected Optional<ModuleWrapper<T>> getModule(String moduleId) {
        return modules.stream().filter(m -> Objects.equals(m.getModuleData().id(), moduleId)).findFirst();
    }
    
    /**
     * Sends registration of the module to the gateway and register property on messenger.
     * @param wrapper wrapper of module;
     * @param type type of IO;
     * @param schemes array with name of schemes;
     * @return created module registration;
     */
    protected ModuleRegistration sendRegistration(ModuleWrapper<T> wrapper, ModuleType type, Map<String, SchemeInstance> schemes) {
        ModuleRegistration registration = new ModuleRegistration(wrapper.getModuleData().id(), 
                type, wrapper.getModuleData().protocol(), wrapper.getModuleData().requiredConfigKeys(), 
                wrapper.getSchemes());
        MessageBus.fire(IOLocalIds.IO_REGISTER_TOPIC, registration, 
                MessageOptions.Builder.newInstance().deliveryNotification()
                        .async().pointToPoint().build());
        MessagePropertyRegistrationRequest propertyRegistrationRequest = new MessagePropertyRegistrationRequest();
        propertyRegistrationRequest.setTag(wrapper.getPropertyTag());
        propertyRegistrationRequest.setPropertyTypes(Arrays.asList(new MessagePropertyRegistrationRequest.Entry(wrapper.getPropetyType(), wrapper.getModuleData().name())));
        MessageBus.fire(MessagePropertyRegistrationRequest.CALL_REGISTER_PROPERTY, propertyRegistrationRequest, 
                MessageOptions.Builder.newInstance().deliveryNotification()
                        .async().pointToPoint().build());
        MessageBus.addSubscription(registration.schemeSaveTopic(), (holder) -> {
            String username = MessageUtils.getAuthFromHeader(holder);
            holder.getResponse().setContent(saveScheme((IOScheme) holder.getContent(), username));
        });
        MessageBus.addSubscription(registration.schemeGetTopic(), (holder) -> {
            holder.getResponse().setContent(getScheme((String) holder.getContent()));
        });
        MessageBus.addSubscription(registration.schemeDeleteTopic(), (holder) -> {
            holder.getResponse().setContent(deleteScheme((String) holder.getContent()));
        });
        return registration;
    }
    
    protected void checkDirectoryAccess(String user, Set<String> directories, String permission) {
        LOGGER.info("Checking access for directories {} for user {} by permission.", directories, user, permission);
        DirectoryCheckAccessRequest request = processByCache(new DirectoryCheckAccessRequest(user, permission, directories));
        if (request.getDirectories().isEmpty()) {
            return;
        }
        try {
            Boolean result = MessageBus.fireCall(DirectoryCheckAccessRequest.CALL_CHECK_DIR_ACCESS, 
                    request, MessageOptions.Builder.newInstance().header(StorageInterceptor.IGNORE_STORAGE_HEADER, "true").deliveryCall().build(), Boolean.class);
            if (result) {
                addToCache(request);
                return;
            }
        } catch (Exception ex) {
            throw new CoreException(CALL_ERROR, ex.getMessage());
        }
        throw new CoreException(ACCESS_DENIED, String.format("User %s doesn't have access for current operation.", user));
    }
    
    protected void sendSchemeStatusUpdate(Set<SchemeStatusUpdate> update) {
        MessageBus.fire(IOLocalIds.IO_SCHEME_STATUS_UPDATED_TOPIC, update, MessageOptions.Builder.newInstance().header(StorageInterceptor.IGNORE_STORAGE_HEADER, "true").header(LocalHttpCons.L_HTTP_NODE_REGISTERED_TYPE_HEADER, IOLocalIds.IO_SCHEME_STATUS_UPDATED_TYPE_NAME).deliveryNotification().build());
    }
    
    private DirectoryCheckAccessRequest processByCache(DirectoryCheckAccessRequest request) {
        if (ExchangerUnit.config.getExchanger().getEnablePermissionCaching()) {
            Instant now = Instant.now();
            Set<String> processedDirs = new HashSet<>();
            for (String directory: request.getDirectories()) {
                Instant expiry = permissionCache.get(getCacheKey(directory, request.getPermission(), request.getUser()));
                if (expiry == null || (expiry != null && !expiry.isAfter(now))) {
                    processedDirs.add(directory);
                    permissionCache.remove(getCacheKey(directory, request.getPermission(), request.getUser()));
                }
            }
            request.setDirectories(processedDirs);
        }
        return request;
    }
    
    private void addToCache(DirectoryCheckAccessRequest request) {
        if (ExchangerUnit.config.getExchanger().getEnablePermissionCaching()) {
            Instant expiry = Instant.now().plus(ExchangerUnit.config.getExchanger().getPermissionCacheExpiry(), ChronoUnit.MINUTES);
            for (String directory: request.getDirectories()) {
                String key = getCacheKey(directory, request.getPermission(), request.getUser());
                if (!permissionCache.containsKey(key)) {
                    permissionCache.put(key, expiry);
                }
            }
        }
    }
    
    private String getCacheKey(String directory, String permission, String user) {
        return String.format("%s@%s@%s", user, permission, directory);
    }
    
    /**
     * Wrapper for modules.
     * @param <T> type of module;
     */
    protected static class ModuleWrapper<T> {
        
        private IOModule moduleData;
        
        private T moduleInstance;
        
        private Map<String, SchemeInstance> schemes;

        public ModuleWrapper() {
            schemes = new ConcurrentHashMap();
        }

        public ModuleWrapper(IOModule moduleData, T moduleInstance) {
            this();
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

        public Map<String, SchemeInstance> getSchemes() {
            return schemes;
        }

        public void setSchemes(Map<String, SchemeInstance> schemes) {
            this.schemes = schemes;
        }
        
        public String getPropertyTag() {
            return String.format("exc-%s", moduleData.id().replace(':', '-'));
        }
        
        public String getPropetyType() {
            return moduleData.id().replace(':', '_').toUpperCase();
        }
    }
}
