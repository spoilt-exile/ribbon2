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
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tk.freaxsoftware.ribbon2.core.data.DirectoryModel;
import tk.freaxsoftware.ribbon2.core.data.MessageModel;
import tk.freaxsoftware.ribbon2.core.data.response.DirectoryPage;
import tk.freaxsoftware.ribbon2.ui.data.MessageLazyPage;

/**
 * Builds tree of system directories and provide access to select one of it.
 * @author Stanislav Nepochatov
 */
@Named(value = "mainHandler")
@SessionScoped
public class MainHandler implements Serializable {
    
    private final static Logger LOGGER = LoggerFactory.getLogger(MainHandler.class);
    
    @Inject
    private UserSession session;
    
    @Inject
    private MessageEditor editor;
    
    @Inject
    private transient GatewayService gatewayService;
    
    private TreeNode<DirectoryModel> directoryTree;
    
    private TreeNode<DirectoryModel> selectedDirectory;
    
    private Set<String> selectedDirectoryPermissions;
    
    private MessageLazyPage messagePage;
    
    private MessageModel selectedMessage;
    
    @PostConstruct
    public void buildTree() {
        directoryTree = new DefaultTreeNode(new DirectoryModel());
        
        Map<String, TreeNode<DirectoryModel>> pathMap = new HashMap<>();
        
        try {
            DirectoryPage dirPage = gatewayService.getDirectoryRestClient().getDirectories(session.getJwtKey());
            
            if (dirPage.getContent().isEmpty()) {
                LOGGER.info("Dir page is empty!");
            }
            
            for (DirectoryModel current: dirPage.getContent()) {
                String parentName = current.parentName();
                LOGGER.info("Processing directory {} with parent {}", current.getFullName(), parentName);
                if (parentName.isEmpty()) {
                    LOGGER.info("Adding {} directory to ROOT", current.getFullName());
                    TreeNode<DirectoryModel> insertedToRoot = new DefaultTreeNode(current, directoryTree);
                    pathMap.put(current.getFullName(), insertedToRoot);
                    insertedToRoot.setExpanded(true);
                } else {
                    if (pathMap.containsKey(current.parentName())) {
                        TreeNode<DirectoryModel> parent = pathMap.get(current.parentName());
                        LOGGER.info("Adding {} directory to {}", current.getFullName(), parent.getData().getFullName());
                        TreeNode<DirectoryModel> insertedInside = new DefaultTreeNode(current, parent);
                        pathMap.put(current.getFullName(), insertedInside);
                        insertedInside.setExpanded(true);
                    } else {
                        LOGGER.warn("Discarding {} directory", current.getFullName());
                    }
                }
            }
        } catch (Exception ex) {
            LOGGER.error("Error on directories loading", ex);
        }
    }
    
    
    public void onDirSelected(NodeSelectEvent e) {
        selectedDirectory = e.getTreeNode();
        selectedMessage = null;
        LOGGER.info("Directory {} selected", selectedDirectory.getData().getFullName());
        try {
            selectedDirectoryPermissions = gatewayService.getDirectoryRestClient()
                    .getDirectoriesPermissions(session.getJwtKey(), selectedDirectory.getData().getFullName());
            messagePage = new MessageLazyPage(gatewayService, selectedDirectory.getData().getFullName(), session.getJwtKey());
        } catch (Exception ex) {
            LOGGER.error("Error on message loading", ex);
        }

    }
    
    public void onMessageSelected(SelectEvent e) {
        MessageModel selectedModel = (MessageModel) e.getObject();
        LOGGER.info("Message {} selected", selectedModel.getUid());
        try {
            selectedMessage = gatewayService.getMessageRestClient().getMessageByUid(session.getJwtKey(), selectedModel.getUid(), selectedDirectory.getData().getFullName());
        } catch (Exception ex) {
            LOGGER.error("Error on message loading", ex);
        }
    }
    
    public boolean canCreateMessage() {
        return checkPermission("canCreateMessage");
    }
    
    public boolean canUpdateMessage() {
        return selectedMessage != null && checkPermission("canUpdateMessage");
    }
    
    public boolean canDeleteMessage() {
        return selectedMessage != null && checkPermission("canDeleteMessage");
    }
    
    private boolean checkPermission(String permissionCode) {
        return selectedDirectoryPermissions != null && selectedDirectoryPermissions.contains(permissionCode);
    }
    
    public String createMessage() {
        editor.initCreate();
        return "pages/message-editor.xhtml?faces-redirect=true";
    }
    
    public String updateMessage() {
        editor.initUpdate(selectedMessage);
        return "pages/message-editor.xhtml?faces-redirect=true";
    }
    
    public void deleteMessage() {
        LOGGER.info("Delete message {}", selectedMessage.getUid());
        try {
            gatewayService.getMessageRestClient().deleteMessage(session.getJwtKey(), selectedMessage.getUid());
            selectedMessage = null;
        } catch (Exception ex) {
            LOGGER.error("Error on message deletion", ex);
        }
    }

    public TreeNode<DirectoryModel> getDirectoryTree() {
        return directoryTree;
    }

    public void setDirectoryTree(TreeNode<DirectoryModel> directoryTree) {
        this.directoryTree = directoryTree;
    }

    public TreeNode<DirectoryModel> getSelectedDirectory() {
        return selectedDirectory;
    }

    public void setSelectedDirectory(TreeNode<DirectoryModel> selectedDirectory) {
        this.selectedDirectory = selectedDirectory;
    }

    public MessageLazyPage getMessagePage() {
        return messagePage;
    }

    public void setMessagePage(MessageLazyPage messagePage) {
        this.messagePage = messagePage;
    }

    public MessageModel getSelectedMessage() {
        return selectedMessage;
    }

    public void setSelectedMessage(MessageModel selectedMessage) {
        this.selectedMessage = selectedMessage;
    }
}
