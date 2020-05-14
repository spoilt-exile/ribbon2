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
package tk.freaxsoftware.ribbon2.directory.config.custom;

import liquibase.change.custom.CustomTaskChange;
import liquibase.database.Database;
import liquibase.exception.CustomChangeException;
import liquibase.exception.SetupException;
import liquibase.exception.ValidationErrors;
import liquibase.resource.ResourceAccessor;
import tk.freaxsoftware.extras.bus.MessageHolder;
import tk.freaxsoftware.extras.bus.MessageOptions;
import tk.freaxsoftware.ribbon2.core.data.DirectoryModel;
import tk.freaxsoftware.ribbon2.core.data.UserModel;
import tk.freaxsoftware.ribbon2.directory.Init;
import tk.freaxsoftware.ribbon2.directory.DirectoryUnit;
import tk.freaxsoftware.ribbon2.directory.service.AuthService;

/**
 * Populate default data custom change.
 * @author Stanislav Nepochatov
 */
public class PopulateDataCustomChange implements CustomTaskChange {

    @Override
    public void execute(Database database) throws CustomChangeException {
        MessageOptions options = MessageOptions.Builder.newInstance().header(UserModel.AUTH_HEADER_USERNAME, AuthService.ROOT_LOGIN).deliveryCall().build();
        for (String newDir: DirectoryUnit.config.getDirectory().getCreateDirs()) {
            DirectoryModel newDirectory = new DirectoryModel();
            newDirectory.setFullName(newDir);
            newDirectory.setDescription("Created by default.");
            Init.appendixMessages.add(new MessageHolder(DirectoryModel.CALL_CREATE_DIRECTORY, options, newDirectory));
        }
    }

    @Override
    public String getConfirmationMessage() {
        return null;
    }

    @Override
    public void setUp() throws SetupException {
        
    }

    @Override
    public void setFileOpener(ResourceAccessor resourceAccessor) {
        
    }

    @Override
    public ValidationErrors validate(Database database) {
        return null;
    }
    
}
