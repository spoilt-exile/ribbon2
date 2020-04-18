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
package tk.freaxsoftware.ribbon2.gateway;

import com.google.gson.Gson;
import io.ebean.Database;
import io.ebean.DatabaseFactory;
import io.ebean.Ebean;
import io.ebean.EbeanServer;
import io.ebean.config.AutoTuneConfig;
import io.ebean.config.AutoTuneMode;
import io.ebean.config.DatabaseConfig;
import io.ebean.datasource.DataSourceConfig;
import java.time.ZonedDateTime;
import spark.Spark;
import tk.freaxsoftware.extras.bus.MessageBus;
import tk.freaxsoftware.extras.bus.MessageOptions;
import tk.freaxsoftware.extras.bus.ResponseHolder;
import tk.freaxsoftware.ribbon2.core.data.Message;

/**
 * Main class for API gateway.
 *
 * @author Stanislav Nepochatov
 */
public class GatewayMain {
    
    /**
     * Gson instance.
     */
    private static final Gson gson = new Gson();

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        initDb();
        Spark.port(9000);
        MessageBus.init();
        Spark.get("/", (req, res) -> {return "OK:200";});
        Spark.post("/call", (req,res) -> {
            Message message = new Message();
            message.setId(Long.MIN_VALUE);
            message.setHeader("Test message to module!");
            message.setContent("Hello there!");
            message.setCreated(ZonedDateTime.now());
            ResponseHolder receivedHolder = new ResponseHolder();
            MessageBus.fire(Message.CALL_CREATE_MESSAGE, message, MessageOptions.Builder.newInstance().deliveryNotification(5).build());
            return "OK";
        }, gson::toJson);
    }

    
    private static void initDb() {
        DataSourceConfig dataSourceConfig = new DataSourceConfig();
        dataSourceConfig.setUsername("ribbon2");
        dataSourceConfig.setPassword("ribbon2");
        dataSourceConfig.setUrl("jdbc:postgresql://localhost:5432/ribbon2-gateway");
        dataSourceConfig.setDriver("org.postgresql.ds.PGSimpleDataSource");
        
        DatabaseConfig config = new DatabaseConfig();
        config.setDdlGenerate(true);
        config.setDdlRun(true);
        
        config.setDataSourceConfig(dataSourceConfig);
        AutoTuneConfig tuneConfig = new AutoTuneConfig();
        tuneConfig.setQueryTuning(false);
        tuneConfig.setMode(AutoTuneMode.DEFAULT_OFF);
        config.setAutoTuneConfig(new AutoTuneConfig());
        Database database = DatabaseFactory.create(config);
        Ebean.register((EbeanServer) database, true);
    }
}
