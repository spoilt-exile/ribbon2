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
    
    public static void init(ApplicationConfig config) {
        initDb(config.getDb());
        initHttp(config.getHttp());
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
    
    private static void initDb(ApplicationConfig.DbConfig dbConfig) {
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
