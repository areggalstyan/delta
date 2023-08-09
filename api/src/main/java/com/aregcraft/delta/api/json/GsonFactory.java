package com.aregcraft.delta.api.json;

import com.aregcraft.delta.api.DeltaPlugin;
import com.aregcraft.delta.api.json.annotation.JsonAdapterFor;
import com.aregcraft.delta.api.json.annotation.JsonHierarchyAdapterFor;
import com.aregcraft.delta.api.util.Classes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapterFactory;

public class GsonFactory {
    private final DeltaPlugin plugin;
    private final GsonBuilder builder;

    public GsonFactory(DeltaPlugin plugin) {
        this.plugin = plugin;
        builder = new GsonBuilder()
                .enableComplexMapKeySerialization();
        registerTypeAdapterFactories();
        registerTypeHierarchyAdapters();
        registerTypeAdapters();
    }

    public Gson create() {
        return builder.create();
    }

    public Gson createWithPrettyPrinting() {
        return builder.setPrettyPrinting().create();
    }

    private void registerTypeAdapterFactories() {
        plugin.getReflections()
                .getSubTypesOf(TypeAdapterFactory.class)
                .forEach(this::registerTypeAdapterFactory);
    }

    private void registerTypeAdapterFactory(Class<? extends TypeAdapterFactory> type) {
        builder.registerTypeAdapterFactory(Classes.newInstanceWithPlugin(type, plugin));
    }

    private void registerTypeHierarchyAdapters() {
        plugin.getReflections()
                .getTypesAnnotatedWith(JsonHierarchyAdapterFor.class)
                .forEach(this::registerTypeHierarchyAdapter);
    }

    private void registerTypeHierarchyAdapter(Class<?> type) {
        builder.registerTypeHierarchyAdapter(type.getAnnotation(JsonHierarchyAdapterFor.class).value(),
                Classes.newInstanceWithPlugin(type, plugin));
    }

    private void registerTypeAdapters() {
        plugin.getReflections()
                .getTypesAnnotatedWith(JsonAdapterFor.class)
                .forEach(this::registerTypeAdapter);
    }

    private void registerTypeAdapter(Class<?> type) {
        builder.registerTypeAdapter(type.getAnnotation(JsonAdapterFor.class).value(),
                Classes.newInstanceWithPlugin(type, plugin));
    }
}
