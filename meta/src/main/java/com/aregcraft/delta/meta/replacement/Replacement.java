package com.aregcraft.delta.meta.replacement;

import com.aregcraft.delta.meta.Ability;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.regex.Pattern;

public abstract class Replacement {
    private final String name;
    private final Pattern pattern;

    protected Replacement(String name) {
        this.name = name;
        pattern = Pattern.compile(getTags("(?s).*"));
    }

    public String replace(String string, Path resourcesPath, List<Ability> abilities) {
        return pattern.matcher(string).replaceAll(it -> {
            try {
                return getTags('\n' + getValue(resourcesPath, abilities) + '\n');
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private String getTags(String value) {
        return "<!-- <%s> -->%s<!-- </%1$s> -->".formatted(name.toLowerCase(), value);
    }

    protected abstract String getValue(Path resourcePath, List<Ability> abilities) throws IOException;
}
