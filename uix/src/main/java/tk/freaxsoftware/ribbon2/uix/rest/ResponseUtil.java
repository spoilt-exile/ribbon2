/*
 * This file is part of Ribbon2 news message system.
 * 
 * Copyright (C) 2024 Freax Software
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
package tk.freaxsoftware.ribbon2.uix.rest;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Objects;
import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import tk.freaxsoftware.extras.bus.bridge.http.util.GsonUtils;
import tk.freaxsoftware.ribbon2.core.exception.CoreError;
import tk.freaxsoftware.ribbon2.core.exception.CoreException;
import tk.freaxsoftware.ribbon2.core.exception.RibbonErrorCodes;

/**
 * Response parsing and handling helper.
 * @author Stanislav Nepochatov
 */
public class ResponseUtil {
    private static final Gson gson = GsonUtils.getGson();
    
    /**
     * Performs parsing of the http response along with error handling. 
     * In case of an error it will throw CoreException.
     * @param <T> type of the response;
     * @param response raw response;
     * @param type type token of the response;
     * @return parsed response in case if it was successful or thorws CoreException;
     */
    public static <T> T handleResponse(HttpResponse response, TypeToken<T> type) {
        checkAndHandleResponse(response);
        return parseResponse(response, type);
    }
    
    public static String handleResponseRaw(HttpResponse response) {
        checkAndHandleResponse(response);
        return getResponseContent(response);
    }
    
    private static void checkAndHandleResponse(HttpResponse response) {
        if (response.getStatusLine().getStatusCode() >= 400) {
            Header contentTypeHeader = response.getEntity().getContentType();
            String contentTypeStr = contentTypeHeader != null ? contentTypeHeader.getValue() : "null";
            if (Objects.equals(contentTypeStr, "application/json")) {
                CoreError coreError = parseResponse(response, new TypeToken<CoreError>() {});
                throw new CoreException(coreError.getCode(), coreError.getMessage());
            } else {
                throw new CoreException(RibbonErrorCodes.CALL_ERROR, getResponseContent(response));
            }
        }
    }
    
    private static <R> R parseResponse(HttpResponse response, TypeToken<R> type) {
        return gson.fromJson(
                getResponseContent(response), 
                type.getType());
    }
    
    private static String getResponseContent(HttpResponse response) {
        try {
            return IOUtils.toString(response.getEntity().getContent(), Charset.defaultCharset());
        } catch (IOException ioex) {
            throw new CoreException(RibbonErrorCodes.UNREGISTERED, ioex.getMessage(), ioex);
        }
    }
}
