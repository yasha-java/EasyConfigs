package org.codec58.configs.utils.reflect;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class EnumUtils {
    public static Object getEnumConstantByName(String name, Class<?> enumClass) {
        if (!enumClass.isEnum())
            return null;

        try {
            Method valueOf = enumClass.getDeclaredMethod("valueOf", String.class);
            return valueOf.invoke(null, name);
        } catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
            return null;
        }
    }
}
