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
package tk.freaxsoftware.ribbon2.io.importer.rss;

import com.sun.syndication.feed.synd.SyndCategory;
import com.sun.syndication.feed.synd.SyndEntry;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import tk.freaxsoftware.ribbon2.core.data.MessageModel;
import tk.freaxsoftware.ribbon2.core.data.MessagePropertyModel;
import tk.freaxsoftware.ribbon2.io.core.importer.ImportMessage;

/**
 * RSS import message.
 * @author Stanislav Nepochatov
 */
public class RSSImportMessage implements ImportMessage {
    
    private final SyndEntry rssEntry;

    public RSSImportMessage(SyndEntry rssEntry) {
        this.rssEntry = rssEntry;
    }

    @Override
    public MessageModel getMessage() {
        MessageModel model = new MessageModel();
        model.setHeader(rssEntry.getTitle());
        StringBuffer contentBuffer = new StringBuffer();
        contentBuffer.append(rssEntry.getDescription().getValue());
        contentBuffer.append("\n\nLink: ");
        contentBuffer.append(rssEntry.getLink());
        model.setContent(contentBuffer.toString());
        model.setTags((Set<String>) rssEntry.getCategories()
                .stream().map(raw -> ((SyndCategory) raw).getName())
                .collect(Collectors.toSet()));
        model.setProperties(new HashSet(
                Set.of(new MessagePropertyModel("COPYRIGHT", 
                        rssEntry.getAuthor()))));
        return model;
    }

    @Override
    public void markAsRead() {
        //Do nothing...
    }

    @Override
    public String getId() {
        return rssEntry.getLink();
    }

    @Override
    public String getHeader() {
        return rssEntry.getTitle();
    }
    
}
