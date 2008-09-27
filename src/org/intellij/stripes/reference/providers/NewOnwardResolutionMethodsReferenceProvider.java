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

package org.intellij.stripes.reference.providers;

import com.intellij.psi.*;
import com.intellij.util.ProcessingContext;
import org.intellij.stripes.reference.JavaStringResolutionMethodsReference;
import org.intellij.stripes.reference.StripesReferenceUtil;
import org.jetbrains.annotations.NotNull;

/**
 * Created by IntelliJ IDEA.
 * User: Mario Arias
 * Date: 10/04/2008
 * Time: 01:20:15 AM
 */
public class NewOnwardResolutionMethodsReferenceProvider extends PsiReferenceProvider {

	@NotNull
	public PsiReference[] getReferencesByElement(@NotNull PsiElement element, @NotNull ProcessingContext context) {
        final PsiClass psiClass = StripesReferenceUtil.getPsiClassFromExpressionList((PsiExpressionList) element.getParent());
        return psiClass == null
                ? PsiReference.EMPTY_ARRAY
                : new PsiReference[]{new JavaStringResolutionMethodsReference((PsiLiteralExpression) element, psiClass)};
	}
}
