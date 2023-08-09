package com.aregcraft.delta.plugin;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.gradle.api.provider.Provider;

import java.io.IOException;
import java.util.Collection;

@SuppressWarnings("rawtypes")
public class ProviderSerializer extends StdSerializer<Provider> {
    protected ProviderSerializer() {
        super(Provider.class);
    }

    @Override
    public void serialize(Provider value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        provider.defaultSerializeValue(value.get(), gen);
    }

    @Override
    public boolean isEmpty(SerializerProvider provider, Provider value) {
        return !value.isPresent() || value.get() instanceof Collection it && it.isEmpty();
    }
}
