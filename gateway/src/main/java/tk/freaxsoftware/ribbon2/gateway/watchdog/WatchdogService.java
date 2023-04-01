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

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tk.freaxsoftware.extras.bus.GlobalCons;
import tk.freaxsoftware.extras.bus.MessageHolder;
import tk.freaxsoftware.extras.bus.annotation.AnnotationUtil;
import tk.freaxsoftware.extras.bus.annotation.Receive;
import tk.freaxsoftware.ribbon2.gateway.config.ApplicationConfig;
import tk.freaxsoftware.ribbon2.gateway.watchdog.data.WatchdogTopic;

/**
 * Watchdog service tracks status of registered topic via message bus 
 * notifications. Registered topics considered as permanent and must not be 
 * disconnected from system. Disconnection may happen on network error or 
 * in case of failure of a node.
 * @author Stanislav Nepochatov
 */
public class WatchdogService {
    
    private final static Logger LOGGER = LoggerFactory.getLogger(WatchdogService.class);
    
    private final ApplicationConfig.WatchdogConfig watchdogConfig;
    
    private Status globalStatus = Status.DISCONNECTED;
    
    private final Map<WatchdogRegistry.Node, Status> nodeStatusMap = new ConcurrentHashMap();
    
    private final Map<String, StatusRecord> topicStatusMap = new ConcurrentHashMap();

    public WatchdogService(ApplicationConfig.WatchdogConfig watchdogConfig) {
        this.watchdogConfig = watchdogConfig;
        WatchdogRegistry.getTopicMap()
                .forEach((topic, record) -> topicStatusMap.put(topic, new StatusRecord(Status.DISCONNECTED, record)));
        AnnotationUtil.subscribeReceiverInstance(this);
        innerUpdateNodeStatusMap(WatchdogRegistry.Node.DIRECTORY);
        innerUpdateNodeStatusMap(WatchdogRegistry.Node.MESSENGER);
        innerUpdateNodeStatusMap(WatchdogRegistry.Node.EXCHANGER);
        innerUpdateGlobalStatus();
        LOGGER.info("Watchdog service started:\nGLOBAL:{}\nDIRECTORY:{}\nMESSENGER:{}\nEXCHANGER:{}",
                globalStatus, 
                nodeStatusMap.get(WatchdogRegistry.Node.DIRECTORY).name(),
                nodeStatusMap.get(WatchdogRegistry.Node.MESSENGER).name(),
                nodeStatusMap.get(WatchdogRegistry.Node.EXCHANGER).name());
    }
    
    private void innerUpdateNodeStatusMap(WatchdogRegistry.Node node) {
        Set<StatusRecord> nodeRecords = topicStatusMap.entrySet().stream()
                .filter(entry -> entry.getValue().getNode() == node)
                .map(nodeEntry -> nodeEntry.getValue())
                .collect(Collectors.toSet());
        Status newStatus = Status.DISCONNECTED;
        for (StatusRecord nodeRecord: nodeRecords) {
            if (nodeRecord.getLevel() == WatchdogRegistry.SeverityLevel.ERROR
                    && nodeRecord.getStatus() == Status.DISCONNECTED) {
                newStatus = Status.DISCONNECTED;
                break;
            }
            if (nodeRecord.getStatus() == Status.CONNECTED) {
                newStatus = Status.CONNECTED;
            }
        }
        nodeStatusMap.put(node, newStatus);
    }
    
    private void innerUpdateGlobalStatus() {
        Boolean allConnected = nodeStatusMap.entrySet().stream()
                .allMatch(entry -> entry.getValue() == Status.CONNECTED);
        globalStatus = allConnected ? Status.CONNECTED : Status.DISCONNECTED;
    }
    
    private void updateWatchdogStatus(WatchdogRegistry.Node node) {
        innerUpdateNodeStatusMap(node);
        innerUpdateGlobalStatus();
    }
    
    @Receive(GlobalCons.G_SUBSCRIBE_TOPIC)
    public void checkSubscribe(MessageHolder<Object> holder) {
        String topic = holder.getHeaders().get(GlobalCons.G_SUBSCRIPTION_DEST_HEADER);
        if (!topicStatusMap.containsKey(topic)) {
            Optional<WatchdogRegistry.Record> recordOpt = WatchdogRegistry.getTopicRecord(topic);
            if (recordOpt.isEmpty()) {
                return; //Abandon the ship!
            }
            topicStatusMap.put(topic, new StatusRecord(Status.CONNECTED, recordOpt.get()));
        }
        LOGGER.info("Watched topic '{}' connected.", topic);
        StatusRecord statusRecord = topicStatusMap.get(topic);
        statusRecord.setStatus(Status.CONNECTED);
        updateWatchdogStatus(statusRecord.getNode());
    }
    
    @Receive(GlobalCons.G_UNSUBSCRIBE_TOPIC)
    public void checkUnsubscribe(MessageHolder<Object> holder) {
        String topic = holder.getHeaders().get(GlobalCons.G_SUBSCRIPTION_DEST_HEADER);
        if (!topicStatusMap.containsKey(topic)) {
            return;
        }
        LOGGER.info("Watched topic '{}' disconnected.", topic);
        StatusRecord statusRecord = topicStatusMap.get(topic);
        statusRecord.setStatus(Status.DISCONNECTED);
        updateWatchdogStatus(statusRecord.getNode());
    }
    
    /**
     * Get watch data by topic with current status if present.
     * @param topic topic to search in service;
     * @return watch data with status or empty optional;
     */
    public Optional<WatchdogTopic> getWatchByTopic(String topic) {
        if (topicStatusMap.containsKey(topic)) {
            return Optional.of(new WatchdogTopic(topic, topicStatusMap.get(topic)));
        }
        return Optional.empty();
    }
    
    public static class StatusRecord extends WatchdogRegistry.Record {
        private Status status;

        public StatusRecord(Status status, WatchdogRegistry.Record record) {
            super(record.getLabel(), record.getLevel(), record.getNode());
            this.status = status;
        }

        public Status getStatus() {
            return status;
        }

        public void setStatus(Status status) {
            this.status = status;
        }
    }
    
    public static enum Status {
        CONNECTED,
        DISCONNECTED
    }
}
