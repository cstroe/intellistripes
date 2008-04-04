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

import com.intellij.facet.FacetManager;
import com.intellij.javaee.web.facet.WebFacet;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.jsp.WebDirectoryUtil;
import com.intellij.psi.impl.source.resolve.reference.PsiReferenceProvider;
import com.intellij.psi.impl.source.resolve.reference.ReferenceType;
import com.intellij.psi.jsp.JspFile;
import com.intellij.psi.scope.PsiScopeProcessor;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.xml.XmlTag;
import org.intellij.stripes.facet.StripesFacet;
import org.intellij.stripes.util.StripesConstants;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA. User: Mario Arias Date: 4/07/2007 Time: 12:15:26 AM
 */
public abstract class AbstractReferenceProvider implements PsiReferenceProvider {
// ------------------------------ FIELDS ------------------------------

    public static Map<String, PsiClass> cacheActionBeanMap = new HashMap<String, PsiClass>(25);

// -------------------------- STATIC METHODS --------------------------

    /**
     * Get an ActionBean PsiClass from tag &lt;stripes:form beanclass="com.foo.bar"&gt;
     *
     * @param xmlTag a stripes:[input] tag
     * @return An ActionBean PsiClass
     */
    @Nullable
    protected static PsiClass getFormBeanClass(XmlTag xmlTag) {
        return getBeanClassFromParentTag(xmlTag, StripesConstants.FORM_TAG);
    }

    protected static PsiClass getPsiClassFromExpressionList(PsiExpressionList list, int position) {
        PsiExpression psiExpression = list.getExpressions()[position];
        return null;
    }

    /**
     * Get an ActionBean PsiClass from a given tag parent
     *
     * @param xmlTag xml tag
     * @param parent parent's name
     * @return An ActionBean PsiClass
     */
    protected static PsiClass getBeanClassFromParentTag(XmlTag xmlTag, String parent) {
        while (true) {
            if (xmlTag == null) {
                return null;
            }
            //Maybe this tag is the stripes:<parent>, maybe not
            XmlTag parentTag = xmlTag.getParentTag();
            // the namespace ini this case stripes
            String namespace = xmlTag.getNamespace();
            boolean isNamespaceEquals = false;
            if (parentTag != null) {
                isNamespaceEquals = parentTag.getNamespace().equals(namespace);
            }
            //is the same namespace??
            if (isNamespaceEquals) {
                //is stripes:<parent> tag?
                if (parentTag.getName().equals(parentTag.getNamespacePrefix() + ':' + parent)) {
                    try {
                        //try to get the PsiClass for the beanclass parameter
                        return getPsiClass(parentTag, parentTag.getAttributeValue(StripesConstants.BEAN_CLASS_ATTRIBUTE));
                    }
                    catch (NullPointerException e) {
                        //form tag don't have beanclass parameter
                        return null;
                    }
                } else {
                    //recursively
                    xmlTag = parentTag;
                }
            } else {
                //recursively
                xmlTag = parentTag;
            }
        }
    }

    /**
     * Get PsiClass from a String
     *
     * @param psiElement For Obtain the project
     * @param className  className
     * @return a PsiClass
     */
    public static PsiClass getPsiClass(PsiElement psiElement, String className) {
        if (className == null) {
            return null;
        }
        Project project = psiElement.getProject();
        PsiManager psiManager = PsiManager.getInstance(project);
        //Cache
        if (cacheActionBeanMap.containsKey(className)) {
            return cacheActionBeanMap.get(className);
        } else {
            PsiClass psiClass = psiManager.findClass(className, GlobalSearchScope.allScope(project));
            cacheActionBeanMap.put(className, psiClass);
            return psiClass;
        }
    }

