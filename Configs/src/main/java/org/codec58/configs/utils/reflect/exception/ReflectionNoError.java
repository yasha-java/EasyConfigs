package org.codec58.configs.utils.reflect.exception;

public class ReflectionNoError extends ReflectionError {
    Object object;

    public ReflectionNoError(Object object) {
        super("");
        this.object = object;
    }

    public Object getObject() {
        if (this.object == null) {
            throw new UnsupportedOperationException("null");
        }
        return object;
    }
}
