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

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tk.freaxsoftware.extras.bus.MessageBus;
import tk.freaxsoftware.extras.bus.MessageOptions;
import tk.freaxsoftware.ribbon2.core.data.MessageModel;
import tk.freaxsoftware.ribbon2.core.data.MessagePropertyModel;
import tk.freaxsoftware.ribbon2.core.data.UserModel;
import tk.freaxsoftware.ribbon2.core.exception.CoreException;
import static tk.freaxsoftware.ribbon2.core.exception.RibbonErrorCodes.IO_SCHEME_NOT_FOUND;
import tk.freaxsoftware.ribbon2.exchanger.ExchangerUnit;
import tk.freaxsoftware.ribbon2.exchanger.converters.SchemeConverter;
import static tk.freaxsoftware.ribbon2.exchanger.engine.IOEngine.sendSchemeStatusUpdate;
import tk.freaxsoftware.ribbon2.exchanger.entity.Register;
import tk.freaxsoftware.ribbon2.exchanger.entity.Scheme;
import tk.freaxsoftware.ribbon2.exchanger.repository.DirectoryRepository;
import tk.freaxsoftware.ribbon2.exchanger.repository.RegisterRepository;
import tk.freaxsoftware.ribbon2.exchanger.repository.SchemeRepository;
import tk.freaxsoftware.ribbon2.io.core.ErrorHandling;
import tk.freaxsoftware.ribbon2.io.core.IOExceptionCodes;
import tk.freaxsoftware.ribbon2.io.core.IOScheme;
import tk.freaxsoftware.ribbon2.io.core.InputOutputException;
import tk.freaxsoftware.ribbon2.io.core.ModuleType;
import tk.freaxsoftware.ribbon2.io.core.SchemeInstance;
import tk.freaxsoftware.ribbon2.io.core.SchemeStatusUpdate;
import tk.freaxsoftware.ribbon2.io.core.importer.ImportMessage;
import tk.freaxsoftware.ribbon2.io.core.importer.ImportSource;
import tk.freaxsoftware.ribbon2.io.core.importer.Importer;

/**
 * Implementation of import engine.
 * @author Stanislav Nepochatov
 */
public class ImportEngine extends IOEngine<Importer> {
    
    private final static Logger LOGGER = LoggerFactory.getLogger(ImportEngine.class);
    
    private final static String PERMISSION_CAN_ASSIGN_IMPORT = "canAssignImport";
    
    private static final String GENERAL_TIMEOUT_KEY = "generalTimeout";
    
    private static final String GENERAL_DIRECTORY_KEY = "generalDirectory";
    
    private final SchemeRepository schemeRepository;
    
    private final SchemeConverter schemeConverter;
    
    private final RegisterRepository registerRepository;
    
    private final DirectoryRepository directoryRepository;
    
    private final ScheduledExecutorService scheduler;
    
    private final Map<String, Future> schemeMap = new ConcurrentHashMap();
    
    private final Map<String, ModuleWrapper> moduleMap = new HashMap();

    public ImportEngine(String[] classes, SchemeRepository schemeRepository, 
            SchemeConverter schemeConverter, RegisterRepository registerRepository,
            DirectoryRepository directoryRepository) {
        super(ModuleType.IMPORT, classes);
        scheduler = Executors.newScheduledThreadPool(ExchangerUnit.config.getExchanger().getImportConfig().getThreadPoolSize());
        this.schemeRepository = schemeRepository;
        this.schemeConverter = schemeConverter;
        this.registerRepository = registerRepository;
        this.directoryRepository = directoryRepository;
    }

    @Override
    public void start() {
        LOGGER.info("Starting Import Engine...");
        for (Object importer: modules) {
            ModuleWrapper<Importer> wrapper = (ModuleWrapper<Importer>) importer;
            LOGGER.info("Processing module {}", wrapper.getModuleData().id());
            List<Scheme> schemes = schemeRepository.findByModuleId(wrapper.getModuleData().id());
            moduleMap.put(wrapper.getModuleData().protocol(), wrapper);
            for (Scheme scheme: schemes) {
                LOGGER.info("Processing scheme {}", scheme.getName());
                if (isConfigValid(scheme.getConfig(), wrapper.getModuleData().requiredConfigKeys())) {
                    launchScheme(wrapper, scheme);
                } else {
                    LOGGER.warn("Some config keys are absent in scheme {}, skipping", scheme.getName());
                }
            }
            sendRegistration(wrapper, ModuleType.IMPORT, wrapper.getSchemes());
        }
    }

