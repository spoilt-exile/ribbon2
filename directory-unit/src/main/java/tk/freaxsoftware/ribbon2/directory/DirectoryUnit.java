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
package tk.freaxsoftware.ribbon2.directory;

import com.google.gson.Gson;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.commons.io.IOUtils;
import tk.freaxsoftware.extras.bus.annotation.AnnotationUtil;
import tk.freaxsoftware.extras.bus.bridge.http.util.GsonUtils;
import tk.freaxsoftware.ribbon2.core.config.PropertyConfigProcessor;
import tk.freaxsoftware.ribbon2.directory.config.DirectoryUnitConfig;
import tk.freaxsoftware.ribbon2.directory.facade.DirectoryAccessFacade;
import tk.freaxsoftware.ribbon2.directory.facade.DirectoryFacade;
import tk.freaxsoftware.ribbon2.directory.facade.PermissionFacade;
import tk.freaxsoftware.ribbon2.directory.facade.UserGroupFacade;
import tk.freaxsoftware.ribbon2.directory.repo.DirectoryRepository;
import tk.freaxsoftware.ribbon2.directory.repo.GroupRepository;
import tk.freaxsoftware.ribbon2.directory.repo.PermissionRepository;
import tk.freaxsoftware.ribbon2.directory.repo.UserRepository;
import tk.freaxsoftware.ribbon2.directory.service.AuthService;
import tk.freaxsoftware.ribbon2.directory.service.DirectoryService;
import tk.freaxsoftware.ribbon2.directory.service.PermissionService;

/**
 * Main class of directory unit.
 * @author Stanislav Nepochatov
 */
public class DirectoryUnit {
    
    private final static Logger LOGGER = LoggerFactory.getLogger(DirectoryUnit.class);
    
    /**
     * Gson instance.
     */
    public static final Gson gson = GsonUtils.getGson();
    
    /**
     * Current application config;
     */
    public static DirectoryUnitConfig config;
    
    /**
     * Available thread pool for various needs;
     */
    public static ExecutorService executor = Executors.newFixedThreadPool(4);
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        LOGGER.info("\n{}", IOUtils.toString(DirectoryUnit.class.getClassLoader().getResourceAsStream("header"), Charset.defaultCharset()));
        config = gson.fromJson(IOUtils.toString(DirectoryUnit.class.getClassLoader().getResourceAsStream("dirconfig.json"), Charset.defaultCharset()), DirectoryUnitConfig.class);
        PropertyConfigProcessor.process(config.getDb());
        LOGGER.info("Directory started, config: {}", config);
        
        Init.init(config);
        
        AnnotationUtil.subscribeReceiverInstance(new PermissionFacade(new PermissionService()));
        AnnotationUtil.subscribeReceiverInstance(new DirectoryFacade(new DirectoryService(new DirectoryRepository(), 
                new UserRepository(), new GroupRepository(), new PermissionRepository())));
        AnnotationUtil.subscribeReceiverInstance(new UserGroupFacade(new UserRepository(), new GroupRepository()));
        AnnotationUtil.subscribeReceiverInstance(new DirectoryAccessFacade(new AuthService(new DirectoryRepository(), 
                new UserRepository(), new GroupRepository(), new PermissionRepository())));
    }
}
