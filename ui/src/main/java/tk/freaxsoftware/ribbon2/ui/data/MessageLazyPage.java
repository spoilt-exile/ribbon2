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
package tk.freaxsoftware.ribbon2.ui.data;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import org.primefaces.model.FilterMeta;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortMeta;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tk.freaxsoftware.ribbon2.core.data.MessageModel;
import tk.freaxsoftware.ribbon2.core.data.response.MessagePage;
import tk.freaxsoftware.ribbon2.ui.managed.GatewayService;

/**
 * Message lazy loading page.
 * @author Stanislav Nepochatov
 */
public class MessageLazyPage extends LazyDataModel<MessageModel>{
    
    private final static Logger LOGGER = LoggerFactory.getLogger(LazyDataModel.class);
    
    private final GatewayService gatewayService;
    
    private final String directory;
    
    private final String jwtKey;
    
    private MessagePage currentPage;
    
    private Boolean init = false;
    
    /**
     * Default constructor.
     * @param gatewayService gateway REST service;
     * @param directory current selected directory;
     * @param jwtKey raw JWT key;
     */
    public MessageLazyPage(GatewayService gatewayService, String directory, String jwtKey) {
        this.gatewayService = gatewayService;
        this.directory = directory;
        this.jwtKey = jwtKey;
    }

    @Override
    public MessageModel getRowData(String rowKey) {
        if (currentPage != null) {
            Optional<MessageModel> message = currentPage.getContent().stream().filter(m -> Objects.equals(rowKey, m.getId().toString())).findFirst();
            if (message.isPresent()) {
                return message.get();
            }
        }
        return null;
    }

    @Override
    public String getRowKey(MessageModel object) {
        return object.getId().toString();
    }

    @Override
    public int count(Map<String, FilterMeta> filterBy) {
        if (currentPage == null) {
            loadCurrentPage(0, 15, Collections.EMPTY_MAP, filterBy);
        }
        return (int) currentPage.getTotalCount();
    }

    @Override
    public List<MessageModel> load(int first, int pageSize, Map<String, SortMeta> sortBy, Map<String, FilterMeta> filterBy) {
        LOGGER.info("Loading messages for dir {}, pageSize {}, first {}", directory, pageSize, first);
        if (currentPage != null && init) {
            loadCurrentPage(first, pageSize, sortBy, filterBy);
        }
        init = true;
        return currentPage != null ? currentPage.getContent() : Collections.EMPTY_LIST;
    }
    
    private void loadCurrentPage(int first, int pageSize, Map<String, SortMeta> sortBy, Map<String, FilterMeta> filterBy) {
        try {
            currentPage = gatewayService.getMessageRestClient().getMessages(jwtKey, directory, pageSize, first / pageSize);
            LOGGER.info("Loaded size {}, total {}", currentPage.getContent().size(), currentPage.getTotalCount());
        } catch (Exception ex) {
            LOGGER.error("Error on messages loading", ex);
        }
    }
    
    
}
