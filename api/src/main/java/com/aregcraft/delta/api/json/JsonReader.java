package com.aregcraft.delta.api.json;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.function.Consumer;
import java.util.function.DoubleConsumer;
import java.util.function.IntConsumer;
import java.util.function.LongConsumer;

public class JsonReader {
    private final JsonDeserializationContext context;
    private final JsonObject obj;

    public JsonReader(JsonDeserializationContext context, JsonElement json) {
        this.context = context;
        this.obj = json.getAsJsonObject();
    }

    public String getString(String name) {
        return obj.get(name).getAsString();
    }

    public byte getByte(String name) {
        return obj.get(name).getAsByte();
    }

    public short getShort(String name) {
        return obj.get(name).getAsShort();
    }

    public int getInt(String name) {
        return obj.get(name).getAsInt();
    }

    public long getLong(String name) {
        return obj.get(name).getAsLong();
    }

    public float getFloat(String name) {
        return obj.get(name).getAsFloat();
    }

    public double getDouble(String name) {
        return obj.get(name).getAsDouble();
    }

    public boolean getBoolean(String name) {
        return obj.get(name).getAsBoolean();
    }

    public JsonObject getObject(String name) {
        return obj.getAsJsonObject(name);
    }

    public JsonArray getArray(String name) {
        return obj.getAsJsonArray(name);
    }

    public JsonReader getReader(String name) {
        return new JsonReader(context, getObject(name));
    }

    public <T> T get(String name, Class<T> type) {
        return get(name, TypeToken.get(type));
    }

    public <T> T get(String name, TypeToken<T> type) {
        return context.deserialize(obj.get(name), type.getType());
    }

    public String getStringOrElse(String name, String other) {
        return obj.has(name) ? getString(name) : other;
    }

    public byte getByteOrElse(String name, byte other) {
        return obj.has(name) ? getByte(name) : other;
    }

    public short getShortOrElse(String name, short other) {
        return obj.has(name) ? getShort(name) : other;
    }

    public int getIntOrElse(String name, int other) {
        return obj.has(name) ? getInt(name) : other;
    }

    public long getLongOrElse(String name, long other) {
        return obj.has(name) ? getLong(name) : other;
    }

    public float getFloatOrElse(String name, float other) {
        return obj.has(name) ? getFloat(name) : other;
    }

    public double getDoubleOrElse(String name, double other) {
        return obj.has(name) ? getDouble(name) : other;
    }

    public boolean getBooleanOrElse(String name, boolean other) {
        return obj.has(name) ? getBoolean(name) : other;
    }

    public <T> T getOrElse(String name, Class<T> type, T other) {
        return getOrElse(name, TypeToken.get(type), other);
    }

    public <T> T getOrElse(String name, TypeToken<T> type, T other) {
        return obj.has(name) ? get(name, type) : other;
    }

    public void acceptString(String name, Consumer<String> action) {
        if (obj.has(name)) {
            action.accept(getString(name));
        }
    }

    public void acceptByte(String name, Consumer<Byte> action) {
        if (obj.has(name)) {
            action.accept(getByte(name));
        }
    }

    public void acceptShort(String name, Consumer<Short> action) {
        if (obj.has(name)) {
            action.accept(getShort(name));
        }
    }

    public void acceptInt(String name, IntConsumer action) {
        if (obj.has(name)) {
            action.accept(getInt(name));
        }
    }

    public void acceptLong(String name, LongConsumer action) {
        if (obj.has(name)) {
            action.accept(getLong(name));
        }
    }

    public void acceptFloat(String name, Consumer<Float> action) {
        if (obj.has(name)) {
            action.accept(getFloat(name));
        }
    }

    public void acceptDouble(String name, DoubleConsumer action) {
        if (obj.has(name)) {
            action.accept(getDouble(name));
        }
    }

    public void acceptBoolean(String name, Consumer<Boolean> action) {
        if (obj.has(name)) {
            action.accept(getBoolean(name));
        }
    }

    public <T> void accept(String name, Class<T> type, Consumer<T> action) {
        accept(name, TypeToken.get(type), action);
    }

    public <T> void accept(String name, TypeToken<T> type, Consumer<T> action) {
        if (obj.has(name)) {
            action.accept(get(name, type));
        }
    }

    public <T> T deserialize(JsonElement json, Class<T> type) {
        return context.deserialize(json, type);
    }
}
