package org.codec58.easyconfigsapi;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.Set;

public interface ConfigRegistry {
    void addThis(JavaPlugin plugin);
    void removeThis(JavaPlugin plugin);
    Set<JavaPlugin> getPlugins();
}
