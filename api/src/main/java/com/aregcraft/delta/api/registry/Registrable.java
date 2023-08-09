package com.aregcraft.delta.api.registry;

import com.aregcraft.delta.api.DeltaPlugin;

public interface Registrable<T extends DeltaPlugin> {
    void register(T plugin);

    default void unregister(T plugin) {
    }

    @SuppressWarnings("unchecked")
    default void registerUnchecked(DeltaPlugin plugin) {
        register((T) plugin);
    }

    @SuppressWarnings("unchecked")
    default void unregisterUnchecked(DeltaPlugin plugin) {
        unregister((T) plugin);
    }
}
