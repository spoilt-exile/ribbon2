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
import tk.freaxsoftware.ribbon2.core.data.convert.Converter;
import tk.freaxsoftware.ribbon2.message.entity.Message;

/**
 * Message converter without content.
 * @author Stanislav Nepochatov
 */
public class MessageGenericConverter implements Converter<Message, MessageModel> {

    @Override
    public MessageModel convert(Message source) {
        MessageModel model = new MessageModel();
        model.setId(source.getId());
        model.setUid(source.getUid());
        model.setParentUid(source.getParentUid());
        model.setCreated(source.getCreated());
        model.setCreatedBy(source.getCreatedBy());
        model.setUpdated(source.getUpdated());
        model.setUpdatedBy(source.getUpdatedBy());
        model.setHeader(source.getHeader());
        model.setTags(source.getTags());
        model.setDirectories(source.getDirectoryNames());
        model.setProperties(source.getProperties());
        return model;
    }
}
