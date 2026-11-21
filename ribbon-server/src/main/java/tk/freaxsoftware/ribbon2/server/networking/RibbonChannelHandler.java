/*
 * This file is part of Ribbon2 news message system.
 * 
 * Copyright (C) 2026 Freax Software
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
package tk.freaxsoftware.ribbon2.server.networking;

import java.util.Objects;
import java.util.Optional;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import tk.freaxsoftware.ribbon2.server.protocol.ConnectionTypes;
import tk.freaxsoftware.ribbon2.server.protocol.ProtocolBinder;
import tk.freaxsoftware.ribbon2.server.protocol.ProtocolUtils;
import tk.freaxsoftware.ribbon2.server.protocol.RibbonCommand;

/**
 * Ribbon protocol channel handler. Contains main logic to get command name and execute it. 
 * Also supports collection of messages and remote auth.
 * @author Stanislav Nepochatov
 */
public class RibbonChannelHandler extends SimpleChannelInboundHandler<String>  {

    private final static Logger LOGGER = LoggerFactory.getLogger(RibbonChannelHandler.class);

    private final HandlerState state = new HandlerState();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        if (state.isInCollectionState()) {
            if (Objects.equals(msg, ProtocolUtils.END_COMMAND)) {
                executeCommand(ctx, state.getCurrentCommand(), state, state.getCurrentCommandArgs());
            } else {
                state.getCollectionBuffer().append(msg);
                state.getCollectionBuffer().append('\n');
            }
            return;
        }
        String[] parsedCommand = ProtocolUtils.parseCommand(msg);
        if (parsedCommand == null || (parsedCommand != null && parsedCommand.length != 2)) {
            LOGGER.warn("[{}]: command format error: {}", state.getId(), msg);
            ctx.writeAndFlush(ProtocolUtils.ERROR_RESPONSE + "Помилка формату команди!");
            return;
        }
        Optional<RibbonCommand> commandOpt = ProtocolBinder.commands.stream().filter(c -> Objects.equals(c.getName(), parsedCommand[0])).findAny();
        if (commandOpt.isEmpty()) {
            LOGGER.warn("[{}]: unknown command: {}", state.getId(), parsedCommand[0]);
            ctx.writeAndFlush(ProtocolUtils.ERROR_RESPONSE + "Невідома команда!");
            return;
        }
        executeCommand(ctx, commandOpt.get(), state, parsedCommand[0]);
    }

    private void executeCommand(ChannelHandlerContext ctx, RibbonCommand command, HandlerState state, String args) {
        String response = command.exec(state, args);
        if (response != null) {
            ctx.writeAndFlush(response);
        }
        //Reset state of the state on the end of command, also sends broadcast messages.
        if (!state.isInCollectionState()) {
            state.setCurrentCommand(null);
            state.setCurrentCommandArgs(null);
            state.getBroadcastBuffer().forEach(bm -> ctx.writeAndFlush(bm));
            if (state.isRemoteAuthUsed) {
                state.setIsRemoteAuthUsed(false);
            }
        }
    }
    
    /**
     * Internal state of protocol handler.
     */
    public static class HandlerState {

        /**
         * Unique id of the connection.
         */
        private String id = UUID.randomUUID().toString();

        /**
         * Type of the connection (NEW, CLIENT or CONTROL).
         */
        private ConnectionTypes type;

        /**
         * Authenticated user name.
         */
        private String authUser;

        /**
         * Flag to enable remote mode of connection. Used primaraly for RibbonWeb.
         */
        private Boolean isRemote;

        /**
         * Flag indicates that remote authentication is used for running commands.
         */
        private Boolean isRemoteAuthUsed;

        /**
         * Remote authenticated user. 
         * Used for remote mode to switch command execution context. 
         * Lasts only for one command.
         */
        private String remouteAuthUser;

        /**
         * Instance of current command. If this field is populated 
         * then command awaits collection of the whole message.
         */
        private RibbonCommand currentCommand;

        /**
         * Copy of current command arguments to keep while message is collecting.
         */
        private String currentCommandArgs;

        /**
         * Message collecting buffer. Once it filled, command will be called again.
         */
        private StringBuffer collectionBuffer;

        /**
         * Queue for broadcast messages from other connections. Once command execution 
         * is complete it will send all it's content to client.
         */
        private Queue<String> broadcastBuffer = new ConcurrentLinkedQueue<>();

        /**
         * Check if handler is busy by collection command body and waiting for another line of input or END: command;
         * @return true if channel is busy, false - if channel is open for commands;
         */
        public Boolean isInCollectionState() {
            return this.currentCommand != null && this.currentCommandArgs != null && this.collectionBuffer != null;
        }

        public String getId() {
            return id;
        }
        public void setId(String id) {
            this.id = id;
        }
        public ConnectionTypes getType() {
            return type;
        }
        public void setType(ConnectionTypes type) {
            this.type = type;
        }
        public String getAuthUser() {
            return authUser;
        }
        public void setAuthUser(String authUser) {
            this.authUser = authUser;
        }
        public Boolean getIsRemote() {
            return isRemote;
        }
        public void setIsRemote(Boolean isRemote) {
            this.isRemote = isRemote;
        }
        public Boolean getIsRemoteAuthUsed() {
            return isRemoteAuthUsed;
        }
        public void setIsRemoteAuthUsed(Boolean isRemoteAuthUsed) {
            this.isRemoteAuthUsed = isRemoteAuthUsed;
        }
        public String getRemouteAuthUser() {
            return remouteAuthUser;
        }
        public void setRemouteAuthUser(String tempAuthUser) {
            this.remouteAuthUser = tempAuthUser;
        }
        public RibbonCommand getCurrentCommand() {
            return currentCommand;
        }
        public void setCurrentCommand(RibbonCommand currentCommand) {
            this.currentCommand = currentCommand;
        }
        public String getCurrentCommandArgs() {
            return currentCommandArgs;
        }
        public void setCurrentCommandArgs(String currentCommandArgs) {
            this.currentCommandArgs = currentCommandArgs;
        }
        public StringBuffer getCollectionBuffer() {
            return collectionBuffer;
        }
        public void setCollectionBuffer(StringBuffer collectionBuffer) {
            this.collectionBuffer = collectionBuffer;
        }
        public Queue<String> getBroadcastBuffer() {
            return broadcastBuffer;
        }
        public void setBroadcastBuffer(Queue<String> broadcastBuffer) {
            this.broadcastBuffer = broadcastBuffer;
        }
    }
}
