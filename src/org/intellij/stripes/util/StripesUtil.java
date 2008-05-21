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

package org.intellij.stripes.util;

import com.intellij.facet.FacetManager;
import com.intellij.ide.BrowserUtil;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.jsp.JspDirectiveKind;
import com.intellij.psi.jsp.JspFile;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PropertyUtil;
import com.intellij.psi.util.PsiElementFilter;
import com.intellij.psi.util.PsiUtil;
import com.intellij.psi.xml.XmlTag;
import com.intellij.ui.HyperlinkLabel;
import org.intellij.stripes.facet.StripesFacet;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import java.util.Hashtable;
import java.util.Map;

/**
 * Created by IntelliJ IDEA. User: Mario Arias Date: 2/07/2007 Time: 02:04:03 AM
 */
public final class StripesUtil {

    public static PsiElementFilter ALL_FILTER = new PsiElementFilter() {
        public boolean isAccepted(PsiElement element) {
            return true;
        }
    };

// -------------------------- STATIC METHODS --------------------------

    public static <T> T[] makeArray(T... parameters) {
        return parameters;
    }

    /**
     * Is Stripes Facet Configured
     *
     * @param module Module
     * @return true or false (D'oh)
     */
    public static boolean isStripesFacetConfigured(Module module) {
        if (module == null) {
            return false;
        }
        StripesFacet stripesFacet = getStripesFacet(module);
        return stripesFacet != null;
    }

    /**
     * Get Stripes Facet
     *
     * @param module Module
     * @return StripesFacetObject
     */
    public static StripesFacet getStripesFacet(Module module) {
        if (module != null) {
            FacetManager facetManager = FacetManager.getInstance(module);
            return facetManager.findFacet(StripesFacet.FACET_TYPE_ID, "Stripes");
        } else {
            return null;
        }
    }

    /**
     * Get a Module
     *
     * @param psiElement PsiElement
     * @return Module
     */
    public static Module getModule(PsiElement psiElement) {
        try {
            return ModuleUtil.findModuleForPsiElement(psiElement);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Create a HyperLink
     *
     * @param text Text
     * @param url  Url
     * @return a HyperLinkLaber, when the user Click on it IntelliJ Open a Navigator with the URL
     */
    public static HyperlinkLabel createLink(final String text, final @NonNls String url) {
        final HyperlinkLabel link = new HyperlinkLabel(text);
        link.addHyperlinkListener(new HyperlinkListener() {
            public void hyperlinkUpdate(HyperlinkEvent e) {
                BrowserUtil.launchBrowser(url);
            }
        });
        return link;
    }

    /**
     * This Jsp have a Stripes Taglib declared
     *
     * @param jspFile JspFile
     * @return true or false
     */
    public static boolean isStripesPage(JspFile jspFile) {
        for (XmlTag tag : jspFile.getDirectiveTags(JspDirectiveKind.TAGLIB, true)) {
            if (tag.getAttributeValue(StripesConstants.URI_ATTR) != null
                    && tag.getAttributeValue(StripesConstants.URI_ATTR).startsWith(StripesConstants.TAGLIB_PREFIX)) {
                return true;
            }
        }
        return false;
    }

    private static Map<String, PsiClass> PSI_CLASS_MAP = new Hashtable<String, PsiClass>();

    public static PsiClass findPsiClassByName(String className, Project project) {
        if (className == null) return null;

        PsiClass retval = PSI_CLASS_MAP.get(className);
        if (null == retval) {
            retval = PsiManager.getInstance(project).findClass(className, GlobalSearchScope.allScope(project));
            if (null != retval) PSI_CLASS_MAP.put(className, retval);
        }
        return retval;
    }

    public static Boolean isSubclass(String baseClassName, PsiClass cls) {
        if (cls == null) return false;
        PsiClass baseClass = findPsiClassByName(baseClassName, cls.getProject());

        return null != baseClass && (cls.isInheritor(baseClass, true) || cls.equals(baseClass));
    }

    public static XmlTag findParent(XmlTag childTag, PsiElementFilter stopFilter, PsiElementFilter returnFilter) {
        for (XmlTag tag = childTag.getParentTag(); tag != null; tag = tag.getParentTag()) {
            if (stopFilter.isAccepted(tag)) {
                return returnFilter.isAccepted(tag) ? tag : null;
            }
        }
        return null;
    }

    public static XmlTag findTag(XmlTag rootTag, PsiElementFilter filter) {
        if (filter.isAccepted(rootTag)) {
            return rootTag;
        } else {
            for (XmlTag tag : rootTag.getSubTags()) {
                XmlTag t = findTag(tag, filter);
                if (null != t) return t;
            }
        }
        return null;
    }

    /**
     * Processes XML tree and collects element matched defined criteria.
     *
     * @param rootTag      root of XML tree
     * @param stopFilter   filter triggering stop current tag children processing
     * @param incudeFilter filter triggering collecting of current tag
     * @param container    container to collect tags that matches criteria
     * @return container
     */
    public static <T> XmlTagContainer<T> collectTags(@NotNull XmlTag rootTag, @NotNull PsiElementFilter stopFilter,
                                                     @NotNull PsiElementFilter incudeFilter, @NotNull XmlTagContainer<T> container) {
        if (stopFilter.isAccepted(rootTag)) {
            if (incudeFilter.isAccepted(rootTag)) {
                container.add(rootTag);
            }
        } else {
            for (XmlTag tag : rootTag.getSubTags()) {
                collectTags(tag, stopFilter, incudeFilter, container);
            }
        }
        return container;
    }

    public static Boolean isSetter(PsiMethod method) {
        return null != method && method.getName().startsWith("set")
                && method.getParameterList().getParametersCount() == 1 && PsiType.VOID.equals(method.getReturnType());
    }

    public static Boolean isGetter(PsiMethod method) {
        return null != method && method.getName().startsWith("get")
                && method.getParameterList().getParametersCount() == 0 && !PsiType.VOID.equals(method.getReturnType());
    }

    public static Boolean isActionBeanPropertySetter(PsiMethod method, Boolean full) {
        if (method == null
                || (full && PropertyUtil.isSimplePropertySetter(method))) return false;

        PsiClass propertyClass = PsiUtil.resolveClassInType(method.getParameterList().getParameters()[0].getType());
        return method.hasModifierProperty(PsiModifier.PUBLIC)
                && !StripesUtil.isSubclass(StripesConstants.ACTION_BEAN_CONTEXT, propertyClass)
                && !StripesUtil.isSubclass(StripesConstants.FILE_BEAN, propertyClass);
    }
}
