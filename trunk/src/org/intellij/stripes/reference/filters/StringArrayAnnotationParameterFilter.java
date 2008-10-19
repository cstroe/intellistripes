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
import com.intellij.psi.PsiLiteralExpression;
import com.intellij.psi.filters.AnnotationParameterFilter;
import org.jetbrains.annotations.NonNls;

public class StringArrayAnnotationParameterFilter extends AnnotationParameterFilter {

    public StringArrayAnnotationParameterFilter(String annotationName, @NonNls String annotationAttributeName) {
        super(PsiLiteralExpression.class, annotationName, annotationAttributeName);
    }

    public boolean isAcceptable(Object o, PsiElement psiElement) {
        return super.isAcceptable(o, psiElement) || super.isAcceptable(((PsiElement) o).getParent(), psiElement);
    }
}
