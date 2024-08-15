/*
 * This file is part of Ribbon2 news message system.
 * 
 * Copyright (C) 2020-2023 Freax Software
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

import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;
import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tk.freaxsoftware.ribbon2.io.core.IOExceptionCodes;
import tk.freaxsoftware.ribbon2.io.core.IOScheme;
import tk.freaxsoftware.ribbon2.io.core.InputOutputException;
import tk.freaxsoftware.ribbon2.io.core.importer.ImportMessage;
import tk.freaxsoftware.ribbon2.io.core.importer.ImportSource;

/**
 * RSS feed message import source.
 * @author Stanislav Nepochatov
 */
public class RSSImportSource implements ImportSource {
    
    private final static Logger LOGGER = LoggerFactory.getLogger(RSSImportSource.class);
    
    private final IOScheme scheme;

    public RSSImportSource(IOScheme scheme) {
        this.scheme = scheme;
    }

    @Override
    public void open() {
        //Do nothing...
    }

    @Override
    public void close() {
        //Do nothing...
    }

    @Override
    public void onSuccess(ImportMessage message, String uid) {
        //Do nothing...
    }

    @Override
    public void onError(ImportMessage message, InputOutputException ex) {
        //Do nothing...
    }

    @Override
    public IOScheme getScheme() {
        return scheme;
    }

    @Override
    public List<ImportMessage> getUnreadMessages() {
        LOGGER.info("Fetching RSS feed from {}", scheme.getConfig().get("rssUrl"));
        try {
            URL feedSource = URI.create((String) scheme.getConfig().get("rssUrl")).toURL();
            SyndFeedInput input = new SyndFeedInput();
            SyndFeed feed = input.build(new XmlReader(feedSource));
            List<SyndEntry> entries = feed.getEntries();
            return entries.stream().map(entry -> new RSSImportMessage(entry)).collect(Collectors.toList());
        } catch (Exception ex) {
            LOGGER.error("Error during processing messages for scheme {}", scheme.getName());
            LOGGER.error("Error", ex);
            throw new InputOutputException(IOExceptionCodes.IMPORT_CHECK_ERROR, "Unable to get RSS feed", ex);
        }
    }
    
}
