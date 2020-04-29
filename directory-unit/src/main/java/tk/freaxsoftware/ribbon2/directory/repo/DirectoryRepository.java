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
import java.util.stream.Collectors;
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
     * Find directories by array of paths.
     * @param dirPaths array with path names;
     * @return set of directories;
     */
    public Set<Directory> findDirByPaths(String[] dirPaths) {
        return DB.getDefault().find(Directory.class).order().asc("id").where().in("fullName", dirPaths).findSet();
    }
    
    /**
     * Find single directory by it's full path.
     * @param fullPath full path to directory;
     * @return single directory or null;
     */
    public Directory findDirectoryByPath(String fullPath) {
        return DB.getDefault().find(Directory.class).where().eq("fullName", fullPath).findOne();
    }
    
    /**
     * Finds directories which begins on path.
     * @param fullPath path to search;
     * @return set of directories;
     */
    public Set<Directory> findDirectoriesByPath(String fullPath) {
        return DB.getDefault().find(Directory.class).where().like("fullName", fullPath + "%").findSet();
    }
    
    /**
     * Save directory.
     * @param directory directory to save;
     * @return saved instance;
     */
    public Directory save(Directory directory) {
        directory.save();
        return directory;
    }
    
    /**
     * Deletes set of directories.
     * @param directories directories to delete.
     */
    public void delete(Set<Directory> directories) {
        LOGGER.warn("Deleting following directories: [{}]", 
                directories.stream().map(dir -> dir.getFullName()).collect(Collectors.toSet()));
        DB.getDefault().deleteAll(directories);
    }
    
}
