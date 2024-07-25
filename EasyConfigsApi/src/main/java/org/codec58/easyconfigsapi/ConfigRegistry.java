package org.codec58.easyconfigsapi;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicesManager;

import java.util.Set;

public interface ConfigRegistry extends Listener {
    void addThis(Plugin plugin);
    void removeThis(Plugin plugin);
    void reloadThis(Plugin plugin);
    void saveThis(Plugin plugin);
    boolean isPluginUsingEasyConfigs(Plugin plugin);
    Set<String> getPluginStrings();

    @EventHandler
    void onPluginUnload(PluginDisableEvent evt);

    EmptyConfigRegistry empty = new EmptyConfigRegistry();
    default ConfigRegistry getInstance() {
        ServicesManager manager = Bukkit.getServicesManager();

        if (!manager.isProvidedFor(ConfigRegistry.class)) {
            return empty;
        }

        RegisteredServiceProvider<ConfigRegistry> registry = manager.getRegistration(ConfigRegistry.class);
        if (registry == null)
            return empty;

        return registry.getProvider();
    }
}
