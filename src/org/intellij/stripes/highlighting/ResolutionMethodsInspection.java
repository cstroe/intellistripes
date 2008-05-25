package org.intellij.stripes.highlighting;

import com.intellij.codeHighlighting.HighlightDisplayLevel;
import com.intellij.codeInspection.InspectionManager;
import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import org.intellij.stripes.reference.StripesReferenceUtil;
import org.intellij.stripes.util.StripesConstants;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class ResolutionMethodsInspection extends LocalInspectionTool {
    @Nls
    @NotNull
    public String getGroupDisplayName() {
        return "Stripes Inspection";
    }

    @Nls
    @NotNull
    public String getDisplayName() {
        return "Resolution Methods";
    }

    @NonNls
    @NotNull
    public String getShortName() {
        return "StripesInspections";
    }

    public boolean isEnabledByDefault() {
        return true;
    }


    @NotNull
    public HighlightDisplayLevel getDefaultLevel() {
        return HighlightDisplayLevel.DO_NOT_SHOW;
    }

    @Nullable
    public ProblemDescriptor[] checkClass(@NotNull PsiClass aClass, @NotNull InspectionManager manager, boolean isOnTheFly) {
        Map<String, PsiMethod> methods = StripesReferenceUtil.getResolutionMethods(aClass);
        Integer defHndlCnt = 0;
        Integer resHndlCnt = 0;
        for (PsiMethod method : methods.values()) {
            if (method.getModifierList().findAnnotation(StripesConstants.DEFAULT_HANDLER_ANNOTATION) != null) {
                defHndlCnt++;
            }

        }
        return super.checkClass(aClass, manager, isOnTheFly);
    }
}