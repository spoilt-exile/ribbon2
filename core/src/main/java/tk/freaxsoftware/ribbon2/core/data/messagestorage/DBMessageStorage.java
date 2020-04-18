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

import io.ebean.DB;
import java.util.Set;
import java.util.stream.Collectors;
import tk.freaxsoftware.extras.bus.MessageHolder;
import tk.freaxsoftware.extras.bus.MessageOptions;
import tk.freaxsoftware.extras.bus.MessageStatus;
import tk.freaxsoftware.extras.bus.storage.MessageStorage;
import tk.freaxsoftware.ribbon2.core.data.convert.impl.DbMessageConverter;

/**
 * DB implementation of message storage.
 * @author Stanislav Nepochatov
 */
public class DBMessageStorage implements MessageStorage {
    
    DbMessageConverter converter = new DbMessageConverter();

    @Override
    public void saveMessage(MessageHolder message) {
        DB.save(converter.convert(message));
    }

    @Override
    public Set<MessageHolder> getUnprocessedMessages() {
        Set<DbMessage> messages = DB.find(DbMessage.class).where()
                .ne("deliveryPolicy", MessageOptions.DeliveryPolicy.CALL).and()
                .eq("status", MessageStatus.ERROR).findSet();
        return messages.stream().map(ms -> converter.convertBack(ms)).collect(Collectors.toSet());
    }

    @Override
    public Set<MessageHolder> getUnprocessedMessagesByTopic(String topic) {
        Set<DbMessage> messages = DB.find(DbMessage.class).where()
                .ne("deliveryPolicy", MessageOptions.DeliveryPolicy.CALL).and()
                .eq("status", MessageStatus.ERROR)
                .and().eq("topic", topic).findSet();
        return messages.stream().map(ms -> converter.convertBack(ms)).collect(Collectors.toSet());
    }

    @Override
    public Set<MessageHolder> getGroupingMessagesByTopic(String topic) {
        Set<DbMessage> messages = DB.find(DbMessage.class).where()
                .ne("deliveryPolicy", MessageOptions.DeliveryPolicy.CALL).and()
                .eq("status", MessageStatus.GROUPING)
                .and().eq("topic", topic).findSet();
        return messages.stream().map(ms -> converter.convertBack(ms)).collect(Collectors.toSet());
    }

    @Override
    public void removeMessage(String id) {
        DB.find(DbMessage.class).where().eq("uuid", id).delete();
    }
}
