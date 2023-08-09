package com.aregcraft.delta.api.json.adapter;

import com.aregcraft.delta.api.json.annotation.JsonHierarchyAdapterFor;
import com.google.gson.*;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.lang.reflect.Type;
import java.util.UUID;

@JsonHierarchyAdapterFor(World.class)
public class WorldAdapter implements JsonDeserializer<World>, JsonSerializer<World> {
    @Override
    public World deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
        return Bukkit.getWorld(context.<UUID>deserialize(json, UUID.class));
    }

    @Override
    public JsonElement serialize(World src, Type typeOfSrc, JsonSerializationContext context) {
        return context.serialize(src.getUID());
    }
}