    @Override
    public IOScheme saveScheme(IOScheme scheme, String username) {
        LOGGER.info("Saving scheme {} with protocol {}", scheme.getName(), scheme.getProtocol());
        if (scheme.getConfig().containsKey(GENERAL_DIRECTORY_KEY)) {
            checkDirectoryAccess(username, ImmutableSet.of((String) scheme.getConfig().get(GENERAL_DIRECTORY_KEY)), PERMISSION_CAN_ASSIGN_IMPORT);
        }
        if (scheme.getConfig().containsKey(GENERAL_TIMEOUT_KEY)) {
            Double timeout = (Double) scheme.getConfig().get(GENERAL_TIMEOUT_KEY);
            scheme.getConfig().put(GENERAL_TIMEOUT_KEY, timeout.longValue());
        }
        Scheme saved = schemeRepository.save(schemeConverter.convertBack(scheme));
        ModuleWrapper<Importer> wrapper = moduleMap.get(saved.getProtocol());
        if (schemeMap.containsKey(saved.getName())) {
            stopScheme(wrapper, saved);
        }
        launchSchemeAndSendUpdate(wrapper, saved);
        return schemeConverter.convert(saved);
    }

    @Override
    public IOScheme getScheme(String name) {
        LOGGER.info("Get scheme by name {}", name);
        Scheme scheme = schemeRepository.findByName(name);
        if (scheme != null) {
            return schemeConverter.convert(scheme);
        }
        LOGGER.error("Scheme by name {} not found", name);
        throw new CoreException(IO_SCHEME_NOT_FOUND, 
                String.format("Scheme by name %s not found!", name));
    }

    @Override
    public Boolean deleteScheme(String name) {
        Scheme existingScheme = schemeRepository.findByName(name);
        if (existingScheme != null) {
            LOGGER.warn("Deleting scheme {}", existingScheme.getName());
            ModuleWrapper<Importer> wrapper = moduleMap.get(existingScheme.getProtocol());
            stopSchemeAndSendUpdate(wrapper, existingScheme);
            return existingScheme.delete();
        }
        LOGGER.error("Scheme by name {} not found", name);
        throw new CoreException(IO_SCHEME_NOT_FOUND, 
                String.format("Scheme by name %s not found!", name));
    }
    
    private SchemeInstance launchScheme(ModuleWrapper<Importer> wrapper, Scheme scheme) {
        LOGGER.info("Launching import for scheme {} by module {}", scheme.getName(), wrapper.getModuleData().id());
        ImportSource source = wrapper.getModuleInstance().createSource(schemeConverter.convert(scheme));
        SchemeInstance instance = scheme.buildInstance();
        Future future = scheduler.scheduleAtFixedRate(new ImportTask(instance, source, registerRepository, directoryRepository, () -> errorDir), 0, 
                (long) scheme.getConfig().get(GENERAL_TIMEOUT_KEY), TimeUnit.SECONDS);
        schemeMap.put(scheme.getName(), future);
        wrapper.getSchemes().put(scheme.getName(), instance);
        return instance;
    }
    
    private void launchSchemeAndSendUpdate(ModuleWrapper<Importer> wrapper, Scheme scheme) {
        SchemeInstance instance = launchScheme(wrapper, scheme);
        sendSchemeStatusUpdate(Sets.newHashSet(buildStatusUpdateNotification(wrapper, instance, type, scheme.getName())));
    }
    
    private SchemeInstance stopScheme(ModuleWrapper<Importer> wrapper, Scheme scheme) {
        LOGGER.warn("Stopping scheme {} current task.", scheme.getName());
        schemeMap.get(scheme.getName()).cancel(true);
        schemeMap.remove(scheme.getName());
        return wrapper.getSchemes().remove(scheme.getName());
    }
    
    private void stopSchemeAndSendUpdate(ModuleWrapper<Importer> wrapper, Scheme scheme) {
        SchemeInstance instance = stopScheme(wrapper, scheme);
        instance.setStatus(SchemeInstance.Status.DELETED);
        sendSchemeStatusUpdate(Sets.newHashSet(buildStatusUpdateNotification(wrapper, instance, type, scheme.getName())));
    }
    
    private Boolean isConfigValid(Map<String,Object> config, String[] requiredConfigKeys) {
        for (String configKey: requiredConfigKeys) {
            if (!config.containsKey(configKey)) {
                return false;
            }
        }
        if (!config.containsKey(GENERAL_TIMEOUT_KEY)) {
            return false;
        }
        return true;
    }
    
    @Override
    public DirectoryRepository getDirectoryRepository() {
        return directoryRepository;
    }
    
    /**
     * Import task routine: reads messages from source, tries to send and then marks them as sended.
     */
    private static class ImportTask implements Runnable {
        
        private final SchemeInstance instance;
        
        private final ImportSource importSource;
        
        private final RegisterRepository registerRepository;
        
        private final DirectoryRepository directoryRepository;
        
        private final Supplier<String> errorDirSupplier;

        public ImportTask(SchemeInstance instance, ImportSource importSource, RegisterRepository registerRepository, 
                DirectoryRepository directoryRepository, Supplier<String> errorDirSupplier) {
            this.instance = instance;
            this.importSource = importSource;
            this.registerRepository = registerRepository;
            this.directoryRepository = directoryRepository;
            this.errorDirSupplier = errorDirSupplier;
        }

