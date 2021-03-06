/*
 * This file is part of Ribbon2 news message system.
 * 
 * Copyright (C) 2020 Freax Software
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
package tk.freaxsoftware.ribbon2.message.facade;

import java.util.List;
import java.util.stream.Collectors;
import tk.freaxsoftware.extras.bus.MessageHolder;
import tk.freaxsoftware.extras.bus.annotation.Receive;
import tk.freaxsoftware.ribbon2.core.data.MessagePropertyTagged;
import tk.freaxsoftware.ribbon2.core.data.request.MessagePropertyRegistrationRequest;
import tk.freaxsoftware.ribbon2.core.data.response.MessagePropertyTaggedHolder;
import tk.freaxsoftware.ribbon2.message.entity.converters.PropertyTypeConverter;
import tk.freaxsoftware.ribbon2.message.service.PropertyTypeService;

/**
 * Facade for property type calls.
 * @author Stanislav Nepochatov
 */
public class PropertyTypeFacade {
    
    private final PropertyTypeService propertyService;

    public PropertyTypeFacade(PropertyTypeService propertyService) {
        this.propertyService = propertyService;
    }
    
    @Receive(MessagePropertyTagged.CALL_GET_PROPERTIES)
    public void getAllPropertyTypes(MessageHolder<Void> message) {
        PropertyTypeConverter converter = new PropertyTypeConverter();
        List<MessagePropertyTagged> types = propertyService.findAll()
                .stream().map(p -> converter.convert(p)).collect(Collectors.toList());
        MessagePropertyTaggedHolder holder = new MessagePropertyTaggedHolder();
        holder.setPropertyTypes(types);
        message.getResponse().setContent(holder);
    }
    
    @Receive(MessagePropertyRegistrationRequest.CALL_REGISTER_PROPERTY)
    public void registerPropertyTypes(MessageHolder<MessagePropertyRegistrationRequest> message) {
        propertyService.registerProperties(message.getContent().getTag(), message.getContent().getPropertyTypes());
    }
    
}
