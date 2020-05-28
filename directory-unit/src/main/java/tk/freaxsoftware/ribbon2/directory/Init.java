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

import io.ebean.Database;
import io.ebean.DatabaseFactory;
import io.ebean.config.AutoTuneConfig;
import io.ebean.config.AutoTuneMode;
import io.ebean.config.DatabaseConfig;
import io.ebean.datasource.DataSourceConfig;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import liquibase.Liquibase;
import liquibase.database.DatabaseConnection;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import liquibase.resource.ResourceAccessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tk.freaxsoftware.extras.bus.MessageBus;
import tk.freaxsoftware.extras.bus.MessageHolder;
import tk.freaxsoftware.extras.bus.MessageOptions;
import tk.freaxsoftware.extras.bus.exceptions.ExceptionServices;
import tk.freaxsoftware.ribbon2.core.config.DbConfig;
import tk.freaxsoftware.ribbon2.core.data.request.DirectoryPermissionHolder;
import tk.freaxsoftware.ribbon2.core.data.DirectoryPermissionModel;
import tk.freaxsoftware.ribbon2.core.exception.RibbonMessageExceptionHandler;
import tk.freaxsoftware.ribbon2.directory.config.DirectoryUnitConfig;

/**
 * Main init of the directory unit.
 * @author Stanislav Nepochatov
 */
public class Init {
    
    private final static Logger LOGGER = LoggerFactory.getLogger(Init.class);
    
    private final static String TAG = "directory";
    
    public static List<MessageHolder> appendixMessages = new CopyOnWriteArrayList<>();
    
    public static void init(DirectoryUnitConfig config) {
        
        LOGGER.info("Init Liquibase...");
        try {
            initLiquibase(config.getDb());
        } catch (SQLException ex) { 
            LOGGER.error("SQL exception ocurred during init of Liquibase: ", ex);
        } catch (LiquibaseException ex) {
            LOGGER.error("Error ocurred during init of Liquibase: ", ex);
        }
        
        LOGGER.info("Init Ebean...");
        initDb(config.getDb());
        
        LOGGER.info("Init MessagBus...");
        MessageBus.init();
        ExceptionServices.registerHandler(new RibbonMessageExceptionHandler());
        
        appendixMessages.add(new MessageHolder(DirectoryPermissionModel.CALL_INIT_PERMISSIONS, 
                MessageOptions.Builder.newInstance().deliveryCall().async().build(),
                new DirectoryPermissionHolder(config.getDirectory().getPermissions(), TAG)));
        
        if (!appendixMessages.isEmpty()) {
            DirectoryUnit.executor.submit(() -> {
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
    
    private static void initLiquibase(DbConfig dbConfig) throws SQLException, LiquibaseException {
        DatabaseConnection conn = new JdbcConnection(DriverManager
                .getConnection(dbConfig.getJdbcUrl(), dbConfig.getUsername(), dbConfig.getPassword()));
        ResourceAccessor accessor = new ClassLoaderResourceAccessor(Init.class.getClassLoader());
        liquibase.Liquibase base = new Liquibase("liquibase/master.xml", accessor, conn);
        base.update("");
    }
    
    private static void initDb(DbConfig dbConfig) {
        DataSourceConfig dataSourceConfig = new DataSourceConfig();
        dataSourceConfig.setUsername(dbConfig.getUsername());
        dataSourceConfig.setPassword(dbConfig.getPassword());
        dataSourceConfig.setUrl(dbConfig.getJdbcUrl());
        dataSourceConfig.setDriver(dbConfig.getDriver());
        
        DatabaseConfig config = new DatabaseConfig();
        
        config.setDataSourceConfig(dataSourceConfig);
        AutoTuneConfig tuneConfig = new AutoTuneConfig();
        tuneConfig.setQueryTuning(false);
        tuneConfig.setMode(AutoTuneMode.DEFAULT_OFF);
        config.setAutoTuneConfig(new AutoTuneConfig());
        Database database = DatabaseFactory.create(config);
    }
}
