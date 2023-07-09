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
import io.javalin.Javalin;
import static io.javalin.apibuilder.ApiBuilder.delete;
import static io.javalin.apibuilder.ApiBuilder.get;
import static io.javalin.apibuilder.ApiBuilder.path;
import static io.javalin.apibuilder.ApiBuilder.post;
import static io.javalin.apibuilder.ApiBuilder.put;
import io.javalin.openapi.ApiKeyAuth;
import io.javalin.openapi.plugin.OpenApiConfiguration;
import io.javalin.openapi.plugin.OpenApiPlugin;
import io.javalin.openapi.plugin.SecurityConfiguration;
import io.javalin.openapi.plugin.swagger.SwaggerConfiguration;
import io.javalin.openapi.plugin.swagger.SwaggerPlugin;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tk.freaxsoftware.extras.bus.MessageContextHolder;
import tk.freaxsoftware.extras.bus.annotation.AnnotationUtil;
import tk.freaxsoftware.extras.bus.bridge.http.util.GsonMapper;
import tk.freaxsoftware.extras.bus.bridge.http.util.GsonUtils;
import tk.freaxsoftware.ribbon2.core.exception.CoreError;
import tk.freaxsoftware.ribbon2.core.exception.CoreException;
import tk.freaxsoftware.ribbon2.core.exception.RibbonErrorCodes;
import tk.freaxsoftware.ribbon2.gateway.config.ApplicationConfig;
import tk.freaxsoftware.ribbon2.gateway.routes.DirectoryRoutes;
import tk.freaxsoftware.ribbon2.gateway.routes.GroupRoutes;
import tk.freaxsoftware.ribbon2.gateway.routes.MessageRoutes;
import tk.freaxsoftware.ribbon2.gateway.routes.UserRoutes;
import tk.freaxsoftware.extras.bus.exceptions.NoSubscriptionMessageException;
import tk.freaxsoftware.ribbon2.core.config.DbConfig;
import tk.freaxsoftware.ribbon2.core.config.EnvironmentOverrider;
import tk.freaxsoftware.ribbon2.core.data.DirectoryAccessModel;
import tk.freaxsoftware.ribbon2.core.data.DirectoryModel;
import tk.freaxsoftware.ribbon2.gateway.io.IOService;
import tk.freaxsoftware.ribbon2.gateway.io.routes.IORoutes;
import tk.freaxsoftware.ribbon2.gateway.routes.AuthRoutes;
import tk.freaxsoftware.ribbon2.gateway.routes.WatchdogRoutes;
import tk.freaxsoftware.ribbon2.gateway.watchdog.WatchdogService;

/**
 * Main class for API gateway.
 *
 * @author Stanislav Nepochatov
 */
public class GatewayMain {
    
    private final static Logger LOGGER = LoggerFactory.getLogger(GatewayMain.class);
    
    /**
     * Gson instance.
     */
    public static final Gson gson = GsonUtils.getGson();
    
    /**
     * Current application config;
     */
    public static ApplicationConfig config;
    
