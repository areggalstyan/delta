package com.aregcraft.delta.api.entity;

import com.aregcraft.delta.api.entity.selector.CombiningSelector;
import com.aregcraft.delta.api.entity.selector.EntitySelector;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.util.BoundingBox;

import java.util.ArrayList;
import java.util.List;

public class EntityFinder {
    private final World world;
    private final BoundingBox boundingBox;

    private EntityFinder(World world, BoundingBox boundingBox) {
        this.world = world;
        this.boundingBox = boundingBox;
    }

    public static EntityFinder createAtBlock(Block block) {
        return new EntityFinder(block.getWorld(), BoundingBox.of(block));
    }

    public static EntityFinder createAtLocation(Location location, double size) {
        return createAtLocation(location, size, size, size);
    }

    public static EntityFinder createAtLocation(Location location, double x, double y, double z) {
        return new EntityFinder(location.getWorld(), BoundingBox.of(location, x, y, z));
    }

    public <T> List<T> find(Class<T> type, EntitySelector... selectors) {
        return find(selectors).stream().filter(type::isInstance).map(type::cast).toList();
    }

    public List<Entity> find(EntitySelector... selectors) {
        return find(new CombiningSelector(selectors));
    }

    private List<Entity> find(EntitySelector selector) {
        return new ArrayList<>(world.getNearbyEntities(boundingBox, selector));
    }
}
