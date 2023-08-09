package com.aregcraft.delta.meta.replacement;

import com.aregcraft.delta.meta.Ability;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class JsonReplacement extends Replacement {
    private final String name;

    public JsonReplacement(String name) {
        super(name + "_json");
        this.name = name;
    }

    @Override
    public String getValue(Path resourcesPath, List<Ability> abilities) throws IOException {
        return "```json\n" + Files.readString(resourcesPath.resolve(name + ".json")) + "\n```";
    }
}
