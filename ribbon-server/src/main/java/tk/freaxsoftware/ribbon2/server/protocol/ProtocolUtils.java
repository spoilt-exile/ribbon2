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
package tk.freaxsoftware.ribbon2.server.protocol;

/**
 * Class with Ribbon protocol constants and utils.
 * @author Stanislav Nepochatov
 */
public class ProtocolUtils {
    public static String RIBBON_PROTOCOL_VERSION = "a2";
    public static String RIBBON_PROTOCOL_DEFAULT_CHARSET = "UTF-8";

    public static String COMMAND_DELIMETER = ":";
    public static String OK_RESPONSE = "OK:";
    public static String ERROR_RESPONSE = "RIBBON_ERROR:";
    public static String PROCEED_RESPONSE = "PROCEED:";
    public static String END_COMMAND = "END:";
    public static String COMMIT_CLOSE_COMMAND = "COMMIT_CLOSE:";

    /**
     * Parse raw command, returns command name and it's arguments.
     * @param rawCommand raw command;
     * @return array with command name and arguments;
     */
    public static String[] parseCommand(String rawCommand) {
        if (rawCommand != null && !rawCommand.isEmpty()) {
            return rawCommand.split(COMMAND_DELIMETER);
        }
        return null;
    }

    /**
     * Parse basic CSV command and validate cound of arguments.
     * @param args string with all arguments;
     * @param count count of arguments after parsing;
     * @return parsed arguments or null if count is incorrect;
     */
    public static String[] parseBasicArguments(String args, int count) {
        if (args != null && !args.isEmpty()) {
            String[] parsed = args.split(",");
            return parsed.length == count ? parsed : null;
        }
        return null;
    }
}
