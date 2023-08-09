package com.aregcraft.delta.api.json.adapter;

import com.aregcraft.delta.api.util.Classes;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.lang.reflect.RecordComponent;
import java.util.Arrays;
import java.util.stream.Collectors;

public class RecordAdapterFactory implements TypeAdapterFactory {
    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
        @SuppressWarnings("unchecked")
        var rawType = (Class<T>) type.getRawType();
        if (!rawType.isRecord()) {
            return null;
        }
        return new TypeAdapter<>() {
            @Override
            public void write(JsonWriter out, T value) throws IOException {
                gson.getDelegateAdapter(RecordAdapterFactory.this, type).write(out, value);
            }

            @Override
            public T read(JsonReader in) throws IOException {
                if (in.peek() == JsonToken.NULL) {
                    in.nextNull();
                    return null;
                }
                var components = rawType.getRecordComponents();
                var names = Arrays.stream(components).map(RecordComponent::getName).toList();
                var types = Arrays.stream(components)
                        .collect(Collectors.toMap(RecordComponent::getName, RecordComponent::getGenericType));
                var values = Arrays.asList(new Object[names.size()]);
                in.beginObject();
                while (in.hasNext()) {
                    var name = in.nextName();
                    values.set(names.indexOf(name), gson.getAdapter(TypeToken.get(types.get(name))).read(in));
                }
                in.endObject();
                return Classes.newInstance(Classes.getRecordConstructor(rawType), values);
            }
        };
    }
}
