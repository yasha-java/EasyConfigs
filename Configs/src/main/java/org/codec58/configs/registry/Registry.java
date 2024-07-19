package org.codec58.configs.registry;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.codec58.configs.JsonUpdater;
import org.codec58.configs.Utils;
import org.codec58.easyconfigsapi.Config;
import org.codec58.easyconfigsapi.ConfigRegistry;
import org.codec58.easyconfigsapi.ConfigVariable;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public class Registry implements ConfigRegistry, Listener {
    private final HashMap<JavaPlugin, Set<Class<?>>> configs;
    private final HashMap<String, HashMap<String, Field>> compiledTabComplete;

    public Registry() {
        this.compiledTabComplete = new HashMap<>();
        configs = new HashMap<>();
    }

    public boolean isPluginUsingEasyConfigs(JavaPlugin plugin) {
        return configs.containsKey(plugin);
    }

    @EventHandler
    public void onPluginDisable(PluginDisableEvent evt) {
        if (isPluginUsingEasyConfigs((JavaPlugin) evt.getPlugin())) {
            removePlugin((JavaPlugin) evt.getPlugin());
        }
    }

    public void reload() {
        System.out.println("Reloading all configs...");
        List<JavaPlugin> plugins = configs.keySet().stream().toList();
        configs.clear();
        compiledTabComplete.clear();
        plugins.parallelStream().forEach(this::addPlugin);
        System.out.println("Done!");
    }

    private void removePlugin(JavaPlugin plugin) {
        Bukkit.getConsoleSender().sendMessage("Removing plugin " + plugin.getName() + "... Configs will not saved");
        this.configs.remove(plugin);
        this.compiledTabComplete.remove(plugin.getName());
        Bukkit.getConsoleSender().sendMessage("Removed!");
    }

    private void addPlugin(JavaPlugin plugin) {
        Bukkit.getConsoleSender().sendMessage("Adding plugin " + plugin.getName() + "...");

        Set<Class<?>> classes;
        try {
            classes = Utils.findAllClassesUsingReflectionsLibrary(plugin);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Bukkit.getConsoleSender().sendMessage("Plugin package size: " + classes.size());

        for (Class<?> clazz : classes) {
            Config cfgAnn = clazz.getAnnotation(Config.class);
            if (!Utils.checkAnnotation(cfgAnn)) {
                Utils.printRuntimeError("Invalid config name: " + cfgAnn.name());
            }

            Field[] fields = clazz.getDeclaredFields();
            if (!Utils.checkFields(fields)) {
                Utils.printRuntimeError("Fields incorrect. cry :)");
            }

            JSONObject cfg = Utils.getConfig(plugin, cfgAnn, clazz);
            if (cfg == null) {
                Utils.printRuntimeError("Can't update configs. cry :)");
            } else {
                JsonUpdater.updateVariables(cfg, clazz);
            }

            addConfig(plugin, clazz);
            compileTabComplete(plugin, clazz);

            Bukkit.getConsoleSender().sendMessage("Plugin " + plugin.getName() + " added!");
        }
    }

    @Override
    public void addThis(JavaPlugin plugin) {
        Class<?> callerClass = getCallerClass();
        if (!callerClass.equals(plugin.getClass())) {
            Bukkit.getConsoleSender().sendMessage("Plugin %s attempt to register configs in unknown class %s".formatted(plugin.getName(), callerClass.getSimpleName()));
            return;
        }
        addPlugin(plugin);
    }

    @Override
    public void removeThis(JavaPlugin plugin) {
        Class<?> callerClass = getCallerClass();
        if (!callerClass.getPackageName().equals(plugin.getClass().getPackageName())) {
            Bukkit.getConsoleSender().sendMessage("Caller %s in package %s attempt to unregister configs in %s plugin!"
                    .formatted(callerClass.getSimpleName(), callerClass.getPackageName(), plugin.getName()));
            return;
        }
        if (!callerClass.equals(plugin.getClass())) {
            Bukkit.getConsoleSender().sendMessage("Plugin %s attempt to unregister configs in unknown class %s".formatted(plugin.getName(), callerClass.getSimpleName()));
            return;
        }
        addPlugin(plugin);
    }

    public static Class<?> getCallerClass() {
        StackWalker walker = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE);
        AtomicReference<Class<?>> callerClass = new AtomicReference<>(null);
        walker.forEach(frame -> {
            // skip the current method
            if (frame.getDeclaringClass().equals(Registry.class)) {
                return;
            }
            // get the class of the caller of the current method
            if (callerClass.get() == null)
                callerClass.set(frame.getDeclaringClass());
        });
        return callerClass.get();
    }

    public Set<JavaPlugin> getPlugins() {
        return configs.keySet();
    }

    public HashMap<String, HashMap<String, Field>> getCompiledTabComplete() {
        return compiledTabComplete;
    }

    public void save() {
        Bukkit.getConsoleSender().sendMessage("Saving configs...");
        this.configs.forEach(
                (plugin, classes) -> {
                    for (Class<?> clazz : classes) {
                        Utils.saveConfig(plugin, clazz);
                    }
                }
        );
        Bukkit.getConsoleSender().sendMessage("Saved!");
    }

    private void addConfig(JavaPlugin plugin, Class<?> cfg) {
        this.configs.computeIfAbsent(plugin, set -> new HashSet<>()).add(cfg);
    }

    private void compileTabComplete(JavaPlugin plugin, Class<?> clazz) {
        HashMap<String, Field> configFields = new HashMap<>();

        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(ConfigVariable.class)) {
                String name = field.getAnnotation(ConfigVariable.class).name();
                configFields.put(name, field);
            }
        }

        this.compiledTabComplete.put(plugin.getName(), configFields);
    }
}
