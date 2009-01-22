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
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Various utility methods collection.
 */
public final class StripesUtil {

    /**
     * Is Stripes Facet Configured
     *
     * @param module Module
     * @return true or false
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
     * @param psiFile JspFile
     * @return true or false
     */

    public static boolean isStripesPage(PsiElement psiFile) {
        if (!(psiFile instanceof JspFile)) return false;

        for (XmlTag tag : ((JspFile) psiFile).getDirectiveTagsInContext(JspDirectiveKind.TAGLIB)) {
            if (tag.getAttributeValue(StripesConstants.URI_ATTR) != null
                    && tag.getAttributeValue(StripesConstants.URI_ATTR).startsWith(StripesConstants.TAGLIB_PREFIX)) {
                return true;
            }
        }
        return false;
    }

    /*
    * Cache for resolving PsiClasses by FQN.
    */
    public static Map<String, PsiClass> PSI_CLASS_MAP = new HashMap<String, PsiClass>(8);

    /**
     * Finds instance of {@link PsiClass} corresponding to FQN, passed as parameter.
     * <p/>
     * Method uses internal caching for speeding search up.
     *
     * @param className fully qualified class name to search for
     * @param project   current project
     * @return instance of {@link PsiClass} if search succesfull, null otherwise
     */
    public static PsiClass findPsiClassByName(String className, Project project) {
        if (className == null) return null;

        PsiClass retval = PSI_CLASS_MAP.get(className);
        if (null == retval) {
            retval = JavaPsiFacade.getInstance(project).findClass(className, GlobalSearchScope.allScope(project));
            if (null != retval) PSI_CLASS_MAP.put(className, retval);
        }

        try {
            retval.getContainingFile();
        } catch (Exception e) {
            PSI_CLASS_MAP.remove(className);
        }

        return retval;
    }

    /**
     * Checks if class presented by {@link PsiClass} instance of another class.
     *
     * @param baseClassName fully qualified name of parent class
     * @param cls           {@link PsiClass} that will be checked for inheritance
     * @return true if subclass, false otherwise
     */
    public static Boolean isSubclass(String baseClassName, PsiClass cls) {
        if (cls == null) return false;
        PsiClass baseClass = findPsiClassByName(baseClassName, cls.getProject());

        return null != baseClass && (cls.isInheritor(baseClass, true) || cls.equals(baseClass));
    }

    /**
     * Walks up XML tree to find and return parent element of XML tag matching criteria.
     *
     * @param childTag     start element of XML tree
     * @param stopFilter   filter triggering stop of walking up
     * @param returnFilter filter allowing return pf found XML tag
     * @return parent {@link com.intellij.psi.xml.XmlTag} or null
     */
    public static XmlTag findParent(XmlTag childTag, PsiElementFilter stopFilter, PsiElementFilter returnFilter) {
        for (XmlTag tag = childTag.getParentTag(); tag != null; tag = tag.getParentTag()) {
            if (stopFilter.isAccepted(tag)) {
                return returnFilter.isAccepted(tag) ? tag : null;
            }
        }
        return null;
    }

    /**
     * Processes XML tree and return child tag matching criteria.
     *
     * @param rootTag start element of XML treed
     * @param filter  filter triggering return of current tag
     * @return {@link com.intellij.psi.xml.XmlTag} or null
     */
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
     * Processes XML tree and collects tags matching criteria.
     *
     * @param rootTag      root of XML tree
     * @param stopFilter   filter triggering stop current tag children processing
     * @param incudeFilter filter triggering collecting of current tag
     * @param container    container to collect tags that match criteria
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

    /**
     * Checks method to be valid Stripes Action Bean property setter.
     *
     * @param method {@link com.intellij.psi.PsiMethod} to be validated
     * @param full   flag indicating full (stripes-specific and JavaBean) or partial (only stripes-specific) validation
     * @return true if method is valid, false otherwise
     */
    public static Boolean isActionBeanPropertySetter(PsiMethod method, Boolean full) {
        if (method == null
                || (full && PropertyUtil.isSimplePropertySetter(method))) return false;

        PsiClass propertyClass = PsiUtil.resolveClassInType(method.getParameterList().getParameters()[0].getType());
        return method.hasModifierProperty(PsiModifier.PUBLIC)
                && !StripesUtil.isSubclass(StripesConstants.ACTION_BEAN_CONTEXT, propertyClass)
                && !StripesUtil.isSubclass(StripesConstants.FILE_BEAN, propertyClass);
    }

//  Methods for working with i18n and formatting.

    private static ResourceBundle stripesBundle = ResourceBundle.getBundle("resources.Stripes");

    public static String message(String template) {
        return stripesBundle.getString(template);
    }

    public static String message(String template, Object... params) {
        return MessageFormat.format(stripesBundle.getString(template), params);
    }
}
