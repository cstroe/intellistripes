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

package org.intellij.stripes.reference;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import com.intellij.psi.util.PropertyUtil;
import com.intellij.psi.util.PsiUtil;
import com.intellij.util.IncorrectOperationException;
import org.intellij.stripes.util.StripesConstants;
import org.intellij.stripes.util.StripesUtil;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class SetterReference<T extends PsiElement> extends PsiReferenceBase<T> {

    private PsiClass actionBeanPsiClass;
    protected Boolean supportBraces;
    protected Boolean hasBraces = false;

    public PsiClass getActionBeanPsiClass() {
        return actionBeanPsiClass;
    }

    public SetterReference(T element, TextRange range, Boolean supportBraces) {
        super(element, range);
        this.supportBraces = supportBraces;
    }

    public SetterReference(T element, TextRange range, PsiClass actionBeanPsiClass, Boolean supportBraces) {
        super(element, range);
        this.actionBeanPsiClass = actionBeanPsiClass;
        this.supportBraces = supportBraces;
    }

    /**
     * Resolves reference to method
     * Must return only valid Stripes setter.
     */
    public PsiElement resolve() {
        if (getVariantsEx().contains(getValue())) {
            return resolveEx();
        }

        PsiMethod method = PropertyUtil.findPropertySetter(getActionBeanPsiClass(), getValue(), false, true);
        if (!StripesUtil.isActionBeanPropertySetter(method, false)) return null;

        if (this.supportBraces) {
            PsiType propertyType = method.getParameterList().getParameters()[0].getType();
            PsiClass propertyClass = PsiUtil.resolveClassInType(propertyType);
            Boolean isIndexedType = StripesUtil.isSubclass(List.class.getName(), propertyClass)
                    || propertyType instanceof PsiArrayType
                    || StripesUtil.isSubclass(Map.class.getName(), propertyClass);

            method = (hasBraces() && !isIndexedType) || (isIndexedType && !hasBraces()) ? null : method;
        }

        return method;
    }

    private Boolean hasBraces() {
        return this.hasBraces;
    }

    public Object[] getVariants() {
        List<String> retval = new LinkedList<String>();
        retval.addAll(StripesReferenceUtil.getWritableProperties(getActionBeanPsiClass(), supportBraces));
        retval.addAll(0, getVariantsEx());

        return StripesReferenceUtil.getVariants(retval, StripesConstants.FIELD_ICON);
    }

    public PsiElement handleElementRename(final String newElementName) throws IncorrectOperationException {
        final String name = PropertyUtil.getPropertyName(newElementName);
        return super.handleElementRename(name == null ? newElementName : name);

//TODO research and implement handle of renaming properties from annotaion attributes
//            if (getElement() instanceof PsiLiteralExpression) {
//                BeanProperty bProp = BeanProperty.createBeanProperty((PsiMethod) resolve());
//                bProp.setName(name);
//                return bProp.getPsiElement();
//            }
//        return retval;
    }

    protected static List<String> EMPTY_VARIANTS = Arrays.asList();

    protected PsiElement resolveEx() {
        return null;
    }

    protected List<String> getVariantsEx() {
        return EMPTY_VARIANTS;
    }
}