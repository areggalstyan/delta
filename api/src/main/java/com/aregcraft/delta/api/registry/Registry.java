package com.aregcraft.delta.api.registry;

import com.aregcraft.delta.api.DeltaPlugin;
import com.aregcraft.delta.api.json.JsonConfigurationLoader;
import com.aregcraft.delta.api.log.Crash;
import com.aregcraft.delta.api.log.Error;
import com.google.gson.reflect.TypeToken;
import org.bukkit.event.Listener;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Registry<T, E extends Identifiable<T>> {
    protected final DeltaPlugin plugin;
    private final String name;
    private final Class<E> type;
    private final JsonConfigurationLoader configurationLoader;
    private final Path path;
    private Map<T, E> cache;

    public Registry(String name, Class<E> type, JsonConfigurationLoader configurationLoader) {
        this(name, type, configurationLoader, () -> {});
    }

    public Registry(String name, Class<E> type, JsonConfigurationLoader configurationLoader, Runnable hook) {
        this.name = name;
        this.type = type;
        this.configurationLoader = configurationLoader;
        plugin = configurationLoader.getPlugin();
        path = plugin.getDataFolder().toPath().resolve(name);
        if (Files.notExists(path) || isPathEmpty()) {
            plugin.registerEnableHook(this::initialize);
        }
        plugin.registerEnableHook(this::loadAll);
        plugin.registerEnableHook(hook);
    }

    private boolean isPathEmpty() {
        try (var list = Files.list(path)) {
            return list.findAny().isEmpty();
        } catch (IOException e) {
            Crash.Default.IO.withThrowable(e).log(plugin);
        }
        return true;
    }

    private void initialize() {
        try {
            Files.createDirectories(path);
        } catch (IOException e) {
            Crash.Default.IO.withThrowable(e).log(plugin);
        }
        getDefaults().forEach(it -> configurationLoader.get(it, type));
    }

    private List<String> getDefaults() {
        return plugin.getResourcesReflections().getResources(".*\\.json").stream()
                .filter(it -> it.startsWith(name + '/'))
                .map(it -> it.substring(0, it.lastIndexOf('.')))
                .toList();
    }

    @SuppressWarnings("unchecked")
    private TypeToken<List<E>> getListType() {
        return (TypeToken<List<E>>) TypeToken.getParameterized(List.class, type);
    }

    public Set<T> getIds() {
        return getAsMap().keySet();
    }

    public List<E> getValues() {
        return new ArrayList<>(getAsMap().values());
    }

    public E findAny(T id) {
        return getAsMap().get(id);
    }

    public Map<T, E> getAsMap() {
        if (cache == null) {
            loadAll();
        }
        return cache;
    }

    protected void loadAll() {
        try (var paths = Files.list(path)) {
            cache = paths.map(it -> name + File.separator + it.getFileName())
                    .map(it -> it.substring(0, it.lastIndexOf('.')))
                    .map(it -> configurationLoader.get(it, type))
                    .collect(Collectors.toMap(Identifiable::getId, Function.identity(), this::mergeDuplicates));
        } catch (IOException e) {
            Crash.Default.IO.withThrowable(e).log(plugin);
        }
    }

    private E mergeDuplicates(E left, E right) {
        Error.Default.LOAD_DUPLICATE_ID.log(plugin, left.getId(), name);
        return left;
    }

    public void invalidateAll() {
        getValues().stream()
                .filter(Listener.class::isInstance)
                .map(Listener.class::cast)
                .forEach(plugin::unregisterListener);
        loadAll();
    }
}
