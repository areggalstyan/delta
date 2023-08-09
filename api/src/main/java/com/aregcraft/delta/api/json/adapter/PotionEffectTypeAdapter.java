package com.aregcraft.delta.api.json.adapter;

import com.aregcraft.delta.api.json.annotation.JsonAdapterFor;
import com.google.gson.*;
import org.bukkit.NamespacedKey;
import org.bukkit.potion.PotionEffectType;

import java.lang.reflect.Type;

@JsonAdapterFor(PotionEffectType.class)
public class PotionEffectTypeAdapter implements JsonDeserializer<PotionEffectType>, JsonSerializer<PotionEffectType> {
    @Override
    public PotionEffectType deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
        return PotionEffectType.getByKey(context.deserialize(json, NamespacedKey.class));
    }

    @Override
    public JsonElement serialize(PotionEffectType src, Type typeOfSrc, JsonSerializationContext context) {
        return context.serialize(src.getKey());
    }
}
