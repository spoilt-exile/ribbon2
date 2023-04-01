/*
 * This file is part of Ribbon2 news message system.
 * 
 * Copyright (C) 2020-2022 Freax Software
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
package tk.freaxsoftware.ribbon2.gateway.watchdog;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import tk.freaxsoftware.extras.bus.GlobalCons;
import tk.freaxsoftware.extras.bus.MessageHolder;
import tk.freaxsoftware.extras.bus.annotation.AnnotationUtil;
import tk.freaxsoftware.ribbon2.gateway.config.ApplicationConfig;

/**
 * Unit test for watchdog service.
 * @author Stanislav Nepochatov
 */
public class WatchdogServiceTest {
    
    static {
        WatchdogRegistry.registerTopic("Dir.TestWarn", "Dir warning", 
                WatchdogRegistry.SeverityLevel.WARNING, WatchdogRegistry.Node.DIRECTORY);
        WatchdogRegistry.registerTopic("Dir.TestErr", "Dir error", 
                WatchdogRegistry.SeverityLevel.ERROR, WatchdogRegistry.Node.DIRECTORY);
        WatchdogRegistry.registerTopic("Message.TestErr1", "Message error 1", 
                WatchdogRegistry.SeverityLevel.ERROR, WatchdogRegistry.Node.MESSENGER);
        WatchdogRegistry.registerTopic("Message.TestErr2", "Message error 2", 
                WatchdogRegistry.SeverityLevel.ERROR, WatchdogRegistry.Node.MESSENGER);
        WatchdogRegistry.registerTopic("Exc.TestWarn1", "Exchanger warning 1", 
                WatchdogRegistry.SeverityLevel.WARNING, WatchdogRegistry.Node.EXCHANGER);
        WatchdogRegistry.registerTopic("Exc.TestWarn2", "Exchanger warning 2", 
                WatchdogRegistry.SeverityLevel.WARNING, WatchdogRegistry.Node.EXCHANGER);
    }
    
    private WatchdogService watchdog;
    
    @Before
    public void init() {
        watchdog = new WatchdogService(new ApplicationConfig.WatchdogConfig());
    }
    
    @After
    public void clear() {
        AnnotationUtil.unsubscribeReceiverInstance(watchdog);
    }
    
    @Test
    public void shouldDissconnectedOnStartup() {
        Assert.assertEquals(watchdog.getWatchByTopic("Dir.TestWarn").get().getStatus(), 
                WatchdogService.Status.DISCONNECTED);
        Assert.assertEquals(watchdog.getWatchByTopic("Dir.TestErr").get().getStatus(), 
                WatchdogService.Status.DISCONNECTED);
        Assert.assertEquals(watchdog.getWatchByTopic("Message.TestErr1").get().getStatus(), 
                WatchdogService.Status.DISCONNECTED);
        Assert.assertEquals(watchdog.getWatchByTopic("Message.TestErr2").get().getStatus(), 
                WatchdogService.Status.DISCONNECTED);
        Assert.assertEquals(watchdog.getWatchByTopic("Exc.TestWarn1").get().getStatus(), 
                WatchdogService.Status.DISCONNECTED);
        Assert.assertEquals(watchdog.getWatchByTopic("Exc.TestWarn1").get().getStatus(), 
                WatchdogService.Status.DISCONNECTED);
    }
    
    @Test
    public void shouldUpdateTopicStatus() {
        Assert.assertEquals(watchdog.getWatchByTopic("Dir.TestWarn").get().getStatus(), 
                WatchdogService.Status.DISCONNECTED);
        watchdog.checkSubscribe(buildHolder("Dir.TestWarn"));
        Assert.assertEquals(watchdog.getWatchByTopic("Dir.TestWarn").get().getStatus(), 
                WatchdogService.Status.CONNECTED);
        watchdog.checkUnsubscribe(buildHolder("Dir.TestWarn"));
        Assert.assertEquals(watchdog.getWatchByTopic("Dir.TestWarn").get().getStatus(), 
                WatchdogService.Status.DISCONNECTED);
    }
    
    private MessageHolder<Object> buildHolder(String topic) {
        MessageHolder<Object> holder = new MessageHolder<>();
        holder.getHeaders().put(GlobalCons.G_SUBSCRIPTION_DEST_HEADER, topic);
        return holder;
    }
}
