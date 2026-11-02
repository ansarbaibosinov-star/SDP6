package com.inspection.visitor;

import com.inspection.element.ConfigurationFile;
import com.inspection.element.DependencyFile;
import com.inspection.element.Directory;
import com.inspection.element.ProjectElement;
import com.inspection.element.SourceCodeFile;
import com.inspection.element.TestFile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ComplianceVisitor implements ProjectVisitor {

    private final List<String> violations = new ArrayList<>();
    private final Set<String> sourceFileNames = new HashSet<>();
    private final Set<String> testedSourceFileNames = new HashSet<>();


    @Override
    public void visit(SourceCodeFile file) {
        sourceFileNames.add(file.getName());
    }

    @Override
    public void visit(TestFile file) {
        String testedFile = file.getTestedSourceFile();

        if (testedFile != null && !testedFile.isBlank()) {
            testedSourceFileNames.add(testedFile);
        }
    }

    @Override
    public void visit(ConfigurationFile file) {
        if (file.hasExposedCredentials()) {
            violations.add(
                    file.getName() + ": exposed credentials are prohibited");
        }
    }

    @Override
    public void visit(DependencyFile file) {
        file.getDependencies().forEach((dependency, version) -> {
            if (version == null || version.isBlank()) {
                violations.add(
                        file.getName() + ": missing version for " + dependency);
            }
        });

        for (String dependency : file.getProhibitedDependencies()) {
            violations.add(
                    file.getName() + ": prohibited dependency " + dependency);
        }
    }

    @Override
    public void visit(Directory directory) {
        for (ProjectElement element : directory.getElements()) {
            element.accept(this);
        }
    }

    public List<String> getViolations() {
        List<String> result = new ArrayList<>(violations);

        for (String sourceFile : sourceFileNames) {
            if (!testedSourceFileNames.contains(sourceFile)) {
                result.add(
                        sourceFile + ": production source file has no test");
            }
        }

        return Collections.unmodifiableList(result);
    }

    public boolean isCompliant() {
        return getViolations().isEmpty();
    }

    public String generateReport() {
        List<String> currentViolations = getViolations();

        StringBuilder report = new StringBuilder();
        report.append("=== COMPLIANCE CHECK ===\n");
        report.append("Violations: ")
                .append(currentViolations.size())
                .append("\n");

        if (currentViolations.isEmpty()) {
            report.append("Compliance result: PASS\n");
        } else {
            report.append("Compliance result: FAIL\n");

            for (String violation : currentViolations) {
                report.append("- ").append(violation).append("\n");
            }
        }

        return report.toString();
    }

    public void reset() {
        violations.clear();
        sourceFileNames.clear();
        testedSourceFileNames.clear();

    }
}