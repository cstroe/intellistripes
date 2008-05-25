package org.intellij.stripes.reference;

import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.xml.XmlAttributeValue;
import org.intellij.stripes.util.StripesConstants;
import org.jetbrains.annotations.Nullable;

public class JspTagAttrFileBeanSetterMethodsReference extends JspTagAttrReference {

    private PsiClass actionBeanPsiClass;

    public JspTagAttrFileBeanSetterMethodsReference(XmlAttributeValue xmlAttributeValue, PsiClass actionBeanPsiClass) {
        super(xmlAttributeValue);
        this.actionBeanPsiClass = actionBeanPsiClass;
    }

    @Nullable
    public PsiElement resolve() {
        PsiMethod[] arr = actionBeanPsiClass.findMethodsByName("set" + StringUtil.capitalize(getCanonicalText()), true);
        return arr.length > 0 ? arr[0] : null;
    }

    public Object[] getVariants() {
        return StripesReferenceUtil.getVariants(StripesReferenceUtil.getFileBeanProperties(actionBeanPsiClass), StripesConstants.FIELD_ICON);
    }
}
