package com.aregcraft.delta.api.entity.selector;

import org.bukkit.entity.Entity;

public class ExcludingSelector implements EntitySelector {
    private final Entity exclude;

    public ExcludingSelector(Entity exclude) {
        this.exclude = exclude;
    }

    @Override
    public boolean test(Entity entity) {
        return !entity.equals(exclude);
    }
}
