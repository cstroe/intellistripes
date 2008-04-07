package org.intellij.stripes.reference.filters;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiExpressionList;
import com.intellij.psi.PsiNewExpression;
import com.intellij.psi.filters.ElementFilter;
import org.intellij.stripes.util.StripesConstants;


/**
 * Filter elamtns that are constructors of RedirectResolution/ForwardResolution with number of parameters passed as initializing parameter.
 * <p/>
 * Example: applies for all constructors of RedirectResolution/ForwardResolution with single Strign parameter
 * <p/>
 * registry.registerReferenceProvider(
 * new ResolutionConstructorFilter(1), PsiLiteralExpression.class, new PsiReferenceProviderBase() {
 * public PsiReference[] getReferencesByElement(PsiElement psiElement) {
 * return null;
 * }
 * )
 */
public class ResolutionConstructorFilter implements ElementFilter {
    private Integer count = -1;

    public ResolutionConstructorFilter(Integer count) {
        this.count = count;
    }

    public boolean isAcceptable(Object element, PsiElement context) {
        if (!(((PsiElement) element).getParent() instanceof PsiExpressionList)) return false;

        PsiElement constructor = ((PsiElement) element).getParent().getParent();
        if (!(constructor instanceof PsiNewExpression)) return false;

        String qName = ((PsiNewExpression) constructor).getClassReference().getQualifiedName();
        return (StripesConstants.FORWARD_RESOLUTION.equals(qName)
                || StripesConstants.REDIRECT_RESOLUTION.equals(qName))
                && ((PsiExpressionList) ((PsiElement) element).getParent()).getExpressions().length == count;
    }

    public boolean isClassAcceptable(Class hintClass) {
        return true;
    }
}
