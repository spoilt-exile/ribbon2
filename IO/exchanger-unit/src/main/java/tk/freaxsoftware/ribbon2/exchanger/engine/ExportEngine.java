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
import freemarker.template.TemplateException;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tk.freaxsoftware.extras.bus.MessageBus;
import tk.freaxsoftware.extras.bus.MessageOptions;
import tk.freaxsoftware.extras.bus.bridge.http.LocalHttpCons;
import tk.freaxsoftware.ribbon2.core.data.MessageModel;
import tk.freaxsoftware.ribbon2.core.data.MessagePropertyModel;
import tk.freaxsoftware.ribbon2.core.data.UserModel;
import tk.freaxsoftware.ribbon2.core.exception.CoreException;
import static tk.freaxsoftware.ribbon2.core.exception.RibbonErrorCodes.DIRECTORY_NOT_FOUND;
import static tk.freaxsoftware.ribbon2.core.exception.RibbonErrorCodes.IO_SCHEME_NOT_FOUND;
import tk.freaxsoftware.ribbon2.core.utils.MessageUtils;
import tk.freaxsoftware.ribbon2.exchanger.ExchangerUnit;
import tk.freaxsoftware.ribbon2.exchanger.converters.SchemeConverter;
import tk.freaxsoftware.ribbon2.exchanger.engine.export.DefaultExportMessage;
import tk.freaxsoftware.ribbon2.exchanger.engine.export.TemplateService;
import tk.freaxsoftware.ribbon2.exchanger.entity.Directory;
import tk.freaxsoftware.ribbon2.exchanger.entity.ExportQueue;
import tk.freaxsoftware.ribbon2.exchanger.entity.Scheme;
import tk.freaxsoftware.ribbon2.exchanger.repository.DirectoryRepository;
import tk.freaxsoftware.ribbon2.exchanger.repository.ExportQueueRepository;
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
    
    private final static String PERMISSION_CAN_ASSIGN_EXPORT = "canAssignExport";
    
    private final SchemeRepository schemeRepository;
    
    private final SchemeConverter schemeConverter;
    
    private final RegisterRepository registerRepository;
    
    private final DirectoryRepository directoryRepository;
    
    private final ExportQueueRepository exportQueueRepository;
    
    private final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
    
    private ScheduledFuture exportQueueFuture;
    
    private final Map<String, ModuleWrapper<Exporter>> moduleMap = new HashMap();
    
    public ExportEngine(String[] classes, SchemeRepository schemeRepository, 
            SchemeConverter schemeConverter, RegisterRepository registerRepository,
            DirectoryRepository directoryRepository, ExportQueueRepository exportQueueRepository) {
        super(ModuleType.EXPORT, classes);
        this.schemeRepository = schemeRepository;
        this.schemeConverter = schemeConverter;
        this.registerRepository = registerRepository;
        this.directoryRepository = directoryRepository;
        this.exportQueueRepository = exportQueueRepository;
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
                String username = MessageUtils.getAuthFromHeader(holder);
                String schemeName = (String) holder.getHeaders().get(IOLocalIds.IO_SCHEME_NAME_HEADER);
                String dirName = (String) holder.getContent();
                holder.getResponse().setContent(assignSchemeToExport(schemeName, dirName, username));
            });
            MessageBus.addSubscription(registration.schemeExportDismissTopic(), (holder) -> {
                String username = MessageUtils.getAuthFromHeader(holder);
                String schemeName = (String) holder.getHeaders().get(IOLocalIds.IO_SCHEME_NAME_HEADER);
                String dirName = (String) holder.getContent();
                holder.getResponse().setContent(dismissSchemeFromExport(schemeName, dirName, username));
            });
            moduleMap.put(wrapper.getModuleData().protocol(), wrapper);
        }
        sendExportDirectoriesRegistration();
        MessageBus.addSubscription(MessageModel.NOTIFICATION_MESSAGE_CREATED, 
                holder -> processExportMessage((MessageModel) holder.getContent()));
        checkQueueRun();
    }
    
    private void checkQueueRun() {
        if (exportQueueFuture != null && !exportQueueFuture.isDone()) {
            return;
        }
        Integer queuePeriod = ExchangerUnit.config.getExchanger().getExportConfig().getQueuePeriod();
        exportQueueFuture = executorService.scheduleAtFixedRate(
                new QueueTask(schemeRepository, exportQueueRepository, 
                        registerRepository, moduleMap, schemeConverter, 
                        new TemplateService()), 
                15, queuePeriod, TimeUnit.SECONDS);
    }
    
    private void sendExportDirectoriesRegistration() {
        List<Scheme> schemes = schemeRepository.findAll();
        Set<String> moduleIds = modules.stream().map(md -> md.getModuleData().id()).collect(Collectors.toSet());
        Map<String, Set<String>> exportDirMap = new HashMap();
        Set<String> dirNames = schemes.stream().flatMap(sch -> sch.getExportList().stream()).collect(Collectors.toSet());
        for (String dirName: dirNames) {
            Set<String> dirSchemes = schemes.stream()
                    .filter(sch -> sch.getExportList().contains(dirName) && moduleIds.contains(sch.getModuleId()))
                    .map(dirSchema -> dirSchema.getName())
                    .collect(Collectors.toSet());
            exportDirMap.put(dirName, dirSchemes);
        }
        MessageBus.fire(IOLocalIds.IO_REGISTER_EXPORT_DIRS, exportDirMap, 
                MessageOptions.Builder.newInstance()
                        .header(LocalHttpCons.L_HTTP_NODE_REGISTERED_TYPE_HEADER, IOLocalIds.IO_REGISTER_EXPORT_DIRS_TYPE_NAME)
                        .deliveryNotification()
                        .async().pointToPoint().build());
    }
    
    private void processExportMessage(MessageModel message) {
        Set<String> moduleIds = moduleMap.values().stream().map(exporter -> exporter.getModuleData().id()).collect(Collectors.toSet());
        List<Scheme> schemes = schemeRepository.findByExportDir(moduleIds, message.getDirectories());
        if (!schemes.isEmpty()) {
            LOGGER.warn("Starting export message {} {}", message.getUid(), message.getHeader());
            for (Scheme scheme: schemes) {
                Set<String> dirIntersect = new HashSet<>(scheme.getExportList());
                dirIntersect.retainAll(message.getDirectories());
                for (String exportDir: dirIntersect) {
                    exportQueueRepository.save(new ExportQueue(exportDir, scheme.getProtocol(), scheme.getName(), message, ZonedDateTime.now()));
                }
            }
        }
        checkQueueRun();
    }

    @Override
    public IOScheme saveScheme(IOScheme scheme, String username) {
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
    
    private Boolean assignSchemeToExport(String name, String dirName, String username) {
        LOGGER.warn("Assign directory {} to export by scheme {}", dirName, name);
        return innerSchemeExportListProcess(name, dirName, username, (exportList) -> exportList.add(dirName));
    }
    
    private Boolean dismissSchemeFromExport(String name, String dirName, String username) {
        LOGGER.warn("Dismiss directory {} from export by scheme {}", dirName, name);
        return innerSchemeExportListProcess(name, dirName, username, (exportList) -> exportList.remove(dirName));
    }
    
    private Boolean innerSchemeExportListProcess(String name, String dirName, String username, Consumer<Set<String>> op) {
        checkDirectoryAccess(username, ImmutableSet.of(dirName), PERMISSION_CAN_ASSIGN_EXPORT);
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
        op.accept(scheme.getExportList());
        scheme.save();
        return true;
    }
    
    /**
     * Export queue task: reads active schemes by protocols and looks corresponding messages in queue to export.
     */
    private final static class QueueTask implements Runnable {
        
        private final SchemeRepository schemeRepository;
        
        private final ExportQueueRepository exportMessageRepository;
        
        private final RegisterRepository registerRepository;
        
        private final Map<String, ModuleWrapper<Exporter>> moduleMap;
        
        private final SchemeConverter schemeConverter;
        
        private final TemplateService templateService;

        public QueueTask(SchemeRepository schemeRepository, 
                ExportQueueRepository exportMessageRepository, 
                RegisterRepository registerRepository, 
                Map<String, ModuleWrapper<Exporter>> moduleMap,
                SchemeConverter schemeConverter,
                TemplateService templateService) {
            this.schemeRepository = schemeRepository;
            this.exportMessageRepository = exportMessageRepository;
            this.registerRepository = registerRepository;
            this.moduleMap = moduleMap;
            this.schemeConverter = schemeConverter;
            this.templateService = templateService;
        }

        @Override
        public void run() {
            Set<String> exportProtocols = moduleMap.entrySet().stream()
                    .map(en -> en.getKey())
                    .collect(Collectors.toSet());
            List<Scheme> schemes = schemeRepository.findAllExportByProtocols(exportProtocols);
            Set<String> exportSchemes = schemes.stream()
                    .map(expScheme -> expScheme.getName())
                    .collect(Collectors.toSet());
            Map<String, Scheme> schemeMap = schemes.stream().collect(Collectors.toMap(Scheme::getName, Function.identity()));
            Set<ExportQueue> exportQueue = exportMessageRepository.findBySchemesAndDate(exportSchemes, ZonedDateTime.now());
            LOGGER.info("Export queue run: protocols {}, schemes {}, size {}", exportProtocols, exportSchemes, exportQueue.size());
            for (ExportQueue exportMessage: exportQueue) {
                innerHandle(exportMessage, schemeMap.get(exportMessage.getScheme()));
            }
        }
        
        private void innerHandle(ExportQueue exportMessage, Scheme scheme) {
            try {
                innerExport(moduleMap.get(exportMessage.getProtocol()).getModuleInstance(), exportMessage, scheme);
            } catch (Exception ex) {
                LOGGER.error("Error during exporting message {} : {} by scheme {} protocol {} in dir {}", 
                        exportMessage.getMessage().getUid(), exportMessage.getMessage().getHeader(),
                        exportMessage.getScheme(), exportMessage.getProtocol(), exportMessage.getExportDirectory());
                LOGGER.error("Error: ", ex);
                exportMessage.setError(ex.getMessage());
                exportMessage.save();
            }
        }
        
        private void innerExport(Exporter exporter, ExportQueue exportMessage, Scheme scheme) throws IOException, TemplateException {
            String externalUid = exporter.export(new DefaultExportMessage(templateService, exportMessage, schemeConverter.convert(scheme)));
            LOGGER.info("Message {} exported by scheme {} protocol {} with id {}", exportMessage.getMessage().getUid(), scheme.getName(), scheme.getProtocol(), externalUid);
            MessagePropertyModel property = new MessagePropertyModel();
            property.setContent(scheme.getName());
            property.setType(scheme.getModuleId().replace(':', '_').toUpperCase());
            MessageBus.fire(MessagePropertyModel.CALL_ADD_PROPERTY, property, MessageOptions.Builder.newInstance()
                    .header(UserModel.AUTH_HEADER_USERNAME, "root")
                    .header(UserModel.AUTH_HEADER_FULLNAME, "root")
                    .header(MessageModel.HEADER_MESSAGE_UID, exportMessage.getMessage().getUid())
                    .deliveryNotification().build());
            registerRepository.saveRegisterRecord(scheme.getModuleId(), exportMessage.getMessage().getUid(), 
                    exportMessage.getMessage().getHeader(), exportMessage.getExportDirectory(), 
                    scheme.getName(), externalUid);
            exportMessage.delete();
        }
    }
}
