package org.codec58.configs.utils.reflect;

import com.google.common.reflect.ClassPath;
import org.codec58.easyconfigsapi.Config;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class ClassPathUtils {
    public static Set<Class<?>> getAllClassesInPackage(ClassLoader cl, Package p) throws IOException {
        String package0 = splitAndJoinClassPath(p.getName(), 0, 1, 2);
        return ClassPath.from(cl)
                .getAllClasses()
                .parallelStream()
                .filter(clazz -> clazz.getPackageName().contains(package0))
                .map(ClassPath.ClassInfo::load)
                .filter(clazz -> clazz.isAnnotationPresent(Config.class))
                .collect(Collectors.toSet());
    }

    public static Class<?> getCallerClass() {
        return getCallerClass0();
    }

    public static boolean isSelfPackage() {
        Class<?> caller = getCallerClass0();
        return caller.getPackageName().contains("org.codec58.configs");
    }

    private static Class<?> getCallerClass0() {
        StackWalker walker = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE);
        AtomicReference<Class<?>> callerClass = new AtomicReference<>(null);
        AtomicInteger skip = new AtomicInteger(0);
        walker.forEach(frame -> {
            skip.addAndGet(1);
            if (skip.get() == 3) {
                callerClass.set(frame.getDeclaringClass());
            }
        });
        return callerClass.get();
    }

    public static String splitAndJoinClassPath(String s, int... indexes) {
        String[] split = s.split("\\.");
        StringBuilder builder = new StringBuilder();
        for (int i : indexes) {
            builder.append('.').append(split[i]);
        }
        return builder.substring(1, builder.length());
    }
}
