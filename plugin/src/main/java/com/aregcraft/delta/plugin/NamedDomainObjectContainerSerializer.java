package com.aregcraft.delta.plugin;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.gradle.api.NamedDomainObjectContainer;

import java.io.IOException;

@SuppressWarnings("rawtypes")
public class NamedDomainObjectContainerSerializer extends StdSerializer<NamedDomainObjectContainer> {
    protected NamedDomainObjectContainerSerializer() {
        super(NamedDomainObjectContainer.class);
    }

    @Override
    public void serialize(NamedDomainObjectContainer value, JsonGenerator gen, SerializerProvider provider)
            throws IOException {
        provider.defaultSerializeValue(value.getAsMap(), gen);
    }

    @Override
    public boolean isEmpty(SerializerProvider provider, NamedDomainObjectContainer value) {
        return value.isEmpty();
    }
}
