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
package tk.freaxsoftware.ribbon2.exchanger.template;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import tk.freaxsoftware.ribbon2.core.data.MessageModel;
import tk.freaxsoftware.ribbon2.io.core.IOScheme;

/**
 * Template service for exported messages.
 * @author Stanislav Nepochatov
 */
public class TemplateService {
    
    private final Configuration cfg = new Configuration(Configuration.VERSION_2_3_32);
    
    public void processMessage(IOScheme scheme, MessageModel message) throws IOException, TemplateException {
        String template = (String) scheme.getConfig().get("GENERAL_TEMPLATE");
        Template t = new Template("test", new StringReader(template), cfg);
        t.process(scheme, new StringWriter());
    }
    
}
