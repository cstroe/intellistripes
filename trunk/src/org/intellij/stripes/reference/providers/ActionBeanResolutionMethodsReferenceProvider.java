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
import org.intellij.stripes.reference.ActionBeanResolutionMethodsReference;
import org.jetbrains.annotations.NotNull;

/**
 * This class Provide References to Resolution Methods in stripes:submmit, image or button tags
 * <p/>
 * Created by IntelliJ IDEA. User: Mario Arias Date: 14/07/2007 Time: 10:34:11 PM
 */
public class ActionBeanResolutionMethodsReferenceProvider extends AbstractReferenceProvider {
// ------------------------ INTERFACE METHODS ------------------------

// --------------------- Interface PsiReferenceProvider ---------------------

    @NotNull
    public PsiReference[] getReferencesByElement(PsiElement psiElement) {
        //the attribute
        XmlAttributeValue value = (XmlAttributeValue) psiElement;
        //the tag
        XmlTag tag = (XmlTag) value.getParent().getParent();
        //the class
        final PsiClass actionBeanPsiClass = getFormBeanClass(tag);
        if (actionBeanPsiClass == null) {
            return PsiReference.EMPTY_ARRAY;
        }
        return new PsiReference[]{new ActionBeanResolutionMethodsReference(value, actionBeanPsiClass)};
    }
}