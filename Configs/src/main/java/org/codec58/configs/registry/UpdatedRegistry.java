package org.codec58.configs.registry;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.plugin.Plugin;
import org.codec58.configs.config.PluginConfig;
import org.codec58.configs.utils.ConfigUtils;
import org.codec58.configs.utils.IOUtils;
import org.codec58.configs.utils.reflect.ClassPathUtils;
import org.codec58.configs.utils.reflect.FieldUtils;
import org.codec58.easyconfigsapi.ConfigRegistry;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class UpdatedRegistry implements ConfigRegistry, Listener {
    private final HashMap<String, HashMap<Class<?>, Set<Field>>> compiledRegistry = new HashMap<>();

    public HashMap<String, HashMap<Class<?>, Set<Field>>> getCompiledRegistry() {
        return compiledRegistry;
    }

    @Override
    public void addThis(Plugin plugin) {
        if (!plugin.getClass().equals(ClassPathUtils.getCallerClass()) && !ClassPathUtils.isSelfPackage()) {
            Bukkit.getConsoleSender().sendMessage( ChatColor.YELLOW + ClassPathUtils.getCallerClass().getSimpleName() + " attempts to registrate our plugin!");
            return;
        }

        Set<Class<?>> classes;
        try {
            classes = ClassPathUtils.getAllClassesInPackage(
                    plugin.getClass().getClassLoader(),
                    plugin.getClass().getPackage()
            );
        } catch (Throwable err) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "Unhandled exception:");
            err.printStackTrace(System.err);
            return;
        }

        HashMap<Class<?>,  Set<Field>> localRegistry = new HashMap<>();

        for (Class<?> packageClass : classes) {
            if (ConfigUtils.isConfigClass(packageClass)) {
                String configName = ConfigUtils.getConfigName(packageClass);
                Set<Field> configFields = ConfigUtils.getConfigFields(packageClass);

                if (ConfigUtils.isConfigExist(configName, plugin)) {
                    JSONObject config;
                    try {
                        config =
                                new JSONObject(IOUtils.readString(ConfigUtils.getConfigFile(configName, plugin)));
                    } catch (JSONException err) {
                        Bukkit.getConsoleSender().sendMessage("Error while reading config: ");
                        err.printStackTrace(System.err);
                        continue;
                    }

                    for (Field field : configFields) {
                        String variableName = ConfigUtils.getConfigVariableName(field);
                        if (config.has(variableName)) {
                            try {
                                FieldUtils.setStatic(field, config.get(variableName));
                            } catch (Throwable ignored) {}
                        }
                    }
                }

                localRegistry.put(packageClass, configFields);
            }
        }

        compiledRegistry.put(plugin.getName(), localRegistry);
    }

    @Override
    public void removeThis(Plugin plugin) {
        compiledRegistry.remove(plugin.getName());
    }

    @Override
    public void saveThis(Plugin plugin) {
        if (!plugin.getClass().equals(ClassPathUtils.getCallerClass()) && !ClassPathUtils.isSelfPackage()) {
            Bukkit.getConsoleSender().sendMessage( ChatColor.YELLOW + ClassPathUtils.getCallerClass().getSimpleName() + " attempts to save our plugin!");
            return;
        }

        Set<Class<?>> cClasses = compiledRegistry.get(plugin.getName()).keySet();

        for (Class<?> cClass : cClasses) {
            String cName = ConfigUtils.getConfigName(cClass);
            JSONObject cConfig = new JSONObject(ConfigUtils.getConfigMapping(cClass));
            IOUtils.writeString(
                    ConfigUtils.getConfigFile(cName, plugin),
                    cConfig.toString(4)
            );
        }
    }

    public void saveAll() {
        compiledRegistry.forEach((pName, lReg) -> {
            Plugin plugin = Bukkit.getPluginManager().getPlugin(pName);

            if (plugin == null) {
                Bukkit.getConsoleSender().sendMessage(PluginConfig.PLUGIN_DEACTIVATED_IN_PROCESS.formatted(pName));
                return;
            }

            lReg.keySet().forEach(cClass -> {
                String cName = ConfigUtils.getConfigName(cClass);
                JSONObject cConfig = new JSONObject(ConfigUtils.getConfigMapping(cClass));
                IOUtils.writeString(
                        ConfigUtils.getConfigFile(cName, plugin),
                        cConfig.toString(4)
                );
            });
        });
    }

    @Override
    public void reloadThis(Plugin plugin) {
        if (!plugin.getClass().equals(ClassPathUtils.getCallerClass()) && !ClassPathUtils.isSelfPackage()) {
            Bukkit.getConsoleSender().sendMessage( ChatColor.YELLOW + ClassPathUtils.getCallerClass().getSimpleName() + " attempts to reload our plugin!");
            return;
        }

        if (compiledRegistry.containsKey(plugin.getName())) {
            compiledRegistry.remove(plugin.getName());

            addThis(plugin);
        }
    }

    public void reloadAll() {
        List<Plugin> tReload = new ArrayList<>();

        compiledRegistry.keySet().forEach(pName -> {
            Plugin p = Bukkit.getPluginManager().getPlugin(pName);
            if (p != null)
                tReload.add(Bukkit.getPluginManager().getPlugin(pName));
        });

        compiledRegistry.clear();

        tReload.forEach(this::addThis);
    }

    @Override
    public Set<String> getPluginStrings() {
        return compiledRegistry.keySet();
    }

    @Override
    public boolean isPluginUsingEasyConfigs(Plugin plugin) {
        return compiledRegistry.containsKey(plugin.getName());
    }

    @Override
    public Set<Plugin> getPlugins() {
        throw new UnsupportedOperationException();
    }

    @Override
    @EventHandler
    public void onPluginUnload(PluginDisableEvent evt) {
        if (isPluginUsingEasyConfigs(evt.getPlugin())) {
            removeThis(evt.getPlugin());
        }
    }
}
