package com.aregcraft.delta.meta;

import com.sun.source.doctree.DocCommentTree;
import com.sun.source.doctree.TextTree;
import com.sun.source.util.SimpleDocTreeVisitor;

import java.util.stream.Collectors;

public class AbilityDocTreeVisitor extends SimpleDocTreeVisitor<String, Void> {
    @Override
    public String visitDocComment(DocCommentTree node, Void unused) {
        return node.getFullBody().stream()
                .map(it -> it.accept(this, null))
                .collect(Collectors.joining());
    }

    @Override
    public String visitText(TextTree node, Void unused) {
        return node.getBody().replaceAll("\n", "");
    }
}
