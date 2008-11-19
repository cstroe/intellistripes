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
import com.intellij.util.containers.ContainerUtil;
import org.intellij.stripes.util.StripesConstants;

import java.util.List;
import java.util.Map;

public class UrlBindingSetterReference<T extends PsiElement> extends SetterReference<T> {

	public UrlBindingSetterReference(T psiElement, TextRange textRange, PsiClass actionBeanPsiClass) {
		super(psiElement, textRange, actionBeanPsiClass);
	}

	@Override
	public Object[] getVariants() {
		List<String> l = StripesReferenceUtil.getWritableProperties(actionBeanPsiClass, false);
		l.add("$event");
		return  StripesReferenceUtil.getVariants(l, StripesConstants.FIELD_ICON);
	}

	@Override
	public PsiElement resolve() {
		if ("$event".equals(getValue())) {
			Map<String, PsiMethod> resMethods = StripesReferenceUtil.getResolutionMethods(actionBeanPsiClass);
			if (resMethods.size() <= 1) {
				return ContainerUtil.getFirstItem(resMethods.values(), null);
			} else {
				for (PsiMethod psiMethod : resMethods.values()) {
					if (psiMethod.getModifierList().findAnnotation(StripesConstants.DEFAULT_HANDLER_ANNOTATION) != null) {
						return psiMethod;
					}
				}				
			}

			return null;
		}
		return super.resolve();
	}
}
