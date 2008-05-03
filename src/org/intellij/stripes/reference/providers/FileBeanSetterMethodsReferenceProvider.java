package org.intellij.stripes.reference.providers;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.psi.xml.XmlTag;
import org.intellij.stripes.reference.JspTagAttrFileBeanSetterMethodsReference;
import org.intellij.stripes.reference.StripesReferenceUtil;
import org.intellij.stripes.util.StripesConstants;
import org.jetbrains.annotations.NotNull;

public class FileBeanSetterMethodsReferenceProvider extends AbstractReferenceProvider {
    @NotNull
    public PsiReference[] getReferencesByElement(PsiElement psiElement) {
        final PsiClass actionBeanPsiClass = StripesReferenceUtil.getBeanClassFromParentTag((XmlTag) psiElement.getParent().getParent(), StripesConstants.FORM_TAG);
        return actionBeanPsiClass == null
                ? PsiReference.EMPTY_ARRAY
                : new PsiReference[]{new JspTagAttrFileBeanSetterMethodsReference((XmlAttributeValue) psiElement, actionBeanPsiClass)};
    }
}
