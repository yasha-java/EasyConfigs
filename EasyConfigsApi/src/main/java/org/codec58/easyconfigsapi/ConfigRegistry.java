package org.codec58.easyconfigsapi;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.plugin.Plugin;

import java.util.Set;

public interface ConfigRegistry extends Listener {
    void addThis(Plugin plugin);
    void removeThis(Plugin plugin);
    void reloadThis(Plugin plugin);
    void saveThis(Plugin plugin);
    boolean isPluginUsingEasyConfigs(Plugin plugin);
    Set<Plugin> getPlugins();
    Set<String> getPluginStrings();

    @EventHandler
    void onPluginUnload(PluginDisableEvent evt);
}
