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
package tk.freaxsoftware.ribbon2.exchanger;

import com.google.gson.Gson;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tk.freaxsoftware.extras.bus.annotation.AnnotationUtil;
import tk.freaxsoftware.extras.bus.bridge.http.TypeResolver;
import tk.freaxsoftware.extras.bus.bridge.http.util.GsonUtils;
import tk.freaxsoftware.ribbon2.core.config.DbConfig;
import tk.freaxsoftware.ribbon2.core.config.EnvironmentOverrider;
import tk.freaxsoftware.ribbon2.exchanger.config.ExchangerUnitConfig;
import tk.freaxsoftware.ribbon2.exchanger.config.ExchangerUnitConfig.ExchangerConfig;
import tk.freaxsoftware.ribbon2.exchanger.converters.DirectoryConverter;
import tk.freaxsoftware.ribbon2.exchanger.converters.SchemeConverter;
import tk.freaxsoftware.ribbon2.exchanger.engine.ExportEngine;
import tk.freaxsoftware.ribbon2.exchanger.engine.ImportEngine;
import tk.freaxsoftware.ribbon2.exchanger.facade.DirectoryFacade;
import tk.freaxsoftware.ribbon2.exchanger.repository.DirectoryRepository;
import tk.freaxsoftware.ribbon2.exchanger.repository.ExportQueueRepository;
import tk.freaxsoftware.ribbon2.exchanger.repository.RegisterRepository;
import tk.freaxsoftware.ribbon2.exchanger.repository.SchemeRepository;
import tk.freaxsoftware.ribbon2.io.core.IOLocalIds;
import tk.freaxsoftware.ribbon2.io.core.ModuleType;

/**
 * Main class of Exchanger Unit.
 * @author Stanislav Nepochatov
 */
public class ExchangerUnit {
    
    private final static Logger LOGGER = LoggerFactory.getLogger(ExchangerUnit.class);
    
    /**
     * Gson instance.
     */
    public static final Gson gson = GsonUtils.getGson();
    
    /**
     * Current application config;
     */
    public static ExchangerUnitConfig config;
    
    /**
     * Available thread pool for various needs;
     */
    public static ExecutorService executor = Executors.newFixedThreadPool(4);
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        LOGGER.info("\n{}", IOUtils.toString(ExchangerUnit.class.getClassLoader().getResourceAsStream("header"), Charset.defaultCharset()));
        config = gson.fromJson(IOUtils.toString(ExchangerUnitConfig.class.getClassLoader().getResourceAsStream("exchangerconfig.json"), Charset.defaultCharset()), ExchangerUnitConfig.class);
        processConfig(config);
        LOGGER.info("Exchanger started, config: {}", config);
        TypeResolver.register(IOLocalIds.IO_REGISTER_EXPORT_DIRS_TYPE_NAME, IOLocalIds.IO_REGISTER_EXPORT_DIRS_TYPE_TOKEN);
        
        Init.init(config);
        if (config.getExchanger().getType() == ModuleType.IMPORT) {
            ImportEngine engine = new ImportEngine(new String[] {config.getExchanger().getModuleClass()}, 
                    new SchemeRepository(), new SchemeConverter(), new RegisterRepository(), 
                    new DirectoryRepository());
            engine.start();
        }
        if (config.getExchanger().getType() == ModuleType.EXPORT) {
            ExportEngine engine = new ExportEngine(new String[] {config.getExchanger().getModuleClass()}, 
                    new SchemeRepository(), new SchemeConverter(), new RegisterRepository(), 
                    new DirectoryRepository(), new ExportQueueRepository());
            engine.start();
        }
        AnnotationUtil.subscribeReceiverInstance(new DirectoryFacade(new DirectoryRepository(), new DirectoryConverter()));
    }
    
    private static void processConfig(ExchangerUnitConfig config) {
        EnvironmentOverrider overrider = new EnvironmentOverrider();
        overrider.registerOverride(new EnvironmentOverrider.OverrideEntry<ExchangerConfig>("EXCHANGER_TYPE", 
                ExchangerUnitConfig.ExchangerConfig.class, (conf, property) -> conf.setType(ModuleType.valueOf(property))));
        overrider.registerOverride(new EnvironmentOverrider.OverrideEntry<ExchangerConfig>("EXCHANGER_CLASS", 
                ExchangerUnitConfig.ExchangerConfig.class, (conf, property) -> conf.setModuleClass(property)));
        overrider.registerOverride(new EnvironmentOverrider.OverrideEntry<ExchangerConfig>("EXCHANGER_ENABLE_PERMISSION_CACHIMG", 
                ExchangerUnitConfig.ExchangerConfig.class, (conf, property) -> conf.setEnablePermissionCaching(Boolean.valueOf(property))));
        overrider.registerOverride(new EnvironmentOverrider.OverrideEntry<ExchangerConfig>("EXCHANGER_PERMISSION_CACHE_EXPIRY", 
                ExchangerUnitConfig.ExchangerConfig.class, (conf, property) -> conf.setPermissionCacheExpiry(Integer.valueOf(property))));
        overrider.registerOverride(new EnvironmentOverrider.OverrideEntry<DbConfig>("DB_JDBC_URL", 
                DbConfig.class, (conf, property) -> conf.setJdbcUrl(property)));
        overrider.registerOverride(new EnvironmentOverrider.OverrideEntry<DbConfig>("DB_USERNAME", 
                DbConfig.class, (conf, property) -> conf.setUsername(property)));
        overrider.registerOverride(new EnvironmentOverrider.OverrideEntry<DbConfig>("DB_PASSWORD", 
                DbConfig.class, (conf, property) -> conf.setPassword(property)));
        overrider.registerOverride(new EnvironmentOverrider.OverrideEntry<ExchangerConfig.ExportConfig>("EXCHANGER_EXPORT_QUEUE_PERIOD", 
                ExchangerUnitConfig.ExchangerConfig.ExportConfig.class, (conf, property) -> conf.setQueuePeriod(Integer.parseInt(property))));
        overrider.registerOverride(new EnvironmentOverrider.OverrideEntry<ExchangerConfig.ImportConfig>("EXCHANGER_IMPORT_THREAD_POOL_SIZE", 
                ExchangerUnitConfig.ExchangerConfig.ImportConfig.class, (conf, property) -> conf.setThreadPoolSize(Integer.parseInt(property))));
        
        overrider.processConfig(config.getExchanger());
        overrider.processConfig(config.getExchanger().getExportConfig());
        overrider.processConfig(config.getExchanger().getImportConfig());
        overrider.processConfig(config.getDb());
    }
    
}
