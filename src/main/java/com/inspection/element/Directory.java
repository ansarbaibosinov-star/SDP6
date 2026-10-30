package com.inspection.element;

import com.inspection.visitor.ProjectVisitor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Directory implements ProjectElement {

    private final String name;
    private final List<ProjectElement> elements = new ArrayList<>();

    public Directory(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException(
                    "Directory name cannot be empty");
        }

        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void addElement(ProjectElement element) {
        if (element == null) {
            throw new IllegalArgumentException(
                    "Directory element cannot be null");
        }

        elements.add(element);
    }

    public List<ProjectElement> getElements() {
        return Collections.unmodifiableList(elements);
    }

    @Override
    public void accept(ProjectVisitor visitor) {
        visitor.visit(this);
    }
}