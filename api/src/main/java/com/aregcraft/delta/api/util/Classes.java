package com.aregcraft.delta.api.util;

import com.aregcraft.delta.api.DeltaPlugin;
import com.aregcraft.delta.api.InjectPlugin;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.RecordComponent;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Classes {
    private Classes() {
    }

    public static <T> Constructor<T> getRecordConstructor(Class<T> type) {
        try {
            return type.getConstructor(Arrays.stream(type.getRecordComponents())
                    .map(RecordComponent::getType).toArray(Class<?>[]::new));
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T newInstance(Constructor<T> constructor, List<Object> args) {
        try {
            return constructor.newInstance(args.toArray());
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T newUncheckedInstance(Constructor<?> constructor, List<Object> args) {
        return (T) newInstance(constructor, args);
    }

    public static <T> T newInstance(Class<T> type) {
        try {
            return newInstance(type.getConstructor(), Collections.emptyList());
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T newUncheckedInstance(Class<?> type) {
        return (T) newInstance(type);
    }

    public static <T> T newInstanceWithPlugin(Class<T> type, DeltaPlugin plugin) {
        var obj = newInstance(type);
        setPluginField(obj, plugin);
        return obj;
    }

    @SuppressWarnings("unchecked")
    public static <T> T newUncheckedInstanceWithPlugin(Class<?> type, DeltaPlugin plugin) {
        return (T) newInstanceWithPlugin(type, plugin);
    }

    public static void setPluginField(Object obj, DeltaPlugin plugin) {
        setPluginField(obj.getClass(), obj, plugin);
    }

    public static <T> void setPluginField(Class<? extends T> type, T obj, DeltaPlugin plugin) {
        Arrays.stream(type.getDeclaredFields())
                .filter(it -> it.isAnnotationPresent(InjectPlugin.class))
                .forEach(it -> setField(obj, it, plugin));
    }

    public static void setField(Object obj, String name, Object value) {
        setField(obj.getClass(), obj, name, value);
    }

    public static <T> void setField(Class<? extends T> type, T obj, String name, Object value) {
        try {
            setField(obj, type.getDeclaredField(name), value);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    public static void setField(Object obj, Field field, Object value) {
        try {
            field.setAccessible(true);
            field.set(obj, value);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> Class<T> getClass(String name) {
        try {
            return (Class<T>) Class.forName(name);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
