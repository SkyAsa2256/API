package com.envyful.api.reflection;

import com.envyful.api.concurrency.UtilLogger;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 *
 * Utility methods for reflective access
 *
 */
public class UtilReflection {

    /**
     *
     * Checks if the class is loaded on the given class loader
     *
     * @param clazz The class
     * @param classLoader The class loader to check on
     * @return True if it is loaded
     */
    public static boolean isClassPresent(Class<?> clazz, ClassLoader classLoader) {
        try {
            Class.forName(clazz.getName(), false, classLoader);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    /**
     *
     * Attempts to set the value of the field in the given object to the given value
     *
     * @param of The object being set in
     * @param fieldName The variable being set
     * @param value The value it is being set to
     * @return True if successful
     */
    public static boolean setFieldValue(Object of, String fieldName, Object value) {
        try {
            boolean isStatic = of instanceof Class;
            Class<?> clazz = isStatic ? (Class<?>) of : of.getClass();

            Field field = getField(clazz, fieldName);
            if (field == null) return false;

            field.setAccessible(true);
            field.set(isStatic ? null : of, value);
            return true;
        } catch (IllegalAccessException e) {
            UtilLogger.getLogger().error("Error setting field value", e);
        }
        return false;
    }

    /**
     *
     * Gets the field with the given name from a class
     *
     * @param clazz The class
     * @param fieldName The field name
     * @return The field
     */
    public static Field getField(Class<?> clazz, String fieldName) {
        try {
            return clazz.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            Class<?> superClass = clazz.getSuperclass();
            return superClass == null ? null : getField(superClass, fieldName);
        }
    }

    /**
     *
     * Gets the type parameter from a class at the given index
     *
     * @param clazz The class
     * @param index The type index
     * @return The type
     */
    public static Type getTypeParam(Class<?> clazz, int index) {
        if (!(clazz.getGenericSuperclass() instanceof ParameterizedType)) {
            throw new IllegalArgumentException("Class does not have type parameters " + clazz.getSimpleName());
        }

        ParameterizedType parameterizedType = (ParameterizedType) clazz.getGenericSuperclass();
        Type[] typeArguments = parameterizedType.getActualTypeArguments();
        return typeArguments[index];
    }

    /**
     *
     * Gets the parent class
     *
     * @param clazz The class
     * @param index The index of the parent class
     * @return The class
     * @param <T> The Type
     */
    public static <T> Class<T> getGenericClass(Class<?> clazz, int index) {
        Type genericSuperclass = clazz.getGenericSuperclass();

        if (genericSuperclass instanceof ParameterizedType) {
            Type[] actualTypeArguments = ((ParameterizedType) genericSuperclass).getActualTypeArguments();

            if (actualTypeArguments.length > index && actualTypeArguments[index] instanceof Class) {
                //noinspection unchecked
                return (Class<T>) actualTypeArguments[index];
            }
        }

        throw new IllegalArgumentException("Unable to determine the generic class.");
    }
}
