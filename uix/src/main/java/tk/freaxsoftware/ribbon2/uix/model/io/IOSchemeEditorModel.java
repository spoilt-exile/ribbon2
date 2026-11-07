package tk.freaxsoftware.ribbon2.uix.model.io;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import tk.freaxsoftware.ribbon2.io.core.ConfigEntry;
import tk.freaxsoftware.ribbon2.io.core.ConfigEntryType;
import tk.freaxsoftware.ribbon2.io.core.IOScheme;

/** Protocol-neutral scheme editor model that masks secret config values before template rendering. */
public class IOSchemeEditorModel {
    private IOProtocol protocol;
    private IOScheme scheme;
    private Set<String> configuredSecretFields = new LinkedHashSet<>();

    public IOSchemeEditorModel() {}

    public IOSchemeEditorModel(IOProtocol protocol, IOScheme source) {
        this.protocol = protocol;
        this.scheme = copyAndMaskSecrets(source, protocol, configuredSecretFields);
    }

    public IOProtocol getProtocol() { return protocol; }
    public void setProtocol(IOProtocol protocol) { this.protocol = protocol; }
    public IOScheme getScheme() { return scheme; }
    public void setScheme(IOScheme scheme) { this.scheme = scheme; }
    public Set<String> getConfiguredSecretFields() { return configuredSecretFields; }
    public void setConfiguredSecretFields(Set<String> configuredSecretFields) { this.configuredSecretFields = configuredSecretFields; }

    private static IOScheme copyAndMaskSecrets(IOScheme source, IOProtocol protocol, Set<String> configuredSecrets) {
        if (source == null) return null;
        IOScheme safe = new IOScheme();
        safe.setId(source.getId());
        safe.setName(source.getName());
        safe.setDescription(source.getDescription());
        safe.setType(source.getType());
        safe.setProtocol(source.getProtocol());
        Map<String, Object> config = source.getConfig() == null
                ? new LinkedHashMap<>() : new LinkedHashMap<>(source.getConfig());
        if (protocol != null && protocol.getConfigEntries() != null) {
            for (ConfigEntry entry : protocol.getConfigEntries()) {
                if (entry.getType() == ConfigEntryType.SECRET && config.containsKey(entry.getName())) {
                    configuredSecrets.add(entry.getName());
                    config.put(entry.getName(), null);
                }
            }
        }
        safe.setConfig(config);
        return safe;
    }
}
