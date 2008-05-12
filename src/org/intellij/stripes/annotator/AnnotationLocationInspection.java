package org.intellij.stripes.annotator;

import com.intellij.codeHighlighting.HighlightDisplayLevel;
import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.intellij.stripes.util.StripesConstants;
import org.intellij.stripes.util.StripesUtil;

public class AnnotationLocationInspection extends LocalInspectionTool {
    @Nls
    @NotNull
    public String getGroupDisplayName() {
        return "Stripes Inspection";
    }

    @Nls
    @NotNull
    public String getDisplayName() {
        return "Annotation Locations";
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

    @NotNull
    public PsiElementVisitor buildVisitor(@NotNull final ProblemsHolder holder, boolean isOnTheFly) {
        return new PsiElementVisitor() {
            public void visitReferenceExpression(PsiReferenceExpression expression) {
            }

            public void visitAnnotation(PsiAnnotation annotation) {
                if (StripesConstants.VALIDATE_ANNOTATION.equals(annotation.getQualifiedName())) {
                    if (null != annotation.findDeclaredAttributeValue("field")) {
                        PsiAnnotation parent = PsiTreeUtil.getParentOfType(annotation, PsiAnnotation.class, true, PsiMethod.class);
                        if (null != parent && StripesConstants.VALIDATE_NESTED_PROPERTIES_ANNOTATION.equals(parent.getQualifiedName())) {

                        } else {
                            holder.registerProblem(annotation, "Makes sense only within @ValidateNestedProperties", ProblemHighlightType.GENERIC_ERROR_OR_WARNING);
                        }
                    }

                    PsiElement p = annotation.getParent().getParent();
                    if ((p instanceof PsiMethod && (StripesUtil.isGetter((PsiMethod) p) || StripesUtil.isGetter((PsiMethod) p)))
                        || p instanceof PsiField) {
                        return;
                    }
                    holder.registerProblem(annotation, "Applied to wrong member", ProblemHighlightType.GENERIC_ERROR_OR_WARNING);
                } else if (StripesConstants.HANDLES_EVENT_ANNOTATION.equals(annotation.getQualifiedName())
                    ||  StripesConstants.DEFAULT_HANDLER_ANNOTATION.equals(annotation.getQualifiedName())) {
                    PsiElement m = annotation.getParent().getParent();
                    if (m instanceof PsiMethod && ((PsiMethod)m).getReturnType().equalsToText(StripesConstants.STRIPES_RESOLUTION_CLASS)) {
                        return;
                    }
                    holder.registerProblem(annotation, "Applied to non Resolution method");
                } else if (StripesConstants.VALIDATION_METHOD_ANNOTATION.equals(annotation.getQualifiedName())) {
                    PsiElement m = annotation.getParent().getParent();
                    if (m instanceof PsiMethod && ((PsiMethod)m).getModifierList().hasModifierProperty("public")) {
                        PsiParameterList params = ((PsiMethod)m).getParameterList();
                        if (params.getParametersCount() == 0
                                || (params.getParametersCount() == 1
                                    && params.getParameters()[0].getType().equalsToText(StripesConstants.VALIDATION_ERRORS))
                        ) {
                            return;
                        }
                    }
                    holder.registerProblem(annotation, "Applied to wrong member");
                }
            }
        };
    }
}
