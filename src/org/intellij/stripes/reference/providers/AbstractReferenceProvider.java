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

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiReference;
import com.intellij.psi.impl.source.resolve.reference.PsiReferenceProvider;
import com.intellij.psi.impl.source.resolve.reference.ReferenceType;
import com.intellij.psi.scope.PsiScopeProcessor;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.xml.XmlTag;
import org.intellij.stripes.util.StripesConstants;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA. User: Mario Arias Date: 4/07/2007 Time: 12:15:26 AM
 */
public abstract class AbstractReferenceProvider implements PsiReferenceProvider
{
    public static Map<String, PsiClass> cacheActionBeanMap = new HashMap<String, PsiClass>(25);

    @Deprecated
    @NotNull
    public PsiReference[] getReferencesByElement(PsiElement psiElement, ReferenceType referenceType)
    {
        return PsiReference.EMPTY_ARRAY;
    }

    @Deprecated
    @NotNull
    public PsiReference[] getReferencesByString(String s, PsiElement psiElement, ReferenceType referenceType, int i)
    {
        return PsiReference.EMPTY_ARRAY;
    }

    public void handleEmptyContext(PsiScopeProcessor psiScopeProcessor, PsiElement psiElement)
    {

    }

    /**Get an ActionBean PsiClass from tag &lt;stripes:form beanclass="com.foo.bar"&gt;
     *
     * @param xmlTag a stripes:[input] tag
     * @return An ActionBean PsiClass
     */
    @Nullable
    protected static PsiClass getFormBeanClass(XmlTag xmlTag)
    {
        return getBeanClassFromParentTag(xmlTag, StripesConstants.FORM_TAG);
    }

    /**Get an ActionBean PsiClass from tag &lt;stripes:link beanclass="com.foo.bar"&gt;
     *
     * @param xmlTag a stripes:link-param tag
     * @return An ActionBean PsiClass
     */
    @Nullable
    protected static PsiClass getLinkBeanClass(XmlTag xmlTag)
    {
        return getBeanClassFromParentTag(xmlTag, StripesConstants.LINK_TAG);
    }


    /**Get an ActionBean PsiClass from a given tag parent
     *
     * @param xmlTag xml tag
     * @param parent parent's name
     * @return An ActionBean PsiClass
     */
    protected static PsiClass getBeanClassFromParentTag(XmlTag xmlTag,String parent)
    {
         if (xmlTag == null)
        {
            return null;
        }
        //Maybe this tag is the stripes:form, maybe not
        XmlTag parentTag = xmlTag.getParentTag();
        // the namespace ini this case stripes
        String namespace = xmlTag.getNamespace();
        boolean isNamespacesEquals = false;
        if (parentTag != null)
        {
            isNamespacesEquals = parentTag.getNamespace().equals(namespace);
        }
        //is the same namespace??
        if (isNamespacesEquals)
        {
            //is stripes:form tag?
            if (parentTag.getName().equals(parentTag.getNamespacePrefix() + ':' + parent))
            {
                try
                {
                    //try to get the PsiClass for the beanclass parameter
                    return getPsiClass(parentTag, parentTag.getAttribute(StripesConstants.BEAN_CLASS_ATTRIBUTE).getValue());
                }
                catch (NullPointerException e)
                {
                    //form tag don't have beanclass parameter
                    return null;
                }
            }
            else
            {
                //recursively
                return getBeanClassFromParentTag(parentTag,parent);
            }
        }
        else
        {
            //recursively
            return getBeanClassFromParentTag(parentTag,parent);
        }
    }


    /**get PsiClass for stripes:link,useActionBean or Url with beanclass parameter
     *
     * @param xmlTag stripes:link,useActionBean or Url tag
     * @return An ActionBean PsiClass
     */
    protected static PsiClass getActionBeanClassFromTag(XmlTag xmlTag)
    {
        if (xmlTag == null)
        {
            return null;
        }
        String beanclass = null;
        try
        {
            beanclass = xmlTag.getAttribute(StripesConstants.BEAN_CLASS_ATTRIBUTE).getValue();
        }
        catch (NullPointerException e)
        {
            return null;
        }
        if (beanclass != null)
        {
            return getPsiClass(xmlTag, beanclass);
        }
        else
        {
            return null;
        }
    }


    /**Get PsiClass from a String
     *
     * @param psiElement For Obtain the project
     * @param className className
     * @return a PsiClass
     */
    protected static PsiClass getPsiClass(PsiElement psiElement, String className)
    {
        Project project = psiElement.getProject();
        PsiManager psiManager = PsiManager.getInstance(project);
        //Cache
        if (cacheActionBeanMap.containsKey(className))
        {
            return cacheActionBeanMap.get(className);
        }
        else
        {
            PsiClass psiClass = psiManager.findClass(className, GlobalSearchScope.allScope(project));
            cacheActionBeanMap.put(className, psiClass);
            return psiClass;
        }
    }
}