        @Override
        public void run() {
            try {
                importSource.open();
                List<ImportMessage> unreadMessages = importSource.getUnreadMessages();
                Map<String, Register> registerMap = registerRepository.findReadedMessages(importSource.getScheme().getId(), 
                        importSource.getScheme().getName(), unreadMessages.stream().map(m -> m.getId()).collect(Collectors.toSet()));
                for (ImportMessage message: unreadMessages) {
                    if (registerMap.containsKey(message.getId())) {
                        LOGGER.warn("Message {} already process by scheme {} and module {}, skipping", 
                                message.getId(), importSource.getScheme().getName(), importSource.getScheme().getId());
                        continue;
                    }
                    try {
                        MessageModel importedMessage = processMessage(importSource.getScheme(), message);
                        importSource.onSuccess(message, importedMessage.getUid());
                        message.markAsRead();
                        for (String directory: importedMessage.getDirectories()) {
                            registerRepository.saveRegisterRecord(importSource.getScheme().getId(), message.getId(), 
                                    message.getHeader(), directory, 
                                    importSource.getScheme().getName(), importedMessage.getUid());
                        }
                        LOGGER.info("Message {} : {} imported by module {} via {} scheme to directories {}.", 
                                importedMessage.getUid(), message.getHeader(), importSource.getScheme().getId(), 
                                importSource.getScheme().getName(), importedMessage.getDirectories());
                    } catch (InputOutputException ex) {
                        importSource.onError(message, ex);
                        throw ex;
                    }
                }
                importSource.close();
                if (instance.getStatus() == SchemeInstance.Status.ERROR) {
                    instance.setErrorDescription(null);
                    instance.setStatus(SchemeInstance.Status.OK);
                    sendUpdate(importSource.getScheme());
                }
            } catch (Exception ex) {
                LOGGER.error("Error on processing import of scheme {} for module {}", 
                        importSource.getScheme().getName(), importSource.getScheme().getId());
                LOGGER.error("Stacktrace:", ex);
                errorHandle(importSource.getScheme(), ex);
            }
        }
        
        private void errorHandle(IOScheme scheme, Exception ex) {
            ErrorHandling errorHandling = ErrorHandling.errorHandling(scheme.getConfig());
            if (errorHandling == ErrorHandling.DROP_ERROR) {
                return;
            }
            instance.setStatus(SchemeInstance.Status.ERROR);
            instance.setErrorDescription(ex.getMessage());
            sendUpdate(scheme);
            if (errorHandling == ErrorHandling.RAISE_ADM_ERROR) {
                postErrorMessage(scheme, null, ex, ModuleType.IMPORT, errorDirSupplier.get());
            }
        }
        
        private void sendUpdate(IOScheme scheme) {
            SchemeStatusUpdate update = new SchemeStatusUpdate(scheme.getId(), 
                    scheme.getType(), scheme.getProtocol(), scheme.getName(), 
                    instance.getStatus(), instance.getErrorDescription(), 
                    instance.getRaisingAdminError(), null);
            sendSchemeStatusUpdate(Sets.newHashSet(update));
        }
        
        private MessageModel processMessage(IOScheme scheme, ImportMessage message) {
            MessageModel messageModel = message.getMessage();
            if (messageModel.getDirectories() == null || (messageModel.getDirectories() != null 
                    && messageModel.getDirectories().isEmpty())) {
                if (scheme.getConfig().containsKey(GENERAL_DIRECTORY_KEY)) {
                    messageModel.setDirectories(Set.of((String) scheme.getConfig().get(GENERAL_DIRECTORY_KEY)));
                } else {
                    throw new InputOutputException(IOExceptionCodes.IMPORT_ERROR,
                            "Can't process message: no directory configured nor module doesn't set them.");
                }
            }
            
            for (String dir: messageModel.getDirectories()) {
                if (directoryRepository.findByFullName(dir) == null) {
                    throw new InputOutputException(IOExceptionCodes.IMPORT_ERROR, 
                            String.format("Can't process message: specified directory %s not available!", dir));
                }
            }
            MessagePropertyModel importProperty = new MessagePropertyModel(scheme.getId().replace(':', '_').toUpperCase(), scheme.getName());
            if (messageModel.getProperties() == null) {
                messageModel.setProperties(Set.of(importProperty));
            } else {
                messageModel.getProperties().add(importProperty);
            }
            
            return sendMessage(messageModel);
        }
        
        private MessageModel sendMessage(MessageModel model) {
            try {
                return MessageBus.fireCall(MessageModel.CALL_CREATE_MESSAGE, model, MessageOptions.Builder.newInstance()
                        .header(UserModel.AUTH_HEADER_USERNAME, "root")
                        .header(UserModel.AUTH_HEADER_FULLNAME, "root")
                        .deliveryCall().build(), MessageModel.class);
            } catch (Exception ex) {
                LOGGER.error("Error during sending message {}: {}", model.getHeader(), ex.getMessage());
                throw new InputOutputException(IOExceptionCodes.PROCESSING_ERROR, 
                        String.format("Error during sending message %s: %s", model.getHeader(), ex.getMessage()));
            }
        }
    }
    
}
