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
package tk.freaxsoftware.ribbon2.core.data.messagestorage;

import io.ebean.Model;
import io.ebean.annotation.DbJson;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Map;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import tk.freaxsoftware.extras.bus.MessageOptions;
import tk.freaxsoftware.extras.bus.MessageStatus;

/**
 * Entity class for storing messages in db;
 * @author Stanislav Nepochatov
 */
@Entity
public class DbMessage extends Model implements Serializable {
    
    /**
     * DB id of the message.
     */
    @Id
    private Long id;
    
    /**
     * Unique id of the message.
     */
    private String uuid;
    
    /**
     * Unique id of the transaction.
     */
    private String trxId;
    
    /**
     * Date of message creation.
     */
    private ZonedDateTime created;
    
    /**
     * Date of the last message update.
     */
    private ZonedDateTime updated;
    
    /**
     * Status of the message.
     */
    @Enumerated(EnumType.STRING)
    private MessageStatus status;
    
    /**
     * Topic of the message.
     */
    private String topic;
    
    /**
     * Options provided to message bus during sending of the message.
     */
    @DbJson
    private MessageOptions options;
    
    /**
     * Copy of redelivery counter from options.
     */
    private Integer redeliveryCounter = 0;
    
    /**
     * Headers of the message.
     */
    @DbJson
    private Map<String, String> headers;
    
    /**
     * Full class name of the content.
     */
    private String contentClass;
    
    /**
     * Content of the message.
     */
    private String content;
    
    /**
     * Full class name of the response content;
     */
    private String responseClass;
    
    /**
     * Response structure of the message.
     */
    private String response;
    
    /**
     * Response headers;
     */
    private Map<String, String> responseHeaders;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getTrxId() {
        return trxId;
    }

    public void setTrxId(String trxId) {
        this.trxId = trxId;
    }

    public ZonedDateTime getCreated() {
        return created;
    }

    public void setCreated(ZonedDateTime created) {
        this.created = created;
    }

    public ZonedDateTime getUpdated() {
        return updated;
    }

    public void setUpdated(ZonedDateTime updated) {
        this.updated = updated;
    }

    public MessageStatus getStatus() {
        return status;
    }

    public void setStatus(MessageStatus status) {
        this.status = status;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public MessageOptions getOptions() {
        return options;
    }

    public void setOptions(MessageOptions options) {
        this.options = options;
    }

    public Integer getRedeliveryCounter() {
        return redeliveryCounter;
    }

    public void setRedeliveryCounter(Integer redeliveryCounter) {
        this.redeliveryCounter = redeliveryCounter;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public String getContentClass() {
        return contentClass;
    }

    public void setContentClass(String contentClass) {
        this.contentClass = contentClass;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getResponseClass() {
        return responseClass;
    }

    public void setResponseClass(String responseClass) {
        this.responseClass = responseClass;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public Map<String, String> getResponseHeaders() {
        return responseHeaders;
    }

    public void setResponseHeaders(Map<String, String> responseHeaders) {
        this.responseHeaders = responseHeaders;
    }
}
