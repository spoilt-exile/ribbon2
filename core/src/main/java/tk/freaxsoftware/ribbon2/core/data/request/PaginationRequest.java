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

import spark.QueryParamsMap;

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
    
    public static PaginationRequest ofRequest(QueryParamsMap queryParams) {
        PaginationRequest request = new PaginationRequest();
        request.setPage(queryParams.hasKey(PARAM_PAGE) ? queryParams.get(PARAM_PAGE).integerValue() : DEFAULT_PAGE);
        request.setSize(queryParams.hasKey(PARAM_SIZE) ? queryParams.get(PARAM_SIZE).integerValue() : DEFAULT_SIZE);
        request.setOrderBy(queryParams.hasKey(PARAM_ORDER_BY) ? queryParams.get(PARAM_ORDER_BY).value() : DEFAULT_ORDER_BY);
        request.setDirection(queryParams.hasKey(PARAM_DIRECTION) ? Order.valueOf(queryParams.get(PARAM_DIRECTION).value()) : DEFAULT_DIRECTION);
        return request;
    }

    @Override
    public String toString() {
        return "{" + "page=" + page + ", size=" + size + ", orderBy=" + orderBy + ", direction=" + direction + '}';
    }
}
