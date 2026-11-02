package com.inspection.visitor;

import com.inspection.element.ConfigurationFile;
import com.inspection.element.DependencyFile;
import com.inspection.element.Directory;
import com.inspection.element.SourceCodeFile;
import com.inspection.element.TestFile;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class VisitorPatternTest {

    @Test
    void securityVisitorDetectsHardcodedCredentials() {
        Directory project = new Directory("project");

        project.addElement(new SourceCodeFile(
                "PaymentService.java", 100, 5, true, false));

        SecurityAuditVisitor visitor = new SecurityAuditVisitor();
        project.accept(visitor);

        assertEquals(1, visitor.getIssueCount());
        assertTrue(visitor.hasCriticalIssues());
        assertTrue(visitor.getIssues().get(0).contains("credentials"));
    }

    @Test
    void securityVisitorDetectsVulnerableDependencies() {
        Directory project = new Directory("project");

        project.addElement(new DependencyFile(
                "pom.xml",
                Map.of("library-a", "1.0"),
                Set.of("library-a"),
                Set.of()
        ));

        SecurityAuditVisitor visitor = new SecurityAuditVisitor();
        project.accept(visitor);

        assertEquals(1, visitor.getIssueCount());
        assertTrue(visitor.getIssues().get(0).contains("library-a"));
    }

    @Test
    void securityVisitorPassesForCleanProject() {
        Directory project = new Directory("project");

        project.addElement(new SourceCodeFile(
                "PaymentService.java", 100, 5, false, false));

        project.addElement(new ConfigurationFile(
                "application.properties", false, false));

        SecurityAuditVisitor visitor = new SecurityAuditVisitor();
        project.accept(visitor);

        assertEquals(0, visitor.getIssueCount());
        assertFalse(visitor.hasCriticalIssues());
        assertEquals(100, visitor.calculateSecurityScore());
    }

    @Test
    void statisticsVisitorCountsFilesAndLines() {
        Directory project = new Directory("project");

        project.addElement(new SourceCodeFile(
                "Service.java", 100, 5, false, false));

        project.addElement(new TestFile(
                "ServiceTest.java", 40, 6, "Service.java"));

        ProjectStatisticsVisitor visitor =
                new ProjectStatisticsVisitor();

        project.accept(visitor);

        assertEquals(2, visitor.getTotalFiles());
        assertEquals(1, visitor.getSourceFiles());
        assertEquals(1, visitor.getTestFiles());
        assertEquals(140, visitor.getTotalLinesOfCode());
        assertEquals(6, visitor.getTotalTests());
    }

    @Test
    void statisticsVisitorCalculatesAverageComplexity() {
        Directory project = new Directory("project");

        project.addElement(new SourceCodeFile(
                "A.java", 50, 4, false, false));

        project.addElement(new SourceCodeFile(
                "B.java", 80, 8, false, false));

        ProjectStatisticsVisitor visitor =
                new ProjectStatisticsVisitor();

        project.accept(visitor);

        assertEquals(6.0, visitor.getAverageComplexity(), 0.001);
    }

    @Test
    void statisticsVisitorCalculatesTestSourceRatio() {
        Directory project = new Directory("project");

        project.addElement(new SourceCodeFile(
                "A.java", 50, 4, false, false));

        project.addElement(new SourceCodeFile(
                "B.java", 80, 8, false, false));

        project.addElement(new TestFile(
                "ATest.java", 30, 3, "A.java"));

        ProjectStatisticsVisitor visitor =
                new ProjectStatisticsVisitor();

        project.accept(visitor);

        assertEquals(0.5, visitor.getTestSourceRatio(), 0.001);
    }

    @Test
    void qualityGatePassesWhenAllCriteriaAreSatisfied() {
        Directory project = new Directory("project");

        project.addElement(new SourceCodeFile(
                "Service.java", 100, 8, false, false));

        project.addElement(new TestFile(
                "ServiceTest.java", 50, 5, "Service.java"));

        QualityGateVisitor visitor = new QualityGateVisitor();
        project.accept(visitor);

        assertTrue(visitor.isSecurityPassed());
        assertTrue(visitor.isComplexityPassed());
        assertTrue(visitor.isTestsPassed());
        assertTrue(visitor.isPassed());
    }

    @Test
    void qualityGateFailsWhenSecurityIssueExists() {
        Directory project = new Directory("project");

        project.addElement(new SourceCodeFile(
                "Service.java", 100, 5, true, false));

        project.addElement(new TestFile(
                "ServiceTest.java", 50, 5, "Service.java"));

        QualityGateVisitor visitor = new QualityGateVisitor();
        project.accept(visitor);

        assertFalse(visitor.isSecurityPassed());
        assertFalse(visitor.isPassed());
    }

    @Test
    void qualityGateFailsWhenComplexityIsTooHigh() {
        Directory project = new Directory("project");

        project.addElement(new SourceCodeFile(
                "ComplexService.java", 200, 15, false, false));

        project.addElement(new TestFile(
                "ComplexServiceTest.java", 60, 5, "ComplexService.java"));

        QualityGateVisitor visitor = new QualityGateVisitor();
        project.accept(visitor);

        assertFalse(visitor.isComplexityPassed());
        assertFalse(visitor.isPassed());
    }

    @Test
    void qualityGateFailsWhenTestRatioIsTooLow() {
        Directory project = new Directory("project");

        project.addElement(new SourceCodeFile(
                "A.java", 50, 4, false, false));

        project.addElement(new SourceCodeFile(
                "B.java", 80, 6, false, false));

        project.addElement(new SourceCodeFile(
                "C.java", 70, 5, false, false));

        project.addElement(new TestFile(
                "ATest.java", 30, 3, "A.java"));

        QualityGateVisitor visitor = new QualityGateVisitor();
        project.accept(visitor);

        assertFalse(visitor.isTestsPassed());
        assertFalse(visitor.isPassed());
    }

    @Test
    void complianceVisitorDetectsMissingDependencyVersion() {
        Directory project = new Directory("project");

        project.addElement(new DependencyFile(
                "pom.xml",
                Map.of("library-a", ""),
                Set.of(),
                Set.of()
        ));

        ComplianceVisitor visitor = new ComplianceVisitor();
        project.accept(visitor);

        assertFalse(visitor.isCompliant());
        assertTrue(visitor.getViolations().get(0).contains("version"));
    }

    @Test
    void complianceVisitorDetectsSourceWithoutTests() {
        Directory project = new Directory("project");

        project.addElement(new SourceCodeFile(
                "PaymentService.java", 100, 5, false, false));

        ComplianceVisitor visitor = new ComplianceVisitor();
        project.accept(visitor);

        assertFalse(visitor.isCompliant());
        assertTrue(visitor.getViolations().stream()
                .anyMatch(violation -> violation.contains("no test")));
    }

    @Test
    void visitorsProcessNestedDirectories() {
        Directory root = new Directory("root");
        Directory src = new Directory("src");
        Directory services = new Directory("services");

        services.addElement(new SourceCodeFile(
                "PaymentService.java", 100, 5, false, false));

        src.addElement(services);
        root.addElement(src);

        ProjectStatisticsVisitor visitor =
                new ProjectStatisticsVisitor();

        root.accept(visitor);

        assertEquals(1, visitor.getTotalFiles());
        assertEquals(1, visitor.getSourceFiles());
    }

    @Test
    void emptyDirectoryIsHandledWithoutErrors() {
        Directory project = new Directory("empty");

        ProjectStatisticsVisitor statistics =
                new ProjectStatisticsVisitor();

        project.accept(statistics);

        assertEquals(0, statistics.getTotalFiles());
        assertEquals(0.0, statistics.getAverageComplexity(), 0.001);
        assertEquals(0.0, statistics.getTestSourceRatio(), 0.001);

        QualityGateVisitor quality = new QualityGateVisitor();
        project.accept(quality);

        assertFalse(quality.isPassed());
    }


    @Test
    void technicalDebtVisitorDetectsHighComplexity() {
        Directory project = new Directory("project");

        project.addElement(new SourceCodeFile(
                "ComplexService.java", 200, 15, false, false));

        TechnicalDebtVisitor visitor = new TechnicalDebtVisitor();
        project.accept(visitor);

        assertEquals(10, visitor.getDebtPoints());
        assertTrue(visitor.getDebtItems().get(0).contains("complexity"));
    }
}