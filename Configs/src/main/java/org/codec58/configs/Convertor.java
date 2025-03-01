package org.codec58.configs;

public class Convertor {
    public static Object double2NumericType(Double value, Class<?> type) {
        if (isNumericType(type)) {
            if (type.equals(double.class) || type.equals(Double.class)) {
                return value;
            } else if (type.equals(int.class) || type.equals(Integer.class)) {
                return value.intValue();
            } else if (type.equals(float.class) || type.equals(Float.class)) {
                return value.floatValue();
            } else if (type.equals(short.class) || type.equals(Short.class)) {
                return value.shortValue();
            } else {
                throw new RuntimeException("Converter error. Unhandled variable type: " + type);
            }
        } else {
            throw new RuntimeException("This is not numeric type!");
        }
    }

    public static boolean isNumericType(Class<?> type) {
        return type.equals(double.class) || type.equals(Double.class) ||
                type.equals(int.class) || type.equals(Integer.class) ||
                type.equals(float.class) || type.equals(Float.class) ||
                type.equals(short.class) || type.equals(Short.class);
    }

    public static boolean isNumber(String s) {
        try {
            Double.parseDouble(s);
            return true;
        } catch (Throwable ignored) {
            return false;
        }
    }

    public static Object parseObject(Object obj, Class<?> type) {
        String s;
        try {
            s = obj.toString();
        } catch (Throwable ignored) {
            return null;
        }

        if (isBoolean(s))
            return getBoolean(s);
        else if (isNumber(s))
            return double2NumericType(getNumber(s), type);
        else if (type.equals(String.class))
            return s;
        else
            return null;
    }

    public static Double getNumber(String s) {
        return Double.parseDouble(s);
    }

    public static boolean isBoolean(String s) {
        return s.equalsIgnoreCase("true") || s.equalsIgnoreCase("false");
    }

    public static boolean getBoolean(String s) {
        return s.equalsIgnoreCase("true");
    }
}
