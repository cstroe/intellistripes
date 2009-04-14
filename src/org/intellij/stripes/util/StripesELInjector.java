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

import com.intellij.lang.injection.MultiHostInjector;
import com.intellij.lang.injection.MultiHostRegistrar;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiLanguageInjectionHost;
import com.intellij.psi.PsiLiteralExpression;
import com.intellij.psi.filters.AnnotationParameterFilter;
import com.intellij.psi.filters.ElementFilter;
import com.intellij.psi.impl.source.jsp.el.ELContextProvider;
import com.intellij.psi.impl.source.jsp.el.ELLanguage;
import org.intellij.lang.regexp.RegExpLanguage;
import org.intellij.stripes.el.StripesELContextProvider;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

public class StripesELInjector implements MultiHostInjector {
	final private static ElementFilter VALIDATE_ANNOTATION_EXPRESSION_ATTR = new AnnotationParameterFilter(PsiLiteralExpression.class, StripesConstants.VALIDATE_ANNOTATION, StripesConstants.EXPRESSION_ATTR);
	final private static ElementFilter VALIDATE_ANNOTATION_MASK_ATTR = new AnnotationParameterFilter(PsiLiteralExpression.class, StripesConstants.VALIDATE_ANNOTATION, StripesConstants.MASK_ATTR);

	public void getLanguagesToInject(@NotNull MultiHostRegistrar registrar, @NotNull PsiElement context) {
		if (VALIDATE_ANNOTATION_EXPRESSION_ATTR.isAcceptable(context, context)) {
			final TextRange range = new TextRange(1, context.getTextLength() - 1);
			registrar.startInjecting(ELLanguage.INSTANCE)
				.addPlace(null, null, (PsiLanguageInjectionHost) context, range)
				.doneInjecting();

			try {
				context.putUserData(ELContextProvider.ourContextProviderKey, new StripesELContextProvider(context.getParent().getParent().getParent()));
			} catch (Exception e) {
				// do nothing ;)
			}
		} else if (VALIDATE_ANNOTATION_MASK_ATTR.isAcceptable(context, context)) {
			final TextRange range = new TextRange(1, context.getTextLength() - 1);
			registrar.startInjecting(RegExpLanguage.INSTANCE)
				.addPlace(null, null, (PsiLanguageInjectionHost) context, range)
				.doneInjecting();

		}
	}

	@NotNull
	public List<? extends Class<? extends PsiElement>> elementsToInjectIn() {
		return Arrays.asList(PsiLiteralExpression.class);
	}
}