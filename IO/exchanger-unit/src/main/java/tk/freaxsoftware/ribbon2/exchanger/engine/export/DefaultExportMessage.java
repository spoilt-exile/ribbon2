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

import freemarker.template.TemplateException;
import java.io.IOException;
import tk.freaxsoftware.ribbon2.core.data.MessageModel;
import tk.freaxsoftware.ribbon2.exchanger.entity.ExportQueue;
import tk.freaxsoftware.ribbon2.io.core.IOScheme;
import tk.freaxsoftware.ribbon2.io.core.exporter.ExportMessage;

/**
 * Implementation of export message. Supports templating.
 * @author Stanislav Nepochatov
 */
public class DefaultExportMessage implements ExportMessage {
    
    private final ExportQueue queueEntry;
    
    private final IOScheme exportScheme;
    
    private final String exportedContent;

    /**
     * Default constructor.
     * @param templateService service to process message content;
     * @param queueEntry entry of queue to export;
     * @param exportScheme export scheme;
     * @throws IOException
     * @throws TemplateException 
     */
    public DefaultExportMessage(TemplateService templateService, 
            ExportQueue queueEntry, IOScheme exportScheme) throws IOException, TemplateException {
        this.queueEntry = queueEntry;
        this.exportScheme = exportScheme;
        this.exportedContent = templateService.processMessage(exportScheme, queueEntry);
    }

    @Override
    public MessageModel getMessage() {
        return queueEntry.getMessage();
    }

    @Override
    public String getExportContent() {
        return exportedContent;
    }

    @Override
    public String getId() {
        return queueEntry.getMessage().getUid();
    }

    @Override
    public String getHeader() {
        return queueEntry.getMessage().getHeader();
    }

    @Override
    public IOScheme getExportScheme() {
        return exportScheme;
    }

    @Override
    public Boolean isContentProcessed() {
        return exportScheme.getConfig().containsKey(TemplateService.GENERAL_TEMPLATE_KEY);
    }
    
}
