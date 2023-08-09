package com.aregcraft.delta.api.json.adapter;

import com.aregcraft.delta.api.json.annotation.JsonAdapterFor;
import com.google.gson.*;
import org.bukkit.NamespacedKey;

import java.lang.reflect.Type;

@JsonAdapterFor(NamespacedKey.class)
public class NamespacedKeyAdapter implements JsonDeserializer<NamespacedKey>, JsonSerializer<NamespacedKey> {
    @Override
    public NamespacedKey deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
        return NamespacedKey.fromString(json.getAsString());
    }

    @Override
    public JsonElement serialize(NamespacedKey src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(src.toString());
    }
}
