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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tk.freaxsoftware.extras.bus.MessageBus;
import tk.freaxsoftware.extras.bus.MessageOptions;
import tk.freaxsoftware.ribbon2.core.data.MessageModel;
import tk.freaxsoftware.ribbon2.core.data.MessagePropertyModel;
import tk.freaxsoftware.ribbon2.core.data.UserModel;
import tk.freaxsoftware.ribbon2.core.exception.CoreException;
import static tk.freaxsoftware.ribbon2.core.exception.RibbonErrorCodes.DIRECTORY_NOT_FOUND;
import static tk.freaxsoftware.ribbon2.core.exception.RibbonErrorCodes.IO_SCHEME_NOT_FOUND;
import tk.freaxsoftware.ribbon2.exchanger.converters.SchemeConverter;
import tk.freaxsoftware.ribbon2.exchanger.entity.Directory;
import tk.freaxsoftware.ribbon2.exchanger.entity.Scheme;
import tk.freaxsoftware.ribbon2.exchanger.repository.DirectoryRepository;
import tk.freaxsoftware.ribbon2.exchanger.repository.RegisterRepository;
import tk.freaxsoftware.ribbon2.exchanger.repository.SchemeRepository;
import tk.freaxsoftware.ribbon2.io.core.IOLocalIds;
import tk.freaxsoftware.ribbon2.io.core.IOScheme;
import tk.freaxsoftware.ribbon2.io.core.ModuleRegistration;
import tk.freaxsoftware.ribbon2.io.core.ModuleType;
import tk.freaxsoftware.ribbon2.io.core.exporter.Exporter;

/**
 * Implementation of export engine.
 * @author Stanislav Nepochatov
 */
public class ExportEngine extends IOEngine<Exporter>{
    
    private final static Logger LOGGER = LoggerFactory.getLogger(ExportEngine.class);
    
    private final SchemeRepository schemeRepository;
    
    private final SchemeConverter schemeConverter;
    
    private final RegisterRepository registerRepository;
    
    private final DirectoryRepository directoryRepository;
    
    private final ExecutorService executorService = Executors.newFixedThreadPool(10);
    
    private final Map<String, ModuleWrapper<Exporter>> moduleMap = new HashMap();
    
    public ExportEngine(String[] classes, SchemeRepository schemeRepository, 
            SchemeConverter schemeConverter, RegisterRepository registerRepository,
            DirectoryRepository directoryRepository) {
        super(ModuleType.EXPORT, classes);
        this.schemeRepository = schemeRepository;
        this.schemeConverter = schemeConverter;
        this.registerRepository = registerRepository;
        this.directoryRepository = directoryRepository;
    }

    @Override
    public void start() {
        LOGGER.info("Starting Export Engine...");
        for (Object exporter: modules) {
            ModuleWrapper<Exporter> wrapper = (ModuleWrapper<Exporter>) exporter;
            LOGGER.info("Processing module {}", wrapper.getModuleData().id());
            List<Scheme> schemes = schemeRepository.findByModuleId(wrapper.getModuleData().id());
            wrapper.setSchemes(schemes.stream().map(scheme -> scheme.getName()).collect(Collectors.toSet()));
            ModuleRegistration registration = sendRegistration(wrapper, ModuleType.EXPORT, wrapper.getSchemes());
            MessageBus.addSubscription(registration.schemeExportAssignTopic(), (holder) -> {
                String schemeName = (String) holder.getHeaders().get(IOLocalIds.IO_SCHEME_NAME_HEADER);
                String dirName = (String) holder.getContent();
                holder.getResponse().setContent(assignSchemeToExport(schemeName, dirName));
            });
            moduleMap.put(wrapper.getModuleData().protocol(), wrapper);
        }
        MessageBus.addSubscription(MessageModel.NOTIFICATION_MESSAGE_CREATED, 
                holder -> processExportMessage((MessageModel) holder.getContent()));
    }
    
    private void processExportMessage(MessageModel message) {
        List<Scheme> schemes = schemeRepository.findByExportDir(message.getDirectories());
        if (!schemes.isEmpty()) {
            LOGGER.warn("Starting export message {} {}", message.getUid(), message.getHeader());
            for (Scheme scheme: schemes) {
                Set<String> dirIntersect = new HashSet<>(scheme.getExportList());
                dirIntersect.retainAll(message.getDirectories());
                for (String exportDir: dirIntersect) {
                    ExportTask task = new ExportTask(moduleMap.get(scheme.getProtocol()).getModuleInstance(), message, schemeConverter.convert(scheme), registerRepository, exportDir);
                    executorService.submit(task);
                }
            }
        }
    }

    @Override
    public IOScheme saveScheme(IOScheme scheme) {
        LOGGER.info("Saving scheme {} with protocol {}", scheme.getName(), scheme.getProtocol());
        return schemeConverter.convert(schemeRepository.save(schemeConverter.convertBack(scheme)));
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
            return existingScheme.delete();
        }
        LOGGER.error("Scheme by name {} not found", name);
        throw new CoreException(IO_SCHEME_NOT_FOUND, 
                String.format("Scheme by name %s not found!", name));
    }
    
    private Boolean assignSchemeToExport(String name, String dirName) {
        LOGGER.warn("Assign directory {} to export by scheme {}", dirName, name);
        Scheme scheme = schemeRepository.findByName(name);
        if (scheme == null) {
            throw new CoreException(IO_SCHEME_NOT_FOUND, 
                    String.format("Scheme by name %s not found!", name));
        }
        Directory directory = directoryRepository.findByFullName(dirName);
        if (directory == null) {
            throw new CoreException(DIRECTORY_NOT_FOUND, 
                    String.format("Directory by full name %s not found!", name));
        }
        if (scheme.getExportList() == null) {
            scheme.setExportList(new HashSet<>());
        }
        scheme.getExportList().add(dirName);
        scheme.save();
        return true;
    }
    
    /**
     * Export task routine: exports message, add it to register and adds property to existing message;
     */
    private final static class ExportTask implements Runnable {
        
        private final Exporter exporter;
        
        private final MessageModel message;
        
        private final IOScheme scheme;
        
        private final RegisterRepository registerRepository;
        
        private final String exportDirectory;

        public ExportTask(Exporter exporter, MessageModel message, IOScheme scheme, RegisterRepository registerRepository, String exportDirectory) {
            this.exporter = exporter;
            this.message = message;
            this.scheme = scheme;
            this.registerRepository = registerRepository;
            this.exportDirectory = exportDirectory;
        }

        @Override
        public void run() {
            String externalUid = exporter.export(message, scheme);
            LOGGER.info("Message {} exported by scheme {} protocol {} with id {}", message.getUid(), scheme.getName(), scheme.getProtocol(), externalUid);
            MessagePropertyModel property = new MessagePropertyModel();
            property.setContent(externalUid);
            property.setType(scheme.getId());
            MessageBus.fire(MessagePropertyModel.CALL_ADD_PROPERTY, property, MessageOptions.Builder.newInstance()
                    .header(UserModel.AUTH_HEADER_USERNAME, "root")
                    .header(UserModel.AUTH_HEADER_FULLNAME, "root")
                    .deliveryNotification().build());
            registerRepository.saveRegisterRecord(scheme.getId(), message.getUid(), 
                    message.getHeader(), exportDirectory, 
                    scheme.getName(), externalUid);
        }
        
    }
}
