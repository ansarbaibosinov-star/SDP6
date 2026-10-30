package com.inspection.visitor;

import com.inspection.element.ConfigurationFile;
import com.inspection.element.DependencyFile;
import com.inspection.element.Directory;
import com.inspection.element.SourceCodeFile;
import com.inspection.element.TestFile;

public interface ProjectVisitor {

    void visit(SourceCodeFile file);

    void visit(TestFile file);

    void visit(ConfigurationFile file);

    void visit(DependencyFile file);

    void visit(Directory directory);
}