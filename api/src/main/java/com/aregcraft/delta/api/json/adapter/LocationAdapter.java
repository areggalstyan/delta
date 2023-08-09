package com.aregcraft.delta.api.json.adapter;

import com.aregcraft.delta.api.json.JsonReader;
import com.aregcraft.delta.api.json.annotation.JsonAdapterFor;
import com.google.gson.*;
import org.bukkit.Location;
import org.bukkit.World;

import java.lang.reflect.Type;

@JsonAdapterFor(Location.class)
public class LocationAdapter implements JsonDeserializer<Location>, JsonSerializer<Location> {
    @Override
    public Location deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        var reader = new JsonReader(context, json);
        return new Location(reader.get("world", World.class),
                reader.getDouble("x"),
                reader.getDouble("y"),
                reader.getDouble("z"),
                reader.getFloatOrElse("yaw", 0),
                reader.getFloatOrElse("pitch", 0));
    }

    @Override
    public JsonElement serialize(Location src, Type typeOfSrc, JsonSerializationContext context) {
        var obj = new JsonObject();
        obj.add("world", context.serialize(src.getWorld()));
        obj.addProperty("x", src.getX());
        obj.addProperty("y", src.getY());
        obj.addProperty("z", src.getZ());
        obj.addProperty("yaw", src.getYaw());
        obj.addProperty("pitch", src.getPitch());
        return obj;
    }
}
