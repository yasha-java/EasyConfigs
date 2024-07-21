package org.codec58.configs;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import org.codec58.configs.commands.executors.ConfigEdit;
import org.codec58.configs.commands.completers.ConfigEditTabCompleter;
import org.codec58.configs.commands.executors.ConfigReload;
import org.codec58.configs.commands.executors.ConfigSave;
import org.codec58.configs.registry.UpdatedRegistry;
import org.codec58.easyconfigsapi.ConfigRegistry;

import java.util.Objects;

public final class Configs extends JavaPlugin implements Listener {
    private final UpdatedRegistry registry = new UpdatedRegistry();

    @Override
    public void onEnable() {
        Bukkit.getServicesManager().register(
                ConfigRegistry.class,
                registry,
                this,
                ServicePriority.Highest
        );

        registry.addThis(this);

        Objects.requireNonNull(getCommand("configEdit"))
                .setTabCompleter(new ConfigEditTabCompleter(registry));
        Objects.requireNonNull(getCommand("configEdit"))
                .setExecutor(new ConfigEdit(registry));

        Objects.requireNonNull(getCommand("configSave"))
                .setExecutor(new ConfigSave(registry));

        Objects.requireNonNull(getCommand("configReload"))
                .setExecutor(new ConfigReload(registry));

        Bukkit.getPluginManager().registerEvents(new ConfigsListener(), this);
        Bukkit.getPluginManager().registerEvents(registry, this);
    }
}
