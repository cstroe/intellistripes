/*
 * Copyright 2000-2009 JetBrains s.r.o.
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
package org.intellij.stripes.reference.contributors;

import com.intellij.psi.*;
import com.intellij.psi.impl.source.resolve.reference.PsiReferenceProviderBase;
import com.intellij.psi.jsp.el.ELExpressionHolder;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.ProcessingContext;
import org.intellij.stripes.components.project.StripesReferencesComponent;
import org.intellij.stripes.reference.GetterReference;
import org.intellij.stripes.util.StripesConstants;
import org.intellij.stripes.util.StripesUtil;
import org.jetbrains.annotations.NotNull;

public class GetterReferenceContributor extends PsiReferenceContributor {

    private PsiReferenceProvider REFERENCE_PROVIDER = new PsiReferenceProviderBase() {
        @NotNull
        @Override
        public PsiReference[] getReferencesByElement(@NotNull PsiElement element, @NotNull ProcessingContext context) {
            if (element.getChildren().length > 1 && element.getChildren()[1] instanceof ELExpressionHolder)
                return PsiReference.EMPTY_ARRAY;

            XmlTag tag = (XmlTag) element.getParent().getParent();
            final PsiClass actionBeanPsiClass = StripesUtil.findPsiClassByName(tag.getAttributeValue(StripesConstants.ENUM_ATTR), element.getProject());

            return actionBeanPsiClass == null
                    ? PsiReference.EMPTY_ARRAY
                    : new PsiReference[]{new GetterReference(element, actionBeanPsiClass)};
        }
    };

    public void registerReferenceProviders(PsiReferenceRegistrar registrar) {
        StripesReferencesComponent.registerXmlAttributeReferenceProvider(registrar, REFERENCE_PROVIDER,
                StripesConstants.LABEL_ATTR, StripesConstants.OPTIONS_ENUMERATION_TAG);

//        StripesReferencesComponent.registerXmlAttributeReferenceProvider(registrar, REFERENCE_PROVIDER,
//                StripesConstants.LABEL_ATTR, StripesConstants.OPTIONS_ENUMERATION_TAG, StripesConstants.OPTIONS_COLLECTION_TAG, StripesConstants.OPTIONS_MAP_TAG);
//        StripesReferencesComponent.registerXmlAttributeReferenceProvider(registrar, REFERENCE_PROVIDER,
//                StripesConstants.VALUE_ATTR, StripesConstants.OPTIONS_COLLECTION_TAG, StripesConstants.OPTIONS_MAP_TAG);
    }
}