package org.intellij.stripes.reference.contributors;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiLiteralExpression;
import com.intellij.psi.PsiReference;
import com.intellij.psi.filters.OrFilter;
import com.intellij.psi.filters.position.ParentElementFilter;
import com.intellij.psi.impl.source.resolve.reference.PsiReferenceProviderBase;
import com.intellij.psi.impl.source.resolve.reference.ReferenceProvidersRegistry;
import com.intellij.psi.util.PsiTreeUtil;
import org.intellij.stripes.reference.JavaStringResolutionMethodsReference;
import org.intellij.stripes.reference.filters.NewForwardResolutionFilter;
import org.intellij.stripes.reference.filters.NewRedirectResolutionFilter;
import org.intellij.stripes.reference.filters.StringArrayAnnotationParameterFilter;
import org.intellij.stripes.reference.providers.NewOnwardResolutionMethodsReferenceProvider;
import org.intellij.stripes.util.StripesConstants;
import org.jetbrains.annotations.NotNull;

public class ResolutionReferenceContributor {

    public void registerReferenceProviders(ReferenceProvidersRegistry registry) {

        registry.registerReferenceProvider(
                new OrFilter(
                    new StringArrayAnnotationParameterFilter(StripesConstants.VALIDATION_METHOD_ANNOTATION, StripesConstants.ON_ATTR),
                    new StringArrayAnnotationParameterFilter(StripesConstants.VALIDATE_ANNOTATION, StripesConstants.ON_ATTR),
                    new StringArrayAnnotationParameterFilter(StripesConstants.AFTER_ANNOTATION, StripesConstants.ON_ATTR),
                    new StringArrayAnnotationParameterFilter(StripesConstants.BEFORE_ANNOTATION, StripesConstants.ON_ATTR)
                ), PsiLiteralExpression.class, new PsiReferenceProviderBase() {

                @NotNull
                public PsiReference[] getReferencesByElement(PsiElement psiElement) {
                    PsiClass cls = PsiTreeUtil.getParentOfType(psiElement, PsiClass.class);
                    return null == cls
                        ? PsiReference.EMPTY_ARRAY
                        : new PsiReference[]{new JavaStringResolutionMethodsReference((PsiLiteralExpression) psiElement, cls)};
                }
        });

        NewOnwardResolutionMethodsReferenceProvider referenceProvider = new NewOnwardResolutionMethodsReferenceProvider();
        registry.registerReferenceProvider(new ParentElementFilter(new NewForwardResolutionFilter()), PsiLiteralExpression.class, referenceProvider);
        registry.registerReferenceProvider(new ParentElementFilter(new NewRedirectResolutionFilter()), PsiLiteralExpression.class, referenceProvider);



    }

}
