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
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.util.PropertyUtil;
import org.intellij.stripes.util.StripesConstants;
import org.intellij.stripes.util.StripesUtil;
import org.jetbrains.annotations.Nullable;

public class SetterReference<T extends PsiElement> extends PsiReferenceBase<T> {

	public static <T extends PsiElement> SetterReference<T> getNullReference() {
		return new SetterReference<T>(null, null) {
			@Override
			public TextRange getRangeInElement() {
				return null;
			}

			@Override
			public T getElement() {
				return null;
			}
		};
	}

	protected PsiClass actionBeanPsiClass;

	public void setActionBeanPsiClass(PsiClass actionBeanPsiClass) {
		this.actionBeanPsiClass = actionBeanPsiClass;
	}

	public SetterReference(T element, TextRange range) {
		super(element, range);
	}

	public SetterReference(T psiElement, TextRange textRange, PsiClass actionBeanPsiClass) {
		super(psiElement, textRange);
		this.actionBeanPsiClass = actionBeanPsiClass;
	}

	@Nullable
	public PsiElement resolve() {
		PsiMethod method = PropertyUtil.findPropertySetter(actionBeanPsiClass, getValue(), false, true);
		return StripesUtil.isActionBeanPropertySetter(method, false) ? method : null;
	}

	public Object[] getVariants() {
		return StripesReferenceUtil.getVariants(StripesReferenceUtil.getWritableProperties(actionBeanPsiClass, false), StripesConstants.FIELD_ICON);
	}
}