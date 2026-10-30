package com.inspection.element;

import com.inspection.visitor.ProjectVisitor;

public class SourceCodeFile implements ProjectElement {

    private final String name;
    private final int linesOfCode;
    private final int complexity;
    private final boolean hardcodedCredentials;
    private final boolean highRisk;

    public SourceCodeFile(
            String name,
            int linesOfCode,
            int complexity,
            boolean hardcodedCredentials,
            boolean highRisk) {

        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("File name cannot be empty");
        }

        if (linesOfCode < 0 || complexity < 0) {
            throw new IllegalArgumentException(
                    "Lines of code and complexity cannot be negative");
        }

        this.name = name;
        this.linesOfCode = linesOfCode;
        this.complexity = complexity;
        this.hardcodedCredentials = hardcodedCredentials;
        this.highRisk = highRisk;
    }

    public String getName() {
        return name;
    }

    public int getLinesOfCode() {
        return linesOfCode;
    }

    public int getComplexity() {
        return complexity;
    }

    public boolean hasHardcodedCredentials() {
        return hardcodedCredentials;
    }

    public boolean isHighRisk() {
        return highRisk;
    }

    @Override
    public void accept(ProjectVisitor visitor) {
        visitor.visit(this);
    }
}