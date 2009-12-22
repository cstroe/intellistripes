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
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiUtil;
import com.intellij.psi.xml.XmlAttributeValue;
import org.intellij.stripes.util.StripesConstants;
import org.intellij.stripes.util.StripesUtil;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

public class FileBeanSetterReference extends PsiReferenceBase<XmlAttributeValue> {

	private PsiClass actionBeanPsiClass;

	public FileBeanSetterReference(XmlAttributeValue xmlAttributeValue, PsiClass actionBeanPsiClass) {
		super(xmlAttributeValue);
		this.actionBeanPsiClass = actionBeanPsiClass;
	}

	@Nullable
	public PsiElement resolve() {
		PsiMethod[] arr = actionBeanPsiClass.findMethodsByName("set" + StringUtil.capitalize(getValue().replaceAll("\\[.*?\\]", "")), true);
		if (arr.length > 0) {
			PsiMethod psiMethod = arr[0];

			PsiType propertyType = psiMethod.getParameterList().getParameters()[0].getType();
			PsiClass propertyClass = StripesReferenceUtil.resolveClassInType(propertyType, actionBeanPsiClass.getProject());

			if (StripesUtil.isSubclass(getElement().getProject(), StripesConstants.FILE_BEAN, propertyClass)) {
				if (getValue().indexOf('[') > 0) {
					propertyClass = PsiUtil.resolveClassInType(propertyType);
					return StripesUtil.isSubclass(getElement().getProject(), List.class.getName(), propertyClass)
						|| propertyType instanceof PsiArrayType
						|| StripesUtil.isSubclass(getElement().getProject(), Map.class.getName(), propertyClass) ? psiMethod : null;
				}
				return psiMethod;
			}
		}
		return null;
	}

	public Object[] getVariants() {
		return StripesReferenceUtil.getVariants(StripesReferenceUtil.getFileBeanProperties(actionBeanPsiClass), StripesConstants.FIELD_ICON);
	}

	public TextRange getRangeInElement() {
		int i = getElement().getText().indexOf('[');
		return i == -1 ? super.getRangeInElement() : new TextRange(1, i);
	}
}
