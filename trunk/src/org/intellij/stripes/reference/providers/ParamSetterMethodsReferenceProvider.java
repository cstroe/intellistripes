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

package org.intellij.stripes.reference.providers;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.psi.xml.XmlTag;
import org.intellij.stripes.reference.ActionBeanSetterMethodsReference;
import org.jetbrains.annotations.NotNull;

/**
 * This Class provide References for Setter methods in stripes:param tag on name parameter
 * <p/>
 * Created by IntelliJ IDEA. User: Mario Arias Date: 22/09/2007 Time: 10:43:04 PM
 */
public class ParamSetterMethodsReferenceProvider extends AbstractReferenceProvider {
// ------------------------ INTERFACE METHODS ------------------------

// --------------------- Interface PsiReferenceProvider ---------------------

    @NotNull
    public PsiReference[] getReferencesByElement(PsiElement psiElement) {
        XmlAttributeValue value = (XmlAttributeValue) psiElement;
        XmlTag tag = (XmlTag) value.getParent().getParent();
        PsiClass actionBeanPsiClass = getLinkBeanClass(tag);
        if (actionBeanPsiClass == null) {
            actionBeanPsiClass = getUrlBeanClass(tag);
        }
        if (actionBeanPsiClass == null) {
            return PsiReference.EMPTY_ARRAY;
        }
        return new PsiReference[]{new ActionBeanSetterMethodsReference(value, actionBeanPsiClass)};
    }
}