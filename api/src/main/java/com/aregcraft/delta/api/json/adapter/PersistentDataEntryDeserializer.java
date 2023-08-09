package com.aregcraft.delta.api.json.adapter;

import com.aregcraft.delta.api.json.JsonReader;
import com.aregcraft.delta.api.json.annotation.JsonAdapterFor;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;

import java.lang.reflect.Type;

@JsonAdapterFor(PersistentDataEntry.class)
public class PersistentDataEntryDeserializer implements JsonDeserializer<PersistentDataEntry> {
    @Override
    public PersistentDataEntry deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
        var reader = new JsonReader(context, json);
        var type = reader.get("type", PrimitivePersistentDataType.class);
        return new PersistentDataEntry(type,
                reader.get("value", type.getPersistentDataType().getComplexType()));
    }
}
