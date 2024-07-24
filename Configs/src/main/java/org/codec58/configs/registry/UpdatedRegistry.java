package org.codec58.configs.registry;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.plugin.Plugin;
import org.codec58.configs.utils.ConfigUpdater;
import org.codec58.configs.utils.ConfigUtils;
import org.codec58.configs.utils.IOUtils;
import org.codec58.configs.utils.errors.ConfigValueError;
import org.codec58.configs.utils.reflect.ClassPathUtils;
import org.codec58.easyconfigsapi.ConfigRegistry;
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
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Unhandled exception:");
            err.printStackTrace(System.err);
            return;
        }

        HashMap<Class<?>,  Set<Field>> localRegistry = new HashMap<>();

        for (Class<?> packageClass : classes) {
            if (ConfigUtils.isConfigClass(packageClass)) {
                String configName = ConfigUtils.getConfigName(packageClass);

                JSONObject config = IOUtils.loadConfig(ConfigUtils.getConfigFile(configName, plugin));
                if (config == null) {
                    Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Can't load config '%s' for plugin '%s'.".formatted(configName, plugin.getName()));
                    continue;
                }

                List<ConfigValueError> errors = ConfigUpdater.updateValues(
                        plugin,
                        config,
                        packageClass
                );

                if (!errors.isEmpty()) {
                    Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "There were some errors when loading the '%s' config: ".formatted(configName));
                    errors.forEach(System.err::println);
                }

                localRegistry.put(packageClass, ConfigUtils.getConfigFields(packageClass));
            }
        }

        compiledRegistry.put(plugin.getName(), localRegistry);
    }

    @Override
    public void removeThis(Plugin plugin) {
        if (!plugin.getClass().equals(ClassPathUtils.getCallerClass()) && !ClassPathUtils.isSelfPackage()) {
            Bukkit.getConsoleSender().sendMessage( ChatColor.YELLOW + ClassPathUtils.getCallerClass().getSimpleName() + " attempts to remove our plugin!");
            return;
        }

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
        if (!ClassPathUtils.isSelfPackage()) {
            Bukkit.getConsoleSender().sendMessage( ChatColor.YELLOW + ClassPathUtils.getCallerClass().getSimpleName() + " attempts to save all configs!");
            return;
        }

        compiledRegistry.forEach((pName, lReg) -> {
            Plugin plugin = Bukkit.getPluginManager().getPlugin(pName);

            if (plugin == null) {
                Bukkit.getConsoleSender().sendMessage("Plugin '%s' has been deactivated in server work process. Can't write config file".formatted(pName));
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
        if (!ClassPathUtils.isSelfPackage()) {
            Bukkit.getConsoleSender().sendMessage( ChatColor.YELLOW + ClassPathUtils.getCallerClass().getSimpleName() + " attempts to reload all configs!");
            return;
        }

        List<Plugin> tReload = new ArrayList<>();

        compiledRegistry.keySet().forEach(pName -> {
            Plugin p = Bukkit.getPluginManager().getPlugin(pName);
            if (p != null)
                tReload.add(Bukkit.getPluginManager().getPlugin(pName));
            else
                Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "Can't reload deactivated plugin '%s'".formatted(pName));
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
    @EventHandler
    public void onPluginUnload(PluginDisableEvent evt) {
        if (isPluginUsingEasyConfigs(evt.getPlugin())) {
            removeThis(evt.getPlugin());
        }
    }
}
