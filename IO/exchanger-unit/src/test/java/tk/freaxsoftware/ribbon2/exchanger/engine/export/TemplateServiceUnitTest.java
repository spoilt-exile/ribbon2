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
package tk.freaxsoftware.ribbon2.exchanger.engine.export;

import com.google.common.collect.Sets;
import freemarker.template.TemplateException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;
import tk.freaxsoftware.ribbon2.core.data.MessageModel;
import tk.freaxsoftware.ribbon2.core.data.MessagePropertyModel;
import tk.freaxsoftware.ribbon2.exchanger.entity.ExportQueue;
import tk.freaxsoftware.ribbon2.io.core.IOScheme;

/**
 * Unit test for template service.
 * @author Stanislav Nepochatov
 */
public class TemplateServiceUnitTest {
    
    private final TemplateService templateService = new TemplateService();
    
    private final ZonedDateTime staticDate = ZonedDateTime.of(LocalDate.of(2022, Month.FEBRUARY, 24), LocalTime.of(5, 0), ZoneId.of("Europe/Kiev"));
    
    @Test
    public void shouldProcessMessagePlain() throws IOException, TemplateException {
        String template = loadTemplate("plain_template.txt");
        IOScheme scheme = buildScheme(template);
        ExportQueue queue = new ExportQueue("System.Test", scheme.getProtocol(), scheme.getName(), "testTrx1", buildMessage(), staticDate);
        String processed = templateService.processMessage(scheme, queue);
        String processedSaved = loadTemplate("plain_processed.txt");
        Assert.assertEquals(processed, processedSaved);
    }
    
    @Test
    public void shouldProcessMessageJson() throws IOException, TemplateException {
        String template = loadTemplate("template.json");
        IOScheme scheme = buildScheme(template);
        ExportQueue queue = new ExportQueue("System.Test", scheme.getProtocol(), scheme.getName(), "testTrx1", buildMessage(), staticDate);
        String processed = templateService.processMessage(scheme, queue);
        String processedSaved = loadTemplate("processed.json");
        Assert.assertEquals(processed, processedSaved);
    }
    
    private String loadTemplate(String filename) throws IOException {
        return IOUtils.resourceToString(filename, Charset.defaultCharset(), getClass().getClassLoader());
    }
    
    private MessageModel buildMessage() {
        MessageModel message = new MessageModel();
        message.setId(100L);
        message.setUid("28969eab-f9f8-4d1d-9028-a464ef623740");
        message.setCreatedBy("root");
        message.setCreated(staticDate);
        message.setDirectories(Sets.newHashSet("System.Test"));
        message.setHeader("Test header");
        message.setContent("This is basic content of test message.\n\nMessage will be exported.");
        message.setTags(Sets.newHashSet("test", "system", "ribbon", "export"));
        message.setProperties(Sets.newHashSet(new MessagePropertyModel("COPYRIGHT", "Ukrinform")));
        return message;
    }
    
    private IOScheme buildScheme(String template) {
        IOScheme scheme = new IOScheme();
        scheme.setName("Testing");
        scheme.setProtocol("test");
        scheme.setId("export:test");
        Map<String, Object> config = new HashMap();
        config.put("generalTemplate", template);
        scheme.setConfig(config);
        return scheme;
    }
    
}
