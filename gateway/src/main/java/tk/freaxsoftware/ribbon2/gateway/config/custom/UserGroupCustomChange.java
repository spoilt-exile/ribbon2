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
package tk.freaxsoftware.ribbon2.gateway.config.custom;

import java.util.HashSet;
import liquibase.change.custom.CustomSqlChange;
import liquibase.database.Database;
import liquibase.exception.CustomChangeException;
import liquibase.exception.SetupException;
import liquibase.exception.ValidationErrors;
import liquibase.resource.ResourceAccessor;
import liquibase.statement.SqlStatement;
import liquibase.statement.core.InsertStatement;
import tk.freaxsoftware.extras.bus.MessageHolder;
import tk.freaxsoftware.extras.bus.MessageOptions;
import tk.freaxsoftware.ribbon2.core.data.GroupModel;
import tk.freaxsoftware.ribbon2.core.data.UserModel;
import tk.freaxsoftware.ribbon2.gateway.Init;
import tk.freaxsoftware.ribbon2.gateway.utils.SHAHash;

/**
 * Custom change for Liquibase to add messages for populated users/groups;
 * @author Stanislav Nepochatov 
 */
public class UserGroupCustomChange implements CustomSqlChange {

    @Override
    public SqlStatement[] generateStatements(Database database) throws CustomChangeException {
        
        UserModel rootUser = new UserModel();
        rootUser.setId(1l);
        rootUser.setLogin("root");
        rootUser.setDescription("Root admin");
        rootUser.setEmail("localhost@localhost");
        rootUser.setEnabled(true);
        rootUser.setGroups(new HashSet<>());
        rootUser.getGroups().add("adm");
        
        GroupModel admGroup = new GroupModel();
        admGroup.setId(1l);
        admGroup.setName("adm");
        admGroup.setDescription("Administrator group");
        
        SqlStatement[] statements = new SqlStatement[3];
        statements[0] = new InsertStatement(null, null, "user_entity")
                .addColumnValue("login", rootUser.getLogin())
                .addColumnValue("description", rootUser.getDescription())
                .addColumnValue("email", rootUser.getEmail())
                .addColumnValue("enabled", rootUser.getEnabled())
                .addColumnValue("password", SHAHash.hashPassword("root"));
        
        statements[1] = new InsertStatement(null, null, "group_entity")
                .addColumnValue("name", admGroup.getName())
                .addColumnValue("description", admGroup.getDescription());
        
        statements[2] = new InsertStatement(null, null, "user_entity_group_entity")
                .addColumnValue("user_id", 1l)
                .addColumnValue("group_id", 1l);
        
        MessageOptions options = MessageOptions.Builder.newInstance().deliveryNotification(5).build();
        
        Init.appendixMessages.add(new MessageHolder(UserModel.NOTIFICATION_USER_CREATED, options, rootUser));
        Init.appendixMessages.add(new MessageHolder(GroupModel.NOTIFICATION_GROUP_CREATED, options, admGroup));
        
        return statements;
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
