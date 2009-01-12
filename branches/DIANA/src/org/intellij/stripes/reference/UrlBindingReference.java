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

package org.intellij.stripes.reference;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.xml.XmlAttributeValue;
import org.intellij.stripes.util.StripesConstants;

//TODO replace with ServletPathProviders (or smth)
public class UrlBindingReference extends PsiReferenceBase<XmlAttributeValue> {

    public UrlBindingReference(XmlAttributeValue element) {
        super(element);
    }

    public PsiElement resolve() {
        return StripesReferenceUtil.getUrlBindings(getElement().getProject()).get(getCanonicalText());
    }

    public Object[] getVariants() {
        return StripesReferenceUtil.getVariants(StripesReferenceUtil.getUrlBindings(getElement().getProject()).keySet(), StripesConstants.ACTION_BEAN_ICON);
    }
}
