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
package tk.freaxsoftware.ribbon2.directory.facade;

import java.util.HashSet;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tk.freaxsoftware.extras.bus.MessageHolder;
import tk.freaxsoftware.extras.bus.annotation.Receive;
import tk.freaxsoftware.ribbon2.core.data.GroupModel;
import tk.freaxsoftware.ribbon2.core.data.UserModel;
import tk.freaxsoftware.ribbon2.directory.entity.GroupEntity;
import tk.freaxsoftware.ribbon2.directory.entity.UserEntity;
import tk.freaxsoftware.ribbon2.directory.repo.GroupRepository;
import tk.freaxsoftware.ribbon2.directory.repo.UserRepository;

/**
 * User/group facade to receive notification from gateway.
 * @author Stanislav Nepochatov
 */
public class UserGroupFacade {
    
    private final static Logger LOGGER = LoggerFactory.getLogger(UserGroupFacade.class);
    
    private UserRepository userRepository;
    
    private GroupRepository groupRepository;

    public UserGroupFacade(UserRepository userRepository, GroupRepository groupRepository) {
        this.userRepository = userRepository;
        this.groupRepository = groupRepository;
    }
    
    @Receive(UserModel.NOTIFICATION_USER_CREATED)
    public void userCreated(MessageHolder<UserModel> holder) {
        UserModel created = holder.getContent();
        LOGGER.info("Handling notification, user {} created;", created.getLogin());
        UserEntity user = new UserEntity();
        user.setId(created.getId());
        user.setLogin(created.getLogin());
        user.setGroups(prepareGroups(created.getGroups()));
        userRepository.save(user);
    }
    
    @Receive(UserModel.NOTIFICATION_USER_UPDATED)
    public void userUpdated(MessageHolder<UserModel> holder) {
        UserModel updated = holder.getContent();
        LOGGER.info("Handling notification, user {} updated;", updated.getLogin());
        UserEntity updateTo = userRepository.findByLogin(updated.getLogin());
        if (updateTo != null) {
            updateTo.setGroups(prepareGroups(updated.getGroups()));
            userRepository.save(updateTo);
        }
    }
    
    @Receive(UserModel.NOTIFICATION_USER_DELETED)
    public void userDeleted(MessageHolder<UserModel> holder) {
        UserModel deleted = holder.getContent();
        LOGGER.info("Handling notification, user {} deleted;", deleted.getLogin());
        userRepository.deleteByLogin(deleted.getLogin());
    }
    
    @Receive(GroupModel.NOTIFICATION_GROUP_CREATED)
    public void groupCreated(MessageHolder<GroupModel> holder) {
        GroupModel created = holder.getContent();
        LOGGER.info("Handling notification, group {} created;", created.getName());
        GroupEntity group = new GroupEntity();
        group.setName(created.getName());
        groupRepository.save(group);
    }
    
    @Receive(GroupModel.NOTIFICATION_GROUP_UPDATED)
    public void groupUpdated(MessageHolder<GroupModel> holder) {
        //Nothing to do
    }
    
    @Receive(GroupModel.NOTIFICATION_GROUP_DELETED)
    public void groupDeleted(MessageHolder<GroupModel> holder) {
        GroupModel deleted = holder.getContent();
        LOGGER.info("Handling notification, group {} deleted;", deleted.getName());
        groupRepository.deleteByName(deleted.getName());
    }
    
    private Set<GroupEntity> prepareGroups(Set<String> groupNames) {
        Set<GroupEntity> groups = new HashSet<>();
        for (String groupName: groupNames) {
            GroupEntity finded = groupRepository.findGroupByName(groupName);
            if (finded == null) {
                GroupEntity newGroup = new GroupEntity();
                newGroup.setName(groupName);
                groupRepository.save(newGroup);
                groups.add(newGroup);
            } else {
                groups.add(finded);
            }
        }
        return groups;
    }
    
}
