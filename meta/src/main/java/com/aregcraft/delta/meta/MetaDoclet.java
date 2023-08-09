package com.aregcraft.delta.meta;

import com.aregcraft.delta.meta.replacement.Replacement;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jdk.javadoc.doclet.Doclet;
import jdk.javadoc.doclet.DocletEnvironment;
import jdk.javadoc.doclet.Reporter;

import javax.lang.model.SourceVersion;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class MetaDoclet implements Doclet {
    private final Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    private final List<Replacement> replacements;
    private Path metaPath;
    private Path descriptionPath;
    private Path resourcesPath;
    private String version;

    protected MetaDoclet(Replacement... replacements) {
        this.replacements = List.of(replacements);
    }

    @Override
    public void init(Locale locale, Reporter reporter) {
    }

    @Override
    public String getName() {
        return getClass().getSimpleName();
    }

    @Override
    public Set<? extends Option> getSupportedOptions() {
        return Set.of(StandardOption.oneArg(this::setupPaths, "-d"),
                StandardOption.oneArg(it -> version = it, "-version"),
                StandardOption.oneArg("-doctitle", "-windowtitle"),
                StandardOption.noArgs("-notimestamp"));
    }

    private void setupPaths(String arg) {
        var path = Path.of(arg);
        metaPath = path.resolve("meta.json");
        descriptionPath = path.resolve("README.md");
        resourcesPath = path.resolve("src/main/resources");
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latest();
    }

    @Override
    public boolean run(DocletEnvironment environment) {
        try (var writer = Files.newBufferedWriter(metaPath)) {
            var abilities = getAbilities(environment);
            gson.toJson(new Meta(version, abilities), writer);
            Files.writeString(descriptionPath, getReplacedDescription(abilities));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return true;
    }

    private String getReplacedDescription(List<Ability> abilities) throws IOException {
        var description = Files.readString(descriptionPath);
        for (var value : replacements) {
            description = value.replace(description, resourcesPath, abilities);
        }
        return description;
    }

    private List<Ability> getAbilities(DocletEnvironment environment) {
        return environment.getSpecifiedElements().stream()
                .filter(it -> it.getKind() == ElementKind.CLASS)
                .map(TypeElement.class::cast)
                .filter(this::isAbilitySuperclassAnnotationPresent)
                .map(new AbilityProcessor(environment)::process)
                .toList();
    }

    private boolean isAbilitySuperclassAnnotationPresent(TypeElement element) {
        return ((DeclaredType) element.getSuperclass()).asElement().getAnnotation(AbilitySuperclass.class) != null;
    }
}
