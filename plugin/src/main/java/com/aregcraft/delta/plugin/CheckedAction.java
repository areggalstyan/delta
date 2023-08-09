package com.aregcraft.delta.plugin;

import org.gradle.api.Action;

@FunctionalInterface
public interface CheckedAction<T> extends Action<T> {
    void executeChecked(T t) throws Exception;

    default void execute(T t) {
        try {
            executeChecked(t);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
