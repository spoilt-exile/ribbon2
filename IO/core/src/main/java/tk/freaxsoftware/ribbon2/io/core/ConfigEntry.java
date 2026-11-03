package tk.freaxsoftware.ribbon2.io.core;

import java.util.Objects;

/** Serializable copy of an {@link IOModule.ConfigEntry} annotation value. */
public class ConfigEntry {
    private String name;
    private ConfigEntryType type;
    private boolean required;
    private String defaultValue;

    public ConfigEntry() {
    }

    public ConfigEntry(String name, ConfigEntryType type, boolean required, String defaultValue) {
        this.name = name;
        this.type = type;
        this.required = required;
        this.defaultValue = defaultValue;
    }

    public ConfigEntry(IOModule.ConfigEntry entry) {
        this(entry.name(), entry.type(), entry.required(),
                entry.defaultValue().isEmpty() ? null : entry.defaultValue());
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public ConfigEntryType getType() { return type; }
    public void setType(ConfigEntryType type) { this.type = type; }
    public boolean isRequired() { return required; }
    public void setRequired(boolean required) { this.required = required; }
    public String getDefaultValue() { return defaultValue; }
    public void setDefaultValue(String defaultValue) { this.defaultValue = defaultValue; }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof ConfigEntry)) return false;
        ConfigEntry that = (ConfigEntry) other;
        return required == that.required && Objects.equals(name, that.name)
                && type == that.type && Objects.equals(defaultValue, that.defaultValue);
    }

    @Override
    public int hashCode() { return Objects.hash(name, type, required, defaultValue); }
}
