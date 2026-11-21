/*
 * This file is part of Ribbon2 news message system.
 * 
 * Copyright (C) 2026 Freax Software
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
package tk.freaxsoftware.ribbon2.server;

import java.io.IOException;
import java.nio.charset.Charset;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import tk.freaxsoftware.extras.bus.bridge.http.util.GsonUtils;
import tk.freaxsoftware.ribbon2.core.config.DbConfig;
import tk.freaxsoftware.ribbon2.core.config.EnvironmentOverrider;
import tk.freaxsoftware.ribbon2.server.config.RibbonServerConfig;
import tk.freaxsoftware.ribbon2.server.config.RibbonServerConfig.AccessConfig;
import tk.freaxsoftware.ribbon2.server.networking.RibbonChannelHandler;
import tk.freaxsoftware.ribbon2.server.networking.TCPInitializer;

/**
 * Ribbon server implementation main class.
 * @author Stanislav Nepochatov
 */
public class ServerMain {

    private final static Logger LOGGER = LoggerFactory.getLogger(ServerMain.class);

    /**
     * Gson instance.
     */
    public static final Gson gson = GsonUtils.getGson();

    /**
     * Current application config;
     */
    public static RibbonServerConfig config;

    /** Active client channels, shared by all protocol handlers. */
    public static final ChannelGroup CLIENT_CHANNELS =
            new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    /**
     * Publish one protocol frame to every connected client. Each channel handler
     * decides whether to write it now or defer it until its current command ends.
     */
    public static void broadcast(String frame) {
        for (Channel channel : CLIENT_CHANNELS) {
            RibbonChannelHandler handler = channel.pipeline().get(RibbonChannelHandler.class);
            if (handler != null) {
                handler.offerBroadcast(frame);
            }
        }
    }

    public static void main(String[] args) throws InterruptedException, IOException {
        LOGGER.info("\n{}", IOUtils.toString(ServerMain.class.getClassLoader().getResourceAsStream("header")), Charset.defaultCharset());
        config = gson.fromJson(IOUtils.toString(ServerMain.class.getClassLoader().getResourceAsStream("serverconfig.json"), Charset.defaultCharset()), RibbonServerConfig.class);
        processConfig(config);
        LOGGER.info("Messenger started, config: {}", config);

        Init.init(config);

        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new TCPInitializer())
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            ChannelFuture f = b.bind(3003).sync();
            if(f.isSuccess()) System.out.println("Server is working");
            f.channel().closeFuture().sync();
        } finally {
            System.out.println("Server shuting down");
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

    private static void processConfig(RibbonServerConfig config) {
        EnvironmentOverrider overrider = new EnvironmentOverrider();

        overrider.registerOverride(new EnvironmentOverrider.OverrideEntry<DbConfig>("DB_JDBC_URL", 
                DbConfig.class, (conf, property) -> conf.setJdbcUrl(property)));
        overrider.registerOverride(new EnvironmentOverrider.OverrideEntry<DbConfig>("DB_USERNAME", 
                DbConfig.class, (conf, property) -> conf.setUsername(property)));
        overrider.registerOverride(new EnvironmentOverrider.OverrideEntry<DbConfig>("DB_PASSWORD", 
                DbConfig.class, (conf, property) -> conf.setPassword(property)));

        overrider.registerOverride(new EnvironmentOverrider.OverrideEntry<RibbonServerConfig>("SERVER_PORT", 
                RibbonServerConfig.class, (conf, property) -> conf.setPort(Integer.valueOf(property))));

        overrider.registerOverride(new EnvironmentOverrider.OverrideEntry<AccessConfig>("ACC_ENABLE_SESSIONS", 
                AccessConfig.class, (conf, property) -> conf.setEnableSessions(Boolean.valueOf(property))));
        overrider.registerOverride(new EnvironmentOverrider.OverrideEntry<AccessConfig>("ACC_ALLOW_REMOTE", 
                AccessConfig.class, (conf, property) -> conf.setAllowRemote(Boolean.valueOf(property))));
        overrider.registerOverride(new EnvironmentOverrider.OverrideEntry<AccessConfig>("ACC_REMOTE_GROUP", 
                AccessConfig.class, (conf, property) -> conf.setRemoteGroup(property)));

        overrider.processConfig(config);
        overrider.processConfig(config.getDb());
        overrider.processConfig(config.getAccess());
    }
    
}
