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

/**This class provide References to Setter Methods for an Action Bean Class, in tags atripes:[input]
 *
 * Created by IntelliJ IDEA. User: Mario Arias Date: 4/07/2007 Time: 12:45:02 AM
 */
public class ActionBeanSetterMethodsReferenceProvider extends AbstractReferenceProvider
{
    //private static final Logger LOGGER = Logger.getInstance(ActionBeanSetterMethodsReferenceProvider.class.getName());

    @NotNull
    public PsiReference[] getReferencesByElement(PsiElement psiElement)
    {
        XmlAttributeValue value = (XmlAttributeValue) psiElement;
        XmlTag tag = (XmlTag) value.getParent().getParent();
        final PsiClass actionBeanPsiClass = getFormBeanClass(tag);
        if (actionBeanPsiClass == null)
        {
            return PsiReference.EMPTY_ARRAY;
        }        
        return new PsiReference[]{new ActionBeanSetterMethodsReference(value, actionBeanPsiClass)};
    }
}
