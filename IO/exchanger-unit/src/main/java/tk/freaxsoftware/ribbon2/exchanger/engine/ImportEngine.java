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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
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
import tk.freaxsoftware.ribbon2.exchanger.converters.SchemeConverter;
import tk.freaxsoftware.ribbon2.exchanger.entity.Register;
import tk.freaxsoftware.ribbon2.exchanger.entity.Scheme;
import tk.freaxsoftware.ribbon2.exchanger.repository.DirectoryRepository;
import tk.freaxsoftware.ribbon2.exchanger.repository.RegisterRepository;
import tk.freaxsoftware.ribbon2.exchanger.repository.SchemeRepository;
import tk.freaxsoftware.ribbon2.io.core.IOExceptionCodes;
import tk.freaxsoftware.ribbon2.io.core.IOScheme;
import tk.freaxsoftware.ribbon2.io.core.InputOutputException;
import tk.freaxsoftware.ribbon2.io.core.ModuleType;
import tk.freaxsoftware.ribbon2.io.core.importer.ImportMessage;
import tk.freaxsoftware.ribbon2.io.core.importer.ImportSource;
import tk.freaxsoftware.ribbon2.io.core.importer.Importer;

/**
 * Implementation of import engine.
 * @author Stanislav Nepochatov
 */
public class ImportEngine extends IOEngine<Importer> {
    
    private final static Logger LOGGER = LoggerFactory.getLogger(ImportEngine.class);
    
    private static final String GENERAL_TIMEOUT_KEY = "generalTimeout";
    
    private static final String GENERAL_DIRECTORY = "generalDirectory";
    
    private final SchemeRepository schemeRepository;
    
    private final SchemeConverter schemeConverter;
    
    private final RegisterRepository registerRepository;
    
    private final DirectoryRepository directoryRepository;
    
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(8);
    
    private final Map<String, Future> schemeMap = new ConcurrentHashMap();
    
    private final Map<String, ModuleWrapper> moduleMap = new HashMap();

    public ImportEngine(String[] classes, SchemeRepository schemeRepository, 
            SchemeConverter schemeConverter, RegisterRepository registerRepository,
            DirectoryRepository directoryRepository) {
        super(ModuleType.IMPORT, classes);
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
                    launchScheme(wrapper, schemeConverter.convert(scheme));
                } else {
                    LOGGER.warn("Some config keys are absent in scheme {}, skipping", scheme.getName());
                }
            }
            sendRegistration(wrapper, ModuleType.IMPORT, wrapper.getSchemes());
        }
    }

    @Override
    public IOScheme saveScheme(IOScheme scheme) {
        LOGGER.info("Saving scheme {} with protocol {}", scheme.getName(), scheme.getProtocol());
        if (scheme.getConfig().containsKey(GENERAL_TIMEOUT_KEY)) {
            Double timeout = (Double) scheme.getConfig().get(GENERAL_TIMEOUT_KEY);
            scheme.getConfig().put(GENERAL_TIMEOUT_KEY, timeout.longValue());
        }
        Scheme saved = schemeRepository.save(schemeConverter.convertBack(scheme));
        ModuleWrapper<Importer> wrapper = moduleMap.get(saved.getProtocol());
        if (schemeMap.containsKey(saved.getName())) {
            stopScheme(wrapper, saved);
        }
        launchScheme(wrapper, scheme);
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
            stopScheme(wrapper, existingScheme);
            return existingScheme.delete();
        }
        LOGGER.error("Scheme by name {} not found", name);
        throw new CoreException(IO_SCHEME_NOT_FOUND, 
                String.format("Scheme by name %s not found!", name));
    }
    
    private void launchScheme(ModuleWrapper<Importer> wrapper, IOScheme scheme) {
        LOGGER.info("Launching import for scheme {} by module {}", scheme.getName(), wrapper.getModuleData().id());
        ImportSource source = wrapper.getModuleInstance().createSource(scheme);
        Future future = scheduler.scheduleAtFixedRate(new ImportTask(source, registerRepository, directoryRepository), 0, 
                (long) scheme.getConfig().get(GENERAL_TIMEOUT_KEY), TimeUnit.SECONDS);
        schemeMap.put(scheme.getName(), future);
        wrapper.getSchemes().add(scheme.getName());
    }
    
    private void stopScheme(ModuleWrapper<Importer> wrapper, Scheme scheme) {
        LOGGER.warn("Stopping scheme {} current task.", scheme.getName());
        schemeMap.get(scheme.getName()).cancel(true);
        schemeMap.remove(scheme.getName());
        wrapper.getSchemes().remove(scheme.getName());
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
    
    /**
     * Import task routine: reads messages from source, tries to send and then marks them as sended.
     */
    private static class ImportTask implements Runnable {
        
        private final ImportSource importSource;
        
        private final RegisterRepository registerRepository;
        
        private final DirectoryRepository directoryRepository;

        public ImportTask(ImportSource importSource, RegisterRepository registerRepository, 
                DirectoryRepository directoryRepository) {
            this.importSource = importSource;
            this.registerRepository = registerRepository;
            this.directoryRepository = directoryRepository;
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
                        String uid = processMessage(importSource.getScheme(), message);
                        importSource.onSuccess(message, uid);
                        message.markAsRead();
                        registerRepository.saveRegisterRecord(importSource.getScheme().getId(), message.getId(), 
                                message.getHeader(), (String) importSource.getScheme().getConfig().get(GENERAL_DIRECTORY), 
                                importSource.getScheme().getName(), uid);
                        LOGGER.info("Message {} : {} imported by module {} via {} scheme.", 
                                uid, message.getHeader(), importSource.getScheme().getId(), 
                                importSource.getScheme().getName());
                    } catch (InputOutputException ex) {
                        LOGGER.error("Error on processing message {} during import of scheme {} for module {}",
                                message.getId(),
                                importSource.getScheme().getName(), importSource.getScheme().getId());
                        LOGGER.error("Stacktrace:", ex);
                        importSource.onError(message, ex);
                    }
                }
                importSource.close();
            } catch (Exception ex) {
                LOGGER.error("Error on processing import of scheme {} for module {}", 
                        importSource.getScheme().getName(), importSource.getScheme().getId());
                LOGGER.error("Stacktrace:", ex);
            }
        }
        
        private String processMessage(IOScheme scheme, ImportMessage message) {
            MessageModel messageModel = message.getMessage();
            if (messageModel.getDirectories() == null || (messageModel.getDirectories() != null 
                    && messageModel.getDirectories().isEmpty())) {
                if (scheme.getConfig().containsKey(GENERAL_DIRECTORY)) {
                    messageModel.setDirectories(Set.of((String) scheme.getConfig().get(GENERAL_DIRECTORY)));
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
            MessagePropertyModel importProperty = new MessagePropertyModel(scheme.getId(), scheme.getName());
            if (messageModel.getProperties() == null) {
                messageModel.setProperties(Set.of(importProperty));
            } else {
                messageModel.getProperties().add(importProperty);
            }
            
            MessageModel sent = sendMessage(messageModel);
            return sent.getUid();
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
