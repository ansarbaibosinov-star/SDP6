package com.inspection;

import com.inspection.element.ConfigurationFile;
import com.inspection.element.DependencyFile;
import com.inspection.element.Directory;
import com.inspection.element.SourceCodeFile;
import com.inspection.element.TestFile;
import com.inspection.visitor.ComplianceVisitor;
import com.inspection.visitor.ProjectStatisticsVisitor;
import com.inspection.visitor.QualityGateVisitor;
import com.inspection.visitor.SecurityAuditVisitor;

import java.util.Map;
import java.util.Set;

public class Main {

    public static void main(String[] args) {

        // Create the root directory.
        Directory project = new Directory("fintech-project");

        // Create project subdirectories.
        Directory src = new Directory("src");
        Directory tests = new Directory("tests");
        Directory config = new Directory("config");

        // Add source files.
        src.addElement(new SourceCodeFile(
                "PaymentService.java",
                150,
                8,
                false,
                false
        ));

        src.addElement(new SourceCodeFile(
                "UserService.java",
                120,
                12,
                true,
                false
        ));

        // Add test files.
        tests.addElement(new TestFile(
                "PaymentServiceTest.java",
                80,
                10,
                "PaymentService.java"
        ));

        // Add configuration.
        config.addElement(new ConfigurationFile(
                "application.properties",
                false,
                false
        ));

        // Add dependencies.
        DependencyFile dependencies = new DependencyFile(
                "pom.xml",
                Map.of(
                        "spring-core", "6.1.0",
                        "junit-jupiter", "5.11.4"
                ),
                Set.of(),
                Set.of()
        );

        // Build the project tree.
        project.addElement(src);
        project.addElement(tests);
        project.addElement(config);
        project.addElement(dependencies);

        // Run Security Audit.
        SecurityAuditVisitor securityVisitor =
                new SecurityAuditVisitor();
        project.accept(securityVisitor);
        System.out.println(securityVisitor.generateReport());

        // Run Project Statistics.
        ProjectStatisticsVisitor statisticsVisitor =
                new ProjectStatisticsVisitor();
        project.accept(statisticsVisitor);
        System.out.println(statisticsVisitor.generateReport());

        // Run Quality Gate.
        QualityGateVisitor qualityVisitor =
                new QualityGateVisitor();
        project.accept(qualityVisitor);
        System.out.println(qualityVisitor.generateReport());

        // Run Compliance Check.
        ComplianceVisitor complianceVisitor =
                new ComplianceVisitor();
        project.accept(complianceVisitor);
        System.out.println(complianceVisitor.generateReport());
    }
}