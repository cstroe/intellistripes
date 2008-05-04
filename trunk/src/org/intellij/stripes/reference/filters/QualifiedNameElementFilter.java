package org.intellij.stripes.reference.filters;

import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiElement;
import com.intellij.psi.filters.ElementFilter;
import org.jetbrains.annotations.NotNull;

public class QualifiedNameElementFilter implements ElementFilter {
    private String qName;

    public QualifiedNameElementFilter(@NotNull String qName) {
        this.qName = qName;
    }

    public boolean isAcceptable(Object element, PsiElement context) {
        return element instanceof PsiAnnotation
                && qName.equals(((PsiAnnotation) element).getQualifiedName());
    }

    public boolean isClassAcceptable(Class hintClass) {
        return PsiAnnotation.class.isAssignableFrom(hintClass);
    }
}
