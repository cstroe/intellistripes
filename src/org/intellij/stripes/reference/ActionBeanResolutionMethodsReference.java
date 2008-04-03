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

import com.intellij.codeInsight.lookup.LookupValueFactory;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.util.IncorrectOperationException;
import org.intellij.stripes.util.StripesConstants;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Created by IntelliJ IDEA. User: Mario Arias Date: 21/09/2007 Time: 01:28:16 AM
 */
public class ActionBeanResolutionMethodsReference extends StripesJspAttributeReference {
// ------------------------------ FIELDS ------------------------------

    private PsiClass actionBeanPsiClass;

// --------------------------- CONSTRUCTORS ---------------------------

    public ActionBeanResolutionMethodsReference(XmlAttributeValue xmlAttributeValue, PsiClass actionBeanPsiClass) {
        super(xmlAttributeValue);
        this.actionBeanPsiClass = actionBeanPsiClass;
    }

// --------------------- GETTER / SETTER METHODS ---------------------

    /**
     * Get all Posible Resolution Methods for an ActionBean Class
     *
     * @return an Array with References to Resolution Methods
     */
    @Override
    public Object[] getVariants() {
        List<String> names = getResolutionMethodsNames(actionBeanPsiClass);
        Object[] variants = new Object[names.size()];
        for (int i = 0; i < variants.length; i++) {
            variants[i] = LookupValueFactory.createLookupValue(names.get(i), StripesConstants.RESOLUTION_ICON);
        }
        return variants;
    }

// ------------------------ INTERFACE METHODS ------------------------

// --------------------- Interface PsiReference ---------------------

    /**
     * Ctrl + Click in the attribute name will be resolved
     *
     * @return Element
     */
    @Nullable
    @Override
    public PsiElement resolve() {
        for (PsiMethod method : getResolutionMethods(actionBeanPsiClass).values()) {
            if (getCanonicalText().equals(method.getName())
                    || getCanonicalText().equals(resolveHandlesEventAnnotation(method))) {
                return method;
            }
        }
        return null;
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
        ((XmlAttribute) xmlAttributeValue.getParent()).setValue(newElementName);
        return resolve();
    }
}
