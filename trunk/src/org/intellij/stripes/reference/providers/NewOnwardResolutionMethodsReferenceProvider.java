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
import org.intellij.stripes.reference.InClassResolutionMethodsReference;
import org.jetbrains.annotations.NotNull;

/**
 * Created by IntelliJ IDEA.
 * User: Mario Arias
 * Date: 10/04/2008
 * Time: 01:20:15 AM
 */
public class NewOnwardResolutionMethodsReferenceProvider extends AbstractReferenceProvider {
    @NotNull
    public PsiReference[] getReferencesByElement(PsiElement psiElement) {
        PsiExpressionList expressionList = (PsiExpressionList) psiElement.getParent();
        PsiClass psiClass = null;
        try {
            psiClass = getPsiClassFromExpressionList(expressionList, 0);
        } catch (NullPointerException e) {
            return PsiReference.EMPTY_ARRAY;
        }
        return new PsiReference[]{new InClassResolutionMethodsReference((PsiLiteralExpression) psiElement, psiClass)};
    }
}
