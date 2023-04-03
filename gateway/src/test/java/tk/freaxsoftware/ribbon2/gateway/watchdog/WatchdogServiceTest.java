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

import java.util.Optional;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import tk.freaxsoftware.extras.bus.GlobalCons;
import tk.freaxsoftware.extras.bus.MessageHolder;
import tk.freaxsoftware.extras.bus.annotation.AnnotationUtil;
import tk.freaxsoftware.ribbon2.gateway.config.ApplicationConfig;
import tk.freaxsoftware.ribbon2.gateway.watchdog.data.WatchdogSystemStatus;

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
        WatchdogRegistry.registerPatternTopic("Exc.Pattern.*", 
                "Exchanger warning 2", WatchdogRegistry.Node.EXCHANGER);
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
    
    @Test
    public void shouldEnableDirNode() {
        Assert.assertEquals(watchdog.getWatchByTopic("Dir.TestErr").get().getStatus(), 
                WatchdogService.Status.DISCONNECTED);
        watchdog.checkSubscribe(buildHolder("Dir.TestErr"));
        Assert.assertEquals(watchdog.getWatchByTopic("Dir.TestErr").get().getStatus(), 
                WatchdogService.Status.CONNECTED);
        WatchdogSystemStatus watchdogStatus = watchdog.getWatchdogStatus();
        Assert.assertEquals(watchdogStatus.getGlobalStatus(), WatchdogService.Status.DISCONNECTED);
        assertNodeAndTopicStatus(watchdogStatus, WatchdogRegistry.Node.DIRECTORY, 
                WatchdogService.Status.CONNECTED, "Dir.TestErr", WatchdogService.Status.CONNECTED);
        assertNodeAndTopicStatus(watchdogStatus, WatchdogRegistry.Node.DIRECTORY, 
                WatchdogService.Status.CONNECTED, "Dir.TestWarn", WatchdogService.Status.DISCONNECTED);
    }
    
    @Test
    public void shouldEnableAllNodes() {
        WatchdogSystemStatus watchdogStatus = watchdog.getWatchdogStatus();
        Assert.assertEquals(watchdogStatus.getGlobalStatus(), WatchdogService.Status.DISCONNECTED);
        assertNodeAndTopicStatus(watchdogStatus, WatchdogRegistry.Node.DIRECTORY, 
                WatchdogService.Status.DISCONNECTED, "Dir.TestErr", WatchdogService.Status.DISCONNECTED);
        assertNodeAndTopicStatus(watchdogStatus, WatchdogRegistry.Node.MESSENGER, 
                WatchdogService.Status.DISCONNECTED, "Message.TestErr1", WatchdogService.Status.DISCONNECTED);
        assertNodeAndTopicStatus(watchdogStatus, WatchdogRegistry.Node.MESSENGER, 
                WatchdogService.Status.DISCONNECTED, "Message.TestErr2", WatchdogService.Status.DISCONNECTED);
        assertNodeAndTopicStatus(watchdogStatus, WatchdogRegistry.Node.EXCHANGER, 
                WatchdogService.Status.DISCONNECTED, "Exc.TestWarn1", WatchdogService.Status.DISCONNECTED);
        watchdog.checkSubscribe(buildHolder("Dir.TestErr"));
        watchdog.checkSubscribe(buildHolder("Message.TestErr1"));
        watchdog.checkSubscribe(buildHolder("Message.TestErr2"));
        watchdogStatus = watchdog.getWatchdogStatus();
        Assert.assertEquals(watchdogStatus.getGlobalStatus(), WatchdogService.Status.DISCONNECTED);
        assertNodeAndTopicStatus(watchdogStatus, WatchdogRegistry.Node.DIRECTORY, 
                WatchdogService.Status.CONNECTED, "Dir.TestErr", WatchdogService.Status.CONNECTED);
        assertNodeAndTopicStatus(watchdogStatus, WatchdogRegistry.Node.MESSENGER, 
                WatchdogService.Status.CONNECTED, "Message.TestErr1", WatchdogService.Status.CONNECTED);
        assertNodeAndTopicStatus(watchdogStatus, WatchdogRegistry.Node.MESSENGER, 
                WatchdogService.Status.CONNECTED, "Message.TestErr2", WatchdogService.Status.CONNECTED);
        assertNodeAndTopicStatus(watchdogStatus, WatchdogRegistry.Node.EXCHANGER, 
                WatchdogService.Status.DISCONNECTED, "Exc.TestWarn1", WatchdogService.Status.DISCONNECTED);
        watchdog.checkSubscribe(buildHolder("Exc.TestWarn1"));
        watchdogStatus = watchdog.getWatchdogStatus();
        Assert.assertEquals(watchdogStatus.getGlobalStatus(), WatchdogService.Status.CONNECTED);
        assertNodeAndTopicStatus(watchdogStatus, WatchdogRegistry.Node.EXCHANGER, 
                WatchdogService.Status.CONNECTED, "Exc.TestWarn1", WatchdogService.Status.CONNECTED);
        assertNodeAndTopicStatus(watchdogStatus, WatchdogRegistry.Node.EXCHANGER, 
                WatchdogService.Status.CONNECTED, "Exc.TestWarn2", WatchdogService.Status.DISCONNECTED);
    }
    
    @Test
    public void shouldIncludeNewTopic() {
        final String newTopic = "Exc.TestWarn3";
        WatchdogRegistry.registerTopic(newTopic, "Exchanger warning 3", 
                WatchdogRegistry.SeverityLevel.WARNING, WatchdogRegistry.Node.EXCHANGER);
        WatchdogSystemStatus watchdogStatus = watchdog.getWatchdogStatus();
        WatchdogSystemStatus.WatchdogNode foundNode = watchdogStatus.getNodes()
                .stream()
                .filter(nodeRec -> nodeRec.getNode() == WatchdogRegistry.Node.EXCHANGER)
                .findFirst().get();
        Assert.assertEquals(foundNode.getStatus(), WatchdogService.Status.DISCONNECTED);
        Optional<WatchdogSystemStatus.WatchdogTopicShort> foundTopic = foundNode.getTopics()
                .stream()
                .filter(topicRec -> topicRec.getTopic().equals(newTopic))
                .findFirst();
        Assert.assertFalse(foundTopic.isPresent());
        watchdog.checkSubscribe(buildHolder(newTopic));
        watchdogStatus = watchdog.getWatchdogStatus();
        assertNodeAndTopicStatus(watchdogStatus, WatchdogRegistry.Node.EXCHANGER, 
                WatchdogService.Status.CONNECTED, newTopic, WatchdogService.Status.CONNECTED);
    }
    
    @Test
    public void shouldProcessPatternTopics() {
        final String patTopic1 = "Exc.Pattern.Topic1";
        final String patTopic2 = "Exc.Pattorn.Topic2";
        Assert.assertFalse(watchdog.getWatchByTopic(patTopic1).isPresent());
        Assert.assertFalse(watchdog.getWatchByTopic(patTopic2).isPresent());
        
        watchdog.checkSubscribe(buildHolder(patTopic1));
        watchdog.checkSubscribe(buildHolder(patTopic2));
        
        Assert.assertEquals(watchdog.getWatchByTopic(patTopic1).get().getStatus(), 
                WatchdogService.Status.CONNECTED);
        Assert.assertFalse(watchdog.getWatchByTopic(patTopic2).isPresent());
        
        WatchdogSystemStatus watchdogStatus = watchdog.getWatchdogStatus();
        assertNodeAndTopicStatus(watchdogStatus, WatchdogRegistry.Node.EXCHANGER, 
                WatchdogService.Status.CONNECTED, patTopic1, WatchdogService.Status.CONNECTED);
    }
    
    private void assertNodeAndTopicStatus(WatchdogSystemStatus status, 
            WatchdogRegistry.Node node, WatchdogService.Status nodeStatus,
            String topic, WatchdogService.Status topicStatus) {
        WatchdogSystemStatus.WatchdogNode foundNode = status.getNodes()
                .stream()
                .filter(nodeRec -> nodeRec.getNode() == node)
                .findFirst().get();
        Assert.assertEquals(foundNode.getStatus(), nodeStatus);
        WatchdogSystemStatus.WatchdogTopicShort foundTopic = foundNode.getTopics()
                .stream()
                .filter(topicRec -> topicRec.getTopic().equals(topic))
                .findFirst().get();
        Assert.assertEquals(foundTopic.getStatus(), topicStatus);
    }
    
    private MessageHolder<Object> buildHolder(String topic) {
        MessageHolder<Object> holder = new MessageHolder<>();
        holder.getHeaders().put(GlobalCons.G_SUBSCRIPTION_DEST_HEADER, topic);
        return holder;
    }
}
