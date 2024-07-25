package org.codec58.easyconfigsapi;

import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.plugin.Plugin;

import java.util.Set;

public class EmptyConfigRegistry implements ConfigRegistry {
    @Override
    public void addThis(Plugin plugin) {

    }

    @Override
    public void removeThis(Plugin plugin) {

    }

    @Override
    public void reloadThis(Plugin plugin) {

    }

    @Override
    public void saveThis(Plugin plugin) {

    }

    @Override
    public boolean isPluginUsingEasyConfigs(Plugin plugin) {
        return false;
    }

    @Override
    public Set<String> getPluginStrings() {
        return Set.of();
    }

    @Override
    public void onPluginUnload(PluginDisableEvent evt) {

    }
}
