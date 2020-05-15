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
package tk.freaxsoftware.ribbon2.message;

import io.ebean.Database;
import io.ebean.DatabaseFactory;
import io.ebean.config.AutoTuneConfig;
import io.ebean.config.AutoTuneMode;
import io.ebean.config.DatabaseConfig;
import io.ebean.datasource.DataSourceConfig;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tk.freaxsoftware.extras.bus.MessageBus;
import tk.freaxsoftware.extras.bus.MessageHolder;
import tk.freaxsoftware.extras.bus.MessageOptions;
import tk.freaxsoftware.ribbon2.core.config.DbConfig;
import tk.freaxsoftware.ribbon2.core.data.DirectoryPermissionHolder;
import tk.freaxsoftware.ribbon2.core.data.DirectoryPermissionModel;
import tk.freaxsoftware.ribbon2.message.config.MessengerUnitConfig;

/**
 * Main init of the message unit.
 * @author Stanislav Nepochatov
 */
public class Init {
    
    private final static Logger LOGGER = LoggerFactory.getLogger(Init.class);
    
    private final static String TAG = "message";
    
    public static List<MessageHolder> appendixMessages = new CopyOnWriteArrayList<>();
    
    public static void init(MessengerUnitConfig config) {
        
        LOGGER.info("Init Ebean...");
        initDb(config.getDb());
        
        LOGGER.info("Init MessagBus...");
        MessageBus.init();
        
        appendixMessages.add(new MessageHolder(DirectoryPermissionModel.CALL_INIT_PERMISSIONS, 
                MessageOptions.Builder.newInstance().deliveryCall().async().build(),
                new DirectoryPermissionHolder(config.getMessenger().getPermissions(), TAG)));
        
        if (!appendixMessages.isEmpty()) {
            MessengerUnit.executor.submit(() -> {
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException ex) {
                    //Do nothing;
                }
                for (MessageHolder holder: appendixMessages) {
                    LOGGER.info("Sending appendix message {}", holder.getTopic());
                    MessageBus.fire(holder);
                }
            });
        }
    }
    
    private static void initDb(DbConfig dbConfig) {
        DataSourceConfig dataSourceConfig = new DataSourceConfig();
        dataSourceConfig.setUsername(dbConfig.getUsername());
        dataSourceConfig.setPassword(dbConfig.getPassword());
        dataSourceConfig.setUrl(dbConfig.getJdbcUrl());
        dataSourceConfig.setDriver(dbConfig.getDriver());
        
        DatabaseConfig config = new DatabaseConfig();
        config.setDdlGenerate(true);
        config.setDdlRun(true);
        
        config.setDataSourceConfig(dataSourceConfig);
        AutoTuneConfig tuneConfig = new AutoTuneConfig();
        tuneConfig.setQueryTuning(false);
        tuneConfig.setMode(AutoTuneMode.DEFAULT_OFF);
        config.setAutoTuneConfig(new AutoTuneConfig());
        Database database = DatabaseFactory.create(config);
    }
}