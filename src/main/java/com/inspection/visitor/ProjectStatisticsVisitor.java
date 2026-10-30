package com.inspection.visitor;

import com.inspection.element.ConfigurationFile;
import com.inspection.element.DependencyFile;
import com.inspection.element.Directory;
import com.inspection.element.ProjectElement;
import com.inspection.element.SourceCodeFile;
import com.inspection.element.TestFile;

public class ProjectStatisticsVisitor implements ProjectVisitor {

    private int totalFiles;
    private int sourceFiles;
    private int testFiles;
    private int configurationFiles;
    private int dependencyFiles;
    private int totalLinesOfCode;
    private int totalTests;
    private int totalComplexity;
    private int totalDependencies;

    @Override
    public void visit(SourceCodeFile file) {
        totalFiles++;
        sourceFiles++;
        totalLinesOfCode += file.getLinesOfCode();
        totalComplexity += file.getComplexity();
    }

    @Override
    public void visit(TestFile file) {
        totalFiles++;
        testFiles++;
        totalLinesOfCode += file.getLinesOfCode();
        totalTests += file.getTestCount();
    }

    @Override
    public void visit(ConfigurationFile file) {
        totalFiles++;
        configurationFiles++;
    }

    @Override
    public void visit(DependencyFile file) {
        totalFiles++;
        dependencyFiles++;
        totalDependencies += file.getDependencies().size();
    }

    @Override
    public void visit(Directory directory) {
        for (ProjectElement element : directory.getElements()) {
            element.accept(this);
        }
    }

    public int getTotalFiles() {
        return totalFiles;
    }

    public int getSourceFiles() {
        return sourceFiles;
    }

    public int getTestFiles() {
        return testFiles;
    }

    public int getConfigurationFiles() {
        return configurationFiles;
    }

    public int getDependencyFiles() {
        return dependencyFiles;
    }

    public int getTotalLinesOfCode() {
        return totalLinesOfCode;
    }

    public int getTotalTests() {
        return totalTests;
    }

    public int getTotalComplexity() {
        return totalComplexity;
    }

    public int getTotalDependencies() {
        return totalDependencies;
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

    public String generateReport() {
        return String.format(
                """
                === PROJECT STATISTICS ===
                Total files: %d
                Source files: %d
                Test files: %d
                Configuration files: %d
                Dependency files: %d
                Lines of code: %d
                Total tests: %d
                Dependencies: %d
                Average complexity: %.2f
                Test/source ratio: %.2f
                """,
                totalFiles,
                sourceFiles,
                testFiles,
                configurationFiles,
                dependencyFiles,
                totalLinesOfCode,
                totalTests,
                totalDependencies,
                getAverageComplexity(),
                getTestSourceRatio()
        );
    }

    public void reset() {
        totalFiles = 0;
        sourceFiles = 0;
        testFiles = 0;
        configurationFiles = 0;
        dependencyFiles = 0;
        totalLinesOfCode = 0;
        totalTests = 0;
        totalComplexity = 0;
        totalDependencies = 0;
    }
}