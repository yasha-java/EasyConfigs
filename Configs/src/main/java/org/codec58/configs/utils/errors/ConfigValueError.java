package org.codec58.configs.utils.errors;

import org.bukkit.plugin.Plugin;
import org.codec58.configs.utils.ConfigUtils;

import java.lang.reflect.Field;

public class ConfigValueError {
    public final Field field;
    public final String reason;
    public final Plugin plugin;
    public final Class<?> configClass;

    public ConfigValueError(String reason, Plugin plugin, Class<?> configClass, Field field) {
        this.field = field;
        this.reason = reason;
        this.plugin = plugin;
        this.configClass = configClass;
    }

    public Class<?> getConfigClass() {
        return this.configClass;
    }

    public Field getField() {
        return this.field;
    }

    public Plugin getPlugin() {
        return this.plugin;
    }

    public String getReason() {
        return this.reason;
    }

    @Override
    public String toString() {
        return """
                {
                    Plugin: %s
                    Config: %s
                    Parameter: %s
                    Reason: %s
                }
                """
                .formatted(
                    this.plugin != null ? this.plugin.getName() : "None",
                    ConfigUtils.getConfigName(this.configClass),
                    ConfigUtils.getConfigVariableName(this.field),
                    reason
                );
    }
}
