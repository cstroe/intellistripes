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
import com.intellij.psi.PsiElement;
import com.intellij.psi.jsp.JspFile;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.IncorrectOperationException;
import org.intellij.stripes.util.StripesConstants;
import org.jetbrains.annotations.Nullable;

/**
 * Created by IntelliJ IDEA. User: Mario Arias Date: 8/11/2007 Time: 11:14:26 PM
 */
public class LayoutComponentReference extends StripesJspAttributeReference {
// ------------------------------ FIELDS ------------------------------

    private static final Object[] EMPTY_OBJECT = new Object[]{};
    private JspFile jspFile;

// --------------------------- CONSTRUCTORS ---------------------------

    public LayoutComponentReference(XmlAttributeValue xmlAttributeValue, JspFile jspFile) {
        super(xmlAttributeValue);
        this.jspFile = jspFile;
    }

// --------------------- GETTER / SETTER METHODS ---------------------

    @Override
    public Object[] getVariants() {
        XmlTag[] tags = getLayoutComponents(jspFile);
        if (tags == null) {
            return EMPTY_OBJECT;
        }
        Object[] variants = new Object[tags.length];
        for (int i = 0; i < tags.length; i++) {
            XmlTag tag = tags[i];
            String name = tag.getAttributeValue(StripesConstants.NAME_ATTRIBUTE);
            assert name != null;
            variants[i] = LookupValueFactory.createLookupValue(name, StripesConstants.LAYOUT_COMPONENT_ICON);
        }
        return variants;
    }

// ------------------------ INTERFACE METHODS ------------------------

// --------------------- Interface PsiReference ---------------------

    @Override
    @Nullable
    public PsiElement resolve() {
        XmlTag[] tags = getLayoutComponents(jspFile);
        if (tags == null) {
            return null;
        }
        XmlTag component = null;
        for (XmlTag tag : tags) {
            String name = tag.getAttributeValue("name");
            assert name != null;
            if (name.equals(getCanonicalText())) {
                component = tag;
            }
        }
        return component;
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
