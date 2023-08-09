package com.aregcraft.delta.api.entity;

import com.aregcraft.delta.api.DeltaPlugin;
import com.aregcraft.delta.api.PersistentDataWrapper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Projectile;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;

public class ProjectileBuilder {
    private final Map<String, Object> persistentData = new HashMap<>();
    private ProjectileSource source;
    private EntityType type;
    private Vector velocity;
    private boolean bounce;

    public ProjectileBuilder persistentData(String key, Object value) {
        persistentData.put(key, value);
        return this;
    }

    public ProjectileBuilder source(ProjectileSource source) {
        this.source = source;
        return this;
    }

    public ProjectileBuilder type(EntityType type) {
        this.type = type;
        return this;
    }

    public ProjectileBuilder velocity(Vector velocity) {
        this.velocity = velocity.clone();
        return this;
    }

    public ProjectileBuilder direction(Entity entity) {
        velocity.multiply(entity.getLocation().getDirection());
        return this;
    }

    public ProjectileBuilder bounce(boolean bounce) {
        this.bounce = bounce;
        return this;
    }

    @SuppressWarnings("unchecked")
    public <T extends Projectile> T build(DeltaPlugin plugin) {
        var projectile = build();
        PersistentDataWrapper.wrap(plugin, projectile).setAll(persistentData);
        return (T) projectile;
    }

    @SuppressWarnings("unchecked")
    public <T extends Projectile> T build() {
        var projectile = Entities.launchProjectile(source, type, velocity);
        projectile.setBounce(bounce);
        return (T) projectile;
    }
}
