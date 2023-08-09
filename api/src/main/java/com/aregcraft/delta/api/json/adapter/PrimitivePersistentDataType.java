package com.aregcraft.delta.api.json.adapter;

import com.aregcraft.delta.api.item.ItemWrapper;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataType;

import java.util.Arrays;

public enum PrimitivePersistentDataType {
    BYTE(PersistentDataType.BYTE),
    SHORT(PersistentDataType.SHORT),
    INTEGER(PersistentDataType.INTEGER),
    LONG(PersistentDataType.LONG),
    FLOAT(PersistentDataType.FLOAT),
    DOUBLE(PersistentDataType.DOUBLE),
    STRING(PersistentDataType.STRING),
    BYTE_ARRAY(PersistentDataType.BYTE_ARRAY),
    INTEGER_ARRAY(PersistentDataType.INTEGER_ARRAY),
    LONG_ARRAY(PersistentDataType.LONG_ARRAY),
    TAG_CONTAINER_ARRAY(PersistentDataType.TAG_CONTAINER_ARRAY),
    TAG_CONTAINER(PersistentDataType.TAG_CONTAINER);

    private final PersistentDataType<?, Object> persistentDataType;

    @SuppressWarnings("unchecked")
    PrimitivePersistentDataType(PersistentDataType<?, ?> persistentDataType) {
        this.persistentDataType = (PersistentDataType<?, Object>) persistentDataType;
    }

    public static PrimitivePersistentDataType get(ItemWrapper item, NamespacedKey key) {
        return Arrays.stream(values())
                .filter(it -> item.getMeta().getPersistentDataContainer().has(key, it.persistentDataType))
                .findAny().orElseThrow();
    }

    public PersistentDataType<?, Object> getPersistentDataType() {
        return persistentDataType;
    }
}
