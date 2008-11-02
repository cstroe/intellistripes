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

import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.patterns.PsiJavaPatterns;
import com.intellij.psi.*;
import com.intellij.psi.filters.*;
import com.intellij.psi.filters.position.FilterPattern;
import com.intellij.psi.filters.position.SuperParentFilter;
import com.intellij.psi.filters.position.ParentElementFilter;
import com.intellij.psi.impl.source.resolve.reference.PsiReferenceProviderBase;
import com.intellij.psi.impl.source.resolve.reference.impl.providers.SeparatedReferenceSetBase;
import com.intellij.psi.util.PropertyUtil;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.ProcessingContext;
import com.intellij.xml.util.XmlUtil;
import org.intellij.stripes.reference.SetterMethodsReferenceSet;
import org.intellij.stripes.reference.StripesReferenceUtil;
import org.intellij.stripes.reference.SetterReference;
import org.intellij.stripes.reference.filters.QualifiedNameElementFilter;
import org.intellij.stripes.reference.filters.StringArrayAnnotationParameterFilter;
import org.intellij.stripes.util.StripesConstants;
import org.intellij.stripes.components.project.StripesReferencesComponent;
import org.jetbrains.annotations.NotNull;

public class SetterReferenceContributor extends PsiReferenceContributor {

	public void registerReferenceProviders(PsiReferenceRegistrar registrar) {
		registrar.registerReferenceProvider(PsiJavaPatterns.literalExpression().and(new FilterPattern(
			new AndFilter(
				new SuperParentFilter(new QualifiedNameElementFilter(StripesConstants.VALIDATE_NESTED_PROPERTIES_ANNOTATION)),
				new AnnotationParameterFilter(PsiLiteralExpression.class, StripesConstants.VALIDATE_ANNOTATION, StripesConstants.FIELD_ATTR)
			)
		)
		), new PsiReferenceProviderBase() {

            @NotNull
            public PsiReference[] getReferencesByElement(@NotNull PsiElement element, @NotNull ProcessingContext context) {
                PsiMember parent = PsiTreeUtil.getParentOfType(element, PsiMethod.class);
                if (parent == null) parent = PsiTreeUtil.getParentOfType(element, PsiField.class);

				PsiClass cls = StripesReferenceUtil.resolveClassInType(PropertyUtil.getPropertyType(parent), element.getProject());
				return null == cls
					? PsiReference.EMPTY_ARRAY
					: new SetterMethodsReferenceSet(element, cls).getPsiReferences();
			}
		}
		);

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

		XmlUtil.registerXmlAttributeValueReferenceProvider(registrar, new String[]{StripesConstants.FIELDS_ATTR},
			new ScopeFilter(new ParentElementFilter(new AndFilter(
				StripesReferencesComponent.STRIPES_NAMESPACE_FILTER,
				new ClassFilter(XmlTag.class),
				new TextFilter(StripesConstants.FIELD_METADATA_TAG)
			), 2)), new PsiReferenceProviderBase() {
				@NotNull public PsiReference[] getReferencesByElement(@NotNull PsiElement element, @NotNull ProcessingContext context) {
					final PsiClass actionBeanPsiClass = StripesReferenceUtil.getBeanClassFromParentTag(
						(XmlTag) element.getParent().getParent(), StripesConstants.FORM_TAG
					);
					return actionBeanPsiClass == null
						? PsiReference.EMPTY_ARRAY
						: new FieldsSetterReferenceSet(element, actionBeanPsiClass).getPsiReferences();
				}
			}
		);
	}

	private static class FieldsSetterReferenceSet extends SeparatedReferenceSetBase {
		private final PsiElement psiElement;
		private final PsiClass actionBeanPsiClass;

		public FieldsSetterReferenceSet(PsiElement psiElement, PsiClass actionBeanPsiClass) {
			super(StringUtil.stripQuotesAroundValue(psiElement.getText()), psiElement, 1, ',');
			this.psiElement = psiElement;
			this.actionBeanPsiClass = actionBeanPsiClass;
		}

		@NotNull
		protected PsiReference createReference(TextRange textRange, int i) {
			return new SetterReference((XmlAttributeValue) psiElement, textRange, actionBeanPsiClass);
		}
	}
}