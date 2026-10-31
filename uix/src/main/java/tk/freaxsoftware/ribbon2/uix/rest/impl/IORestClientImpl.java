/*
 * This file is part of Ribbon2 news message system.
 * 
 * Copyright (C) 2026 Freax Software
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

package tk.freaxsoftware.ribbon2.uix.rest.impl;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import tk.freaxsoftware.extras.bus.bridge.http.util.GsonUtils;
import tk.freaxsoftware.ribbon2.core.exception.CoreException;
import tk.freaxsoftware.ribbon2.core.exception.RibbonErrorCodes;
import tk.freaxsoftware.ribbon2.io.core.IOScheme;
import tk.freaxsoftware.ribbon2.uix.model.io.IOModuleScheme;
import tk.freaxsoftware.ribbon2.uix.model.io.IOProtocol;
import tk.freaxsoftware.ribbon2.uix.rest.IORestClient;

/**
 * IO REST client implementation.
 * @author Stanislav Nepochatov
 */
public class IORestClientImpl implements IORestClient {

    private final HttpClientBuilder clientBuilder = HttpClientBuilder.create();
    
    private final String baseUrl;

    private final Gson gson = GsonUtils.getGson();

    public IORestClientImpl(String baseUrl) {
        this.baseUrl = baseUrl + "/api/io";
    }

    @Override
    public List<IOProtocol> getProtocols(String jwtKey) throws URISyntaxException, IOException {
        HttpGet request = new HttpGet(baseUrl + "/" + "protocol");
        request.addHeader("x-ribbon2-auth", jwtKey);
        HttpResponse response = clientBuilder.build().execute(request);
        return ResponseUtil.handleResponse(response, new TypeToken<List<IOProtocol>>() {});
    }

    @Override
    public List<IOModuleScheme> getSchemes(String jwtKey) throws URISyntaxException, IOException {
        HttpGet request = new HttpGet(baseUrl + "/" + "scheme");
        request.addHeader("x-ribbon2-auth", jwtKey);
        HttpResponse response = clientBuilder.build().execute(request);
        return ResponseUtil.handleResponse(response, new TypeToken<List<IOModuleScheme>>() {});
    }

    @Override
    public List<IOModuleScheme> getExportSchemesByDirectory(String jwtKey, String directory) throws URISyntaxException, IOException {
        HttpGet request = new HttpGet(baseUrl + "/export/scheme/" + directory);
        request.addHeader("x-ribbon2-auth", jwtKey);
        HttpResponse response = clientBuilder.build().execute(request);
        return ResponseUtil.handleResponse(response, new TypeToken<List<IOModuleScheme>>() {});
    }

    @Override
    public Optional<IOScheme> getScheme(String jwtKey, String type, String protocol, String name) throws URISyntaxException, IOException {
        HttpGet request = new HttpGet(baseUrl + "/scheme/" + type + "/" + protocol + "/" + name);
        request.addHeader("x-ribbon2-auth", jwtKey);
        HttpResponse response = clientBuilder.build().execute(request);
        if (response.getStatusLine().getStatusCode() == 404) {
            return Optional.empty();
        } else {
            return Optional.of(ResponseUtil.handleResponse(response, new TypeToken<IOScheme>() {}));
        }
    }

    @Override
    public IOScheme saveScheme(String jwtKey, IOScheme scheme) throws URISyntaxException, IOException {
        HttpPost request = new HttpPost(baseUrl + "/scheme");
        request.addHeader("x-ribbon2-auth", jwtKey);
        request.setEntity(new StringEntity(gson.toJson(scheme), ContentType.APPLICATION_JSON));
        HttpResponse response = clientBuilder.build().execute(request);
        return ResponseUtil.handleResponse(response, new TypeToken<IOScheme>() {});
    }

    @Override
    public void deleteScheme(String jwtKey, String type, String protocol, String name) throws URISyntaxException, IOException {
        HttpDelete request = new HttpDelete(baseUrl + "/scheme/" + type + "/" + protocol + "/" + name);
        request.addHeader("x-ribbon2-auth", jwtKey);
        HttpResponse response = clientBuilder.build().execute(request);
        if (response.getStatusLine().getStatusCode() != 200) {
            throw new CoreException(RibbonErrorCodes.CALL_ERROR, "Delete IO scheme request failed with status: " + response.getStatusLine().toString());
        }
    }

    @Override
    public void assignExportScheme(String jwtKey, String protocol, String name, String dir) throws URISyntaxException, IOException {
        HttpPost request = new HttpPost(baseUrl + "/export/scheme/" + protocol + "/" + name + "/assign/" + dir);
        request.addHeader("x-ribbon2-auth", jwtKey);
        HttpResponse response = clientBuilder.build().execute(request);
        if (response.getStatusLine().getStatusCode() != 200) {
            throw new CoreException(RibbonErrorCodes.CALL_ERROR, "Assign IO scheme request failed with status: " + response.getStatusLine().toString());
        }
    }

    @Override
    public void dismissExportScheme(String jwtKey, String protocol, String name, String dir) throws URISyntaxException, IOException {
        HttpDelete request = new HttpDelete(baseUrl + "/export/scheme/" + protocol + "/" + name + "/dismiss/" + dir);
        request.addHeader("x-ribbon2-auth", jwtKey);
        HttpResponse response = clientBuilder.build().execute(request);
        if (response.getStatusLine().getStatusCode() != 200) {
            throw new CoreException(RibbonErrorCodes.CALL_ERROR, "Assign IO scheme request failed with status: " + response.getStatusLine().toString());
        }
    }
    
}
