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

import com.intellij.codeInsight.lookup.LookupValueFactory;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.progress.ProcessCanceledException;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.PsiClassReferenceType;
import com.intellij.psi.util.PsiElementFilter;
import com.intellij.psi.util.PsiUtil;
import com.intellij.psi.xml.XmlTag;
import org.intellij.stripes.util.StripesConstants;
import org.intellij.stripes.util.StripesUtil;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Util Class
 * Created by IntelliJ IDEA.
 * User: Mario Arias
 * Date: 7/04/2008
 * Time: 04:01:50 AM
 */
public final class StripesReferenceUtil {

    private static List<String> EMPTY_STRING_LIST = new ArrayList<String>();

    public static PsiElementFilter NAME_ATTR_FILTER = new PsiElementFilter() {
        public boolean isAccepted(PsiElement element) {
            return element instanceof XmlTag
                    && ((XmlTag) element).getAttributeValue(StripesConstants.NAME_ATTR) != null;
        }
    };

    public static PsiElementFilter BEANCLASS_ATTR_FILTER = new PsiElementFilter() {
        public boolean isAccepted(PsiElement element) {
            return element instanceof XmlTag
                    && ((XmlTag) element).getAttributeValue(StripesConstants.BEANCLASS_ATTR) != null;
        }
    };

