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

package tk.freaxsoftware.ribbon2.core.data;

import java.util.Set;
import lombok.Data;

/**
 * User data class.
 * @author Stanislav Nepochatov
 */
@Data
public class User {
    
    /**
     * Id of the user.
     */
    private Long id;
    
    /**
     * User's login.
     */
    private String login;
    
    /**
     * User's firstname.
     */
    private String firstName;
    
    /**
     * User's secondname.
     */
    private String secondName;
    
    /**
     * User's description for useful info like phones etc.
     */
    private String description;
    
    /**
     * User email.
     */
    private String email;
    
    /**
     * Password hash value.
     */
    private String password;
    
    /**
     * Enabled flag for user.
     */
    private Boolean enabled;
    
    /**
     * Groups of this user.
     */
    private Set<Group> groups;
    
}
