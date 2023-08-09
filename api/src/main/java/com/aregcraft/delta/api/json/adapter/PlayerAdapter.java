package com.aregcraft.delta.api.json.adapter;

import com.aregcraft.delta.api.json.annotation.JsonHierarchyAdapterFor;
import com.google.gson.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.Type;
import java.util.UUID;

@JsonHierarchyAdapterFor(Player.class)
public class PlayerAdapter implements JsonDeserializer<Player>, JsonSerializer<Player> {
    @Override
    public Player deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
        return Bukkit.getPlayer(context.<UUID>deserialize(json, UUID.class));
    }

    @Override
    public JsonElement serialize(Player src, Type typeOfSrc, JsonSerializationContext context) {
        return context.serialize(src.getUniqueId());
    }
}
