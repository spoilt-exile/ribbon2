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
package tk.freaxsoftware.ribbon2.uix.rest;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.function.Consumer;
import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import tk.freaxsoftware.extras.bus.bridge.http.util.GsonUtils;
import tk.freaxsoftware.ribbon2.core.exception.CoreError;
import tk.freaxsoftware.ribbon2.core.exception.RibbonErrorCodes;

/**
 * Result holder of REST operations. Contains object of result of details 
 * about error depending on status of response.
 * @author Stanislav Nepochatov
 */
public class RestResult<T> {
    
    private static final Gson gson = GsonUtils.getGson();
    
    private T result;
    
    private Error error;

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }

    public Error getError() {
        return error;
    }

    public void setError(Error error) {
        this.error = error;
    }
    
    public boolean isSuccess() {
        return error == null && result != null;
    }
    
    public RestResult<T> onSuccess(Consumer<T> onSuccessOperation) {
        if (isSuccess()) {
            onSuccessOperation.accept(result);
        }
        return this;
    }
    
    public RestResult<T> onError(Consumer<Error> onErrorOperation) {
        if (!isSuccess()) {
            onErrorOperation.accept(error);
        }
        return this;
    }
    
    /**
     * Create REST result by response. If successful (less than 400) response  
     * will be parsed by provided type in result portion. In case of error result 
     * will be return only with error portion. Core errors on gateway parsed automatically.
     * @param <T> type of reuslt;
     * @param response http response;
     * @param type type of the result for parser;
     * @return rest result with result or error;
     */
    public static <T> RestResult<T> ofResponse(HttpResponse response, TypeToken<T> type) {
        if (response.getStatusLine().getStatusCode() < 400) {
            try {
                T result = parseResponse(response, type);
                RestResult<T> restResult = new RestResult();
                restResult.setResult(result);
                return restResult;
            } catch (IOException ex) {
                RestResult<T> errorResult = new RestResult();
                Error error = new Error();
                error.setStatus(response.getStatusLine().getStatusCode());
                error.setStatusLine(response.getStatusLine().getReasonPhrase());
                Header contentTypeHeader = response.getEntity().getContentType();
                error.setContentType(contentTypeHeader != null ? contentTypeHeader.getValue() : "null");
                error.setIoError(true);
                errorResult.setError(error);
                return errorResult;
            }
        }
        return createErrorResult(response);
    }
    
    /**
     * Create REST result by response. If successful (less than 400) response 
     * will be set as raw sting in result. In case of error result will be return 
     * only with error portion. Core errors on gateway parsed automatically.
     * @param response http response;
     * @return rest result with string result or error;
     */
    public static RestResult<String> ofResponseRaw(HttpResponse response) {
        if (response.getStatusLine().getStatusCode() == 200) {
            try {
                String result = IOUtils.toString(response.getEntity().getContent(), Charset.defaultCharset());
                RestResult<String> restResult = new RestResult();
                restResult.setResult(result);
                return restResult;
            } catch (IOException ex) {
                RestResult errorResult = new RestResult();
                Error error = new Error();
                error.setStatus(response.getStatusLine().getStatusCode());
                error.setStatusLine(response.getStatusLine().getReasonPhrase());
                Header contentTypeHeader = response.getEntity().getContentType();
                error.setContentType(contentTypeHeader != null ? contentTypeHeader.getValue() : "null");
                error.setIoError(true);
                errorResult.setError(error);
                return errorResult;
            }
        }
        return createErrorResult(response);
    }
    
    private static RestResult createErrorResult(HttpResponse response) {
        RestResult errorResult = new RestResult();
        Error error = new Error();
        error.setStatus(response.getStatusLine().getStatusCode());
        error.setStatusLine(response.getStatusLine().getReasonPhrase());
        Header contentTypeHeader = response.getEntity().getContentType();
        error.setContentType(contentTypeHeader != null ? contentTypeHeader.getValue() : "null");
        errorResult.setError(error);
        try {
            if (error.getContentType().equals("application/json")) {
                CoreError coreError = parseResponse(response, new TypeToken<CoreError>() {});
                error.setCoreErrorCode(coreError.getCode());
                error.setCoreErrorMessage(coreError.getMessage());
            } else {
                error.setContent(IOUtils.toString(response.getEntity().getContent(), Charset.defaultCharset()));
            }
        } catch (IOException ex) {
            error.setIoError(true);
        }
        return errorResult;
    }
    
    private static <R> R parseResponse(HttpResponse response, TypeToken<R> type) throws IOException {
        return gson.fromJson(
                IOUtils.toString(response.getEntity().getContent(), Charset.defaultCharset()), 
                type.getType());
    }
    
    public static class Error {
        private int status;
        private String statusLine;
        private String contentType;
        private String content;
        private RibbonErrorCodes coreErrorCode;
        private String coreErrorMessage;
        private boolean ioError;

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public String getStatusLine() {
            return statusLine;
        }

        public void setStatusLine(String statusLine) {
            this.statusLine = statusLine;
        }

        public String getContentType() {
            return contentType;
        }

        public void setContentType(String contentType) {
            this.contentType = contentType;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public RibbonErrorCodes getCoreErrorCode() {
            return coreErrorCode;
        }

        public void setCoreErrorCode(RibbonErrorCodes coreErrorCode) {
            this.coreErrorCode = coreErrorCode;
        }

        public String getCoreErrorMessage() {
            return coreErrorMessage;
        }

        public void setCoreErrorMessage(String coreErrorMessage) {
            this.coreErrorMessage = coreErrorMessage;
        }

        public boolean isIoError() {
            return ioError;
        }

        public void setIoError(boolean ioError) {
            this.ioError = ioError;
        }
    }
    
}
