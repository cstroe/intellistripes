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
package org.intellij.stripes.reference.contributors;

import com.intellij.patterns.PsiJavaPatterns;
import com.intellij.psi.*;
import com.intellij.psi.filters.OrFilter;
import com.intellij.psi.filters.position.FilterPattern;
import com.intellij.psi.filters.position.ParentElementFilter;
import com.intellij.psi.impl.source.resolve.reference.PsiReferenceProviderBase;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ProcessingContext;
import org.intellij.stripes.reference.JavaStringResolutionMethodsReference;
import org.intellij.stripes.reference.filters.NewForwardResolutionFilter;
import org.intellij.stripes.reference.filters.NewRedirectResolutionFilter;
import org.intellij.stripes.reference.filters.StringArrayAnnotationParameterFilter;
import org.intellij.stripes.reference.providers.NewOnwardResolutionMethodsReferenceProvider;
import org.intellij.stripes.util.StripesConstants;
import org.jetbrains.annotations.NotNull;

public class ResolutionReferenceContributor extends PsiReferenceContributor {

    public void registerReferenceProviders(PsiReferenceRegistrar registrar) {

        registrar.registerReferenceProvider(PsiJavaPatterns.literalExpression().and(new FilterPattern(
                new OrFilter(
                        new StringArrayAnnotationParameterFilter(StripesConstants.VALIDATION_METHOD_ANNOTATION, StripesConstants.ON_ATTR),
                        new StringArrayAnnotationParameterFilter(StripesConstants.VALIDATE_ANNOTATION, StripesConstants.ON_ATTR),
                        new StringArrayAnnotationParameterFilter(StripesConstants.AFTER_ANNOTATION, StripesConstants.ON_ATTR),
                        new StringArrayAnnotationParameterFilter(StripesConstants.BEFORE_ANNOTATION, StripesConstants.ON_ATTR),
                        new StringArrayAnnotationParameterFilter(StripesConstants.WIZARD_ANNOTATION, StripesConstants.START_EVENTS_ATTR)
                ))), new PsiReferenceProviderBase() {
            @NotNull
            public PsiReference[] getReferencesByElement(@NotNull PsiElement element, @NotNull ProcessingContext context) {
                PsiClass cls = PsiTreeUtil.getParentOfType(element, PsiClass.class);
                return null == cls
                        ? PsiReference.EMPTY_ARRAY
                        : new PsiReference[]{new JavaStringResolutionMethodsReference((PsiLiteralExpression) element, cls)};
            }
        });

        NewOnwardResolutionMethodsReferenceProvider referenceProvider = new NewOnwardResolutionMethodsReferenceProvider();
        registrar.registerReferenceProvider(PsiJavaPatterns.literalExpression().and(new FilterPattern(
                new ParentElementFilter(new NewForwardResolutionFilter())
        )), referenceProvider);

        registrar.registerReferenceProvider(PsiJavaPatterns.literalExpression().and(new FilterPattern(
                new ParentElementFilter(new NewRedirectResolutionFilter())
        )), referenceProvider);
    }

}
