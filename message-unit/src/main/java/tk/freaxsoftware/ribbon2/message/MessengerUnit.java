/*
 * This file is part of Ribbon2 news message system.
 * 
 * Copyright (C) 2017 Freax Software
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
package tk.freaxsoftware.ribbon2.message;

import com.google.gson.Gson;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.utils.IOUtils;
import tk.freaxsoftware.extras.bus.annotation.AnnotationUtil;
import tk.freaxsoftware.extras.bus.bridge.http.util.GsonUtils;
import tk.freaxsoftware.ribbon2.core.config.PropertyConfigProcessor;
import tk.freaxsoftware.ribbon2.message.config.MessengerUnitConfig;
import tk.freaxsoftware.ribbon2.message.entity.converters.DirectoryConverter;
import tk.freaxsoftware.ribbon2.message.facade.DirectoryFacade;
import tk.freaxsoftware.ribbon2.message.facade.MessageFacade;
import tk.freaxsoftware.ribbon2.message.facade.PropertyTypeFacade;
import tk.freaxsoftware.ribbon2.message.repo.DirectoryRepository;
import tk.freaxsoftware.ribbon2.message.repo.MessageRepository;
import tk.freaxsoftware.ribbon2.message.repo.PropertyTypeRepository;
import tk.freaxsoftware.ribbon2.message.service.MessageService;
import tk.freaxsoftware.ribbon2.message.service.PropertyTypeService;

/**
 * Unit main class.
 *
 * @author Stanislav Nepochatov
 */
public class MessengerUnit {

    private final static Logger LOGGER = LoggerFactory.getLogger(MessengerUnit.class);

    /**
     * Gson instance.
     */
    public static final Gson gson = GsonUtils.getGson();

    /**
     * Current application config;
     */
    public static MessengerUnitConfig config;

    /**
     * Available thread pool for various needs;
     */
    public static ExecutorService executor = Executors.newFixedThreadPool(4);

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        LOGGER.info("\n{}", IOUtils.toString(MessengerUnit.class.getClassLoader().getResourceAsStream("header")));
        config = gson.fromJson(IOUtils.toString(MessengerUnit.class.getClassLoader().getResourceAsStream("messageconfig.json")), MessengerUnitConfig.class);
        PropertyConfigProcessor.process(config.getDb());
        LOGGER.info("Messenger started, config: {}", config);

        Init.init(config);

        AnnotationUtil.subscribeReceiverInstance(new PropertyTypeFacade(new PropertyTypeService(new PropertyTypeRepository())));
        AnnotationUtil.subscribeReceiverInstance(new DirectoryFacade(new DirectoryRepository(), new DirectoryConverter()));
        AnnotationUtil.subscribeReceiverInstance(new MessageFacade(new MessageService(new DirectoryRepository(), new MessageRepository(), new PropertyTypeRepository())));
    }

}
