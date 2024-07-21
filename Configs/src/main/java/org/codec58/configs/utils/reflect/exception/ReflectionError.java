package org.codec58.configs.utils.reflect.exception;

public class ReflectionError extends RuntimeException {
    public ReflectionError(String cause) {
        super(cause);
    }
    public ReflectionError(Throwable cause) {
        super(cause);
    }
}
