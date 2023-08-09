package com.aregcraft.delta.api.json.adapter;

import com.aregcraft.delta.api.item.ItemWrapper;
import org.bukkit.NamespacedKey;

public class PersistentDataEntry {
    private final PrimitivePersistentDataType type;
    private final Object value;

    public PersistentDataEntry(ItemWrapper item, NamespacedKey key) {
        type = PrimitivePersistentDataType.get(item, key);
        value = item.getMeta().getPersistentDataContainer().get(key, type.getPersistentDataType());
    }

    public PersistentDataEntry(PrimitivePersistentDataType type, Object value) {
        this.type = type;
        this.value = value;
    }

    public void set(ItemWrapper item, NamespacedKey key) {
        item.editMeta(it -> it.getPersistentDataContainer().set(key, type.getPersistentDataType(), value));
    }
}
