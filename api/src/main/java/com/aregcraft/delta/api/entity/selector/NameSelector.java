package com.aregcraft.delta.api.entity.selector;

import org.bukkit.entity.Entity;

public class NameSelector implements EntitySelector {
    private final String name;

    public NameSelector(String name) {
        this.name = name;
    }

    @Override
    public boolean test(Entity entity) {
        return entity.getName().equals(name);
    }
}
