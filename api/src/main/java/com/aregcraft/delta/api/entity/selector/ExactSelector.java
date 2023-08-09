package com.aregcraft.delta.api.entity.selector;

import org.bukkit.entity.Entity;

public class ExactSelector implements EntitySelector {
    private final Entity exact;

    public ExactSelector(Entity exact) {
        this.exact = exact;
    }

    @Override
    public boolean test(Entity entity) {
        return entity.equals(exact);
    }
}
