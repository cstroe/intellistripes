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

package org.intellij.stripes.reference.filters;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiExpressionList;
import com.intellij.psi.PsiNewExpression;
import com.intellij.psi.filters.ElementFilter;
import org.intellij.stripes.util.StripesConstants;

public class NewStreamingResolutionFilter implements ElementFilter {
// ------------------------ INTERFACE METHODS ------------------------

// ------------------d--- Interface ElementFilter ---------------------

	public boolean isAcceptable(Object element, PsiElement psiElement) {
		PsiExpressionList expressionList = (PsiExpressionList) element;
		if (expressionList.getExpressions()[0].equals(psiElement)) {
			if (expressionList.getParent() instanceof PsiNewExpression) {
				PsiNewExpression newExpression = (PsiNewExpression) expressionList.getParent();
				return StripesConstants.STREAMING_RESOLUTION.equals(newExpression.getClassReference().getQualifiedName());
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	public boolean isClassAcceptable(Class aClass) {
		return PsiExpressionList.class.isAssignableFrom(aClass);
	}
}
