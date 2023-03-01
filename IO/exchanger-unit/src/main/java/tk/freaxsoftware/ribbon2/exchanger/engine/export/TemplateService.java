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

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.Version;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import no.api.freemarker.java8.Java8ObjectWrapper;
import tk.freaxsoftware.ribbon2.core.data.MessagePropertyModel;
import tk.freaxsoftware.ribbon2.exchanger.entity.ExportQueue;
import tk.freaxsoftware.ribbon2.io.core.IOScheme;

/**
 * Template service for exported messages.
 * @author Stanislav Nepochatov
 */
public class TemplateService {
    
    private static final String GENERAL_TEMPLATE_KEY = "generalTemplate";
    
    private static final String TEMP_MESSAGE_KEY = "message";
    
    private static final String TEMP_COPYRIGHT_KEY = "copyright";
    
    private static final String TEMP_EXPORT_DATE = "exportDate";
    
    private static final String TEMP_EXPORT_DIRECTORY = "exportDirectory";
    
    private static final String TEMP_EXPORT_SCHEME = "exportScheme";
    
    private static final String TEMP_EXPORT_SCHEME_PROTOCOL = "exportSchemeProtocol";
    
    private final Configuration cfg;

    /**
     * Empty constructor.
     */
    public TemplateService() {
        cfg = new Configuration(Configuration.VERSION_2_3_32);
        cfg.setObjectWrapper(new Java8ObjectWrapper(Configuration.VERSION_2_3_32));
    }
    
    /**
     * Process export message's content by template from config. If template in config is absent it returns original content untouched.
     * @param scheme export scheme with config;
     * @param message message to process;
     * @return processed or original content;
     * @throws IOException
     * @throws TemplateException 
     */
    public String processMessage(IOScheme scheme, ExportQueue message) throws IOException, TemplateException {
        //cfg.setObjectWrapper(new Java8);
        if (!scheme.getConfig().containsKey(GENERAL_TEMPLATE_KEY)) {
            return message.getMessage().getContent();
        }
        String template = (String) scheme.getConfig().get(GENERAL_TEMPLATE_KEY);
        Template t = new Template("test", new StringReader(template), cfg);
        StringWriter writer = new StringWriter();
        t.process(buildDataModel(scheme, message), writer);
        return writer.toString();
    }
    
    private Map<String, Object> buildDataModel(IOScheme scheme, ExportQueue message) {
        Map<String, Object> attrs = new HashMap();
        attrs.put(TEMP_MESSAGE_KEY, message.getMessage());
        attrs.put(TEMP_COPYRIGHT_KEY, extractProperty(message.getMessage().getProperties(), "COPYRIGHT", message.getMessage().getCreatedBy()));
        attrs.put(TEMP_EXPORT_DATE, message.getTillDate());
        attrs.put(TEMP_EXPORT_DIRECTORY, message.getExportDirectory());
        attrs.put(TEMP_EXPORT_SCHEME, message.getScheme());
        attrs.put(TEMP_EXPORT_SCHEME_PROTOCOL, scheme.getProtocol());
        return attrs;
    }
    
    private String extractProperty(Set<MessagePropertyModel> properties, String key, String defaultValue) {
        if (properties == null) {
            return defaultValue;
        }
        Optional<String> propertyValue = properties.stream()
                .filter(pr -> Objects.equals(pr.getType(), key))
                .map(prop -> prop.getContent())
                .findFirst();
        return propertyValue.isPresent() ? propertyValue.get() : defaultValue;
    }
}
