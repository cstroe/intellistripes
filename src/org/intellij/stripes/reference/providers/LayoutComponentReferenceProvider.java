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

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceProvider;
import com.intellij.psi.jsp.JspFile;
import com.intellij.psi.util.PsiElementFilter;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.ProcessingContext;
import org.intellij.stripes.reference.JspTagAttrLayoutComponentReference;
import org.intellij.stripes.reference.StripesReferenceUtil;
import org.intellij.stripes.util.StripesConstants;
import org.intellij.stripes.util.StripesUtil;
import org.jetbrains.annotations.NotNull;

public class LayoutComponentReferenceProvider extends PsiReferenceProvider {
    private static PsiElementFilter LAYOUT_RENDER_FILTER = new PsiElementFilter() {
        public boolean isAccepted(PsiElement element) {
            return element instanceof XmlTag
                    && ((XmlTag) element).getNamespace().startsWith(StripesConstants.TAGLIB_PREFIX)
                    && StripesConstants.LAYOUT_RENDER_TAG.equals(((XmlTag) element).getLocalName());
        }
    };

    /**
     * Get JspFile from given layout-render tag that includes layout-component tag passed as parameter
     *
     * @param xmlTag XmlTag
     * @return JspFile
     */
    private static JspFile getLayoutDefinitionJspFile(@NotNull XmlTag xmlTag) {
        if (!StripesUtil.isStripesPage(xmlTag.getContainingFile())) return null;

        XmlTag layoutRenderTag = StripesUtil.findParent(xmlTag, LAYOUT_RENDER_FILTER, StripesReferenceUtil.NAME_ATTR_FILTER);
        if (layoutRenderTag != null) {
            PsiReference[] refs = layoutRenderTag.getAttribute("name").getValueElement().getReferences();
            for (PsiReference ref : refs) {
                PsiElement el = ref.resolve();
                return StripesUtil.isStripesPage(el) ? (JspFile) el : null;
            }
        }

        return null;
    }

	@NotNull
	public PsiReference[] getReferencesByElement(@NotNull PsiElement element, @NotNull ProcessingContext context) {
		final JspFile jspFile = getLayoutDefinitionJspFile((XmlTag) element.getParent().getParent());
		return jspFile == null
				? PsiReference.EMPTY_ARRAY
				: new PsiReference[]{new JspTagAttrLayoutComponentReference((XmlAttributeValue) element, jspFile)};
	}
}
