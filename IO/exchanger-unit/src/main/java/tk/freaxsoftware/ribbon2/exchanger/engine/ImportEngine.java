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

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
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
import tk.freaxsoftware.ribbon2.exchanger.converters.SchemeConverter;
import tk.freaxsoftware.ribbon2.exchanger.entity.Register;
import tk.freaxsoftware.ribbon2.exchanger.entity.Scheme;
import tk.freaxsoftware.ribbon2.exchanger.repository.RegisterRepository;
import tk.freaxsoftware.ribbon2.exchanger.repository.SchemeRepository;
import tk.freaxsoftware.ribbon2.io.core.IOScheme;
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
    
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(8);

    public ImportEngine(String[] classes, SchemeRepository schemeRepository, 
            SchemeConverter schemeConverter, RegisterRepository registerRepository) {
        super(ModuleType.IMPORT, classes);
        this.schemeRepository = schemeRepository;
        this.schemeConverter = schemeConverter;
        this.registerRepository = registerRepository;
    }

    @Override
    public void start() {
        LOGGER.info("Starting Import Engine...");
        for (Object importer: modules) {
            ModuleWrapper<Importer> wrapper = (ModuleWrapper<Importer>) importer;
            LOGGER.info("Processing module {}", wrapper.getModuleData().id());
            List<Scheme> schemes = schemeRepository.findByModuleId(wrapper.getModuleData().id());
            for (Scheme scheme: schemes) {
                LOGGER.info("Processing scheme {}", scheme.getName());
                if (isConfigValid(scheme.getConfig(), wrapper.getModuleData().requiredConfigKeys())) {
                    LOGGER.info("Launching import for scheme {} by module {}", scheme.getName(), wrapper.getModuleData().id());
                    ImportSource source = wrapper.getModuleInstance().createSource(schemeConverter.convert(scheme));
                    scheduler.scheduleAtFixedRate(new ImportTask(source, registerRepository), 0, 
                            (long) scheme.getConfig().get(GENERAL_TIMEOUT_KEY), TimeUnit.SECONDS);
                    wrapper.getSchemes().add(scheme.getName());
                } else {
                    LOGGER.warn("Some config keys are absent in scheme {}, skipping", scheme.getName());
                }
            }
            if (!wrapper.getSchemes().isEmpty()) {
                sendRegistration(wrapper, ModuleType.IMPORT, wrapper.getSchemes());
            }
        }
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
    
    private static class ImportTask implements Runnable {
        
        private final ImportSource importSource;
        
        private final RegisterRepository registerRepository;

        public ImportTask(ImportSource importSource, RegisterRepository registerRepository) {
            this.importSource = importSource;
            this.registerRepository = registerRepository;
        }

        @Override
        public void run() {
            try {
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
                        message.markAsRead();
                        registerRepository.saveRegisterRecord(importSource.getScheme().getId(), message.getId(), 
                                message.getHeader(), (String) importSource.getScheme().getConfig().get(GENERAL_DIRECTORY), 
                                importSource.getScheme().getName(), uid);
                    } catch (Exception ex) {
                        LOGGER.error("Error on processing message {} during import of scheme {} for module {}",
                                message.getId(),
                                importSource.getScheme().getName(), importSource.getScheme().getId());
                        LOGGER.error("Stacktrace:", ex);
                    }
                }
            } catch (Exception ex) {
                LOGGER.error("Error on processing import of scheme {} for module {}", 
                        importSource.getScheme().getName(), importSource.getScheme().getId());
                LOGGER.error("Stacktrace:", ex);
            }
        }
        
        private String processMessage(IOScheme scheme, ImportMessage message) throws Exception {
            MessageModel messageModel = message.getMessage();
//            if (messageModel.getDirectories() == null || (messageModel.getDirectories() != null 
//                    && messageModel.getDirectories().isEmpty())) {
//                if (scheme.getConfig().containsKey(GENERAL_DIRECTORY)) {
//                    messageModel.setDirectories(Set.of((String) scheme.getConfig().get(GENERAL_DIRECTORY)));
//                } else {
//                    throw new IllegalArgumentException("Can't process message: no directory configured nor module doesn't set them.");
//                }
//            }

            messageModel.setDirectories(Set.of((String) scheme.getConfig().get(GENERAL_DIRECTORY)));
            messageModel.setProperties(Set.of(new MessagePropertyModel(scheme.getId(), scheme.getName())));
            MessageModel sent = sendMessage(messageModel);
            return sent.getUid();
        }
        
        private MessageModel sendMessage(MessageModel model) throws Exception {
            return MessageBus.fireCall(MessageModel.CALL_CREATE_MESSAGE, model, MessageOptions.Builder.newInstance()
                    .header(UserModel.AUTH_HEADER_USERNAME, "root")
                    .header(UserModel.AUTH_HEADER_FULLNAME, "root")
                    .deliveryCall().build(), MessageModel.class);
        }
    }
    
}
