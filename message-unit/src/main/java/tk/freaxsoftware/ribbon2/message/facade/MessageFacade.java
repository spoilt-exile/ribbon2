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
package tk.freaxsoftware.ribbon2.message.facade;

import tk.freaxsoftware.extras.bus.MessageBus;
import tk.freaxsoftware.extras.bus.MessageHolder;
import tk.freaxsoftware.extras.bus.MessageOptions;
import tk.freaxsoftware.extras.bus.ResponseHolder;
import tk.freaxsoftware.extras.bus.annotation.Receive;
import tk.freaxsoftware.ribbon2.core.data.MessageModel;
import tk.freaxsoftware.ribbon2.core.utils.MessageUtils;
import tk.freaxsoftware.ribbon2.message.service.MessageService;
import tk.freaxsoftware.ribbon2.message.entity.Message;
import tk.freaxsoftware.ribbon2.message.entity.converters.MessageConverter;

/**
 * Facade for handling message calls.
 * @author Stanislav Nepochatov
 */
public class MessageFacade {
    
    private final MessageConverter converter = new MessageConverter();
    private final MessageService messageService;

    public MessageFacade(MessageService messageService) {
        this.messageService = messageService;
    }
    
    @Receive(MessageModel.CALL_CREATE_MESSAGE)
    public void createMessage(MessageHolder<MessageModel> createMessage) {
        String userLogin = MessageUtils.getAuthFromHeader(createMessage);
        Message message = converter.convert(createMessage.getContent());
        Message saved = messageService.createMessage(message, userLogin);
        createMessage.setResponse(new ResponseHolder());
        MessageModel savedModel = converter.convertBack(saved);
        createMessage.getResponse().setContent(savedModel);
        MessageBus.fire(MessageModel.NOTIFICATION_MESSAGE_CREATED, savedModel, 
                MessageOptions.Builder.newInstance().deliveryNotification(5).build());
    }
    
    @Receive(MessageModel.CALL_UPDATE_MESSAGE)
    public void updateMessage(MessageHolder<MessageModel> updateMessage) {
        
    }
    
    @Receive(MessageModel.CALL_DELETE_MESSAGE)
    public void deleteMessage(MessageHolder<MessageModel> deleteMessage) {
        
    }
    
}
