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
package tk.freaxsoftware.ribbon2.exchanger.config;

import java.util.Set;
import tk.freaxsoftware.ribbon2.core.config.DbConfig;
import tk.freaxsoftware.ribbon2.core.data.DirectoryPermissionModel;
import tk.freaxsoftware.ribbon2.io.core.ModuleType;

/**
 * Config for exchanger unit.
 * @author Stanislav Nepochatov
 */
public class ExchangerUnitConfig {
    
    private DbConfig db;
    
    private ExchangerConfig exchanger;

    public DbConfig getDb() {
        return db;
    }

    public void setDb(DbConfig db) {
        this.db = db;
    }

    public ExchangerConfig getExchanger() {
        return exchanger;
    }

    public void setExchanger(ExchangerConfig exchanger) {
        this.exchanger = exchanger;
    }

    @Override
    public String toString() {
        return "{" + "db=" + db + ", exchanger=" + exchanger + '}';
    }
    
    public static class ExchangerConfig {
        
        private ModuleType type;
        
        private String moduleClass;
        
        private Boolean enablePermissionCaching;
        
        private Integer permissionCacheExpiry;
        
        private Set<DirectoryPermissionModel> importPermissions;
        
        private Set<DirectoryPermissionModel> exportPermissions;
        
        private ImportConfig importConfing;
        
        private ExportConfig exportConfig;

        public ModuleType getType() {
            return type;
        }

        public void setType(ModuleType type) {
            this.type = type;
        }

        public String getModuleClass() {
            return moduleClass;
        }

        public void setModuleClass(String moduleClass) {
            this.moduleClass = moduleClass;
        }

        public Boolean getEnablePermissionCaching() {
            return enablePermissionCaching;
        }

        public void setEnablePermissionCaching(Boolean enablePermissionCaching) {
            this.enablePermissionCaching = enablePermissionCaching;
        }

        public Integer getPermissionCacheExpiry() {
            return permissionCacheExpiry;
        }

        public void setPermissionCacheExpiry(Integer permissionCacheExpiry) {
            this.permissionCacheExpiry = permissionCacheExpiry;
        }

        public Set<DirectoryPermissionModel> getImportPermissions() {
            return importPermissions;
        }

        public void setImportPermissions(Set<DirectoryPermissionModel> importPermissions) {
            this.importPermissions = importPermissions;
        }

        public Set<DirectoryPermissionModel> getExportPermissions() {
            return exportPermissions;
        }

        public void setExportPermissions(Set<DirectoryPermissionModel> exportPermissions) {
            this.exportPermissions = exportPermissions;
        }

        public ImportConfig getImportConfing() {
            return importConfing;
        }

        public void setImportConfing(ImportConfig importConfing) {
            this.importConfing = importConfing;
        }

        public ExportConfig getExportConfig() {
            return exportConfig;
        }

        public void setExportConfig(ExportConfig exportConfig) {
            this.exportConfig = exportConfig;
        }

        @Override
        public String toString() {
            return "{" + "type=" + type + ", moduleClass=" + moduleClass + ", enablePermissionCaching=" + enablePermissionCaching + ", permissionCacheExpiry=" + permissionCacheExpiry + ", importPermissions=" + importPermissions + ", exportPermissions=" + exportPermissions + ", importConfing=" + importConfing + ", exportConfig=" + exportConfig + '}';
        }
        
        public static class ImportConfig {
            
            private Integer threadPoolSize;

            public Integer getThreadPoolSize() {
                return threadPoolSize;
            }

            public void setThreadPoolSize(Integer threadPoolSize) {
                this.threadPoolSize = threadPoolSize;
            }

            @Override
            public String toString() {
                return "{" + "threadPoolSize=" + threadPoolSize + '}';
            }
        }
        
        public static class ExportConfig {
            
            private Integer queuePeriod;

            public Integer getQueuePeriod() {
                return queuePeriod;
            }

            public void setQueuePeriod(Integer queuePeriod) {
                this.queuePeriod = queuePeriod;
            }

            @Override
            public String toString() {
                return "{" + "queuePeriod=" + queuePeriod + '}';
            }
        }
    }
}
