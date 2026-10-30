package com.inspection.element;

import com.inspection.visitor.ProjectVisitor;

public class ConfigurationFile implements ProjectElement {

    private final String name;
    private final boolean insecureConfiguration;
    private final boolean exposedCredentials;

    public ConfigurationFile(
            String name,
            boolean insecureConfiguration,
            boolean exposedCredentials) {

        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("File name cannot be empty");
        }

        this.name = name;
        this.insecureConfiguration = insecureConfiguration;
        this.exposedCredentials = exposedCredentials;
    }

    public String getName() {
        return name;
    }

    public boolean isInsecureConfiguration() {
        return insecureConfiguration;
    }

    public boolean hasExposedCredentials() {
        return exposedCredentials;
    }

    @Override
    public void accept(ProjectVisitor visitor) {
        visitor.visit(this);
    }
}