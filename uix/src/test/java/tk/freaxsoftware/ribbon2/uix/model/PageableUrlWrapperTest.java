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

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import tk.freaxsoftware.ribbon2.core.data.response.Page;

/**
 * Test for pagination wrapper.
 * @author Stanislav Nepochatov
 */
public class PageableUrlWrapperTest {
    
    @Test
    public void wrapMiddlePage() {
        final String baseUrl = "/messages";
        final int page = 5;
        final int pageSize = 30;
        final int totalCount = 321;
        PageableUrlWrapper wrapper = new PageableUrlWrapper(new DummyPage((long) totalCount), baseUrl, pageSize, page);
        
        List<PageableUrlWrapper.UrlPagingEntry> pagination = wrapper.getPagination();
        Assert.assertTrue(pagination.size() == 11);
        assertUrlEntry(pagination, 0, "<<", true, renderUrlTest(baseUrl, 0, pageSize));
        assertUrlEntry(pagination, 1, "<", true, renderUrlTest(baseUrl, 4, pageSize));
        
        assertUrlEntry(pagination, 2, "3", true, renderUrlTest(baseUrl, 2, pageSize));
        assertUrlEntry(pagination, 3, "4", true, renderUrlTest(baseUrl, 3, pageSize));
        assertUrlEntry(pagination, 4, "5", true, renderUrlTest(baseUrl, 4, pageSize));
        assertUrlEntry(pagination, 5, "6", false, renderUrlTest(baseUrl, 5, pageSize));
        assertUrlEntry(pagination, 6, "7", true, renderUrlTest(baseUrl, 6, pageSize));
        assertUrlEntry(pagination, 7, "8", true, renderUrlTest(baseUrl, 7, pageSize));
        assertUrlEntry(pagination, 8, "9", true, renderUrlTest(baseUrl, 8, pageSize));
        
        assertUrlEntry(pagination, 9, ">", true, renderUrlTest(baseUrl, 6, pageSize));
        assertUrlEntry(pagination, 10, ">>", true, renderUrlTest(baseUrl, 11, pageSize));
    }
    
    @Test
    public void wrapSinglePageDivisionError() {
        final String baseUrl = "/messages";
        final int page = 0;
        final int pageSize = 30;
        final int totalCount = 23;
        PageableUrlWrapper wrapper = new PageableUrlWrapper(new DummyPage((long) totalCount), baseUrl, pageSize, page);
        
        List<PageableUrlWrapper.UrlPagingEntry> pagination = wrapper.getPagination();
        Assert.assertTrue(pagination.size() == 1);
        assertUrlEntry(pagination, 0, "1", false, renderUrlTest(baseUrl, 0, pageSize));
    }
     
    private void assertUrlEntry(List<PageableUrlWrapper.UrlPagingEntry> pagination, int index, String caption, boolean active, String url) {
        PageableUrlWrapper.UrlPagingEntry pagingEntry = pagination.get(index);
        
        Assert.assertEquals(pagingEntry.getCaption(), caption);
        Assert.assertEquals(pagingEntry.isActive(), active);
        Assert.assertEquals(pagingEntry.getUrl(), url);
    }
    
    private String renderUrlTest(String baseUrl, int pageNumber, int pageSize) {
        return String.format("%s?page=%d&size=%d", baseUrl, pageNumber, pageSize);
    }
    
    private static class DummyPage implements Page {
        
        private final Long totalCount;

        public DummyPage(Long totalCount) {
            this.totalCount = totalCount;
        }

        @Override
        public List getContent() {
            return Collections.EMPTY_LIST;
        }

        @Override
        public long getTotalCount() {
            return totalCount;
        }
        
    }
}
