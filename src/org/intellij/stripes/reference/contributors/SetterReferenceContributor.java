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
import com.intellij.psi.filters.AndFilter;
import com.intellij.psi.filters.AnnotationParameterFilter;
import com.intellij.psi.filters.OrFilter;
import com.intellij.psi.filters.position.FilterPattern;
import com.intellij.psi.filters.position.SuperParentFilter;
import com.intellij.psi.impl.source.resolve.reference.PsiReferenceProviderBase;
import com.intellij.psi.util.PropertyUtil;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ProcessingContext;
import org.intellij.stripes.reference.SetterMethodsReferenceSet;
import org.intellij.stripes.reference.StripesReferenceUtil;
import org.intellij.stripes.reference.filters.QualifiedNameElementFilter;
import org.intellij.stripes.reference.filters.StringArrayAnnotationParameterFilter;
import org.intellij.stripes.util.StripesConstants;
import org.jetbrains.annotations.NotNull;

public class SetterReferenceContributor extends PsiReferenceContributor {

    public void registerReferenceProviders(PsiReferenceRegistrar registrar) {
        registrar.registerReferenceProvider(PsiJavaPatterns.literalExpression().and(new FilterPattern(
                new AndFilter(
                        new SuperParentFilter(new QualifiedNameElementFilter(StripesConstants.VALIDATE_NESTED_PROPERTIES_ANNOTATION)),
                        new AnnotationParameterFilter(PsiLiteralExpression.class, StripesConstants.VALIDATE_ANNOTATION, StripesConstants.FIELD_ATTR)
                )
        )), new PsiReferenceProviderBase() {

            @NotNull
            public PsiReference[] getReferencesByElement(@NotNull PsiElement element, @NotNull ProcessingContext context) {
                PsiMember parent = PsiTreeUtil.getParentOfType(element, PsiMethod.class);
                if (parent == null) parent = PsiTreeUtil.getParentOfType(element, PsiField.class);

                PsiClass cls = StripesReferenceUtil.resolveClassInType(PropertyUtil.getPropertyType(parent), element.getProject());
                return null == cls
                        ? PsiReference.EMPTY_ARRAY
                        : new SetterMethodsReferenceSet(element, cls).getPsiReferences();
            }
        });

        registrar.registerReferenceProvider(PsiJavaPatterns.literalExpression().and(new FilterPattern(
                new OrFilter(
                        new StringArrayAnnotationParameterFilter(StripesConstants.STRICT_BINDING_ANNOTATION, StripesConstants.ALLOW_ATTR),
                        new StringArrayAnnotationParameterFilter(StripesConstants.STRICT_BINDING_ANNOTATION, StripesConstants.DENY_ATTR)
                )
        )), new PsiReferenceProviderBase() {
            @NotNull
            public PsiReference[] getReferencesByElement(@NotNull PsiElement element, @NotNull ProcessingContext context) {
                PsiClass cls = PsiTreeUtil.getParentOfType(element, PsiClass.class);
                return null == cls
                        ? PsiReference.EMPTY_ARRAY
                        : new SetterMethodsReferenceSet(element, cls, true).getPsiReferences();
            }
        });
    }
}