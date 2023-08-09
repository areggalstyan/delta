package com.aregcraft.delta.api.entity.selector;

import com.aregcraft.delta.api.DeltaPlugin;
import com.aregcraft.delta.api.PersistentDataWrapper;
import org.bukkit.entity.Entity;

public class PersistentDataSelector implements EntitySelector {
    private final DeltaPlugin plugin;
    private final String key;
    private final Object value;

    public PersistentDataSelector(DeltaPlugin plugin, String key, Object value) {
        this.plugin = plugin;
        this.key = key;
        this.value = value;
    }

    @Override
    public boolean test(Entity entity) {
        return PersistentDataWrapper.wrap(plugin, entity).check(key, value);
    }
}
