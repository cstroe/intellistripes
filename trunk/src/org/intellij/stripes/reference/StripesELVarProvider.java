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

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.impl.source.jsp.JspImplicitVariableImpl;
import com.intellij.psi.impl.source.jsp.el.impl.ELElementProcessor;
import com.intellij.psi.impl.source.jsp.el.impl.JspElVariablesProvider;
import com.intellij.psi.jsp.JspFile;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.SearchScope;
import com.intellij.psi.util.PsiElementFilter;
import com.intellij.psi.xml.XmlTag;
import org.intellij.stripes.util.StripesConstants;
import org.intellij.stripes.util.StripesTagFilter;
import org.intellij.stripes.util.StripesUtil;
import org.intellij.stripes.util.XmlTagContainer;
import org.jetbrains.annotations.NotNull;

import java.util.Hashtable;
import java.util.Map;

public class StripesELVarProvider extends JspElVariablesProvider {

    private static String ACTION_BEAN = "actionBean";

    private static PsiElementFilter ACTION_BEAN_PROVIDER_FILTER = new StripesTagFilter() {
        protected boolean isDetailsAccepted(XmlTag tag) {
            return StripesConstants.USE_ACTION_BEAN_TAG.equals(tag.getLocalName())
                    || StripesConstants.FORM_TAG.equals(tag.getLocalName());
        }
    };
    public static PsiElementFilter BEANCLASS_ATTR_FILTER = new StripesTagFilter() {
        protected boolean isDetailsAccepted(XmlTag tag) {
            return tag.getAttributeValue(StripesConstants.BEANCLASS_ATTR) != null;
        }
    };

    public boolean processImplicitVariables(@NotNull PsiElement psiElement, @NotNull final JspFile jspFile, @NotNull ELElementProcessor elElementProcessor) {
        if (!StripesUtil.isStripesPage(jspFile)) return true;

        Map<String, String> actionBeans = StripesUtil.collectTags(jspFile.getDocument().getRootTag(),
                ACTION_BEAN_PROVIDER_FILTER, BEANCLASS_ATTR_FILTER,
                new XmlTagContainer<Map<String, String>>(new Hashtable<String, String>()) {
                    public void add(XmlTag tag) {
                        if (StripesConstants.USE_ACTION_BEAN_TAG.equals(tag.getLocalName())) {
                            if (tag.getAttributeValue(StripesConstants.ID_ATTR) == null && tag.getAttributeValue(StripesConstants.VAR_ATTR) == null) {
                                container.put(StripesConstants.USE_ACTION_BEAN_TAG, tag.getAttributeValue(StripesConstants.BEANCLASS_ATTR));
                            } else if (tag.getAttributeValue(StripesConstants.ID_ATTR) != null) {
                                container.put(tag.getAttributeValue(StripesConstants.ID_ATTR), tag.getAttributeValue(StripesConstants.BEANCLASS_ATTR));
                            } else if (tag.getAttributeValue(StripesConstants.VAR_ATTR) != null) {
                                container.put(tag.getAttributeValue(StripesConstants.VAR_ATTR), tag.getAttributeValue(StripesConstants.BEANCLASS_ATTR));
                            }
                        } else if (StripesConstants.FORM_TAG.equals(tag.getLocalName())) {
                            container.put(ACTION_BEAN, tag.getAttributeValue(StripesConstants.BEANCLASS_ATTR));
                        }
                    }
                }).getContainer();

        String clsName = actionBeans.remove(StripesConstants.USE_ACTION_BEAN_TAG);
        if (null != clsName) {
            actionBeans.put(ACTION_BEAN, clsName);
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
}
