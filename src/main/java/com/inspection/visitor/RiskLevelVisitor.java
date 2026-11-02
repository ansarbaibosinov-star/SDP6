package com.inspection.visitor;

import com.inspection.element.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RiskLevelVisitor implements ProjectVisitor {

    private final List<String> risks = new ArrayList<>();

    @Override
    public void visit(SourceCodeFile file) {
        if (file.hasHardcodedCredentials()) {
            risks.add("CRITICAL: Hardcoded credentials in " + file.getName());
        }

        if (file.isHighRisk()) {
            risks.add("HIGH: High-risk source file " + file.getName());
        }

        if (file.getComplexity() > 10) {
            risks.add("MEDIUM: High code complexity in " + file.getName());
        }
    }

    @Override
    public void visit(TestFile file) {
        if (file.getTestCount() == 0) {
            risks.add("MEDIUM: No tests found in " + file.getName());
        }
    }

    @Override
    public void visit(ConfigurationFile file) {
        if (file.hasExposedCredentials()) {
            risks.add("CRITICAL: Exposed credentials in " + file.getName());
        }

        if (file.isInsecureConfiguration()) {
            risks.add("HIGH: Insecure configuration in " + file.getName());
        }
    }

    @Override
    public void visit(DependencyFile file) {
        for (String dependency : file.getVulnerableDependencies()) {
            risks.add("HIGH: Vulnerable dependency: " + dependency);
        }

        for (String dependency : file.getProhibitedDependencies()) {
            risks.add("CRITICAL: Prohibited dependency: " + dependency);
        }
    }

    @Override
    public void visit(Directory directory) {
        for (var element : directory.getElements()) {
            element.accept(this);
        }
    }

    public List<String> getRisks() {
        return Collections.unmodifiableList(risks);
    }

    public String getOverallRiskLevel() {
        for (String risk : risks) {
            if (risk.startsWith("CRITICAL")) {
                return "CRITICAL";
            }
        }

        for (String risk : risks) {
            if (risk.startsWith("HIGH")) {
                return "HIGH";
            }
        }

        for (String risk : risks) {
            if (risk.startsWith("MEDIUM")) {
                return "MEDIUM";
            }
        }

        return "LOW";
    }

    public String generateReport() {
        StringBuilder report = new StringBuilder();

        report.append("\n========== RISK LEVEL REPORT ==========\n");

        if (risks.isEmpty()) {
            report.append("No risks detected.\n");
        } else {
            for (String risk : risks) {
                report.append("- ").append(risk).append("\n");
            }
        }

        report.append("---------------------------------------\n");
        report.append("OVERALL RISK LEVEL: ")
                .append(getOverallRiskLevel())
                .append("\n");
        report.append("Total risks: ").append(risks.size()).append("\n");
        report.append("=======================================\n");

        return report.toString();
    }

    public void reset() {
        risks.clear();
    }
}