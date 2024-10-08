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
package tk.freaxsoftware.ribbon2.message.entity.converters;

import tk.freaxsoftware.ribbon2.core.data.MessageModel;
import tk.freaxsoftware.ribbon2.core.data.convert.TwoWayConverter;
import tk.freaxsoftware.ribbon2.message.entity.Message;

/**
 * Message converter.
 * @author Stanislav Nepochatov
 */
public class MessageConverter implements TwoWayConverter<MessageModel, Message> {

    @Override
    public MessageModel convertBack(Message destination) {
        MessageModel model = new MessageModel();
        model.setId(destination.getId());
        model.setUid(destination.getUid());
        model.setParentUid(destination.getParentUid());
        model.setCreated(destination.getCreated());
        model.setCreatedBy(destination.getCreatedBy());
        model.setUpdated(destination.getUpdated());
        model.setUpdatedBy(destination.getUpdatedBy());
        model.setHeader(destination.getHeader());
        model.setContent(destination.getContent());
        model.setTags(destination.getTags());
        model.setDirectories(destination.getDirectories());
        model.setProperties(destination.getProperties());
        return model;
    }

    @Override
    public Message convert(MessageModel source) {
        Message message = new Message();
        message.setId(source.getId());
        message.setUid(source.getUid());
        message.setParentUid(source.getParentUid());
        message.setCreated(source.getCreated());
        message.setCreatedBy(source.getCreatedBy());
        message.setUpdated(source.getUpdated());
        message.setUpdatedBy(source.getUpdatedBy());
        message.setHeader(source.getHeader());
        message.setContent(source.getContent());
        message.setTags(source.getTags());
        message.setDirectories(source.getDirectories());
        message.setProperties(source.getProperties());
        return message;
    }
}
