/*
 * This file is part of Ribbon2 news message system.
 * 
 * Copyright (C) 2020-2022 Freax Software
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
package tk.freaxsoftware.ribbon2.ui.managed;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.primefaces.model.DualListModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tk.freaxsoftware.ribbon2.core.data.DirectoryModel;
import tk.freaxsoftware.ribbon2.core.data.MessageModel;

/**
 * Holds logic and data to create new or edit existing message;
 * @author Stanislav Nepochatov
 */
@Named(value = "editor")
@SessionScoped
public class MessageEditor implements Serializable {
    
    private final static Logger LOGGER = LoggerFactory.getLogger(MessageEditor.class);
    
    private String header;
    
    private List<String> tags;
    
    private DualListModel<String> directories;
    
    private String content;
    
    private MessageModel message;
    
    private List<String> forbiddenDirs;
    
    private Modes mode;
    
    @Inject
    private UserSession session;
    
    @Inject
    private transient GatewayService gatewayService;
    
    public void initCreate() {
        header = "";
        tags = new ArrayList();
        content = "";
        mode = Modes.CREATE;
        try {
            Set<DirectoryModel> availableDirectories = gatewayService.getDirectoryRestClient().getDirectoriesByPermission(session.getJwtKey(), "canCreateMessage");
            List<String> availDirNames = availableDirectories.stream().map(dir -> dir.getFullName()).collect(Collectors.toList());
            LOGGER.info("Dirs available for {}: {}", session.getLogin(), availDirNames);
            directories = new DualListModel(availDirNames, new ArrayList());
        } catch (Exception ex) {
            LOGGER.error("Unable to init available directories", ex);
        }
    }
    
    public void initUpdate(MessageModel toUpdate) {
        header = toUpdate.getHeader();
        tags = new ArrayList();
        tags.addAll(toUpdate.getTags());
        mode = Modes.UPDATE;
        message = toUpdate;
        content = message.getContent();
        try {
            Set<DirectoryModel> availableDirectories = gatewayService.getDirectoryRestClient().getDirectoriesByPermission(session.getJwtKey(), "canCreateMessage");
            List<String> availDirNames = availableDirectories.stream().map(dir -> dir.getFullName()).collect(Collectors.toList());
            Set<String> messageDirNames = message.getDirectories();
            List<String> selectedDirNames = new ArrayList();
            forbiddenDirs = new ArrayList();
            messageDirNames.forEach(mDir -> {
                if (availDirNames.contains(mDir)) {
                    selectedDirNames.add(mDir);
                } else {
                    forbiddenDirs.add(mDir);
                }
            });
            availDirNames.removeIf(dir -> selectedDirNames.contains(dir));
            LOGGER.info("Dirs available for {}: {} and selected {}", session.getLogin(), availDirNames, selectedDirNames);
            directories = new DualListModel(availDirNames, selectedDirNames);
        } catch (Exception ex) {
            LOGGER.error("Unable to init available directories", ex);
        }
    }
    
    public String save() {
        return this.mode == Modes.CREATE ? create() : update();
    }
    
    public String create() {
        LOGGER.info("Creating message {}", header);
        MessageModel model = new MessageModel();
        model.setHeader(header);
        model.setTags(tags.stream().collect(Collectors.toSet()));
        model.setDirectories(directories.getTarget().stream().collect(Collectors.toSet()));
        model.setContent(content);
        try {
            MessageModel saved = gatewayService.getMessageRestClient().createMessage(session.getJwtKey(), model);
            LOGGER.info("Created {} message", saved.getUid());
        } catch (Exception ex) {
            LOGGER.error("Unable to create message", ex);
        }
        return "/index.xhtml?faces-redirect=true";
    }
    
    public String update() {
        LOGGER.info("Updating message {} id {}", header, message.getId());
        message.setHeader(header);
        message.setTags(tags.stream().collect(Collectors.toSet()));
        message.setDirectories(directories.getTarget().stream().collect(Collectors.toSet()));
        message.setContent(content);
        try {
            MessageModel saved = gatewayService.getMessageRestClient().updateMessage(session.getJwtKey(), message);
            LOGGER.info("Updated {} message", saved.getUid());
        } catch (Exception ex) {
            LOGGER.error("Unable to update message", ex);
        }
        return "/index.xhtml?faces-redirect=true";
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public DualListModel<String> getDirectories() {
        return directories;
    }

    public void setDirectories(DualListModel<String> directories) {
        this.directories = directories;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Modes getMode() {
        return mode;
    }

    public void setMode(Modes mode) {
        this.mode = mode;
    }
    
    /**
     * Enum with message editor modes.
     */
    public static enum Modes {
        CREATE,
        UPDATE;
    }
}
