package com.aregcraft.delta.api;

import com.aregcraft.delta.api.registry.Identifiable;

import java.util.Map;

public class Language implements Identifiable<String> {
    private final String id;
    private final Map<String, String> localizations;

    public Language(String id, Map<String, String> localizations) {
        this.id = id;
        this.localizations = localizations;
    }

    @Override
    public String getId() {
        return id;
    }

    public String getLocalized(String name) {
        return localizations.get(name);
    }
}
