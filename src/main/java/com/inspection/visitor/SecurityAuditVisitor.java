package com.inspection.visitor;

import com.inspection.element.ConfigurationFile;
import com.inspection.element.DependencyFile;
import com.inspection.element.Directory;
import com.inspection.element.SourceCodeFile;
import com.inspection.element.TestFile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SecurityAuditVisitor implements ProjectVisitor {

    private final List<String> issues = new ArrayList<>();

    @Override
    public void visit(SourceCodeFile file) {
        if (file.hasHardcodedCredentials()) {
            issues.add(file.getName() + ": hardcoded credentials detected");
        }

        if (file.isHighRisk()) {
            issues.add(file.getName() + ": high-risk source file");
        }
    }

    @Override
    public void visit(TestFile file) {
        // Test files do not introduce security issues in this model.
    }

    @Override
    public void visit(ConfigurationFile file) {
        if (file.isInsecureConfiguration()) {
            issues.add(file.getName() + ": insecure configuration");
        }

        if (file.hasExposedCredentials()) {
            issues.add(file.getName() + ": exposed credentials detected");
        }
    }

    @Override
    public void visit(DependencyFile file) {
        for (String dependency : file.getVulnerableDependencies()) {
            issues.add(file.getName() + ": vulnerable dependency: " + dependency);
        }
    }

    @Override
    public void visit(Directory directory) {
        for (var element : directory.getElements()) {
            element.accept(this);
        }
    }

    public int getIssueCount() {
        return issues.size();
    }

    public boolean hasCriticalIssues() {
        return !issues.isEmpty();
    }

    public List<String> getIssues() {
        return Collections.unmodifiableList(issues);
    }

    public int calculateSecurityScore() {
        return Math.max(0, 100 - issues.size() * 20);
    }

    public void reset() {
        issues.clear();
    }

    public String generateReport() {
        StringBuilder report = new StringBuilder();

        report.append("=== SECURITY AUDIT ===\n");
        report.append("Issues found: ").append(issues.size()).append("\n");
        report.append("Security score: ")
                .append(calculateSecurityScore())
                .append("/100\n");

        if (issues.isEmpty()) {
            report.append("Security result: PASS\n");
        } else {
            report.append("Security result: FAIL\n");

            for (String issue : issues) {
                report.append("- ").append(issue).append("\n");
            }
        }

        return report.toString();
    }
}