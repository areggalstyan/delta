package com.aregcraft.delta.meta.replacement;

import com.aregcraft.delta.meta.Ability;

import java.nio.file.Path;
import java.util.List;

public class AbilityReplacement extends Replacement {
    public AbilityReplacement(String name) {
        super(name);
    }

    @Override
    public String getValue(Path resourcesPath, List<Ability> bases) {
        var builder = new StringBuilder("| Name | Description |\n")
                .append("| --- | --- |");
        bases.stream().map(this::getRow).forEach(builder::append);
        return builder.toString();
    }

    private String getRow(Ability ability) {
        return "\n| " + ability.name() + " | " + ability.description() + " |";
    }
}
