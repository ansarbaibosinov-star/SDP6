package com.inspection.visitor;

import com.inspection.element.ConfigurationFile;
import com.inspection.element.DependencyFile;
import com.inspection.element.Directory;
import com.inspection.element.ProjectElement;
import com.inspection.element.SourceCodeFile;
import com.inspection.element.TestFile;

public class QualityGateVisitor implements ProjectVisitor {

    private static final double MAX_AVERAGE_COMPLEXITY = 10.0;
    private static final double MIN_TEST_SOURCE_RATIO = 0.40;

    private int sourceFiles;
    private int testFiles;
    private int totalComplexity;
    private int criticalSecurityIssues;

    @Override
    public void visit(SourceCodeFile file) {
        sourceFiles++;
        totalComplexity += file.getComplexity();

        if (file.hasHardcodedCredentials()) {
            criticalSecurityIssues++;
        }

        if (file.isHighRisk()) {
            criticalSecurityIssues++;
        }
    }

    @Override
    public void visit(TestFile file) {
        testFiles++;
    }

    @Override
    public void visit(ConfigurationFile file) {
        if (file.isInsecureConfiguration()) {
            criticalSecurityIssues++;
        }

        if (file.hasExposedCredentials()) {
            criticalSecurityIssues++;
        }
    }

    @Override
    public void visit(DependencyFile file) {
        criticalSecurityIssues += file.getVulnerableDependencies().size();
    }

    @Override
    public void visit(Directory directory) {
        for (ProjectElement element : directory.getElements()) {
            element.accept(this);
        }
    }

    public double getAverageComplexity() {
        if (sourceFiles == 0) {
            return 0.0;
        }

        return (double) totalComplexity / sourceFiles;
    }

    public double getTestSourceRatio() {
        if (sourceFiles == 0) {
            return 0.0;
        }

        return (double) testFiles / sourceFiles;
    }

    public int getCriticalSecurityIssues() {
        return criticalSecurityIssues;
    }

    public boolean isComplexityPassed() {
        return getAverageComplexity() <= MAX_AVERAGE_COMPLEXITY;
    }

    public boolean isTestsPassed() {
        return getTestSourceRatio() >= MIN_TEST_SOURCE_RATIO;
    }

    public boolean isSecurityPassed() {
        return criticalSecurityIssues == 0;
    }

    public boolean isPassed() {
        return isComplexityPassed()
                && isTestsPassed()
                && isSecurityPassed();
    }

    public String generateReport() {
        return String.format(
                """
                === QUALITY GATE ===
                Security: %s
                Complexity: %s
                Tests: %s

                Average complexity: %.2f
                Test/source ratio: %.2f
                Critical security issues: %d

                FINAL RESULT: %s
                """,
                status(isSecurityPassed()),
                status(isComplexityPassed()),
                status(isTestsPassed()),
                getAverageComplexity(),
                getTestSourceRatio(),
                criticalSecurityIssues,
                isPassed() ? "PASSED" : "FAILED"
        );
    }

    private String status(boolean passed) {
        return passed ? "PASS" : "FAIL";
    }

    public void reset() {
        sourceFiles = 0;
        testFiles = 0;
        totalComplexity = 0;
        criticalSecurityIssues = 0;
    }
}