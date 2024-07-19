package org.codec58.configs;

import com.google.common.reflect.ClassPath;
import org.bukkit.plugin.java.JavaPlugin;
import org.codec58.easyconfigsapi.Config;
import org.codec58.easyconfigsapi.ConfigVariable;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.file.Path;
import java.util.Set;
import java.util.stream.Collectors;

public final class Utils {
    public static boolean checkAnnotation(Config cfg) {
        return !cfg.name().isEmpty() && !cfg.name().equals(" ");
    }

    public static boolean checkFields(Field[] fields) {
        for (Field field : fields) {
            field.setAccessible(true);

            if (field.isAnnotationPresent(ConfigVariable.class)) {
                String name = field.getAnnotation(ConfigVariable.class).name();
                if (name.isEmpty() || name.equals(" ")) {
                    printRuntimeError("The configuration variable '%s' must be named".formatted(field.getName()));
                    return false;
                }

                if (!Modifier.isStatic(field.getModifiers())) {
                    printRuntimeError("The configuration variable '%s' must be static".formatted(field.getName()));
                    return false;
                }

                try {
                    if (field.get(null) == null) {
                        printRuntimeError("The configuration variable '%s' haven't default value".formatted(field.getName()));
                        return false;
                    }
                } catch (Throwable ignored) {
                    return false;
                }
            }
        }

        return true;
    }

    public static void saveConfig(JavaPlugin plugin, Class<?> configClass) {
        if (!plugin.getDataFolder().exists() && !plugin.getDataFolder().mkdirs()) {
            printRuntimeError("Error while creating plugin config folder: '%s'".formatted(plugin.getDataFolder().toPath()));
            return;
        }

        Config cfgData = configClass.getAnnotation(Config.class);
        File cfg = join(plugin.getDataFolder().toPath(), cfgData.name() + ".json").toFile();

        if (!cfg.exists() && !cfg.isFile()) {
            boolean ignored = cfg.delete();
            try {
                if (!cfg.createNewFile()) {
                    printRuntimeError("Error while creating config file: '%s'".formatted(cfgData.name() + ".json"));
                    return;
                }
            } catch (IOException e) {
                printRuntimeError("Error while creating config file: '%s'".formatted(cfgData.name() + ".json"));
                return;
            }
        }

        JSONObject object = JsonUpdater.getJsonByConfigClass(configClass);
        try (FileOutputStream stream = new FileOutputStream(cfg)) {
            stream.write(object.toString(4).getBytes());
        } catch (IOException e) {
            printRuntimeError("Error while writing config: '%s'".formatted(cfgData.name()+".json"));
            e.printStackTrace(System.err);
        }
    }

    public static JSONObject getConfig(JavaPlugin plugin, Config cfgData, Class<?> configClass) {
        boolean ignored = false;

        if (!plugin.getDataFolder().exists() && !plugin.getDataFolder().mkdirs()) {
            printRuntimeError("Error while creating config/data folder in plugin %s. (?why?)".formatted(plugin.getName()));
            return null;
        }

        File cfg = join(plugin.getDataFolder().toPath(), cfgData.name() + ".json").toFile();
        String json;

        if (!cfg.exists() && !cfg.isFile()) {
            ignored = cfg.delete();

            try {
                if (!cfg.createNewFile()) {
                    printRuntimeError("Error while creating config file: '%s'".formatted(cfgData.name() + ".json"));
                    return null;
                }
            } catch (IOException e) {
                printRuntimeError("Error while creating config file: '%s'".formatted(cfgData.name() + ".json"));
                return null;
            }

            json = JsonUpdater.getJsonByConfigClass(configClass).toString();

            try (FileOutputStream out = new FileOutputStream(cfg)) {
                out.write(json.getBytes());
            }catch (IOException e) {
                printRuntimeError("Error while saving json config: '%s'".formatted(cfgData.name() + ".json"));
                return null;
            }
        } else {
            try (FileInputStream stream = new FileInputStream(cfg)) {
                json = new String(stream.readAllBytes());

                if (json.isEmpty()) {
                    json = JsonUpdater.getJsonByConfigClass(configClass).toString();
                }
            } catch (IOException e) {
                printRuntimeError("Error while loading json config: '%s'".formatted(cfgData.name() + ".json"));
                return null;
            }
        }

        JSONObject object;

        try {
            object = new JSONObject(json);
        } catch (JSONException err) {
            printRuntimeError("Error while reading json config: '%s'".formatted(cfgData.name() + ".json"));
            err.printStackTrace(System.err);
            return null;
        }

        Field[] fields = configClass.getDeclaredFields();
        if (!checkFields(fields)) {
            printRuntimeError("Field in plugin %s incorrect. Can't load current/default config".formatted(plugin.getName()));
            return null;
        }

        for (Field field : configClass.getDeclaredFields()) {
            if (field.isAnnotationPresent(ConfigVariable.class)) {
                String name = field.getAnnotation(ConfigVariable.class).name();

                try {
                    if (field.get(null) == null)
                        continue;
                } catch (Throwable ignored0) {}

                if (!object.has(name)) {
                    try {
                        object.put(name, field.get(null));
                    } catch (IllegalAccessException e) {
                        printRuntimeError("Error while creating json object: '%s'".formatted(cfgData.name() + ".json"));
                    }
                }
            }
        }

        return object;
    }

    public static Set<Class<?>> findAllClassesUsingReflectionsLibrary(JavaPlugin plugin) throws IOException {
        String[] split = plugin.getClass().getPackageName().split("\\.");
        String joinedPack = String.join(".", split[0], split[1], split[2]);
        return ClassPath.from(plugin.getClass().getClassLoader())
                .getAllClasses()
                .parallelStream()
                .filter(clazz -> clazz.getPackageName().contains(joinedPack))
                .map(ClassPath.ClassInfo::load)
                .filter(clazz -> clazz.isAnnotationPresent(Config.class))
                .collect(Collectors.toSet());
    }

    public static void printRuntimeError(String cause) {
        new RuntimeException(cause).printStackTrace(System.err);
    }

    public static Path join(Path path, String file) {
        return Path.of(path.toAbsolutePath().toString(), file);
    }
}
