package com.aregcraft.delta.api.json.adapter;

import com.aregcraft.delta.api.json.JsonReader;
import com.aregcraft.delta.api.json.annotation.JsonAdapterFor;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.EquipmentSlot;

import java.lang.reflect.Type;
import java.util.UUID;

@JsonAdapterFor(AttributeModifier.class)
public class AttributeModifierDeserializer implements JsonDeserializer<AttributeModifier> {
    @Override
    public AttributeModifier deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
        var reader = new JsonReader(context, json);
        return new AttributeModifier(reader.getOrElse("uuid", UUID.class, UUID.randomUUID()),
                reader.getString("name"),
                reader.getDouble("amount"),
                reader.getOrElse("operation", AttributeModifier.Operation.class,
                        AttributeModifier.Operation.ADD_NUMBER),
                reader.get("slot", EquipmentSlot.class));
    }
}
