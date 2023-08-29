package com.aregcraft.delta.api.util;

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.potion.PotionEffectType;

import java.lang.reflect.InvocationTargetException;

public class Compatibility {
    private static final BiMap<String, String> POTION_EFFECT_TYPE_NAME_TO_KEY = ImmutableBiMap
            .<String, String>builder()
            .put("SLOW", "slowness")
            .put("FAST_DIGGING", "haste")
            .put("SLOW_DIGGING", "mining_fatigue")
            .put("INCREASE_DAMAGE", "strength")
            .put("HEAL", "instant_health")
            .put("HARM", "instant_damage")
            .put("JUMP", "jump_boost")
            .put("CONFUSION", "nausea")
            .put("DAMAGE_RESISTANCE", "resistance")
            .build();

    public static PotionEffectType getPotionEffectTypeByKey(NamespacedKey namespacedKey) {
        var key = namespacedKey.getKey();
        return PotionEffectType.getByName(POTION_EFFECT_TYPE_NAME_TO_KEY.inverse().getOrDefault(key, key));
    }

    public static NamespacedKey getPotionEffectTypeKey(PotionEffectType type) {
        var name = type.getName();
        return NamespacedKey.minecraft(POTION_EFFECT_TYPE_NAME_TO_KEY.getOrDefault(name, name).toLowerCase());
    }

    public static void setEntityVisualFire(Entity entity, boolean fire) {
        try {
            entity.getClass().getMethod("setVisualFire", boolean.class).invoke(entity, fire);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException ignored) {
        }
    }
}
