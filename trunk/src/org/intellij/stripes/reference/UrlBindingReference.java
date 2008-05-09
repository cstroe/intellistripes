package org.intellij.stripes.reference;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.xml.XmlAttributeValue;

public class UrlBindingReference extends PsiReferenceBase<XmlAttributeValue> {

    public UrlBindingReference(XmlAttributeValue element) {
        super(element);
    }

    public PsiElement resolve() {
        return StripesReferenceUtil.getUrlBindings(getElement().getProject()).get(getCanonicalText());
    }

    public Object[] getVariants() {
        return StripesReferenceUtil.getUrlBindings(getElement().getProject()).keySet().toArray();
    }
}
