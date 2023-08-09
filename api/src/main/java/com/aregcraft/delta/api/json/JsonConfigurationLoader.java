package com.aregcraft.delta.api.json;

import com.aregcraft.delta.api.DeltaPlugin;
import com.aregcraft.delta.api.InjectPlugin;
import com.aregcraft.delta.api.json.annotation.JsonConfiguration;
import com.aregcraft.delta.api.log.Crash;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

public class JsonConfigurationLoader {
    private final DeltaPlugin plugin;
    private final Map<TypeToken<?>, String> names;
    private final LoadingCache<PluginCacheKey, Object> cache;

    public JsonConfigurationLoader(DeltaPlugin plugin) {
        this(plugin, new HashMap<>());
    }

    private JsonConfigurationLoader(DeltaPlugin plugin, Map<TypeToken<?>, String> names) {
        this.plugin = plugin;
        this.names = names;
        plugin.getReflections()
                .getTypesAnnotatedWith(JsonConfiguration.class)
                .forEach(this::addConfiguration);
        cache = CacheBuilder.newBuilder().build(new PluginCacheLoader());
    }

    public DeltaPlugin getPlugin() {
        return plugin;
    }

    private void addConfiguration(Class<?> type) {
        names.put(TypeToken.get(type), type.getAnnotation(JsonConfiguration.class).value());
    }

    public static Builder builder() {
        return new Builder();
    }

    public void invalidate(String name, Class<?> type) {
        invalidate(name, TypeToken.get(type));
    }

    public void invalidate(Class<?> type) {
        invalidate(TypeToken.get(type));
    }

    public void invalidate(String name, TypeToken<?> type) {
        cache.invalidate(new PluginCacheKey(name, type));
    }

    public void invalidate(TypeToken<?> type) {
        invalidate(names.get(type), type);
    }

    public void invalidateAll() {
        cache.invalidateAll();
    }

    public <T> T get(String name, Class<T> type) {
        return get(name, TypeToken.get(type));
    }

    public <T> T get(Class<T> type) {
        return get(TypeToken.get(type));
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String name, TypeToken<T> type) {
        try {
            return (T) cache.get(new PluginCacheKey(name, type));
        } catch (ExecutionException e) {
            Crash.Default.IO.withThrowable(e).log(plugin);
        }
        return null;
    }

    public <T> T get(TypeToken<T> type) {
        return get(names.get(type), type);
    }

    public void set(String name, Object value) {
        var key = new PluginCacheKey(name, TypeToken.get(value.getClass()));
        cache.put(key, Optional.of(value));
        try (var writer = Files.newBufferedWriter(key.getPath())) {
            plugin.getGson().toJson(value, writer);
        } catch (Exception e) {
            Crash.Default.IO.withThrowable(e).log(plugin);
        }
    }

    public void set(Object value) {
        set(names.get(TypeToken.get(value.getClass())), value);
    }

    public static class Builder {
        private final Map<TypeToken<?>, String> names = new HashMap<>();
        @InjectPlugin
        private DeltaPlugin plugin;

        private Builder() {
        }

        public Builder name(Class<?> type, String name) {
            return name(TypeToken.get(type), name);
        }

        public Builder name(TypeToken<?> type, String name) {
            names.put(type, name);
            return this;
        }

        public Builder plugin(DeltaPlugin plugin) {
            this.plugin = plugin;
            return this;
        }

        public JsonConfigurationLoader build() {
            return new JsonConfigurationLoader(plugin, names);
        }
    }

    private class PluginCacheKey {
        private final String name;
        private final TypeToken<?> type;

        private PluginCacheKey(String name, TypeToken<?> type) {
            this.name = name + ".json";
            this.type = type;
        }

        public Path getPath() {
            return plugin.getDataFolder().toPath().resolve(name);
        }

        public InputStream getResource() {
            return plugin.getResource(name);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            var that = (PluginCacheKey) o;
            return Objects.equals(name, that.name) && Objects.equals(type, that.type);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name, type);
        }
    }

    private class PluginCacheLoader extends CacheLoader<PluginCacheKey, Object> {
        @Override
        public Object load(PluginCacheKey key) throws IOException {
            var path = key.getPath();
            if (Files.notExists(path)) {
                Files.createDirectories(path.getParent());
                Files.copy(key.getResource(), path);
            }
            try (var reader = Files.newBufferedReader(path)) {
                return plugin.getGson().fromJson(reader, key.type.getType());
            } catch (Exception e) {
                Crash.Default.JSON_SYNTAX.withThrowable(e).log(plugin, key.name);
            }
            return null;
        }
    }
}
