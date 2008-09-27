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

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import com.intellij.psi.util.PropertyUtil;
import com.intellij.psi.util.PsiUtil;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.spring.model.properties.ReferenceSetBase;
import org.intellij.stripes.util.StripesConstants;
import org.intellij.stripes.util.StripesUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SetterMethodsReferenceSet extends ReferenceSetBase<SetterMethodsReferenceSet.SetterReference> {

    private final PsiClass actionBeanClass;

    public SetterMethodsReferenceSet(@NotNull PsiElement element, @NotNull PsiClass beanClass) {
        super(element, 0);
        this.actionBeanClass = beanClass;
    }

	@Override
	protected void parse(String str, int offset) {
        List<SetterReference> retval = getReferences();

        Boolean braces = getElement() instanceof XmlAttributeValue;
        if (!braces) return;

		retval.clear();
        for (int i = 0, wStart = 0, lBrace = 0, index = 0, wEnd = 0; i < str.length(); i++) {
            if (str.charAt(i) == '.' && lBrace == 0) {
                retval.add(createReference(new TextRange(offset + wStart, offset + wEnd + 1), index++, wEnd != (i - 1)));
                wStart = i + 1;
            } else if (str.charAt(i) == '[') {
                lBrace++;
            } else if (str.charAt(i) == ']') {
                lBrace--;
            } else if (lBrace == 0) {
                wEnd = i;
            }

            if (i == (str.length() - 1)) {
                retval.add(createReference(new TextRange(offset + wStart, offset + (wStart < wEnd ? wEnd + 1 : i + 1)), index++, wEnd != i));
            }
        }
    }

    @NotNull
    private SetterReference createReference(TextRange range, int index, Boolean hasBraces) {
        return new SetterReference(this, range, index, hasBraces);
    }

    @NotNull
    protected SetterReference createReference(TextRange range, int index) {
        return createReference(range, index, false);
    }

    public PsiClass getActionBeanClass() {
        return actionBeanClass;
    }

    public Boolean isSupportsBraces() {
        return getElement() instanceof XmlAttributeValue;
    }

    public static final class SetterReference extends PsiReferenceBase<PsiElement> {
        final private SetterMethodsReferenceSet referenceSet;
        final private Integer index;
        final private Boolean hasBraces;

        public SetterReference(SetterMethodsReferenceSet referenceSet, TextRange range, Integer index, Boolean hasBraces) {
            super(referenceSet.getElement(), range);
            this.index = index;
            this.referenceSet = referenceSet;
            this.hasBraces = hasBraces;
        }

        @Nullable
/**
 * Resolves reference to method
 * Must return only valid Stripes setter.
 */
        public PsiElement resolve() {
            PsiMethod method = PropertyUtil.findPropertySetter(getHostPsiClass(), getValue(), false, true);
            if (!StripesUtil.isActionBeanPropertySetter(method, false)) return null;

            if (referenceSet.isSupportsBraces()) {
                PsiType propertyType = method.getParameterList().getParameters()[0].getType();
                PsiClass propertyClass = PsiUtil.resolveClassInType(propertyType);
                Boolean isIndexedType = StripesUtil.isSubclass(List.class.getName(), propertyClass)
                        || propertyType instanceof PsiArrayType
                        || StripesUtil.isSubclass(Map.class.getName(), propertyClass);

                method = (hasBraces && !isIndexedType) ? null : method;
            }
            return method;
        }

        public Object[] getVariants() {
            return StripesReferenceUtil.getVariants(StripesReferenceUtil.getWritableProperties(
                    getHostPsiClass(), referenceSet.isSupportsBraces()), StripesConstants.FIELD_ICON
            );
        }

        private PsiClass getHostPsiClass() {
            if (index == 0) {
                return referenceSet.getActionBeanClass();
            } else {
                final PsiMethod method = (PsiMethod) referenceSet.getReference(index - 1).resolve();
                if (method != null) {
                    return StripesReferenceUtil.resolveClassInType(method.getParameterList().getParameters()[0].getType(), referenceSet.getElement().getProject());
                }
            }
            return null;
        }
    }
}
