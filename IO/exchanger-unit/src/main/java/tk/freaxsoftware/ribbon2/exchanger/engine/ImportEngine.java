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
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tk.freaxsoftware.ribbon2.exchanger.converters.SchemeConverter;
import tk.freaxsoftware.ribbon2.exchanger.entity.Scheme;
import tk.freaxsoftware.ribbon2.exchanger.repository.SchemeRepository;
import tk.freaxsoftware.ribbon2.io.core.ModuleType;
import tk.freaxsoftware.ribbon2.io.core.importer.ImportSource;
import tk.freaxsoftware.ribbon2.io.core.importer.Importer;

/**
 * Implementation of import engine.
 * @author Stanislav Nepochatov
 */
public class ImportEngine extends IOEngine<Importer> {
    
    private final static Logger LOGGER = LoggerFactory.getLogger(ImportEngine.class);
    
    private static final String GENERAL_TIMEOUT_KEY = "generalTimeout";
    
    private final SchemeRepository schemeRepository;
    
    private final SchemeConverter schemeConverter;
    
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(8);

    public ImportEngine(String[] classes, SchemeRepository schemeRepository, 
            SchemeConverter schemeConverter) {
        super(ModuleType.IMPORT, classes);
        this.schemeRepository = schemeRepository;
        this.schemeConverter = schemeConverter;
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
                    scheduler.scheduleAtFixedRate(new ImportTask(source), 0, 
                            (long) scheme.getConfig().get(GENERAL_TIMEOUT_KEY), TimeUnit.SECONDS);
                } else {
                    LOGGER.warn("Some config keys are absent in scheme {}, skipping", scheme.getName());
                }
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

        public ImportTask(ImportSource importSource) {
            this.importSource = importSource;
        }

        @Override
        public void run() {
            LOGGER.info("PING!");
        }
    }
    
}
