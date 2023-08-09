package com.aregcraft.delta.api.util;

import java.util.Collection;
import java.util.Map;
import java.util.function.BiConsumer;

public class CollectionMaps {
    private CollectionMaps() {
    }

    public static <K, V> void forEach(Map<? extends K, ? extends Collection<? extends V>> map,
                                      BiConsumer<? super K, ? super V> action) {
        map.forEach((key, value) -> value.forEach(it -> action.accept(key, it)));
    }
}
