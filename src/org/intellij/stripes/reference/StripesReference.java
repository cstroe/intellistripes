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

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.*;
import com.intellij.psi.jsp.JspFile;
import com.intellij.psi.util.PsiUtil;
import com.intellij.psi.xml.XmlDocument;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.IncorrectOperationException;
import org.intellij.stripes.util.StripesConstants;
import org.intellij.stripes.util.StripesUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Created by IntelliJ IDEA. User: Mario Arias Date: 4/07/2007 Time: 12:57:24 AM
 */
public abstract class StripesReference implements PsiReference {
// ------------------------------ FIELDS ------------------------------

    private static final Object[] EMPTY_OBJECT = new Object[0];

// -------------------------- STATIC METHODS --------------------------

    protected static List<String> getWritableProperties(PsiClass psiClass) {
        List<String> methodNames = new ArrayList<String>(16);

        if (null == psiClass) return methodNames;

        PsiMethod[] psiMethods = psiClass.getMethods();
        for (PsiMethod psiMethod : psiMethods) {
            String methodName = psiMethod.getName();
            //method start with set and have only one parameter
            if (methodName.startsWith("set") && psiMethod.getParameterList().getParametersCount() == 1) {
                String mName = StringUtil.decapitalize(methodName.replaceFirst("set", ""));

                PsiType paramType = psiMethod.getParameterList().getParameters()[0].getType();
                if (paramType instanceof PsiClassType) {

                    PsiClass propertyClass = PsiUtil.resolveClassInType(paramType);
                    //Don't show ActionBeanContext Properties, maybe result in a security flaw
                    if (!StripesUtil.isSubclass(propertyClass, StripesConstants.STRIPES_ACTION_BEAN_CONTEXT)) {
                        List<String> props = getWritableProperties(propertyClass);
                        if (!props.isEmpty()) {
                            for (String prop : props) methodNames.add(mName + '.' + prop);
                        } else {
                            methodNames.add(mName);
                        }
                    }
                } else {
                    methodNames.add(mName);
                }
            }
        }

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

    protected static List<String> getResolutionMethodsNames(PsiClass psiClass) {
        List<String> methodNames = new ArrayList<String>(16);
        for (PsiMethod method : getResolutionMethods(psiClass).values()) {
            String s = resolveHandlesEventAnnotation(method);
            //add the @HandlesEvent value
            methodNames.add(null == s ? method.getName() : s);
        }
        return methodNames;
    }

    /**
     * Get the Event methods (Resolution methods) for an ActionBean Class
     *
     * @param psiClass an ActionBean PsiClass
     * @return List with all Resolution Methods
     */
    protected static Map<String, PsiMethod> getResolutionMethods(PsiClass psiClass) {
        Map<String, PsiMethod> psiMethods = new HashMap<String, PsiMethod>(8);

//Add Resultion methods for super classes
        PsiClass superClass = psiClass.getSuperClass();
        assert superClass != null;

        if (!("java.lang.Object".equals(superClass.getQualifiedName()))) {
            psiMethods.putAll(getResolutionMethods(superClass));
        }

        for (PsiMethod method : psiClass.getMethods()) {
//method return Resolution and have 0 parameters
            PsiType returnType = method.getReturnType();
            if (returnType != null) {
                if (returnType.equalsToText(StripesConstants.STRIPES_RESOLUTION_CLASS) && method.getParameterList().getParametersCount() == 0) {
                    psiMethods.put(method.getName(), method);
                }
            }
        }

        return psiMethods;
    }

    protected static String resolveHandlesEventAnnotation(PsiMethod method) {
        String retval = null;
        PsiAnnotation a = method.getModifierList().findAnnotation(StripesConstants.STRIPES_HANDLES_EVENT_ANNOTATION);
        try {
            if (a != null) {
                PsiAnnotationMemberValue psvm = a.findAttributeValue("value");
                if (psvm instanceof PsiLiteralExpression) {
                    retval = StringUtil.stripQuotesAroundValue(psvm.getText());
                } else if (psvm instanceof PsiReferenceExpression) {
                    PsiElement el = psvm.getReference().resolve();
                    if (el instanceof PsiField) {
                        retval = StringUtil.stripQuotesAroundValue(((PsiField) el).getInitializer().getText());
                    }
                }
            }
        } catch (Exception e) {
            Logger.getInstance("IntelliStripes").error("Error resolving annotation", e);
        }
        return retval;
    }

    /**
     * Get all the tags layout-component in a JspFIle
     *
     * @param jspFile JspFile
     * @return a bunch of tags
     */
    protected static List<XmlTag> getLayoutComponents(JspFile jspFile) {
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
            if (rootTag.getName().equals(layoutDefinition)) {// get all layout-component tags inside
                return getLayoutComponentTags(rootTag, layoutComponent);
            } else {//get the layout-definition tag
                XmlTag[] tags = rootTag.findSubTags(layoutDefinition);
                try {// get all layout-component tags inside
                    return getLayoutComponentTags(tags[0], layoutComponent);
                } catch (ArrayIndexOutOfBoundsException e) {
                    return null;
                }
            }
        }
//Nop, this pages don't have a Stripes taglib, bad luck
        return null;
    }

    protected static List<XmlTag> getLayoutComponentTags(XmlTag xmlTag, String name) {
        List<XmlTag> retval = new ArrayList<XmlTag>();

        if (name.equals(xmlTag.getName())) {
            retval.add(xmlTag);
        } else {
            for (XmlTag tag : xmlTag.getSubTags()) {
                retval.addAll(getLayoutComponentTags(tag, name));
            }
        }

        return retval;
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
