package com.aregcraft.delta.meta;

import com.sun.source.doctree.DocTreeVisitor;
import com.sun.source.util.DocTrees;
import jdk.javadoc.doclet.DocletEnvironment;

import javax.lang.model.element.*;
import java.util.List;

public class AbilityProcessor {
    private final DocTrees docTrees;
    private final DocTreeVisitor<String, Void> docTreeVisitor;

    public AbilityProcessor(DocletEnvironment environment) {
        docTrees = environment.getDocTrees();
        docTreeVisitor = new AbilityDocTreeVisitor();
    }

    public Ability process(TypeElement element) {
        return new Ability(getNameWithoutAbility(element), getDescription(element), getProperties(element));
    }

    private String getNameWithoutAbility(Element element) {
        return getName(element).split("Ability")[0].split("Perk")[0];
    }

    private List<Property> getProperties(TypeElement element) {
        return element.getEnclosedElements().stream()
                .filter(this::isProperty)
                .map(VariableElement.class::cast)
                .map(this::toProperty)
                .toList();
    }

    private boolean isProperty(Element element) {
        var modifiers = element.getModifiers();
        return element.getKind() == ElementKind.FIELD
                && !modifiers.contains(Modifier.TRANSIENT)
                && !modifiers.contains(Modifier.FINAL)
                && docTrees.getDocCommentTree(element) != null;
    }

    private Property toProperty(VariableElement element) {
        return new Property(getType(element), getName(element), getDescription(element));
    }

    private String getName(Element element) {
        return element.getSimpleName().toString();
    }

    private String getType(Element element) {
        var type = element.asType().toString().split("\\.");
        return type[type.length - 1];
    }

    private String getDescription(Element element) {
        return docTrees.getDocCommentTree(element).accept(docTreeVisitor, null);
    }
}
