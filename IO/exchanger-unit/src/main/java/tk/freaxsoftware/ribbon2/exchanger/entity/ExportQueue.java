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
package tk.freaxsoftware.ribbon2.exchanger.entity;

import io.ebean.Model;
import io.ebean.annotation.DbJsonB;
import java.time.ZonedDateTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import tk.freaxsoftware.ribbon2.core.data.MessageModel;

/**
 * Export message queue entry.
 * @author Stanislav Nepochatov
 */
@Entity
@Table(name = "export_queue")
public class ExportQueue extends Model {
    
    @Id
    private Long id;
    
    @Column(name = "export_directory")
    private String exportDirectory;
    
    private String protocol;
    
    private String scheme;
    
    private String trxId;
    
    @DbJsonB
    private MessageModel message;
    
    private ZonedDateTime tillDate;
    
    private String error;

    /**
     * Empty constructor.
     */
    public ExportQueue() {
    }

    /**
     * Full parametric constructor.
     * @param exportDirectory directory which export initated from;
     * @param protocol protocol of module to export;
     * @param scheme name of scheme;
     * @param trxId transaction id;
     * @param message message to export;
     * @param tillDate date till message should be exported;
     */
    public ExportQueue(String exportDirectory, 
            String protocol, 
            String scheme, 
            String trxId,
            MessageModel message, 
            ZonedDateTime tillDate) {
        this.exportDirectory = exportDirectory;
        this.protocol = protocol;
        this.scheme = scheme;
        this.trxId = trxId;
        this.message = message;
        this.tillDate = tillDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getExportDirectory() {
        return exportDirectory;
    }

    public void setExportDirectory(String exportDirectory) {
        this.exportDirectory = exportDirectory;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getScheme() {
        return scheme;
    }

    public void setScheme(String scheme) {
        this.scheme = scheme;
    }

    public String getTrxId() {
        return trxId;
    }

    public void setTrxId(String trxId) {
        this.trxId = trxId;
    }

    public MessageModel getMessage() {
        return message;
    }

    public void setMessage(MessageModel message) {
        this.message = message;
    }

    public ZonedDateTime getTillDate() {
        return tillDate;
    }

    public void setTillDate(ZonedDateTime tillDate) {
        this.tillDate = tillDate;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
    
}
