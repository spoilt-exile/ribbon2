/*
 * This file is part of Ribbon2 news message system.
 * 
 * Copyright (C) 2023 Freax Software
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
package tk.freaxsoftware.ribbon2.uix.model;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.LongStream;
import tk.freaxsoftware.ribbon2.core.data.response.Page;

/**
 * Wraps page with content and generates pagination navigation links.
 * @author Stanislav Nepochatov
 */
public class PageableUrlWrapper<T> {
    
    private final Page<T> innerPage;
    
    private final String baseUrl;
    
    private final int pageSize;
    
    private final int pageNumber;

    public PageableUrlWrapper(Page<T> innerPage, String baseUrl, int pageSize, int pageNumber) {
        this.innerPage = innerPage;
        this.baseUrl = baseUrl;
        this.pageSize = pageSize;
        this.pageNumber = pageNumber;
    }

    public List<T> getContent() {
        return innerPage.getContent();
    }
    
    public List<UrlPagingEntry> getPagination() {
        List<UrlPagingEntry> pagination = new ArrayList();
        
        //Add first page link
        if (pageNumber > 2) {
            pagination.add(new UrlPagingEntry(renderUrl(0, pageSize), "<<", true));
        }
        
        //Add prev page link
        if (pageNumber > 0) {
            pagination.add(new UrlPagingEntry(renderUrl(pageNumber - 1, pageSize), "<", true));
        }
        
        long maxPageDiv = innerPage.getTotalCount() % pageSize;
        long maxPage = (innerPage.getTotalCount() / pageSize) + (maxPageDiv > 0 ? 1 : 0);
        
        //Add array of pages (3 previous and 3 next pages)
        LongStream.rangeClosed(pageNumber - 3, pageNumber + 3)
                .filter(lgn -> lgn > -1 && lgn < maxPage)
                .forEach(lgn -> pagination.add(
                        new UrlPagingEntry(
                                renderUrl((int) lgn, pageSize), 
                                String.valueOf(lgn + 1), 
                                lgn != pageNumber)));
        
        //Add next page link
        if (maxPage - 1 > pageNumber) {
            pagination.add(new UrlPagingEntry(renderUrl(pageNumber + 1, pageSize), ">", true));
        }
        
        //Add last page link
        if (maxPage - 2 > pageNumber) {
            pagination.add(new UrlPagingEntry(renderUrl((int) maxPage, pageSize), ">>", true));
        }
        return pagination;
    }
    
    private String renderUrl(int pageNumber, int pageSize) {
        return String.format("%s?page=%d&size=%d", baseUrl, pageNumber, pageSize);
    }
    
    /**
     * URL entry for pagination. Contains url and caption. Also with active flag;
     */
    public static class UrlPagingEntry {
        private String url;
        private String caption;
        private boolean active;

        public UrlPagingEntry(String url, String caption, boolean active) {
            this.url = url;
            this.caption = caption;
            this.active = active;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getCaption() {
            return caption;
        }

        public void setCaption(String caption) {
            this.caption = caption;
        }

        public boolean isActive() {
            return active;
        }

        public void setActive(boolean active) {
            this.active = active;
        }
    }
}
