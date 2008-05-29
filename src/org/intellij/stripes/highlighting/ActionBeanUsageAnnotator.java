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

package org.intellij.stripes.highlighting;

import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder;
import com.intellij.ide.util.PsiElementListCellRenderer;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.openapi.editor.markup.GutterIconRenderer;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.search.searches.ReferencesSearch;
import com.intellij.psi.util.PsiElementFilter;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.Processor;
import org.intellij.stripes.util.StripesConstants;
import org.intellij.stripes.util.StripesTagFilter;
import org.intellij.stripes.util.StripesUtil;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collection;

public class ActionBeanUsageAnnotator implements Annotator {
    private static PsiElementFilter FILTER_TAG = new StripesTagFilter() {
        protected boolean isDetailsAccepted(XmlTag tag) {
            return true;
        }
    };

    private static PsiElementFilter FILTER_ATTR = new PsiElementFilter() {
        public boolean isAccepted(PsiElement element) {
            return element instanceof XmlAttributeValue
                    && StripesConstants.BEANCLASS_ATTR.equals(((XmlAttribute) element.getParent()).getName());
        }
    };

    private static class XmlTagProcessor implements Processor<PsiReference> {
        private Collection<XmlTag> tags = new ArrayList<XmlTag>();
        public boolean process(PsiReference ref) {
            PsiElement el = ref.getElement();
            if (ActionBeanUsageAnnotator.FILTER_ATTR.isAccepted(el) && ActionBeanUsageAnnotator.FILTER_TAG.isAccepted(el.getParent().getParent())) {
                tags.add((XmlTag) el.getParent().getParent());
            }
            return true;
        }
        public Collection<XmlTag> getTags() {
            return tags;
        }
    }

    public void annotate(PsiElement psiElement, AnnotationHolder holder) {
        if (psiElement instanceof PsiClass && StripesUtil.isSubclass(StripesConstants.ACTION_BEAN, (PsiClass) psiElement)) {
            XmlTagProcessor proc = new XmlTagProcessor();
            ReferencesSearch.search(psiElement).forEach(proc);

            NavigationGutterIconBuilder.create(StripesConstants.STRIPES_ICON)
                .setTargets(proc.getTags()).setTooltipText("ActionBean usages").setPopupTitle("ActionBean usages")
                .setAlignment(GutterIconRenderer.Alignment.LEFT)
                .setCellRenderer(new PsiElementListCellRenderer<XmlTag>() {
                    public String getElementText(XmlTag psiElement) {
                        return psiElement.getName();
                    }

                    protected String getContainerText(PsiElement psiElement, String s) {
                        return "in " + psiElement.getContainingFile().getName();
                    }

                    protected int getIconFlags() {
                        return 0;
                    }

                    protected Icon getIcon(PsiElement psiElement) {
                        return null;
                    }
                })
                .install(holder, ((PsiClass)psiElement).getNameIdentifier());
        }
    }
}
