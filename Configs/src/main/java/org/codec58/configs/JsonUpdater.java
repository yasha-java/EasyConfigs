package org.codec58.configs;

import org.codec58.easyconfigsapi.ConfigVariable;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public final class JsonUpdater {

    /**
    * Example
     * {
     *     "PROP_NAME_1" : "Hello, from string!",
     *     "PROP_NAME_2" : 12, //int
     *     "PROP_NAME_3" : true, //boolean
     * }
    */
    public static void updateVariables(JSONObject config, Class<?> clazz) {
        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(ConfigVariable.class)) {
                ConfigVariable prop = field.getAnnotation(ConfigVariable.class);

                if (config.has(prop.name())) {
                    try {
                        field.set(null, config.get(prop.name()));
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }

    public static JSONObject getJsonByConfigClass(Class<?> cfgClass) {
        JSONObject json = new JSONObject();

        for (Field field : cfgClass.getDeclaredFields()) {
            if (field.isAnnotationPresent(ConfigVariable.class) && Modifier.isStatic(field.getModifiers())) {
                ConfigVariable prop = field.getAnnotation(ConfigVariable.class);
                Object value;
                try {
                    value = field.get(null);
                } catch (IllegalAccessException e) {
                    Utils.printRuntimeError("Error while creating json object by config class: '%s'".formatted(cfgClass.getSimpleName()));
                    throw new RuntimeException(e);
                }

                json.put(prop.name(), value);
            }
        }

        return json;
    }
}
