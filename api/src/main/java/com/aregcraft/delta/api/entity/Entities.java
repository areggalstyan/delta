package com.aregcraft.delta.api.entity;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Projectile;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.util.Vector;

import java.util.Objects;

public class Entities {
    private Entities() {
    }

    @SuppressWarnings("unchecked")
    public static <T extends Entity> T spawnEntity(EntityType type, Location location) {
        return (T) Objects.requireNonNull(location.getWorld()).spawnEntity(location, type);
    }

    public static <T extends Entity> T spawnEntity(Class<T> type, Location location) {
        return Objects.requireNonNull(location.getWorld()).spawn(location, type);
    }

    public static void spawnParticle(Particle particle, Location location) {
        Objects.requireNonNull(location.getWorld()).spawnParticle(particle, location, 0);
    }

    public static Projectile launchProjectile(ProjectileSource source, EntityType type, Vector velocity) {
        return source.launchProjectile(getProjectileClass(type), velocity);
    }

    public static Projectile launchProjectile(ProjectileSource source, EntityType type) {
        return source.launchProjectile(getProjectileClass(type));
    }

    @SuppressWarnings("unchecked")
    private static Class<? extends Projectile> getProjectileClass(EntityType type) {
        return (Class<? extends Projectile>) type.getEntityClass();
    }

    public static void addPotionEffect(LivingEntity entity, PotionEffectType type, int duration, int amplifier,
                                       boolean hideParticles) {
        entity.addPotionEffect(new PotionEffect(type, duration, amplifier, !hideParticles, !hideParticles));
    }
}
