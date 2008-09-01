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

/**
 * Filter for Onward Resolution with Event parameter
 * Created by IntelliJ IDEA. User: Mario Arias Date: 22/03/2008 Time: 10:45:50 PM
 */
public abstract class NewOnwardResolutionFilter implements ElementFilter {

// ------------------------ INTERFACE METHODS ------------------------

// --------------------- Interface ElementFilter ---------------------

    public boolean isAcceptable(Object element, PsiElement psiElement) {

        if (element instanceof PsiExpressionList) {
            PsiExpressionList expressionList = (PsiExpressionList) element;
            if (expressionList.getExpressions().length == 2) {
                if (expressionList.getParent() instanceof PsiNewExpression) {
                    PsiNewExpression newExpression = (PsiNewExpression) expressionList.getParent();
                    return newExpression.getClassReference().getQualifiedName().equals(getResolutionClassName());
                } else {
                    return false;
                }
            } else {
                return false;
            }
        } else {
            return false;
        }

    }

    public boolean isClassAcceptable(Class aClass) {
        return true;
    }

    protected abstract String getResolutionClassName();
}
