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
import com.intellij.psi.impl.source.resolve.reference.PsiReferenceProviderBase;
import com.intellij.psi.jsp.el.ELExpressionHolder;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.psi.xml.XmlTag;
import org.intellij.stripes.reference.JspTagAttrResolutionMethodsReference;
import org.intellij.stripes.reference.StripesReferenceUtil;
import org.intellij.stripes.util.StripesConstants;
import org.jetbrains.annotations.NotNull;

/**
 * This class Provide References to Resolution Methods in stripes:submmit, image or button tags
 * <p/>
 * Created by IntelliJ IDEA. User: Mario Arias Date: 14/07/2007 Time: 10:34:11 PM
 */
public class TagResolutionMethodsReferenceProvider extends PsiReferenceProviderBase {
// ------------------------ INTERFACE METHODS ------------------------

// --------------------- Interface PsiReferenceProvider ---------------------

    @NotNull
    public PsiReference[] getReferencesByElement(PsiElement psiElement) {
        if (psiElement.getChildren().length > 1 && psiElement.getChildren()[1] instanceof ELExpressionHolder) return PsiReference.EMPTY_ARRAY;

        final PsiClass actionBeanPsiClass = StripesReferenceUtil.getBeanClassFromParentTag((XmlTag) psiElement.getParent().getParent(), StripesConstants.FORM_TAG);
        return actionBeanPsiClass == null
                ? PsiReference.EMPTY_ARRAY
                : new PsiReference[]{new JspTagAttrResolutionMethodsReference((XmlAttributeValue) psiElement, actionBeanPsiClass)};
    }
}
