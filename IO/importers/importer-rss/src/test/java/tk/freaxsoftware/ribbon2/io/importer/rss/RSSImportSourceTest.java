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

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import tk.freaxsoftware.ribbon2.core.data.MessageModel;
import tk.freaxsoftware.ribbon2.core.data.MessagePropertyModel;
import tk.freaxsoftware.ribbon2.io.core.IOScheme;
import tk.freaxsoftware.ribbon2.io.core.importer.ImportMessage;

/**
 * Unit test for RSS import source.
 * @author Stanislav Nepochatov
 */
public class RSSImportSourceTest {
    
    @Test
    @Ignore
    public void shouldReadRss() {
        IOScheme scheme = new IOScheme();
        scheme.setName("RSS test");
        scheme.setProtocol("rss");
        scheme.setId("import:rss");
        scheme.setConfig(Map.of("rssUrl", "https://itc.ua/ua/feed/"));
        RSSImportSource source = new RSSImportSource(scheme);
        List<ImportMessage> messages = source.getUnreadMessages();
        Assert.assertFalse(messages.isEmpty());
        Set<String> ids = new HashSet();
        for (ImportMessage impMessage: messages) {
            MessageModel model = impMessage.getMessage();
            Assert.assertNotNull(model.getHeader());
            Assert.assertNotNull(model.getContent());
            Assert.assertTrue(model.getContent().contains("https://itc.ua/"));
            Optional<MessagePropertyModel> copyrightPropertyOPt = model.getProperties().stream()
                    .filter(p -> p.getType().equals("COPYRIGHT"))
                    .findFirst();
            Assert.assertTrue(copyrightPropertyOPt.isPresent());
            Assert.assertTrue(copyrightPropertyOPt.get().getContent() != null);
            String messageId = impMessage.getId();
            Assert.assertFalse(ids.contains(messageId));
            ids.add(messageId);
        }
    }
}
