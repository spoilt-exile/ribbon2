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
import java.util.concurrent.CopyOnWriteArraySet;
import org.apache.commons.lang3.tuple.Pair;

/**
 * Provides global registry for watchdog service. It allows 
 * to make system notification if crucial node is disconnected 
 * or missing. Watchdog works on notifications from message bus.
 * @author Stanislav Nepochatov
 */
public class WatchdogRegistry {
    
    private final static Map<String, Record> topicRecordMap = new ConcurrentHashMap();
    
    private final static Set<Pair<String, Record>> patternTopicRecords = new CopyOnWriteArraySet();
    
    /**
     * Register topic to watch. Use it for permanent topics, topics 
     * can't be deleted from watchdog registry.
     * @param topic topic to wacth;
     * @param label label describes functionality of topic;
     * @param level sevirity level of topic disconnection event;
     * @param node type of node which provides specified topic;
     */
    public static void registerTopic(String topic, String label, SeverityLevel level, Node node) {
        topicRecordMap.put(topic, new Record(label, level, node));
    }
    
    /**
     * Register topic pattern to watch. Use it for permanent topics, patterns 
     * can't be deleted from watchodg registry. Watchdog allows only placing patterns 
     * with {@code WARNING} severity level. Topic patterns has lesser priority over normal topic records.
     * @see SeverityLevel#WARNING
     * @param topicRegex regular expression to match topic;
     * @param label label describes functionality of topic;
     * @param node type of node which provides specified topic;
     */
    public static void registerPatternTopic(String topicRegex, String label, Node node) {
        patternTopicRecords.add(Pair.of(topicRegex, new Record(label, SeverityLevel.WARNING, node)));
    }
    
    /**
     * Get copy of topic map with records. It doesn't include pattern topics.
     * @return copy of topic map;
     */
    public static Map<String, Record> getTopicMap() {
        return Map.copyOf(topicRecordMap);
    }
    
    /**
     * Get record with label and severity level by topic or topic pattern.
     * @param topic topic to search;
     * @return optional of record;
     */
    public static Optional<Record> getTopicRecord(String topic) {
        if (topicRecordMap.containsKey(topic)) {
            return Optional.of(topicRecordMap.get(topic));
        }
        return patternTopicRecords.stream()
                .filter(pr -> topic.matches(pr.getLeft()))
                .map(matched -> matched.getRight())
                .findFirst();
    }
    
    /**
     * Holder of topic additional data.
     */
    public static class Record {
        
        /**
         * Label describes functionality of topic and what it does in system.
         */
        private final String label;
        
        /**
         * Level of notification on topic disconnection event.
         */
        private final SeverityLevel level;
        
        /**
         * Node to connect.
         */
        private final Node node;

        public Record(String label, SeverityLevel level, Node node) {
            this.label = label;
            this.level = level;
            this.node = node;
        }

        public String getLabel() {
            return label;
        }

        public SeverityLevel getLevel() {
            return level;
        }

        public Node getNode() {
            return node;
        }
    }
    
    /**
     * Severity level of action executed on watched topic disconnection.
     */
    public static enum SeverityLevel {
        
        /**
         * Place only warning which will not affect system status.
         */
        WARNING,
        
        /**
         * Place global error on system.
         */
        ERROR;
    }
    
    /**
     * Type of nodes.
     */
    public static enum Node {
        DIRECTORY,
        MESSENGER,
        EXCHANGER;
    }
}