    /**
     * Available thread pool for various needs;
     */
    public static ExecutorService executor = Executors.newFixedThreadPool(4);

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        LOGGER.info("\n{}", IOUtils.toString(GatewayMain.class.getClassLoader().getResourceAsStream("header"), Charset.defaultCharset()));
        config = gson.fromJson(IOUtils.toString(GatewayMain.class.getClassLoader().getResourceAsStream("appconfig.json"), Charset.defaultCharset()), ApplicationConfig.class);
        processConfig(config);
        LOGGER.info("Gateway started, config: {}", config);
        DirectoryRoutes.initWatchdog();
        MessageRoutes.initWatchdog();
        IORoutes.initWatchdog();
        Javalin app = Javalin.create(javalinConfig -> {
            OpenApiConfiguration openApiConfiguration = new OpenApiConfiguration();
            openApiConfiguration.getInfo().setTitle("Ribbon2 System API");
            openApiConfiguration.setSecurity(new SecurityConfiguration().withSecurityScheme("ribbonToken", new ApiKeyAuth(config.getHttp().getAuthType().name().toLowerCase(), config.getHttp().getAuthTokenName())));
            javalinConfig.plugins.register(new OpenApiPlugin(openApiConfiguration));
            javalinConfig.plugins.register(new SwaggerPlugin(new SwaggerConfiguration()));
            javalinConfig.jsonMapper(new GsonMapper());
        }).routes(() -> {
            //Main auth method
            path("/auth", () -> {
                post(AuthRoutes::auth);
            });
            //Root of api (authorized only)
            path("/api", () -> {
                //Get current account
                path("/account", () -> {
                    get(AuthRoutes::account);
                });
                
                //User routes
                path("/user", () -> {
                    post(UserRoutes::createUser);
                    put(UserRoutes::updateUser);
                    get(UserRoutes::getUserPage);
                    path("/{id}", () -> {
                        get(UserRoutes::getUser);
                        delete(UserRoutes::deleteUser);
                    }); 
                });
                
                //Group routes
                path("/group", () -> {
                    post(GroupRoutes::createGroup);
                    put(GroupRoutes::updateGroup);
                    get(GroupRoutes::getGroupPage);
                    path("/{id}", () -> {
                        get(GroupRoutes::getGroup);
                        delete(GroupRoutes::deleteGroup);
                    }); 
                });
                
                //Directory routes
                path("/directory", () -> {
                    post(DirectoryRoutes::createDirectory);
                    put(DirectoryRoutes::updateDirectory);
                    get(DirectoryRoutes::getDirectoryPage);
                    path("/{path}", () -> {
                        get(DirectoryRoutes::getDirectory);
                        delete(DirectoryRoutes::deleteDirectory);
                    }); 
                    path("/access/{path}", () -> {
                        get(DirectoryRoutes::getDirectoryAccess);
                        post(DirectoryRoutes::editDirectoryAccess);
                    });
                    path("/access/permission/all", () -> {
                        get(DirectoryRoutes::getAllDirectoriesPermissions);
                    });
                    path("/permission/{permission}", () -> {
                        get(DirectoryRoutes::getDirectoriesByPermission);
                    });
                    path("/access/permission/current/{path}", () -> {
                        get(DirectoryRoutes::getCurrentPermissionsByDirectory);
                    });
                });
                
                //Message routes
                path("/message", () -> {
                    post(MessageRoutes::createMesage);
                    put(MessageRoutes::updateMessage);
                    path("/{uid}", () -> {
                        delete(MessageRoutes::deleteMessage);
                    });
                    path("/{dir}", () -> {
                        get(MessageRoutes::getMessagePage);
                    });
                    path("/{uid}/dir/{dir}", () -> {
                        get(MessageRoutes::getMessage);
                    });
                    path("/property/all", () -> {
                        get(MessageRoutes::getMessageProperties);
                    });
                    path("/property/{uid}", () -> {
                        post(MessageRoutes::addMessageProperty);
                    });
                });
                
                //IO routes
                path("io", () -> {
                    path("/protocol", () -> {
                        get(IORoutes::getProtocols);
                    });
                    path("/scheme", () -> {
                        post(IORoutes::saveScheme);
                        get(IORoutes::getSchemes);
                    });
                    path("/scheme/{type}/{protocol}/{name}", () -> {
                        get(IORoutes::getScheme);
                        delete(IORoutes::deleteScheme);
                    });
                    path("/export/scheme/{protocol}/{name}/assign/{dir}", () -> {
                        post(IORoutes::assignExportScheme);
                    });
                    path("/export/scheme/{protocol}/{name}/dismiss/{dir}", () -> {
                        delete(IORoutes::dismissExportScheme);
                    });
                    path("/export/scheme/{dirName}", () -> {
                        get(IORoutes::getExportSchemesByDirectory);
                    });
                });
                
                path("watchdog", () -> {
                    path("/statusByTopic/{topic}", () -> {
                        get(WatchdogRoutes::getWatchByTopic);
                    });
                    path("/status", () -> {
                        get(WatchdogRoutes::getWatch);
                    });
                });
            });
        }).start(config.getHttp().getPort());
        Init.init(config);
        AuthRoutes.init(app, config.getHttp());;
        WatchdogRoutes.init(new WatchdogService(config.getWatchdog()));
        
