package com.aregcraft.delta.api.entity.selector;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

public class TypeSelector implements EntitySelector {
    private final EntityType type;

    public TypeSelector(EntityType type) {
        this.type = type;
    }

    @Override
    public boolean test(Entity entity) {
        return entity.getType() == type;
    }
}
