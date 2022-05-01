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
package tk.freaxsoftware.ribbon2.message.service;

import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tk.freaxsoftware.ribbon2.core.data.request.MessagePropertyRegistrationRequest;
import tk.freaxsoftware.ribbon2.message.entity.PropertyType;
import tk.freaxsoftware.ribbon2.message.repo.PropertyTypeRepository;

/**
 * Property type service.
 * @author Stanislav Nepochatov
 */
public class PropertyTypeService {
    
    private final static Logger LOGGER = LoggerFactory.getLogger(PropertyTypeService.class);
    
    private final PropertyTypeRepository propertyRepository;

    public PropertyTypeService(PropertyTypeRepository propertyRepository) {
        this.propertyRepository = propertyRepository;
    }
    
    /**
     * Register specified properties by remote tag.
     * @param tag service or unit id;
     * @param propertyTypes list of properties;
     */
    public void registerProperties(String tag, List<MessagePropertyRegistrationRequest.Entry> propertyTypes) {
        LOGGER.info("Removing properties by tag {}", tag);
        propertyRepository.deleteByTag(tag);
        LOGGER.info("Request to register properties");
        propertyRepository.saveAll(propertyTypes.stream().map(prop -> {
            PropertyType type = new PropertyType();
            type.setType(prop.getType());
            type.setDescription(prop.getDescription());
            type.setTag(tag);
            return type;
        }).collect(Collectors.toList()));
    }
    
    public List<PropertyType> findAll() {
        LOGGER.info("Get all property types");
        return propertyRepository.findAll();
    }
    
}
