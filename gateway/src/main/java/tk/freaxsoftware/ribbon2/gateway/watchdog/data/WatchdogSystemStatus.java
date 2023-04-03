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
package tk.freaxsoftware.ribbon2.gateway.watchdog.data;

import java.util.List;
import tk.freaxsoftware.ribbon2.gateway.watchdog.WatchdogRegistry;
import tk.freaxsoftware.ribbon2.gateway.watchdog.WatchdogService;

/**
 * Contains data of global system status.
 * @author Stanislav Nepochatov
 */
public class WatchdogSystemStatus {
    
    private WatchdogService.Status globalStatus;
    
    private List<WatchdogNode> nodes;

    public WatchdogService.Status getGlobalStatus() {
        return globalStatus;
    }

    public void setGlobalStatus(WatchdogService.Status globalStatus) {
        this.globalStatus = globalStatus;
    }

    public List<WatchdogNode> getNodes() {
        return nodes;
    }

    public void setNodes(List<WatchdogNode> nodes) {
        this.nodes = nodes;
    }
    
    /**
     * Contains data of node status.
     */
    public static class WatchdogNode {
        
        private WatchdogRegistry.Node node;
        
        private WatchdogService.Status status;
        
        private List<WatchdogTopicShort> topics;

        public WatchdogRegistry.Node getNode() {
            return node;
        }

        public void setNode(WatchdogRegistry.Node node) {
            this.node = node;
        }

        public WatchdogService.Status getStatus() {
            return status;
        }

        public void setStatus(WatchdogService.Status status) {
            this.status = status;
        }

        public List<WatchdogTopicShort> getTopics() {
            return topics;
        }

        public void setTopics(List<WatchdogTopicShort> topics) {
            this.topics = topics;
        }
    }
    
    /**
     * Contains short data about topic.
     */
    public static class WatchdogTopicShort {
        
        private String topic;
        
        private String label;
        
        private WatchdogService.Status status;

        public WatchdogTopicShort() {
        }
        
        public WatchdogTopicShort(String topic, WatchdogService.StatusRecord statusRecord) {
            this.topic = topic;
            this.label = statusRecord.getLabel();
            this.status = statusRecord.getStatus();
        }

        public String getTopic() {
            return topic;
        }

        public void setTopic(String topic) {
            this.topic = topic;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public WatchdogService.Status getStatus() {
            return status;
        }

        public void setStatus(WatchdogService.Status status) {
            this.status = status;
        }
    }
}
