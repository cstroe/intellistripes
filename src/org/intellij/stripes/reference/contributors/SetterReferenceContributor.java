package org.intellij.stripes.reference.contributors;

import com.intellij.psi.*;
import com.intellij.psi.filters.AndFilter;
import com.intellij.psi.filters.AnnotationParameterFilter;
import com.intellij.psi.filters.OrFilter;
import com.intellij.psi.filters.position.SuperParentFilter;
import com.intellij.psi.impl.source.resolve.reference.PsiReferenceProviderBase;
import com.intellij.psi.impl.source.resolve.reference.ReferenceProvidersRegistry;
import com.intellij.psi.util.PropertyUtil;
import com.intellij.psi.util.PsiTreeUtil;
import org.intellij.stripes.reference.SetterMethodsReferenceSet;
import org.intellij.stripes.reference.StripesReferenceUtil;
import org.intellij.stripes.reference.filters.QualifiedNameElementFilter;
import org.intellij.stripes.reference.filters.StringArrayAnnotationParameterFilter;
import org.intellij.stripes.util.StripesConstants;
import org.jetbrains.annotations.NotNull;

public class SetterReferenceContributor {

    public void registerReferenceProviders(ReferenceProvidersRegistry registry) {
        registry.registerReferenceProvider(
                new AndFilter(
                        new SuperParentFilter(new QualifiedNameElementFilter(StripesConstants.VALIDATE_NESTED_PROPERTIES_ANNOTATION)),
                        new AnnotationParameterFilter(PsiLiteralExpression.class, StripesConstants.VALIDATE_ANNOTATION, "field")
                ), PsiLiteralExpression.class, new PsiReferenceProviderBase() {
            @NotNull
            public PsiReference[] getReferencesByElement(PsiElement psiElement) {
                PsiMember parent = PsiTreeUtil.getParentOfType(psiElement, PsiMethod.class);
                if (parent == null) parent = PsiTreeUtil.getParentOfType(psiElement, PsiField.class);

                PsiClass cls = StripesReferenceUtil.resolveClassInType(PropertyUtil.getPropertyType(parent), psiElement.getProject());
                return null == cls
                        ? PsiReference.EMPTY_ARRAY
                        : new SetterMethodsReferenceSet(psiElement, cls).getPsiReferences();
            }
        });


        registry.registerReferenceProvider(
                new OrFilter(
                    new StringArrayAnnotationParameterFilter(StripesConstants.STRICT_BINDING_ANNOTATION, StripesConstants.ALLOW_ATTR),
                    new StringArrayAnnotationParameterFilter(StripesConstants.STRICT_BINDING_ANNOTATION, StripesConstants.DENY_ATTR)
                ), PsiLiteralExpression.class, new PsiReferenceProviderBase() {
            @NotNull
            public PsiReference[] getReferencesByElement(PsiElement psiElement) {
                PsiClass cls = PsiTreeUtil.getParentOfType(psiElement, PsiClass.class);
                return null == cls
                    ? PsiReference.EMPTY_ARRAY
                    : new SetterMethodsReferenceSet(psiElement, cls, true).getPsiReferences();
            }
        });
    }
}