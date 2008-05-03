package org.intellij.stripes.util;

import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiElementFilter;
import com.intellij.psi.xml.XmlTag;

public abstract class StripesTagFilter implements PsiElementFilter {
    public boolean isAccepted(PsiElement element) {
        return element instanceof XmlTag
                && ((XmlTag) element).getNamespace().startsWith(StripesConstants.TAGLIB_PREFIX)
                && isDetailsAccepted((XmlTag) element);
    }

    protected abstract boolean isDetailsAccepted(XmlTag tag);
}
