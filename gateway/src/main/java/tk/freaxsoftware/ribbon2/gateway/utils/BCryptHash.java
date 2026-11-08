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

package tk.freaxsoftware.ribbon2.gateway.utils;

import org.mindrot.jbcrypt.BCrypt;

/**
 * BCrypt hashing util.
 * @author Stanislav Nepochatov
 */
public class BCryptHash {
    
    private static final Integer COST_FACTOR = 12;

    /**
     * Hash password with BCrypt;
     * @param rawPassword raw passwod;
     * @return hash of the password with salt;
     */
    public static String hashPassword(String rawPassword) {
        return BCrypt.hashpw(rawPassword, BCrypt.gensalt(COST_FACTOR));
    }

    /**
     * Check whether is password valid;
     * @param rawPassword raw password;
     * @param hash hash of the password;
     * @return true if password is valid, false if it's not;
     */
    public static Boolean checkPassword(String rawPassword, String hash) {
        return BCrypt.checkpw(rawPassword, hash);
    }

}