        AnnotationUtil.subscribeReceiverInstance(IOService.getInstance());
        
        app.exception(CoreException.class, (ex, ctx) -> {
            LOGGER.error("Error occurred:", ex);
            ctx.status(ex.getCode().getHttpCode());
            ctx.json(new CoreError(ex));
        });
        
        app.exception(NoSubscriptionMessageException.class, (ex, ctx) -> {
            LOGGER.error("Call error occurred:", ex);
            CoreError error = new CoreError();
            error.setCode(RibbonErrorCodes.CALL_ERROR);
            error.setMessage(ex.getMessage());
            ctx.status(RibbonErrorCodes.CALL_ERROR.getHttpCode());
            ctx.json(error);
        });
        app.after((cntxt) -> {
            MessageContextHolder.clearContext();
        });
        registerTypes();
    }
    
    private static void processConfig(ApplicationConfig config) {
        EnvironmentOverrider overrider = new EnvironmentOverrider();
        overrider.registerOverride(new EnvironmentOverrider.OverrideEntry<DbConfig>("DB_JDBC_URL", 
                DbConfig.class, (conf, property) -> conf.setJdbcUrl(property)));
        overrider.registerOverride(new EnvironmentOverrider.OverrideEntry<DbConfig>("DB_USERNAME", 
                DbConfig.class, (conf, property) -> conf.setUsername(property)));
        overrider.registerOverride(new EnvironmentOverrider.OverrideEntry<DbConfig>("DB_PASSWORD", 
                DbConfig.class, (conf, property) -> conf.setPassword(property)));
        
        overrider.registerOverride(new EnvironmentOverrider.OverrideEntry<ApplicationConfig.HttpConfig>("HTTP_PORT", 
                ApplicationConfig.HttpConfig.class, (conf, property) -> conf.setPort(Integer.valueOf(property))));
        overrider.registerOverride(new EnvironmentOverrider.OverrideEntry<ApplicationConfig.HttpConfig>("HTTP_URL", 
                ApplicationConfig.HttpConfig.class, (conf, property) -> conf.setUrl(property)));
        overrider.registerOverride(new EnvironmentOverrider.OverrideEntry<ApplicationConfig.HttpConfig>("HTTP_AUTH_TYPE", 
                ApplicationConfig.HttpConfig.class, (conf, property) -> conf.setAuthType(ApplicationConfig.HttpConfig.AuthType.valueOf(property))));
        overrider.registerOverride(new EnvironmentOverrider.OverrideEntry<ApplicationConfig.HttpConfig>("HTTP_AUTH_TOKEN_NAME", 
                ApplicationConfig.HttpConfig.class, (conf, property) -> conf.setAuthTokenName(property)));
        overrider.registerOverride(new EnvironmentOverrider.OverrideEntry<ApplicationConfig.HttpConfig>("HTTP_AUTH_TOKEN_SECRET", 
                ApplicationConfig.HttpConfig.class, (conf, property) -> conf.setAuthTokenSecret(property)));
        overrider.registerOverride(new EnvironmentOverrider.OverrideEntry<ApplicationConfig.HttpConfig>("HTTP_AUTH_TOKEN_VALID_DAYS", 
                ApplicationConfig.HttpConfig.class, (conf, property) -> conf.setAuthTokenValidDays(Integer.valueOf(property))));
        
        overrider.registerOverride(new EnvironmentOverrider.OverrideEntry<ApplicationConfig.WatchdogConfig>("WATCHDOG_ENABLE",
                ApplicationConfig.WatchdogConfig.class, (conf, property) -> conf.setEnable(Boolean.valueOf(property))));
        overrider.registerOverride(new EnvironmentOverrider.OverrideEntry<ApplicationConfig.WatchdogConfig>("WATCHDOG_IGNORE_TOPICS",
                ApplicationConfig.WatchdogConfig.class, (conf, property) -> conf.setIgnoreTopics(List.of(property.split(",")))));
        
        overrider.processConfig(config.getDb());
        overrider.processConfig(config.getHttp());
    }
    
    private static void registerTypes() {
        DirectoryModel.registerListType();
        DirectoryAccessModel.registerSetType();;
    }
}
