package com.aregcraft.delta.meta.replacement;

import com.aregcraft.delta.meta.Ability;
import com.aregcraft.delta.meta.Property;

import java.nio.file.Path;
import java.util.List;

public class BaseReplacement extends Replacement {
    public BaseReplacement(String name) {
        super(name);
    }

    @Override
    public String getValue(Path resourcesPath, List<Ability> bases) {
        var builder = new StringBuilder();
        bases.stream().map(this::getAbility).forEach(builder::append);
        builder.setLength(builder.length() - 2);
        return builder.toString();
    }

    private String getAbility(Ability ability) {
        var builder = new StringBuilder("#### ").append(ability.name()).append('\n')
                .append('\n')
                .append("| Name | Type | Description |\n")
                .append("| --- | --- | --- |\n");
        ability.properties().stream().map(this::getRow).forEach(builder::append);
        return builder.append('\n').toString();
    }

    private String getRow(Property property) {
        return "| " + property.name() + " | `" + property.type() + "` | " + property.description() + " |\n";
    }
}
