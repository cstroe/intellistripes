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

package org.intellij.stripes.util;

import com.intellij.find.findUsages.FindUsagesHandler;
import com.intellij.find.findUsages.FindUsagesHandlerFactory;
import com.intellij.psi.PsiElement;

public class StripesFindUsagesHandlerFactory extends FindUsagesHandlerFactory {
	public boolean canFindUsages(PsiElement psiElement) {
//		return psiElement instanceof PsiMethod
//			&& StripesUtil.isSubclass(StripesConstants.ACTION_BEAN, ((PsiMethod) psiElement).getContainingClass())
//			&& StripesUtil.isActionBeanPropertySetter((PsiMethod) psiElement, true);

		return false;
	}

	public FindUsagesHandler createFindUsagesHandler(PsiElement psiElement, boolean b) {
//		return psiElement instanceof PsiMethod
//			? new BeanPropertyFindUsagesHandler(BeanProperty.createBeanProperty((PsiMethod)psiElement))
//			: null;

		return null;
	}
}
