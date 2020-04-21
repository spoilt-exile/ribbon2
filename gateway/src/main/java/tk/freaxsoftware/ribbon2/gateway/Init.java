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
package tk.freaxsoftware.ribbon2.gateway;

import io.ebean.DB;
import io.ebean.Database;
import io.ebean.DatabaseFactory;
import io.ebean.config.AutoTuneConfig;
import io.ebean.config.AutoTuneMode;
import io.ebean.config.DatabaseConfig;
import io.ebean.datasource.DataSourceConfig;
import java.sql.DriverManager;
import java.sql.SQLException;
import liquibase.Liquibase;
import liquibase.database.DatabaseConnection;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import liquibase.resource.ResourceAccessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Spark;
import tk.freaxsoftware.extras.bus.MessageBus;
import tk.freaxsoftware.ribbon2.gateway.config.ApplicationConfig;
import tk.freaxsoftware.ribbon2.gateway.entity.UserEntity;
import tk.freaxsoftware.ribbon2.gateway.routes.AuthRoutes;
import tk.freaxsoftware.ribbon2.gateway.utils.SHAHash;

/**
 * Main init of the gateway.
 * @author Stanislav Nepochatov
 */
public class Init {
    
    private final static Logger LOGGER = LoggerFactory.getLogger(Init.class);
    
    public static void init(ApplicationConfig config) {
        
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
        
        LOGGER.info("Init Spark...");
        initHttp(config.getHttp());
        
        LOGGER.info("Init MessagBus...");
        MessageBus.init();
        
        UserEntity root = new UserEntity();
        root.setEnabled(true);
        root.setLogin("root");
        root.setPassword(SHAHash.hashPassword("root"));
        root.setEmail("localhost@localhost");
        root.setDescription("Admin");
        DB.getDefault().save(root);
    }
    
    private static void initHttp(ApplicationConfig.HttpConfig httpConfig) {
        Spark.port(httpConfig.getPort());
        AuthRoutes.init(httpConfig);
    }
    
    private static void initLiquibase(ApplicationConfig.DbConfig dbConfig) throws SQLException, LiquibaseException {
        DatabaseConnection conn = new JdbcConnection(DriverManager
                .getConnection(dbConfig.getJdbcUrl(), dbConfig.getUsername(), dbConfig.getPassword()));
        ResourceAccessor accessor = new ClassLoaderResourceAccessor(Init.class.getClassLoader());
        liquibase.Liquibase base = new Liquibase("liquibase/master.xml", accessor, conn);
        base.update("");
    }
    
    private static void initDb(ApplicationConfig.DbConfig dbConfig) {
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