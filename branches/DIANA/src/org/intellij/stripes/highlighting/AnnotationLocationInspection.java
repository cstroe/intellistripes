/*
 * Copyright 2000-2009 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.intellij.stripes.highlighting;

import com.intellij.codeHighlighting.HighlightDisplayLevel;
import com.intellij.codeInspection.*;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.css.impl.util.RemoveElementAction;
import com.intellij.psi.util.PropertyUtil;
import com.intellij.psi.util.PsiTreeUtil;
import org.intellij.stripes.util.StripesConstants;
import org.intellij.stripes.util.StripesUtil;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public class AnnotationLocationInspection extends LocalInspectionTool {
    @Nls
    @NotNull
    public String getGroupDisplayName() {
        return StripesUtil.message("inspection.groupDisplayName");
    }

    @Nls
    @NotNull
    public String getDisplayName() {
        return StripesUtil.message("inspection.annotationLocations");
    }

    @NonNls
    @NotNull
    public String getShortName() {
        return StripesUtil.message("inspection.annotationLocations.shortName");
    }

    public boolean isEnabledByDefault() {
        return true;
    }


    @NotNull
    public HighlightDisplayLevel getDefaultLevel() {
        return HighlightDisplayLevel.WARNING;
    }

    @NotNull
    public PsiElementVisitor buildVisitor(@NotNull final ProblemsHolder holder, boolean isOnTheFly) {
        return new JavaElementVisitor() {
            public void visitReferenceExpression(PsiReferenceExpression expression) {
            }

            public void visitAnnotation(PsiAnnotation annotation) {
                if (StripesConstants.VALIDATE_ANNOTATION.equals(annotation.getQualifiedName())) {
                    if (annotation.getParameterList().getAttributes().length == 0) {
                        holder.registerProblem(annotation, StripesUtil.message("inspection.noAttributes"), ProblemHighlightType.J2EE_PROBLEM);
                    }

                    PsiAnnotationMemberValue onAttr = annotation.findAttributeValue(StripesConstants.ON_ATTR);
                    PsiAnnotationMemberValue requiredAttr = annotation.findDeclaredAttributeValue(StripesConstants.REQUIRED_ATTR);

                    if (onAttr != null && requiredAttr == null) {
                        holder.registerProblem(onAttr.getParent(), StripesUtil.message("inspection.validWithRequired"), new MyLocalQuickFix(annotation));
                    }

                    PsiAnnotation parent = PsiTreeUtil.getParentOfType(annotation, PsiAnnotation.class, true, PsiMethod.class);
                    PsiAnnotationMemberValue value = annotation.findDeclaredAttributeValue("field");

                    if (null != parent) {
                        if (StripesConstants.VALIDATE_NESTED_PROPERTIES_ANNOTATION.equals(parent.getQualifiedName())) {
                            if (null == value) {
                                holder.registerProblem(annotation, StripesUtil.message("inspection.noFieldAttribute"));
                            }
                            return;
                        }
                    }
// no @ValidateNestedProperties at top level, so field attribute doesn't make sense
                    if (null != value) {
                        holder.registerProblem(value.getParent(), StripesUtil.message("inspection.validWithinNested"),
                                ProblemHighlightType.GENERIC_ERROR_OR_WARNING, new RemoveElementAction(value.getParent())
                        );
                    }

// check if standalone @Validate annotaion is apllied to valid setter/getter or simple field
                    PsiElement p = annotation.getParent().getParent();
                    if ((p instanceof PsiMethod && (PropertyUtil.isSimplePropertyAccessor((PsiMethod) p))) || p instanceof PsiField) {
                        return;
                    }
                    holder.registerProblem(annotation, StripesUtil.message("inspection.wrongMember"), ProblemHighlightType.GENERIC_ERROR_OR_WARNING,
                            p instanceof PsiModifierListOwner
                                    ? new LocalQuickFix[]{new RemoveAnnotationQuickFix(annotation, (PsiModifierListOwner) p)}
                                    : LocalQuickFix.EMPTY_ARRAY
                    );
                } else if (StripesConstants.VALIDATE_NESTED_PROPERTIES_ANNOTATION.equals(annotation.getQualifiedName())) {
                    PsiElement p = annotation.getParent().getParent();
                    if ((p instanceof PsiMethod && PropertyUtil.isSimplePropertyAccessor((PsiMethod) p)) || p instanceof PsiField) {
                        return;
                    }
                    holder.registerProblem(annotation, StripesUtil.message("inspection.wrongMember"), ProblemHighlightType.GENERIC_ERROR_OR_WARNING,
                            p instanceof PsiModifierListOwner
                                    ? new LocalQuickFix[]{new RemoveAnnotationQuickFix(annotation, (PsiModifierListOwner) p)}
                                    : LocalQuickFix.EMPTY_ARRAY
                    );
                } else if (StripesConstants.HANDLES_EVENT_ANNOTATION.equals(annotation.getQualifiedName())
                        || StripesConstants.DEFAULT_HANDLER_ANNOTATION.equals(annotation.getQualifiedName())) {
                    PsiElement m = annotation.getParent().getParent();
                    if (m instanceof PsiMethod && ((PsiMethod) m).getReturnType().equalsToText(StripesConstants.STRIPES_RESOLUTION_CLASS)) {
                        return;
                    }
                    holder.registerProblem(annotation, StripesUtil.message("inspection.appliedToNonResolution"), m instanceof PsiModifierListOwner
                            ? new LocalQuickFix[]{new RemoveAnnotationQuickFix(annotation, (PsiModifierListOwner) m)}
                            : LocalQuickFix.EMPTY_ARRAY
                    );
                } else if (StripesConstants.VALIDATION_METHOD_ANNOTATION.equals(annotation.getQualifiedName())) {
                    PsiElement m = annotation.getParent().getParent();
                    if (m instanceof PsiMethod && ((PsiMethod) m).getModifierList().hasModifierProperty(PsiModifier.PUBLIC)) {
                        PsiParameterList params = ((PsiMethod) m).getParameterList();
                        if (params.getParametersCount() == 0
                                || (params.getParametersCount() == 1 && params.getParameters()[0].getType().equalsToText(StripesConstants.VALIDATION_ERRORS))) {
                            return;
                        }
                    }
                    holder.registerProblem(annotation, StripesUtil.message("inspection.wrongMember"), m instanceof PsiModifierListOwner
                            ? new LocalQuickFix[]{new RemoveAnnotationQuickFix(annotation, (PsiModifierListOwner) m)}
                            : LocalQuickFix.EMPTY_ARRAY
                    );
                } else if (StripesConstants.SPRING_BEAN_ANNOTATION.equals(annotation.getQualifiedName())) {
                    PsiElement m = annotation.getParent().getParent();
                    if (m instanceof PsiMethod && StripesUtil.isActionBeanPropertySetter((PsiMethod) m, true)) {
                        holder.registerProblem(annotation, StripesUtil.message("inspection.appliedToPubSetter"),
                                new RemoveAnnotationQuickFix(annotation, (PsiModifierListOwner) m)
                        );
                    }
                }
            }
        };
    }

    private static class MyLocalQuickFix implements LocalQuickFix {
        private PsiAnnotation annotation;

        private MyLocalQuickFix(PsiAnnotation annotation) {
            this.annotation = annotation;
        }

        @NotNull
        public String getName() {
            return "Add required attribute";
        }

        @NotNull
        public String getFamilyName() {
            return "Family";
        }

        public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor descriptor) {
            try {
                PsiAnnotation ann = JavaPsiFacade.getInstance(project).getElementFactory().createAnnotationFromText(StripesUtil.message("generate.validateWithRequired"), null);
                this.annotation.getParameterList().add(ann.getParameterList().getAttributes()[0]);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
