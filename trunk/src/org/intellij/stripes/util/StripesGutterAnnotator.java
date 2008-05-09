package org.intellij.stripes.util;

import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder;
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

import java.util.ArrayList;
import java.util.Collection;

public class StripesGutterAnnotator implements Annotator {
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
            if (StripesGutterAnnotator.FILTER_ATTR.isAccepted(el) && StripesGutterAnnotator.FILTER_TAG.isAccepted(el.getParent().getParent())) {
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

            NavigationGutterIconBuilder.create(StripesConstants.STRIPES_JSP_ICON)
                .setTargets(proc.getTags()).setTooltipText("ActionBean usages").setPopupTitle("ActionBean usages")
                .setAlignment(GutterIconRenderer.Alignment.LEFT)
                .install(holder, ((PsiClass)psiElement).getNameIdentifier());
        }
    }
}
