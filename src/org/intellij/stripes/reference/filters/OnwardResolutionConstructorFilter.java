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


/**
 * Filter elamtns that are constructors of RedirectResolution/ForwardResolution with number of parameters passed as initializing parameter.
 * <p/>
 * Example: applies for all constructors of RedirectResolution/ForwardResolution with single Strign parameter
 * <p/>
 * registry.registerReferenceProvider(
 * new ResolutionConstructorFilter(1), PsiLiteralExpression.class, new PsiReferenceProviderBase() {
 * public PsiReference[] getReferencesByElement(PsiElement psiElement) {
 * return null;
 * }
 * )
 */
public class OnwardResolutionConstructorFilter implements ElementFilter {
    private Integer count = -1;

    public OnwardResolutionConstructorFilter(Integer count) {
        this.count = count;
    }

    public boolean isAcceptable(Object element, PsiElement context) {
        if (!(((PsiElement) element).getParent() instanceof PsiExpressionList)) return false;

        PsiElement constructor = ((PsiElement) element).getParent().getParent();
        if (!(constructor instanceof PsiNewExpression)) return false;

        String qName = ((PsiNewExpression) constructor).getClassReference().getQualifiedName();
        return (StripesConstants.FORWARD_RESOLUTION.equals(qName)
                || StripesConstants.REDIRECT_RESOLUTION.equals(qName))
                && ((PsiExpressionList) ((PsiElement) element).getParent()).getExpressions().length == count;
    }

    public boolean isClassAcceptable(Class hintClass) {
        return true;
    }
}
