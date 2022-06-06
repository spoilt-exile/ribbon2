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
package tk.freaxsoftware.ribbon2.core.data.request;

import java.util.List;
import java.util.Map;

/**
 * Pagination request for page of entities.
 * @author Stanislav Nepochatov
 */
public class PaginationRequest {
    
    public final static String PARAM_PAGE = "page";
    
    public final static String PARAM_SIZE = "size";
    
    public final static String PARAM_ORDER_BY = "orderBy";
    
    public final static String PARAM_DIRECTION = "direction";
    
    private final static int DEFAULT_PAGE = 0;
    
    private final static int DEFAULT_SIZE = 30;
    
    private final static String DEFAULT_ORDER_BY = "id";
    
    private final static Order DEFAULT_DIRECTION = Order.ASC;
    
    private int page;
    
    private int size;
    
    private String orderBy;
    
    private Order direction;

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }

    public Order getDirection() {
        return direction;
    }

    public void setDirection(Order direction) {
        this.direction = direction;
    }
    
    public static enum Order {
        ASC, DESC;
    }
    
    public static PaginationRequest ofRequest(Map<String, List<String>> queryParams) {
        PaginationRequest request = new PaginationRequest();
        request.setPage(queryParams.containsKey(PARAM_PAGE) ? Integer.parseInt(queryParams.get(PARAM_PAGE).get(0)) : DEFAULT_PAGE);
        request.setSize(queryParams.containsKey(PARAM_SIZE) ? Integer.parseInt(queryParams.get(PARAM_SIZE).get(0)) : DEFAULT_SIZE);
        request.setOrderBy(queryParams.containsKey(PARAM_ORDER_BY) ? queryParams.get(PARAM_ORDER_BY).get(0) : DEFAULT_ORDER_BY);
        request.setDirection(queryParams.containsKey(PARAM_DIRECTION) ? Order.valueOf(queryParams.get(PARAM_DIRECTION).get(0)) : DEFAULT_DIRECTION);
        return request;
    }

    @Override
    public String toString() {
        return "{" + "page=" + page + ", size=" + size + ", orderBy=" + orderBy + ", direction=" + direction + '}';
    }
}
