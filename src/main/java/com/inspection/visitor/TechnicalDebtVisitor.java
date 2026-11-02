package com.inspection.visitor;

import com.inspection.element.ConfigurationFile;
import com.inspection.element.DependencyFile;
import com.inspection.element.Directory;
import com.inspection.element.ProjectElement;
import com.inspection.element.SourceCodeFile;
import com.inspection.element.TestFile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TechnicalDebtVisitor implements ProjectVisitor {

    private final List<String> debtItems = new ArrayList<>();
    private int debtPoints;

    @Override
    public void visit(SourceCodeFile file) {
        if (file.getComplexity() > 10) {
            int excess = file.getComplexity() - 10;
            debtPoints += excess * 2;

            debtItems.add(
                    file.getName()
                            + ": high complexity ("
                            + file.getComplexity()
                            + ")");
        }

        if (file.isHighRisk()) {
            debtPoints += 5;
            debtItems.add(file.getName() + ": high-risk source file");
        }
    }

    @Override
    public void visit(TestFile file) {
        // Test files do not add debt points in this model.
    }

    @Override
    public void visit(ConfigurationFile file) {
        if (file.isInsecureConfiguration()) {
            debtPoints += 5;
            debtItems.add(
                    file.getName() + ": insecure configuration");
        }
    }

    @Override
    public void visit(DependencyFile file) {
        for (String dependency : file.getVulnerableDependencies()) {
            debtPoints += 5;
            debtItems.add(
                    file.getName()
                            + ": vulnerable dependency "
                            + dependency);
        }

        for (var entry : file.getDependencies().entrySet()) {
            if (entry.getValue() == null || entry.getValue().isBlank()) {
                debtPoints += 2;
                debtItems.add(
                        file.getName()
                                + ": unspecified version for "
                                + entry.getKey());
            }
        }
    }

    @Override
    public void visit(Directory directory) {
        for (ProjectElement element : directory.getElements()) {
            element.accept(this);
        }
    }

    public int getDebtPoints() {
        return debtPoints;
    }

    public List<String> getDebtItems() {
        return Collections.unmodifiableList(debtItems);
    }

    public String generateReport() {
        StringBuilder report = new StringBuilder();

        report.append("=== TECHNICAL DEBT ===\n");
        report.append("Debt points: ")
                .append(debtPoints)
                .append("\n");

        if (debtItems.isEmpty()) {
            report.append("Technical debt: LOW\n");
        } else {
            report.append("Technical debt items:\n");

            for (String item : debtItems) {
                report.append("- ").append(item).append("\n");
            }
        }

        return report.toString();
    }

    public void reset() {
        debtItems.clear();
        debtPoints = 0;
    }
}