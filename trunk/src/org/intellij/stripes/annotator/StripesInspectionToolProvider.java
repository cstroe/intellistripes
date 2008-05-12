package org.intellij.stripes.annotator;

import com.intellij.codeInspection.InspectionToolProvider;

public class StripesInspectionToolProvider implements InspectionToolProvider {
    public Class[] getInspectionClasses() {
        return new Class[]{AnnotationLocationInspection.class};
    }

}
