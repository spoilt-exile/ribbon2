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
import org.primefaces.model.FilterMeta;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortMeta;
import tk.freaxsoftware.ribbon2.core.data.response.DefaultPage;
import tk.freaxsoftware.ribbon2.ui.managed.GatewayService;

/**
 * General use class to access paged data on REST.
 * @author Stanislav Nepochatov
 */
public abstract class LazyLoadingPage<T> extends LazyDataModel<T> {
    
    protected final GatewayService gatewayService;
    
    protected final String jwtKey;
    
    protected DefaultPage<T> currentPage;
    
    protected Boolean init = false;

    public LazyLoadingPage(GatewayService gatewayService, String jwtKey) {
        this.gatewayService = gatewayService;
        this.jwtKey = jwtKey;
    }

    @Override
    public int count(Map<String, FilterMeta> filterBy) {
        if (currentPage == null) {
            currentPage = loadCurrentPage(0, 15, Collections.EMPTY_MAP, filterBy);
        }
        return (int) currentPage.getTotalCount();
    }

    @Override
    public List<T> load(int first, int pageSize, Map<String, SortMeta> sortBy, Map<String, FilterMeta> filterBy) {
        if (currentPage != null && init) {
            loadCurrentPage(first, pageSize, sortBy, filterBy);
        }
        init = true;
        return currentPage != null ? currentPage.getContent() : Collections.EMPTY_LIST;
    }
    
    public abstract DefaultPage<T> loadCurrentPage(int first, int pageSize, Map<String, SortMeta> sortBy, Map<String, FilterMeta> filterBy);
}
