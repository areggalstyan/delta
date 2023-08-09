package com.aregcraft.delta.meta;

import jdk.javadoc.doclet.Doclet;

import java.util.List;
import java.util.function.Consumer;

public class StandardOption implements Doclet.Option {
    private final Consumer<String> action;
    private final List<String> names;

    private StandardOption(Consumer<String> action, String... names) {
        this.action = action;
        this.names = List.of(names);
    }

    public static StandardOption noArgs(String... names) {
        return new StandardOption(null, names);
    }

    public static StandardOption oneArg(String... names) {
        return oneArg(it -> {}, names);
    }

    public static StandardOption oneArg(Consumer<String> action, String... names) {
        return new StandardOption(action, names);
    }

    @Override
    public int getArgumentCount() {
        return action == null ? 0 : 1;
    }

    @Override
    public String getDescription() {
        return "";
    }

    @Override
    public Kind getKind() {
        return Kind.STANDARD;
    }

    @Override
    public List<String> getNames() {
        return names;
    }

    @Override
    public String getParameters() {
        return "";
    }

    @Override
    public boolean process(String option, List<String> arguments) {
        if (action != null) {
            action.accept(arguments.get(0));
        }
        return true;
    }
}
