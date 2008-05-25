package org.intellij.stripes.highlighting;

import com.intellij.codeInspection.InspectionToolProvider;

public class StripesInspectionToolProvider implements InspectionToolProvider {
    public Class[] getInspectionClasses() {
        return new Class[]{AnnotationLocationInspection.class};
    }

}
