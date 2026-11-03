package tk.freaxsoftware.ribbon2.io.core;

import java.util.List;

/**
 * A contract to module to return it's config entries. 
 * It's required for module to be configured on UI.
 * @author Stanislav Nepochatov
 */
public interface ConfigurableModule {
    
    /**
     * Get list of config entries to be rendered as form on UI.
     * @return list of config entries;
     */
    List<ConfigEntry> getConfigEntries();

    /**
     * Class of config entry for UI. 
     */
    public static class ConfigEntry {

        /**
         * Name of the param (also binds as form parameter).
         */
        private String name;

        /**
         * Type of the entry. Controls it's rendering on UI.
         */
        private EntryType type;

        /**
         * Flag whether the config entry is required for module to even pass basic validation.
         */
        private Boolean required;

        public ConfigEntry(String name, EntryType type, Boolean required) {
            this.name = name;
            this.type = type;
            this.required = required;
        }

        public String getName() {
            return name;
        }

        public EntryType getType() {
            return type;
        }

        public Boolean isRequired() {
            return required;
        }
    }

    /**
     * Enumeration of config entry types.
     */
    public static enum EntryType {
        /** Drop list of directories. Single select. */
        DIRECTORY,
        /** Simple boolean flag */
        BOOLEAN,
        /** Chunk of short text */
        TEXT,
        /** Number (decimal or not) */
        NUMBER,
        /** Secret text */
        SECRET;
    }
}
