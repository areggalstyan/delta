package com.aregcraft.delta.api.json.adapter;

import com.aregcraft.delta.api.json.annotation.JsonHierarchyAdapterFor;
import com.google.gson.*;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;

import java.lang.reflect.Type;

@JsonHierarchyAdapterFor(Enchantment.class)
public class EnchantmentHierarchyAdapter implements JsonDeserializer<Enchantment>, JsonSerializer<Enchantment> {
    @Override
    public Enchantment deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
        return Enchantment.getByKey(context.deserialize(json, NamespacedKey.class));
    }

    @Override
    public JsonElement serialize(Enchantment src, Type typeOfSrc, JsonSerializationContext context) {
        return context.serialize(src.getKey());
    }
}