    /**
     * Get the Event methods (Resolution methods) for an ActionBean Class
     *
     * @param psiClass an ActionBean PsiClass
     * @return List with all Resolution Methods names
     */
    public static List<String> getResolutionMethodsNames(PsiClass psiClass) {
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
    public static Map<String, PsiMethod> getResolutionMethods(PsiClass psiClass) {
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

    public static String resolveHandlesEventAnnotation(PsiMethod method) {
        String retval = null;
        PsiAnnotation a = method.getModifierList().findAnnotation(StripesConstants.HANDLES_EVENT_ANNOTATION);
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
        } catch (ProcessCanceledException e) {
            //Do nothig, this exception is very common and can be throw for intellij
            //Logger don't be reported or just raise an ugly error
        } catch (Exception e) {
            Logger.getInstance("IntelliStripes").error("Error resolving annotation", e);
        }
        return retval;
    }

    static ElementManipulator<PsiLiteralExpression> getManipulator(PsiLiteralExpression expression) {
        return PsiManager.getInstance(expression.getProject()).getElementManipulatorsRegistry().getManipulator(expression);
    }

    /**
     * Returns list of properties for certain class and its superclasses that have setter method
     *
     * @param psiClass class to examine
     * @return {@link java.util.List java.util.List} of property names
     */
    public static List<String> getWritableProperties(PsiClass psiClass) {
        if (null == psiClass) return EMPTY_STRING_LIST;

        List<String> methodNames = new ArrayList<String>(16);
        for (PsiMethod psiMethod : psiClass.getAllMethods()) {
            String methodName = psiMethod.getName();
            if (methodName.startsWith("set") && psiMethod.getParameterList().getParametersCount() == 1) {
                PsiType propertyType = psiMethod.getParameterList().getParameters()[0].getType();
                PsiClass propertyClass = PsiUtil.resolveClassInType(propertyType);

                if (StripesUtil.isSubclass(StripesConstants.ACTION_BEAN_CONTEXT, propertyClass)) continue;

                if (propertyType instanceof PsiArrayType
                        || StripesUtil.isSubclass("java.util.List", propertyClass)
                        || StripesUtil.isSubclass("java.util.Map", propertyClass)) {
                    methodNames.add(StringUtil.decapitalize(methodName.replaceFirst("set", "")) + "[]");
                } else {
                    methodNames.add(StringUtil.decapitalize(methodName.replaceFirst("set", "")));
                }
            }
        }

        return methodNames;
    }

    /**
     * Returns properties of FileBean o caertain PsiClass that can be set by Stripes.
     *
     * @param psiClass
     * @return {@link java.util.List list} of property names
     */
    public static List<String> getFileBeanProperties(PsiClass psiClass) {
        if (null == psiClass) return EMPTY_STRING_LIST;

        List<String> methodNames = new ArrayList<String>(16);
        for (PsiMethod psiMethod : psiClass.getAllMethods()) {
            String methodName = psiMethod.getName();
            if (methodName.startsWith("set") && psiMethod.getParameterList().getParametersCount() == 1) {
                PsiClass propertyClass = PsiUtil.resolveClassInType(psiMethod.getParameterList().getParameters()[0].getType());
                if (StripesUtil.isSubclass(StripesConstants.FILE_BEAN, propertyClass)) {
                    methodNames.add(StringUtil.decapitalize(methodName.replaceFirst("set", "")));
                }
            }
        }

        return methodNames;
    }

    public static List<String> splitNestedVar(String var) {
        List<String> retval = new ArrayList<String>();
        for (int i = 0, wStart = 0, lBrace = 0; i < var.length(); i++) {
            if (var.charAt(i) == '.' && lBrace == 0) {
                retval.add(var.substring(wStart, i));
                wStart = i + 1;
            } else if (var.charAt(i) == '[') {
                lBrace++;
            } else if (var.charAt(i) == ']') {
                lBrace--;
            }

            if (i == (var.length() - 1)) {
                retval.add(var.substring(wStart, var.length()));
            }
        }
        return retval;
    }

    public static PsiClass resolveActionBeanSetterReturnType(PsiClass host, String field) {
        PsiClass cls = null;

        try {
            PsiMethod setter = host.findMethodsByName("set" + StringUtil.capitalize(field.replaceAll("\\[.*?\\]", "")), true)[0];
            PsiType propertyType = setter.getParameterList().getParameters()[0].getType();
            PsiClass propertyClass = PsiUtil.resolveClassInType(setter.getParameterList().getParameters()[0].getType());

            if (StripesUtil.isSubclass("java.util.List", propertyClass)) {
                if (((PsiClassReferenceType) propertyType).getParameters().length == 1) {
                    cls = PsiUtil.resolveClassInType(((PsiClassReferenceType) propertyType).getParameters()[0]);
                } else {
                    cls = StripesUtil.findPsiClassByName("java.lang.Object", host.getProject());
                }
            } else if (StripesUtil.isSubclass("java.util.Map", propertyClass)) {
                if (((PsiClassReferenceType) propertyType).getParameters().length == 2) {
                    cls = PsiUtil.resolveClassInType(((PsiClassReferenceType) propertyType).getParameters()[1]);
                } else {
                    cls = StripesUtil.findPsiClassByName("java.lang.Object", host.getProject());
                }
            } else {
                cls = propertyClass;
            }
        } catch (Exception e) {
            cls = null;
        }

        return cls;
    }

    /**
     * Gets s PsiClass from a ExressionList of type com.foo.MyClass.class
     *
     * @param list
     * @return
     */
    public static PsiClass getPsiClassFromExpressionList(PsiExpressionList list) {
        String className = list.getExpressions()[0] instanceof PsiClassObjectAccessExpression
                ? ((PsiClassObjectAccessExpression) list.getExpressions()[0]).getOperand().getType().getCanonicalText()
                : null;

        return StripesUtil.findPsiClassByName(className, list.getProject());
    }

    /**
     * Get an ActionBean PsiClass from a given tag parent
     *
     * @param xmlTag xml tag
     * @param parent parent's name
     * @return An ActionBean PsiClass
     */
    public static PsiClass getBeanClassFromParentTag(@NotNull XmlTag xmlTag, @NotNull String parent) {
        for (XmlTag tag = xmlTag.getParentTag(); tag != null; tag = tag.getParentTag()) {
            if (tag.getNamespace().startsWith(StripesConstants.TAGLIB_PREFIX)
                    && parent.equals(tag.getLocalName())) {
                PsiClass cls = StripesUtil.findPsiClassByName(tag.getAttributeValue(StripesConstants.BEANCLASS_ATTR), tag.getProject());
                return StripesUtil.isSubclass(StripesConstants.ACTION_BEAN, cls) ? cls : null;
            }
        }

        return null;
    }

    public static Object[] getVariants(@NotNull List<String> list, String prefix, @NotNull Icon icon) {
        if (list.isEmpty()) return PsiReferenceBase.EMPTY_ARRAY;

        Object[] retval = new Object[list.size()];
        for (int i = 0; i < list.size(); i++) {
            retval[i] = LookupValueFactory.createLookupValue(prefix + list.get(i), icon);
        }
        return retval;
    }
}
