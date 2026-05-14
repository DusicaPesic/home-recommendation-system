package com.splendor.assistant.model.explanation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ExplanationTreeNode {
    private final String label;
    private final List<ExplanationTreeNode> children = new ArrayList<>();

    public ExplanationTreeNode(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public List<ExplanationTreeNode> getChildren() {
        return Collections.unmodifiableList(children);
    }

    public void addChild(ExplanationTreeNode child) {
        children.add(child);
    }
}