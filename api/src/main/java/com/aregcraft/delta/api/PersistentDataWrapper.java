package com.aregcraft.delta.api;

import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataHolder;
import org.bukkit.persistence.PersistentDataType;

import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

public class PersistentDataWrapper {
    private final DeltaPlugin plugin;
    private final PersistentDataContainer container;
    private final Consumer<PersistentDataContainer> action;

    private PersistentDataWrapper(DeltaPlugin plugin, PersistentDataContainer container,
                                  Consumer<PersistentDataContainer> action) {
        this.plugin = plugin;
        this.container = container;
        this.action = action;
    }

    public static PersistentDataWrapper wrap(DeltaPlugin plugin, PersistentDataContainer container,
                                             Consumer<PersistentDataContainer> action) {
        return new PersistentDataWrapper(plugin, container, action);
    }

    public static PersistentDataWrapper wrap(DeltaPlugin plugin, PersistentDataHolder holder) {
        return wrap(plugin, holder.getPersistentDataContainer());
    }

    public static PersistentDataWrapper wrap(DeltaPlugin plugin, PersistentDataHolder holder,
                                             Consumer<PersistentDataContainer> action) {
        return wrap(plugin, holder.getPersistentDataContainer(), action);
    }

    public static PersistentDataWrapper wrap(DeltaPlugin plugin, PersistentDataContainer container) {
        return wrap(plugin, container, it -> {});
    }

    public boolean has(String key, Class<?> type) {
        return container.has(new NamespacedKey(plugin, key), new JsonPersistentDataType<>(type));
    }

    public <T> T get(String key, Class<T> type) {
        return container.get(new NamespacedKey(plugin, key), new JsonPersistentDataType<>(type));
    }

    public <T> T getOrElse(String key, T other) {
        @SuppressWarnings("unchecked")
        var type = (Class<T>) other.getClass();
        return has(key, type) ? get(key, type) : other;
    }

    public boolean check(String key, Object value) {
        return Objects.equals(get(key, value.getClass()), value);
    }

    public void setIfAbsent(String key, Object value) {
        if (!has(key, value.getClass())) {
            set(key, value);
        }
    }

    public void set(String key, Object value) {
        var namespacedKey = new NamespacedKey(plugin, key);
        if (value == null) {
            container.remove(namespacedKey);
        } else {
            container.set(namespacedKey, getPersistentDataType(value), value);
        }
        action.accept(container);
    }

    public void setAll(Map<String, Object> map) {
        map.forEach(this::set);
    }

    public void remove(String key) {
        set(key, null);
    }

    @SuppressWarnings("unchecked")
    private <T> PersistentDataType<String, T> getPersistentDataType(Object value) {
        return new JsonPersistentDataType<>((Class<T>) value.getClass());
    }

    public PersistentDataContainer unwrap() {
        return container;
    }

    private class JsonPersistentDataType<T> implements PersistentDataType<String, T> {
        private final Class<T> complexType;

        private JsonPersistentDataType(Class<T> complexType) {
            this.complexType = complexType;
        }

        @Override
        public Class<String> getPrimitiveType() {
            return String.class;
        }

        @Override
        public Class<T> getComplexType() {
            return complexType;
        }

        @Override
        public String toPrimitive(T complex, PersistentDataAdapterContext context) {
            return plugin.getPersistentDataGson().toJson(complex);
        }

        @Override
        public T fromPrimitive(String primitive, PersistentDataAdapterContext context) {
            return plugin.getGson().fromJson(primitive, complexType);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            var that = (JsonPersistentDataType<?>) o;
            return Objects.equals(complexType, that.complexType);
        }

        @Override
        public int hashCode() {
            return Objects.hash(complexType);
        }
    }
}
