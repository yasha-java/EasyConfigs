package org.codec58.configs.utils;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.codec58.configs.utils.reflect.FieldUtils;
import org.codec58.configs.utils.reflect.exception.NonStaticFieldError;
import org.codec58.configs.utils.reflect.exception.ReflectionError;
import org.codec58.configs.utils.reflect.exception.ReflectionNoError;
import org.codec58.configs.utils.reflect.exception.ValueEqualsNullError;
import org.codec58.easyconfigsapi.Config;
import org.codec58.easyconfigsapi.ConfigVariable;
import org.json.JSONObject;

import java.io.File;
import java.lang.reflect.Field;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ConfigUtils {
    public static boolean isConfigClass(Class<?> clazz) {
        return clazz.isAnnotationPresent(Config.class);
    }

    public static boolean isConfigVariable(Field field) {
        Class<?> genericType = field.getType();
        if (field.isAnnotationPresent(ConfigVariable.class))
            return genericType.equals(int.class) || genericType.equals(Integer.class) ||
                    genericType.equals(boolean.class) || genericType.equals(Boolean.class) ||
                    genericType.equals(float.class) || genericType.equals(Float.class) ||
                    genericType.equals(double.class) || genericType.equals(Double.class) ||
                    genericType.equals(short.class) || genericType.equals(Short.class) ||
                    genericType.equals(String.class) || genericType.equals(JSONObject.class);
        return false;
    }

    public static String getConfigName(Class<?> clazz) {
        if (isConfigClass(clazz)) {
            return clazz.getAnnotation(Config.class).name();
        } else {
            return null;
        }
    }

    public static String getConfigVariableName(Field field) {
        if (isConfigVariable(field)) {
            return field.getAnnotation(ConfigVariable.class).name();
        } else {
            return null;
        }
    }

    public static File getConfigFile(String configName, Plugin plugin) {
        return Path.of(plugin.getDataFolder().getAbsolutePath(), configName + ".json").toFile();
    }

    public static boolean isConfigExist(String configName, Plugin plugin) {
        return getConfigFile(configName, plugin).exists();
    }

    public static Map<String, Object> getConfigMapping(Class<?> clazz) {
        if (isConfigClass(clazz)) {
            Field[] fields = clazz.getDeclaredFields();
            Map<String, Object> mapped = new HashMap<>();

            for (Field f : fields) {
                if (isConfigVariable(f)) {
                    if (FieldUtils.isFinal(f)) {
                        Bukkit.getConsoleSender()
                                .sendMessage("Field %s is final. Can't set value. Ignored"
                                        .formatted(f.getName()));
                        continue;
                    }

                    String variableName = getConfigVariableName(f);
                    ReflectionError output = FieldUtils.getStatic(f);

                    if (output instanceof NonStaticFieldError) {
                        Bukkit.getConsoleSender()
                                .sendMessage("Field %s not static. Can't get/set value. Ignored"
                                        .formatted(f.getName()));
                    } else if (output instanceof ValueEqualsNullError) {
                        Bukkit.getConsoleSender()
                                .sendMessage("Field %s haven't default value. Can't can't create base config. Ignored"
                                        .formatted(f.getName()));
                    } else if (output instanceof ReflectionNoError noError) {
                        mapped.put(variableName, noError.getObject());
                    }
                }
            }

            return mapped;
        } else {
            throw new RuntimeException("This is not config class: " + clazz.getName());
        }
    }

    public static Set<Field> getConfigFields(Class<?> clazz) {
        if (isConfigClass(clazz)) {
            Field[] fields = clazz.getDeclaredFields();
            Set<Field> output = new HashSet<>();

            for (Field field : fields) {
                if (!FieldUtils.isFinal(field) && FieldUtils.isStatic(field) && FieldUtils.isPublic(field)) {
                    if (isConfigVariable(field))
                        output.add(field);
                }
            }

            return output;
        } else {
            throw new RuntimeException("This is not config class: " + clazz.getName());
        }
    }
}
