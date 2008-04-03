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
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiUtil;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.util.IncorrectOperationException;
import org.intellij.stripes.util.StripesConstants;
import org.intellij.stripes.util.StripesUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA. User: Mario Arias Date: 21/09/2007 Time: 01:21:38 AM
 */
public class ActionBeanSetterMethodsReference extends StripesJspAttributeReference {
// ------------------------------ FIELDS ------------------------------

    private PsiClass actionBeanPsiClass;

// --------------------------- CONSTRUCTORS ---------------------------

    public ActionBeanSetterMethodsReference(XmlAttributeValue xmlAttributeValue, PsiClass actionBeanPsiClass) {
        super(xmlAttributeValue);
        this.actionBeanPsiClass = actionBeanPsiClass;
    }

// ------------------------ INTERFACE METHODS ------------------------

// --------------------- Interface PsiReference ---------------------

    /**
     * Ctrl + Click in the attribute name will be resolved
     *
     * @return Element
     */
    @Override
    @Nullable
    public PsiElement resolve() {
        if (getCanonicalText().endsWith(".")) return null;
        List<String> arr = StringUtil.split(getCanonicalText(), ".");
        try {
            if (arr.size() == 1) {
                return actionBeanPsiClass.findMethodsByName("set" + StringUtil.capitalize(getCanonicalText()), true)[0];
            } else if (arr.size() > 1) {
                PsiClass cls = actionBeanPsiClass;
                for (int i = 1; i < arr.size(); i++) {
                    PsiType type = cls.findMethodsByName("set" + StringUtil.capitalize(arr.get(i - 1)), true)[0]
                            .getParameterList().getParameters()[0].getType();
                    if (type instanceof PsiClassType) {
                        cls = ((PsiClassType) type).resolve();
                    }
                }
                return cls.findMethodsByName("set" + StringUtil.capitalize(arr.get(arr.size() - 1)), true)[0];
            }
        } catch (Exception e) {
            //e.printStackTrace(System.out);
            return null;
        }
        return null;
    }

    /**
     * When Method will renamed
     *
     * @param newElementName the new methodName
     * @return Element
     * @throws com.intellij.util.IncorrectOperationException
     *
     */
    @Override
    public PsiElement handleElementRename(String newElementName) throws IncorrectOperationException {
        ((XmlAttribute) xmlAttributeValue.getParent()).setValue(StringUtil.decapitalize(newElementName.replace("set", "")));
        return resolve();
    }

    @Override
    public PsiElement bindToElement(@NotNull PsiElement element) throws IncorrectOperationException {
        return super.bindToElement(element);
    }

    @Override
    public boolean isReferenceTo(PsiElement element) {
        return super.isReferenceTo(element);
    }

    @Override
    public Object[] getVariants() {
        PsiClass cls = actionBeanPsiClass;
        String prefix = "";
        String cText = getCanonicalText().replaceAll("IntellijIdeaRulezzz", "");
        cText = cText.substring(0, cText.indexOf(' '));

        if (cText.contains(".")) {
            List<String> arr = StringUtil.split(cText, ".");
            if (!cText.endsWith(".")) arr.remove(arr.size() - 1);
            prefix = StringUtil.join(arr, ".") + '.'/*Why Char ??:One Single Character String already
            creates an Object String in the Memory Heap, A char is a primitive value, with less memory footprint
            and have the same funcionality (not in every case)*/;

            for (String field : arr) {
                try {
                    PsiMethod setter = cls.findMethodsByName("set" + StringUtil.capitalize(field), true)[0];
                    cls = PsiUtil.resolveClassInType(setter.getParameterList().getParameters()[0].getType());
                } catch (Exception e) {
                    cls = null;
                    break;
                }
            }
        }

        if (null == cls) return PsiReference.EMPTY_ARRAY;

        List<String> properties = getProperties(cls);
        List<Object> variants = new ArrayList<Object>(properties.size());
        for (String property : properties) {
            variants.add(LookupValueFactory.createLookupValue(prefix + property, StripesConstants.FIELD_ICON));
        }
        return variants.toArray();
//        actionBeanPsiClass.accept(new PsiRecursiveElementVisitor() {
//            public void visitMethod(PsiMethod method) {
//                System.out.println(method.getName());
//            }
//        });
//        return EMPTY_ARRAY;
    }

    //Why Static??? static methods (in other languages Class Methods) have a minor memory footprint
    //cause they only have one instance (each method have an Instance of Method Class) for ClassLoader
    //Instead Normal Methods (or instance Methods) that have one instace for each Class Instance
    private static List<String> getProperties(PsiClass psiClass) {
        List<String> methodNames = new ArrayList<String>(16);
        if (null == psiClass) return methodNames;

        PsiMethod[] psiMethods = psiClass.getAllMethods();
        for (PsiMethod psiMethod : psiMethods) {
            String methodName = psiMethod.getName();
            if (methodName.startsWith("set")
                    && psiMethod.getParameterList().getParametersCount() == 1
                    && !isActionBeanCointextSetter(psiMethod)) {
                methodNames.add(StringUtil.decapitalize(methodName.replaceFirst("set", "")));
            }
        }

        return methodNames;
    }

    private static Boolean isActionBeanCointextSetter(PsiMethod method) {
        PsiClass propertyClass = PsiUtil.resolveClassInType(method.getParameterList().getParameters()[0].getType());
        return StripesUtil.isSubclass(propertyClass, StripesConstants.STRIPES_ACTION_BEAN_CONTEXT);
    }
}
