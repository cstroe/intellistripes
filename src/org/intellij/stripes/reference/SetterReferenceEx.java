package org.intellij.stripes.reference;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;

/**
 * Setter reference intended to work within reference set.
 * @param <T>
 */
public class SetterReferenceEx<T extends PsiElement> extends SetterReference<T> {

    private final StripesReferenceSetBase referenceSet;
    private final int index;

    public SetterReferenceEx(TextRange range, Boolean supportBraces, StripesReferenceSetBase referenceSet, Integer index) {
        super((T) referenceSet.getElement(), range, supportBraces);
        this.referenceSet = referenceSet;
        this.index = index;
    }

    public SetterReferenceEx(TextRange range, Boolean supportBraces, Boolean hasBraces, StripesReferenceSetBase referenceSet, Integer index) {
        super((T) referenceSet.getElement(), range, supportBraces);
        this.hasBraces = hasBraces;
        this.referenceSet = referenceSet;
        this.index = index;
    }

    public PsiClass getActionBeanPsiClass() {
        if (index == 0) {
            return referenceSet.getActionBeanPsiClass();
        } else {
	        final PsiElement method = referenceSet.getReference(this.index - 1).resolve();
            if (method instanceof PsiMethod) {
                return StripesReferenceUtil
                        .resolveClassInType(((PsiMethod)method).getParameterList().getParameters()[0].getType(), getElement().getProject());
            }
        }
        return null;
    }

    public StripesReferenceSetBase getReferenceSet() {
        return referenceSet;
    }

    public int getIndex() {
        return index;
    }
}
