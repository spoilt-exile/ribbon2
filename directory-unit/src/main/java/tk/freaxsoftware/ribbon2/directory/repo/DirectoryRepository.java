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
package tk.freaxsoftware.ribbon2.directory.repo;

import io.ebean.DB;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tk.freaxsoftware.ribbon2.directory.entity.Directory;

/**
 * Persistance repository for directories.
 * @author Stanislav Nepochatov
 */
public class DirectoryRepository {
    
    private final static Logger LOGGER = LoggerFactory.getLogger(DirectoryRepository.class);
    
    /**
     * Find directoris by array of paths.
     * @param dirPaths array with path names;
     * @return set of directories;
     */
    public Set<Directory> findDirByPaths(String[] dirPaths) {
        LOGGER.info("Finding directories along path: {}", dirPaths);
        return DB.getDefault().find(Directory.class).order().asc("id").where().in("fullName", dirPaths).findSet();
    }
    
}
