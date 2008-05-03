/*
 * Copyright 2000-2007 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.intellij.stripes.reference;

import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiUtil;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.util.IncorrectOperationException;
import org.intellij.stripes.util.StripesConstants;
import org.intellij.stripes.util.StripesUtil;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Created by IntelliJ IDEA. User: Mario Arias Date: 21/09/2007 Time: 01:21:38 AM
 */
public class JspTagAttrSetterMethodsReference extends JspTagAttrReference {
// ------------------------------ FIELDS ------------------------------

    private PsiClass actionBeanPsiClass;

// --------------------------- CONSTRUCTORS ---------------------------

    public JspTagAttrSetterMethodsReference(XmlAttributeValue xmlAttributeValue, PsiClass actionBeanPsiClass) {
        super(xmlAttributeValue);
        this.actionBeanPsiClass = actionBeanPsiClass;
    }

// ------------------------ INTERFACE METHODS ------------------------

// --------------------- Interface PsiReference --  -------------------

    /**
     * Ctrl + Click in the attribute name will be resolved
     *
     * @return Element
     */
    @Override
    @Nullable
    public PsiElement resolve() {
        try {
            List<String> arr = StripesReferenceUtil.splitNestedVar(getCanonicalText().replaceAll("IntellijIdeaRulezzz ", ""));
            if (arr.size() == 0) return null;

            String f = arr.remove(arr.size() - 1);
            if ("".equals(f)) return null;

            PsiClass cls = actionBeanPsiClass;
            for (String field : arr) {
                cls = StripesReferenceUtil.resolveActionBeanSetterReturnType(cls, field);
                if (null == cls) break;
            }

            Integer i = f.lastIndexOf("[");
            if (f.endsWith("]")) {
                if (i > 0) f = f.substring(0, i);
            } else {
                i = -1;
            }

            PsiMethod method = cls.findMethodsByName("set" + StringUtil.capitalize(f), true)[0];
            if (i > 0) {
                PsiType propertyType = method.getParameterList().getParameters()[0].getType();
                PsiClass propertyClass = PsiUtil.resolveClassInType(propertyType);
                return StripesUtil.isSubclass("java.util.List", propertyClass)
                        || propertyType instanceof PsiArrayType
                        || StripesUtil.isSubclass("java.util.Map", propertyClass) ? method : null;
            }

            return method;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * When Method will renamed
     *
     * @param newElementName the new methodName
     * @return Element
     * @throws com.intellij.util.IncorrectOperationException
     *
     */
    @Override
    public PsiElement handleElementRename(String newElementName) throws IncorrectOperationException {
        ((XmlAttribute) xmlAttributeValue.getParent()).setValue(StringUtil.decapitalize(newElementName.replace("set", "")));
        return resolve();
    }

    @Override
    public Object[] getVariants() {
        System.out.println(getCanonicalText());
        PsiClass cls = actionBeanPsiClass;
        String prefix = "";
        String cText = getCanonicalText().replaceAll("IntellijIdeaRulezzz", "");
        int end = cText.indexOf(' ');
        cText = cText.substring(0, end == -1 ? cText.length() : end);

        if (cText.contains(".")) {
            List<String> arr = StripesReferenceUtil.splitNestedVar(cText);
            arr.remove(arr.size() - 1);
            prefix = StringUtil.join(arr, ".") + '.';
            for (String field : arr) {
                cls = StripesReferenceUtil.resolveActionBeanSetterReturnType(cls, field);
                if (cls == null) break;
            }
        }

        return StripesReferenceUtil.getVariants(StripesReferenceUtil.getWritableProperties(cls), prefix, StripesConstants.FIELD_ICON);
    }

}
