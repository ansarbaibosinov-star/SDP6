# Smart Code Inspection & Compliance Engine

## Overview

Smart Code Inspection & Compliance Engine is a Java 17 application that demonstrates the Visitor Design Pattern for analyzing software projects.

The application represents project files and directories as elements and processes them through separate visitors. Each visitor performs a different analysis without placing all inspection operations inside the element classes.

## Technologies

- Java 17
- Maven
- JUnit 5
- Git
- PlantUML

## Architecture

### Project Elements

The `com.inspection.element` package contains:

- `ProjectElement` — common interface for project elements.
- `SourceCodeFile` — represents a source code file.
- `TestFile` — represents a test file.
- `ConfigurationFile` — represents a configuration file.
- `DependencyFile` — represents project dependencies.
- `Directory` — represents a directory containing other project elements.

### Visitors

The `com.inspection.visitor` package contains:

- `ProjectVisitor` — interface defining visit operations.
- `SecurityAuditVisitor` — detects modeled security issues.
- `ProjectStatisticsVisitor` — calculates project statistics.
- `QualityGateVisitor` — evaluates release readiness.
- `ComplianceVisitor` — checks development and compliance rules.
- `TechnicalDebtVisitor` — estimates technical debt using a simple scoring model.

## What problem does Visitor solve?

Visitor separates operations from the objects on which those operations are performed.

In this project, the same files can be analyzed for security, statistics, quality, compliance, and technical debt without implementing each operation inside every element class.

This makes it easier to introduce new operations while keeping the element classes focused on representing project data.

## Where are Element and Visitor?

`ProjectElement` is the Element interface in the `com.inspection.element` package.

`ProjectVisitor` is the Visitor interface in the `com.inspection.visitor` package.

The concrete elements implement `ProjectElement`, while the concrete visitors implement `ProjectVisitor`.

## What is double dispatch?

Double dispatch is a technique in which the operation is selected using both the visitor's concrete type and the element's concrete type.

For example, `SourceCodeFile.accept(visitor)` calls `visitor.visit(this)`.

Because `this` is a `SourceCodeFile`, Java selects the corresponding overloaded `visit(SourceCodeFile)` method. The concrete visitor then determines what operation is performed.

This design avoids using `instanceof` or large switch statements to identify element types.

## Why not put all operations inside the Element classes?

Putting every operation inside the element classes would mix project data with multiple unrelated responsibilities.

Every new inspection operation could require changes to several element classes.

Visitor keeps these responsibilities separate and makes it easier to add new operations.

## Is it easier to add a new Visitor or a new Element? Why?

Adding a new Visitor is easier because a new operation can be implemented as a separate class.

Adding a new element is more expensive because the `ProjectVisitor` interface must be extended with a new visit method, and every concrete visitor must implement the new operation for that element type.

Visitor is therefore most useful when the element structure is relatively stable and new operations are added frequently.

## When would Visitor be a bad choice?

Visitor may be a poor choice when new element types are added frequently or when the hierarchy changes often.

Every new element type requires changes to the visitor interface and its concrete implementations. This can make the design expensive to maintain.

It may also introduce unnecessary complexity when the project has only a few simple operations.

## How is Visitor different from Strategy?

Visitor separates operations over a set of element types. It provides a visit method for each concrete element type and allows different operations to be implemented independently.

Strategy encapsulates interchangeable algorithms behind a common interface. The client selects the strategy that should perform a particular operation.

In this project, Visitor is appropriate because the same project elements must support multiple different inspection operations.

## Which SOLID principles can you identify?

### Single Responsibility Principle

Each concrete visitor has one main responsibility, such as security auditing or calculating statistics.

The element classes primarily represent project data and provide the `accept()` method.

### Open/Closed Principle

New operations can be added by introducing new visitor classes without modifying existing element classes.

However, adding a new element type requires changes to the visitor interface and concrete visitors.

### Dependency Inversion Principle

Elements depend on the `ProjectVisitor` abstraction instead of concrete visitor implementations.

The `ProjectVisitor` interface also defines the contract used by all concrete visitors.

## Visitor Limitation Challenge: DatabaseMigrationFile

If a new element called `DatabaseMigrationFile` is introduced, the following changes are required:

1. Create `DatabaseMigrationFile` implementing `ProjectElement`.
2. Add `visit(DatabaseMigrationFile file)` to `ProjectVisitor`.
3. Implement the new visit method in every concrete visitor.
4. Add tests for the new element and its behavior.
5. Update the UML diagram.

This demonstrates the main limitation of Visitor: adding new operations is easy, but adding new element types requires changes to existing visitors.

## Quality Gate Rules

The Quality Gate evaluates three criteria:

- Average source-file complexity must be at most 10.
- The test-file/source-file ratio must be at least 0.40.
- Critical security issues must equal zero.

All three conditions must pass for the project to be marked as ready for release.

An empty project does not pass the Quality Gate because it has no source files and its test/source ratio is zero.

## Testing

The project uses JUnit 5 to test visitor behavior.

The test suite covers:

- Security issues and clean projects.
- Project statistics.
- Average complexity and test/source ratio.
- Quality Gate PASS and FAIL scenarios.
- Compliance violations.
- Nested and empty directories.
- Technical debt calculation.

Run the tests with:

```bash
mvn test
```

Compile the project with:

```bash
mvn compile
```

Run the demonstration using the `com.inspection.Main` class in IntelliJ IDEA.

## UML Diagrams

The `docs` directory contains:

- `class-diagram.puml` — class structure and relationships.
- `sequence-diagram.puml` — interaction between elements and visitors.

These diagrams use PlantUML syntax.

## Limitations

The application analyzes supplied metadata rather than parsing real Java source files or automatically scanning external vulnerability databases.

Security flags, complexity values, dependency vulnerability information, and test relationships are provided as demonstration data.

The technical debt score is a simplified educational metric rather than a standardized measurement.

## Author

Student project for Assignment 6 — Visitor Pattern.