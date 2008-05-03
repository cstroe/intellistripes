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
import com.intellij.psi.util.PsiElementFilter;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.IncorrectOperationException;
import org.intellij.stripes.util.StripesConstants;
import org.intellij.stripes.util.StripesUtil;
import org.intellij.stripes.util.XmlTagContainer;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA. User: Mario Arias Date: 8/11/2007 Time: 11:14:26 PM
 */
public class JspTagAttrLayoutComponentReference extends JspTagAttrReference {

    private static PsiElementFilter LAYOUT_COMPONENT_FILTER = new PsiElementFilter() {
        public boolean isAccepted(PsiElement element) {
            return element instanceof XmlTag
                    && ((XmlTag) element).getNamespace().startsWith(StripesConstants.TAGLIB_PREFIX)
                    && StripesConstants.LAYOUT_COMPONENT_TAG.equals(((XmlTag) element).getLocalName());
        }
    };

    private static PsiElementFilter LAYOUT_DEFINITION_FILTER = new PsiElementFilter() {
        public boolean isAccepted(PsiElement element) {
            return element instanceof XmlTag
                    && ((XmlTag) element).getNamespace().startsWith(StripesConstants.TAGLIB_PREFIX)
                    && StripesConstants.LAYOUT_DEFINITION_TAG.equals(((XmlTag) element).getLocalName());
        }
    };

    /**
     * Get all the tags layout-component in a {@link JspFile}.
     * <p/>
     * Returns only tags having name attribute.
     *
     * @param jspFile {@link JspFile} to search tags for
     * @return a {@link java.util.List} of tags
     */
    private static List<XmlTag> getLayoutComponents(JspFile jspFile) {
        List<XmlTag> retval = new ArrayList<XmlTag>(16);
        XmlTag layoutDefTag = StripesUtil.findTag(jspFile.getDocument().getRootTag(), LAYOUT_DEFINITION_FILTER);
        if (null != layoutDefTag) {
            StripesUtil.collectTags(jspFile.getDocument().getRootTag(),
                    LAYOUT_COMPONENT_FILTER, StripesReferenceUtil.NAME_ATTR_FILTER,
                    new XmlTagContainer<List<XmlTag>>(retval) {
                        public void add(XmlTag tag) {
                            container.add(tag);
                        }
                    });
        }
        return retval;
    }

    private JspFile layoutDefJspFile;

    public JspTagAttrLayoutComponentReference(XmlAttributeValue xmlAttributeValue, JspFile layoutDefJsp) {
        super(xmlAttributeValue);
        this.layoutDefJspFile = layoutDefJsp;
    }

    @Override
    public Object[] getVariants() {
        List<XmlTag> tags = getLayoutComponents(layoutDefJspFile);
        Object[] variants = new Object[tags.size()];
        for (int i = 0; i < tags.size(); i++) {
            variants[i] = LookupValueFactory.createLookupValue(
                    tags.get(i).getAttributeValue(StripesConstants.NAME_ATTR), StripesConstants.LAYOUT_COMPONENT_ICON
            );
        }
        return variants;
    }

// ------------------------ INTERFACE METHODS ------------------------

// --------------------- Interface PsiReference ---------------------

    @Override
    @Nullable
    public PsiElement resolve() {
        for (XmlTag tag : getLayoutComponents(layoutDefJspFile)) {
            if (getCanonicalText().equals(tag.getAttributeValue("name"))) {
                return tag;
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
