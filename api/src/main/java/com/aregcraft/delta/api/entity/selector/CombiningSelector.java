package com.aregcraft.delta.api.entity.selector;

import org.bukkit.entity.Entity;

import java.util.Set;

public class CombiningSelector implements EntitySelector {
    private final Set<EntitySelector> selectors;

    public CombiningSelector(EntitySelector... selectors) {
        this.selectors = Set.of(selectors);
    }

    @Override
    public boolean test(Entity entity) {
        return selectors.stream().allMatch(it -> it.test(entity));
    }
}
