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
package tk.freaxsoftware.ribbon2.core.data.convert.impl;

import com.google.gson.Gson;
import tk.freaxsoftware.extras.bus.MessageHolder;
import tk.freaxsoftware.extras.bus.MessageOptions;
import tk.freaxsoftware.extras.bus.ResponseHolder;
import tk.freaxsoftware.extras.bus.bridge.http.util.GsonUtils;
import tk.freaxsoftware.ribbon2.core.data.convert.TwoWayConverter;
import tk.freaxsoftware.ribbon2.core.data.messagestorage.DbMessage;

/**
 * Message converter.
 * @author Stanislav Nepochatov
 */
public class DbMessageConverter implements TwoWayConverter<MessageHolder, DbMessage> {
    
    public static final String DB_ID_HEADER = "Trans.DbId";
    
    private Gson gson = GsonUtils.getGson();

    @Override
    public MessageHolder convertBack(DbMessage destination) {
        MessageHolder holder = new MessageHolder();
        holder.setHeaders(destination.getHeaders());
        holder.getHeaders().put(DB_ID_HEADER, destination.getId().toString());
        holder.setId(destination.getUuid());
        holder.setTrxId(destination.getTrxId());
        holder.setCreated(destination.getCreated());
        holder.setUpdated(destination.getUpdated());
        holder.setStatus(destination.getStatus());
        holder.setTopic(destination.getTopic());
        holder.setOptions(destination.getOptions());
        holder.setRedeliveryCounter(destination.getRedeliveryCounter());
        holder.setContent(getByClass(destination.getContent(), 
                destination.getContentClass()));
        holder.setResponse(new ResponseHolder());
        holder.getResponse().setHeaders(destination.getResponseHeaders());
        holder.getResponse().setContent(getByClass(destination.getResponse(), 
                destination.getResponseClass()));
        return holder;
    }
    
    private Object getByClass(String json, String className) {
        if (json != null && className != null) {
            try {
                Class classz = Class.forName(className);
                return gson.fromJson(json, classz);
            } catch (ClassNotFoundException ex) {
                ex.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public DbMessage convert(MessageHolder source) {
        DbMessage message = new DbMessage();
        if (source.getHeaders().containsKey(DB_ID_HEADER)) {
            message.setId(Long.parseLong((String) source.getHeaders().get(DB_ID_HEADER)));
        }
        message.setUuid(source.getId());
        message.setTopic(source.getTopic());
        message.setTrxId(source.getTrxId());
        message.setCreated(source.getCreated());
        message.setUpdated(source.getUpdated());
        message.setStatus(source.getStatus());
        
        MessageOptions options = new MessageOptions();
        options.setAsync(source.getOptions().isAsync());
        options.setBroadcast(source.getOptions().isBroadcast());
        options.setDeliveryPolicy(source.getOptions().getDeliveryPolicy());
        options.setRedeliveryCounter(source.getOptions().getRedeliveryCounter());
        options.setHeaders(source.getOptions().getHeaders());
        message.setOptions(options);
        
        message.setRedeliveryCounter(source.getRedeliveryCounter());
        message.setHeaders(source.getHeaders());
        Object content = source.getContent();
        if (content != null) {
            message.setContentClass(content.getClass().getCanonicalName());
            message.setContent(gson.toJson(content));
        }
        Object responseContent = source.getResponse().getContent();
        if (responseContent != null) {
            message.setResponseClass(responseContent.getClass().getCanonicalName());
            message.setResponse(gson.toJson(responseContent));
        }
        message.setResponseHeaders(source.getResponse().getHeaders());
        return message;
    }
    
}