    /**
     * Get an ActionBean PsiClass from tag &lt;stripes:link beanclass="com.foo.bar"&gt;
     *
     * @param xmlTag a stripes:link-param tag
     * @return An ActionBean PsiClass
     */
    @Nullable
    protected static PsiClass getLinkBeanClass(XmlTag xmlTag) {
        return getBeanClassFromParentTag(xmlTag, StripesConstants.LINK_TAG);
    }

    /**
     * Get an ActionBean PsiClass from tag &lt;stripes:url beanclass="com.foo.bar"&gt;
     *
     * @param xmlTag a stripes:url tag
     * @return An Action Bean Class
     */
    @Nullable
    protected static PsiClass getUrlBeanClass(XmlTag xmlTag) {
        return getBeanClassFromParentTag(xmlTag, StripesConstants.URL_TAG);
    }

    /**
     * get PsiClass for stripes:link,useActionBean or Url with beanclass parameter
     *
     * @param xmlTag stripes:link,useActionBean or Url tag
     * @return An ActionBean PsiClass
     */
    protected static PsiClass getActionBeanClassFromTag(XmlTag xmlTag) {
        if (xmlTag == null) {
            return null;
        }
        String beanclass;
        try {
            beanclass = xmlTag.getAttribute(StripesConstants.BEAN_CLASS_ATTRIBUTE).getValue();
        }
        catch (NullPointerException e) {
            return null;
        }
        if (beanclass != null) {
            return getPsiClass(xmlTag, beanclass);
        } else {
            return null;
        }
    }

    /**
     * Get JspFile from a given Tag and parent
     *
     * @param xmlTag XmlTag
     * @param parent Parent
     * @return JspFile
     */
    protected static JspFile getJspFileFromParentTag(XmlTag xmlTag, String parent) {
        while (true) {
            if (xmlTag == null) {
                return null;
            }
            XmlTag parentTag = xmlTag.getParentTag();
            String namespace = xmlTag.getNamespace();
            boolean isNamespaceEquals = false;
            if (parentTag != null) {
                isNamespaceEquals = parentTag.getNamespace().equals(namespace);
            }
            if (isNamespaceEquals) {
                if (parentTag.getName().equals(parentTag.getNamespacePrefix() + ':' + parent)) {
                    try {
                        return getJspFile(parentTag, parentTag.getAttributeValue(StripesConstants.NAME_ATTRIBUTE));
                    }
                    catch (NullPointerException e) {
                        return null;
                    }
                } else {
                    xmlTag = parentTag;
                }
            } else {
                xmlTag = parentTag;
            }
        }
    }

    /**
     * Get a JspFile from a String
     *
     * @param psiElement PsiElement
     * @param url        String whit the URL
     * @return JspFile
     */
    public static JspFile getJspFile(PsiElement psiElement, String url) {
        WebDirectoryUtil webDirectoryUtil = WebDirectoryUtil.getWebDirectoryUtil(psiElement.getProject());
        Module module = ModuleUtil.findModuleForPsiElement(psiElement);
        FacetManager facetManager = FacetManager.getInstance(module);
        StripesFacet stripesFacet = facetManager.findFacet(StripesFacet.FACET_TYPE_ID, "Stripes");
        if (stripesFacet == null) {
            return null;
        }
        WebFacet webFacet = stripesFacet.getWebFacet();
        return (JspFile) webDirectoryUtil.findFileByPath(url, webFacet);
    }

// ------------------------ INTERFACE METHODS ------------------------

// --------------------- Interface PsiReferenceProvider ---------------------

    @Deprecated
    @NotNull
    public PsiReference[] getReferencesByElement(PsiElement psiElement, ReferenceType referenceType) {
        return PsiReference.EMPTY_ARRAY;
    }

    @Deprecated
    @NotNull
    public PsiReference[] getReferencesByString(String s, PsiElement psiElement, ReferenceType referenceType, int i) {
        return PsiReference.EMPTY_ARRAY;
    }

    public void handleEmptyContext(PsiScopeProcessor psiScopeProcessor, PsiElement psiElement) {

    }
}
