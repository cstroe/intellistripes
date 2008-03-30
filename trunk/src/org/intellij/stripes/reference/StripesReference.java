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

import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.*;
import com.intellij.psi.jsp.JspFile;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlDocument;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.IncorrectOperationException;
import org.intellij.stripes.util.StripesConstants;
import org.intellij.stripes.util.StripesUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by IntelliJ IDEA. User: Mario Arias Date: 4/07/2007 Time: 12:57:24 AM
 */
public abstract class StripesReference implements PsiReference {
// ------------------------------ FIELDS ------------------------------

    private static final Object[] EMPTY_OBJECT = new Object[0];

// -------------------------- STATIC METHODS --------------------------

    /**
     * Get Setter methods for a class
     *
     * @param psiClass a Class
     * @return a list with Setter methods
     */
    protected static List<String> getSetterMethods(PsiClass psiClass) {
        List<String> properties = getWritableProperties(psiClass);
        List<String> methods = new ArrayList<String>(16);
        for (String property : properties) {
            methods.add(MessageFormat.format("set{0}", StringUtil.capitalize(property)));
        }
        return methods;
    }

    /**
     * Get Setter Methods without the set (setFoo(String bar) will be foo) from a class minus setContext for an ActionBean
     *
     * @param psiClass a Class
     * @return a list with writable properties
     */
    protected static List<String> getWritableProperties(PsiClass psiClass) {
        List<String> methodNames = new ArrayList<String>(16);
        PsiMethod[] psiMethods = psiClass.getMethods();
        for (PsiMethod psiMethod : psiMethods) {
            String methodName = psiMethod.getName();
            //method start with set and have only one parameter
            if (methodName.startsWith("set") && psiMethod.getParameterList().getParametersCount() == 1) {
                methodNames.add(StringUtil.decapitalize(methodName.replaceFirst("set", "")));
            }
        }
        methodNames.remove("context");
        //to recover the super class methods
        PsiClass superClass = psiClass.getSuperClass();
        assert superClass != null;
        if (!(superClass.getQualifiedName().equals("java.lang.Object"))) {
            methodNames.addAll(getWritableProperties(superClass));
        }
        return methodNames;
    }

    /**
     * get all the methos that match with the given List
     *
     * @param psiClass    a Class
     * @param methodNames list with method names
     * @return a List with methods
     */
    protected static PsiMethod[] getPsiMethods(PsiClass psiClass, List<String> methodNames) {
        List<PsiMethod> psiMethods = new ArrayList<PsiMethod>(16);
        for (String methodName : methodNames) {
            PsiMethod[] methods = psiClass.findMethodsByName(methodName, true);
            psiMethods.addAll(Arrays.asList(methods));
        }
        return psiMethods.toArray(PsiMethod.EMPTY_ARRAY);
    }

    /**
     * Get the Event methods (Resolution methods) for an ActionBean Class
     *
     * @param psiClass an ActionBean PsiClass
     * @return List with all Resolution Methods names
     */
    protected static String[] getResolutionMethodsNames(PsiClass psiClass) {
        PsiMethod[] psiMethods = getResolutionMethods(psiClass);
        String[] methodNames = new String[psiMethods.length];
        for (int i = 0; i < methodNames.length; i++) {
            methodNames[i] = psiMethods[i].getName();
        }
        return methodNames;
    }

    /**
     * Get the Event methods (Resolution methods) for an ActionBean Class
     *
     * @param psiClass an ActionBean PsiClass
     * @return List with all Resolution Methods
     */
    protected static PsiMethod[] getResolutionMethods(PsiClass psiClass) {
        List<PsiMethod> psiMethods = new ArrayList<PsiMethod>(16);
        PsiMethod[] methods = psiClass.getMethods();
        for (PsiMethod method : methods) {
            //method return Resolution and have 0 parameters
            PsiType returnType = method.getReturnType();
            if (returnType != null) {
                if (returnType.equalsToText(StripesConstants.STRIPES_RESOLUTION_CLASS) && method.getParameterList().getParametersCount() == 0) {
                    psiMethods.add(method);
                }
            }
        }
        //Add Resultion methods for super classes
        PsiClass superClass = psiClass.getSuperClass();
        assert superClass != null;
        if (!(superClass.getQualifiedName().equals("java.lang.Object"))) {
            psiMethods.addAll(Arrays.asList(getResolutionMethods(superClass)));
        }
        return psiMethods.toArray(PsiMethod.EMPTY_ARRAY);
    }

    /**
     * Get all the tags layout-component in a JspFIle
     *
     * @param jspFile JspFile
     * @return a bunch of tags
     */
    protected static XmlTag[] getLayoutComponents(JspFile jspFile) {
        // get the stripes namespace in the jspFile
        String stripesNamespace = StripesUtil.getStripesNamespace(jspFile);
        //yes this page have a stripes taglib
        if (stripesNamespace != null) {
            XmlDocument document = jspFile.getDocument();
            assert document != null;
            XmlTag rootTag = document.getRootTag();
            String layoutDefinition = stripesNamespace + ':' + StripesConstants.LAYOUT_DEFINITION;
            String layoutComponent = stripesNamespace + ':' + StripesConstants.LAYOUT_COMPONENT;
            assert rootTag != null;
            //this tag is the layout-definition?
            if (rootTag.getName().equals(layoutDefinition)) {
                // get all layout-component tags inside
                return getStripesTags(rootTag, layoutComponent);
            } else {
                //get the layout-definition tag
                XmlTag[] tags = getStripesTags(rootTag, layoutDefinition);
                try {
                    // get all layout-component tags inside
                    return getStripesTags(tags[0], layoutComponent);
                }
                catch (ArrayIndexOutOfBoundsException e) {
                    return null;
                }
            }
        }
        //Nop, this pages don't have a Stripes taglib, bad luck
        else {
            return null;
        }
    }

    /**
     * Get the Subtags
     *
     * @param xmlTag the parent tag
     * @param name   tag name
     * @return a lot of tags
     */
    protected static XmlTag[] getStripesTags(XmlTag xmlTag, String name) {
        return xmlTag.findSubTags(name);
    }

    /**
     * Get a XmlTag from a XmlAttribute
     *
     * @param xmlAttribute XmlAttribute
     * @return Xmltag
     */
    public static XmlTag getXmlTag(XmlAttribute xmlAttribute) {
        return (XmlTag) xmlAttribute.getParent().getParent();
    }

// ------------------------ INTERFACE METHODS ------------------------

// --------------------- Interface PsiReference ---------------------

    @Nullable
    public PsiElement resolve() {
        return null;
    }

    public String getCanonicalText() {
        return null;
    }

    public PsiElement handleElementRename(String newElementName) throws IncorrectOperationException {
        return null;
    }

    public PsiElement bindToElement(@NotNull PsiElement element) throws IncorrectOperationException {
        return null;
    }

    public boolean isReferenceTo(PsiElement element) {
        return element == resolve();
    }

    public Object[] getVariants() {
        return EMPTY_OBJECT;
    }

    public boolean isSoft() {
        return false;
    }
}
