package com.aregcraft.delta.api;

import java.util.function.Supplier;

public class LazyValue<T> implements Supplier<T> {
    private final Supplier<T> supplier;
    private T value;

    private LazyValue(Supplier<T> supplier) {
        this.supplier = supplier;
    }

    public static <T> LazyValue<T> of(Supplier<T> supplier) {
        return new LazyValue<>(supplier);
    }

    @Override
    public T get() {
        if (value == null) {
            value = supplier.get();
        }
        return value;
    }
}
