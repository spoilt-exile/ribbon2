/*
 * This file is part of Ribbon2 news message system.
 * 
 * Copyright (C) 2017 Freax Software
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
package tk.freaxsoftware.ribbon2.message;

import java.util.UUID;
import tk.freaxsoftware.extras.bus.Callback;
import tk.freaxsoftware.extras.bus.MessageBus;
import tk.freaxsoftware.extras.bus.MessageOptions;
import tk.freaxsoftware.extras.bus.ResponseHolder;
import tk.freaxsoftware.ribbon2.core.data.Message;
import tk.freaxsoftware.ribbon2.core.data.User;

/**
 * Unit main class.
 *
 * @author Stanislav Nepochatov
 */
public class UnitMain {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        String hostId = UUID.randomUUID().toString();
        
        MessageBus.addSubscription(Message.CALL_CREATE_MESSAGE, (holder) -> {
            User user = new User();
            MessageBus.fire(User.CALL_CHECK_AUTH, null, MessageOptions.callOptions(null, (Callback) (ResponseHolder response) -> {
                user.setLogin((String) response.getContent());
                System.out.println(user.getLogin());
            }));
            Message content = (Message) holder.getContent();
            System.out.println(content.getContent());
            content.setContent("Hello gateway!\nFrom: " + hostId);
            //holder.getResponse().setContent(content);
        });
    }

}
