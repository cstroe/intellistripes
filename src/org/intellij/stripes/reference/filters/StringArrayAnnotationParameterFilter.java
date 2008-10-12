package org.intellij.stripes.reference.filters;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiLiteralExpression;
import com.intellij.psi.filters.AnnotationParameterFilter;
import org.jetbrains.annotations.NonNls;

public class StringArrayAnnotationParameterFilter extends AnnotationParameterFilter{

    public StringArrayAnnotationParameterFilter(String annotationName, @NonNls String annotationAttributeName) {
        super(PsiLiteralExpression.class, annotationName, annotationAttributeName);
    }

    public boolean isAcceptable(Object o, PsiElement psiElement) {
        return super.isAcceptable(o, psiElement) || super.isAcceptable(((PsiElement)o).getParent(), psiElement);
    }
}
