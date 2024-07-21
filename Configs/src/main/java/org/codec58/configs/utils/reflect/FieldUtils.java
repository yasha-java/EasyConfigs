package org.codec58.configs.utils.reflect;

import org.codec58.configs.utils.reflect.exception.NonStaticFieldError;
import org.codec58.configs.utils.reflect.exception.ReflectionError;
import org.codec58.configs.utils.reflect.exception.ReflectionNoError;
import org.codec58.configs.utils.reflect.exception.ValueEqualsNullError;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class FieldUtils {
    public static boolean isStatic(Field f) {
        return Modifier.isStatic(f.getModifiers());
    }

    public static boolean isPublic(Field f) {
        return Modifier.isPublic(f.getModifiers());
    }

    public static boolean isFinal(Field f) {
        return Modifier.isFinal(f.getModifiers());
    }

    public static ReflectionError doPrivileged(ReflectionConsumer con, Object object, Field f) {
        boolean currentAccessor = f.canAccess(object);
        f.setAccessible(true);
        ReflectionError err = con.accept(f);
        f.setAccessible(currentAccessor);
        return err;
    }

    public static ReflectionError setStatic(Field f, Object value) {
        if (isStatic(f)) {
            try {
                return doPrivileged(
                        field -> {
                            try {
                                field.set(null, value);
                                return new ReflectionNoError(null);
                            } catch (IllegalAccessException e) {
                                return new ReflectionError(e);
                            }
                        },
                        null, f
                );
            } catch (Throwable e) {
                return new ReflectionError(e);
            }
        } else {
            return new NonStaticFieldError(f.getName());
        }
    }

    public static ReflectionError getStatic(Field f) {
        if (isStatic(f)) {
            return doPrivileged(field -> {
                        try {
                            Object fieldValue = field.get(null);
                            if (fieldValue == null) {
                                return new ValueEqualsNullError(f.getName());
                            }

                            return new ReflectionNoError(fieldValue);
                        } catch (IllegalAccessException e) {
                            return new ReflectionError(e);
                        }
                    },
                    null, f
            );
        } else {
            return new NonStaticFieldError(f.getName());
        }
    }

    @FunctionalInterface
    public interface ReflectionConsumer {
        ReflectionError accept(Field f);
    }
}
