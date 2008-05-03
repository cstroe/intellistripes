package org.intellij.stripes.reference;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.impl.source.jsp.JspImplicitVariableImpl;
import com.intellij.psi.impl.source.jsp.el.impl.ELElementProcessor;
import com.intellij.psi.impl.source.jsp.el.impl.JspElVariablesProvider;
import com.intellij.psi.impl.source.jsp.el.impl.MethodSignatureFilter;
import com.intellij.psi.jsp.JspFile;
import com.intellij.psi.jsp.el.ELExpressionHolder;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.SearchScope;
import com.intellij.psi.util.PsiElementFilter;
import com.intellij.psi.xml.XmlTag;
import org.intellij.stripes.util.StripesConstants;
import org.intellij.stripes.util.StripesTagFilter;
import org.intellij.stripes.util.StripesUtil;
import org.intellij.stripes.util.XmlTagContainer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Hashtable;
import java.util.Map;

public class StripesELVarProvider extends JspElVariablesProvider {

    private static PsiElementFilter ACTION_BEAN_PROVIDER_FILTER = new StripesTagFilter() {
        protected boolean isDetailsAccepted(XmlTag tag) {
            return StripesConstants.USE_ACTION_BEAN_TAG.equals(tag.getLocalName())
                    || StripesConstants.FORM_TAG.equals(tag.getLocalName());
        }
    };

    public boolean processImplicitVariables(@NotNull PsiElement psiElement, @NotNull final JspFile jspFile, @NotNull ELElementProcessor elElementProcessor) {
        if (!StripesUtil.isStripesPage(jspFile)) return true;

        Map<String, String> actionBeans = StripesUtil.collectTags(jspFile.getDocument().getRootTag(),
                ACTION_BEAN_PROVIDER_FILTER, StripesReferenceUtil.BEANCLASS_ATTR_FILTER,
                new XmlTagContainer<Map<String, String>>(new Hashtable<String, String>()) {
                    public void add(XmlTag tag) {
                        if (StripesConstants.USE_ACTION_BEAN_TAG.equals(tag.getLocalName())) {
                            if (tag.getAttributeValue(StripesConstants.ID_ATTR) == null && tag.getAttributeValue(StripesConstants.VAR_ATTR) == null) {
                                container.put("useActionBean", tag.getAttributeValue(StripesConstants.BEANCLASS_ATTR));
                            } else if (tag.getAttributeValue(StripesConstants.ID_ATTR) != null) {
                                container.put(tag.getAttributeValue(StripesConstants.ID_ATTR), tag.getAttributeValue(StripesConstants.BEANCLASS_ATTR));
                            } else if (tag.getAttributeValue(StripesConstants.VAR_ATTR) != null) {
                                container.put(tag.getAttributeValue(StripesConstants.VAR_ATTR), tag.getAttributeValue(StripesConstants.BEANCLASS_ATTR));
                            }
                        } else if (StripesConstants.FORM_TAG.equals(tag.getLocalName())) {
                            container.put("actionBean", tag.getAttributeValue(StripesConstants.BEANCLASS_ATTR));
                        }
                    }
                }).getContainer();

        String clsName = actionBeans.remove("useActionBean");
        if (null != clsName) {
            actionBeans.put("actionBean", clsName);
        }

        for (String actionBeanName : actionBeans.keySet()) {
            PsiClass actionBeanClass = StripesUtil.findPsiClassByName(actionBeans.get(actionBeanName), psiElement.getProject());
            if (actionBeanClass == null) continue;
            elElementProcessor.processVariable(
                    new JspImplicitVariableImpl(actionBeanClass,
                            actionBeanName, actionBeanClass.getManager().getElementFactory().createType(actionBeanClass),
                            actionBeanClass, JspImplicitVariableImpl.NESTED_RANGE) {

                        public PsiElement getDeclaration() {
                            return null;
                        }

                        @NotNull
                        public SearchScope getUseScope() {
                            return GlobalSearchScope.fileScope(jspFile);
                        }
                    }
            );
        }
        return true;
    }

    @Nullable
    public MethodSignatureFilter getMethodSignatureFilter(@NotNull PsiElement psiElement, ELExpressionHolder elExpressionHolder, PsiElement psiElement1) {
        return super.getMethodSignatureFilter(psiElement, elExpressionHolder, psiElement1);
    }
}
