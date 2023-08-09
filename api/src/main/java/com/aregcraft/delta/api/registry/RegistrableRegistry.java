package com.aregcraft.delta.api.registry;

import com.aregcraft.delta.api.json.JsonConfigurationLoader;

public class RegistrableRegistry<T, E extends Identifiable<T> & Registrable<?>> extends Registry<T, E> {
    public RegistrableRegistry(String name, Class<E> type, JsonConfigurationLoader configurationLoader) {
        super(name, type, configurationLoader);
    }

    public RegistrableRegistry(String name, Class<E> type, JsonConfigurationLoader configurationLoader,
                               Runnable hook) {
        super(name, type, configurationLoader, hook);
    }

    @Override
    protected void loadAll() {
        super.loadAll();
        getValues().forEach(it -> it.registerUnchecked(plugin));
    }

    @Override
    public void invalidateAll() {
        getValues().forEach(it -> it.unregisterUnchecked(plugin));
        super.invalidateAll();
    }
}
