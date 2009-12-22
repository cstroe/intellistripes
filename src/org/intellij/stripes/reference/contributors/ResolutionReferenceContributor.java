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
package org.intellij.stripes.reference.contributors;

import com.intellij.patterns.PsiJavaPatterns;
import com.intellij.patterns.StandardPatterns;
import com.intellij.patterns.XmlPatterns;
import com.intellij.psi.*;
import com.intellij.psi.filters.OrFilter;
import com.intellij.psi.filters.position.FilterPattern;
import com.intellij.psi.filters.position.ParentElementFilter;
import com.intellij.psi.impl.source.resolve.reference.PsiReferenceProviderBase;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ProcessingContext;
import org.intellij.stripes.reference.JavaStringResolutionMethodsReference;
import org.intellij.stripes.reference.StripesReferenceUtil;
import org.intellij.stripes.reference.filters.NewForwardResolutionFilter;
import org.intellij.stripes.reference.filters.NewRedirectResolutionFilter;
import org.intellij.stripes.reference.filters.StringArrayAnnotationParameterFilter;
import org.intellij.stripes.reference.providers.TagResolutionMethodsReferenceProvider;
import org.intellij.stripes.util.StripesConstants;
import org.jetbrains.annotations.NotNull;

public class ResolutionReferenceContributor extends PsiReferenceContributor {
	private static final PsiReferenceProvider ONWARD_RESOLUTION_REFERENCE_PROVIDER = new PsiReferenceProvider() {
		@NotNull
		public PsiReference[] getReferencesByElement(@NotNull PsiElement element, @NotNull ProcessingContext context) {
			final PsiClass psiClass = StripesReferenceUtil.getPsiClassFromExpressionList((PsiExpressionList) element.getParent());
			return psiClass == null
				? PsiReference.EMPTY_ARRAY
				: new PsiReference[]{new JavaStringResolutionMethodsReference((PsiLiteralExpression) element, psiClass)};
		}
	};

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

		registrar.registerReferenceProvider(PsiJavaPatterns.literalExpression().and(new FilterPattern(
			new ParentElementFilter(new NewForwardResolutionFilter())
		)), ONWARD_RESOLUTION_REFERENCE_PROVIDER);

		registrar.registerReferenceProvider(PsiJavaPatterns.literalExpression().and(new FilterPattern(
			new ParentElementFilter(new NewRedirectResolutionFilter())
		)), ONWARD_RESOLUTION_REFERENCE_PROVIDER);

		registrar.registerReferenceProvider(XmlPatterns.xmlAttributeValue().withParent(XmlPatterns.xmlAttribute(StripesConstants.VALUE_ATTR).withParent(
			XmlPatterns.xmlTag().withNamespace(StripesConstants.STRIPES_TLD, StripesConstants.STRIPES_DYNAMIC_TLD)
				.withLocalName(StandardPatterns.string().oneOf(StripesConstants.INPUT_TAGS)).withChild(XmlPatterns.xmlAttribute(StripesConstants.NAME_ATTR).withText(StandardPatterns.string().contains("_eventName")))
		)), new TagResolutionMethodsReferenceProvider());
	}

}
