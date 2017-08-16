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
package tk.freaxsoftware.ribbon2.test.unit;

import io.vertx.core.Vertx;
import io.vertx.ext.stomp.StompClient;
import io.vertx.ext.stomp.StompClientConnection;

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
        StompClient client = StompClient.create(Vertx.vertx())
                .connect(ar -> {
                    if (ar.succeeded()) {
                        StompClientConnection connection = ar.result();
                        connection.subscribe("/test",
                                frame -> System.out.println("Just received a frame from /queue : " + frame.getBodyAsString()));
                    } else {
                        System.out.println("Failed to connect to the STOMP server: " + ar.cause().toString());
                    }
                });
    }

}
