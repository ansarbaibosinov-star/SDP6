package com.inspection.element;

import com.inspection.visitor.ProjectVisitor;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class DependencyFile implements ProjectElement {

    private final String name;
    private final Map<String, String> dependencies;
    private final Set<String> vulnerableDependencies;
    private final Set<String> prohibitedDependencies;

    public DependencyFile(
            String name,
            Map<String, String> dependencies,
            Set<String> vulnerableDependencies,
            Set<String> prohibitedDependencies) {

        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("File name cannot be empty");
        }

        this.name = name;

        this.dependencies = new LinkedHashMap<>(
                dependencies == null ? Collections.emptyMap() : dependencies);

        this.vulnerableDependencies = new LinkedHashSet<>(
                vulnerableDependencies == null
                        ? Collections.emptySet()
                        : vulnerableDependencies);

        this.prohibitedDependencies = new LinkedHashSet<>(
                prohibitedDependencies == null
                        ? Collections.emptySet()
                        : prohibitedDependencies);
    }

    public String getName() {
        return name;
    }

    public Map<String, String> getDependencies() {
        return Collections.unmodifiableMap(dependencies);
    }

    public Set<String> getVulnerableDependencies() {
        return Collections.unmodifiableSet(vulnerableDependencies);
    }

    public Set<String> getProhibitedDependencies() {
        return Collections.unmodifiableSet(prohibitedDependencies);
    }

    @Override
    public void accept(ProjectVisitor visitor) {
        visitor.visit(this);
    }
}