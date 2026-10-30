package com.inspection.element;

import com.inspection.visitor.ProjectVisitor;

public interface ProjectElement {

    void accept(ProjectVisitor visitor);
}