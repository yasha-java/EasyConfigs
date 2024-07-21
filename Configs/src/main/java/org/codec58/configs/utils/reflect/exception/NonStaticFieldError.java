package org.codec58.configs.utils.reflect.exception;

public class NonStaticFieldError extends ReflectionError {
    public NonStaticFieldError(String cause) {
        super(cause);
    }
}
