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
package tk.freaxsoftware.ribbon2.gateway;

import com.google.gson.Gson;
import java.util.UUID;
import spark.Spark;
import tk.freaxsoftware.extras.bus.HeaderBuilder;
import tk.freaxsoftware.extras.bus.MessageBus;
import tk.freaxsoftware.extras.bus.MessageOptions;
import tk.freaxsoftware.extras.bus.ResponseHolder;
import tk.freaxsoftware.ribbon2.core.data.Message;

/**
 * Main class for API gateway.
 *
 * @author Stanislav Nepochatov
 */
public class GatewayMain {
    
    /**
     * Gson instance.
     */
    private static final Gson gson = new Gson();

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Spark.port(9000);
        MessageBus.init();
        System.out.println("TEST!!");
        Spark.get("/", (req, res) -> {return "OK:200";});
        Spark.post("/call", (req,res) -> {
            Message message = new Message();
            message.setId(Long.MIN_VALUE);
            message.setHeader("Test message to module!");
            message.setContent("Hello there!");
            ResponseHolder receivedHolder = new ResponseHolder();
            MessageBus.fire(Message.MESSAGE_ID_ADD_MESSAGE, message, HeaderBuilder.newInstance().build(), MessageOptions.Builder.newInstance().ensure().callback((holder) -> {
                receivedHolder.setContent(holder);
            }).build());
            return receivedHolder.getContent();
        }, gson::toJson);
    }

}
