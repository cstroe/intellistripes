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

import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.patterns.PsiJavaPatterns;
import com.intellij.patterns.StandardPatterns;
import com.intellij.psi.*;
import com.intellij.psi.filters.AndFilter;
import com.intellij.psi.filters.AnnotationParameterFilter;
import com.intellij.psi.filters.OrFilter;
import com.intellij.psi.filters.position.FilterPattern;
import com.intellij.psi.filters.position.SuperParentFilter;
import com.intellij.psi.impl.source.resolve.reference.PsiReferenceProviderBase;
import com.intellij.psi.jsp.el.ELExpressionHolder;
import com.intellij.psi.util.PropertyUtil;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.ProcessingContext;
import org.intellij.stripes.components.project.StripesReferencesComponent;
import org.intellij.stripes.reference.SetterReferenceEx;
import org.intellij.stripes.reference.SetterReferenceExSet;
import org.intellij.stripes.reference.StripesReferenceSetBase;
import org.intellij.stripes.reference.StripesReferenceUtil;
import org.intellij.stripes.reference.filters.QualifiedNameElementFilter;
import org.intellij.stripes.reference.filters.StringArrayAnnotationParameterFilter;
import org.intellij.stripes.util.StripesConstants;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class SetterReferenceContributor extends PsiReferenceContributor {

	private static class StrictBindingReference extends SetterReferenceEx<PsiElement> {
		private static List<String> EXTRA_VALUES = Arrays.asList("*", "**");

		public StrictBindingReference(TextRange range, Boolean supportBraces, StripesReferenceSetBase referenceSet, Integer index) {
			super(range, supportBraces, referenceSet, index);
		}

		@Override
		protected List<String> getVariantsEx() {
			return EXTRA_VALUES;
		}

		@Override
		protected PsiElement resolveEx() {
			if (getIndex() == 0) return getReferenceSet().getActionBeanPsiClass();
			PsiMethod method = (PsiMethod) getReferenceSet().getReference(getIndex() - 1).resolve();
			return null == method ? null : method.getContainingClass();
		}
	}

	private static class UrlBindingReference extends PsiPolyVariantReferenceBase<PsiElement> {
		private static String[] VARIANTS = {"$event"};

		private PsiClass actionBeanClass;

		public UrlBindingReference(PsiElement psiElement, TextRange range, PsiClass actionBeanClass) {
			super(psiElement, range, false);
			this.actionBeanClass = actionBeanClass;
		}

		public Object[] getVariants() {
			return VARIANTS;
		}

		@NotNull
		public ResolveResult[] multiResolve(boolean incompleteCode) {
			Collection<PsiMethod> resMethods = StripesReferenceUtil.getResolutionMethodsAsList(actionBeanClass);
			ResolveResult[] retval = new ResolveResult[resMethods.size()];
			int i = 0;
			for (PsiMethod method : resMethods) {
				retval[i++] = new PsiElementResolveResult(method);
			}

			return retval;
		}
	}

	/**
	 * This class provide References to ActionBean setter methods in stripes tags.
	 */
	private static class SetterMethodsReferenceProvider extends PsiReferenceProvider {

		private String parentTag;

		public SetterMethodsReferenceProvider(String parentTag) {
			this.parentTag = parentTag;
		}

		@NotNull
		public PsiReference[] getReferencesByElement(@NotNull PsiElement element, @NotNull ProcessingContext context) {
			if (PsiTreeUtil.getChildOfType(element, ELExpressionHolder.class) != null) {
				return PsiReference.EMPTY_ARRAY;
			}

			final PsiClass actionBeanPsiClass = StripesReferenceUtil.getBeanClassFromParentTag(
				(XmlTag) element.getParent().getParent(), parentTag
			);

			return actionBeanPsiClass == null
				? PsiReference.EMPTY_ARRAY
				: new SetterReferenceExSet(element, 1, '.', actionBeanPsiClass, true).getPsiReferences();
		}
	}

	public void registerReferenceProviders(PsiReferenceRegistrar registrar) {

//errors tag add Reference Provider for Setters Method on parameter field
		StripesReferencesComponent.registerXmlAttributeReferenceProvider(registrar, new SetterMethodsReferenceProvider(StripesConstants.FORM_TAG),
			StripesConstants.FIELD_ATTR, StripesConstants.ERRORS_TAG);
//all stripes tags for input form add Reference Provider for Setters Method
		StripesReferencesComponent.registerXmlAttributeReferenceProvider(registrar, new SetterMethodsReferenceProvider(StripesConstants.FORM_TAG),
			StripesConstants.NAME_ATTR, StripesConstants.INPUT_TAGS);
//param tag add Reference Provider for Setter Methods
		StripesReferencesComponent.registerXmlAttributeReferenceProvider(registrar, new SetterMethodsReferenceProvider(StripesConstants.LINK_TAG),
			StripesConstants.NAME_ATTR, StripesConstants.PARAM_TAG);
		StripesReferencesComponent.registerXmlAttributeReferenceProvider(registrar, new SetterMethodsReferenceProvider(StripesConstants.URL_TAG),
			StripesConstants.NAME_ATTR, StripesConstants.PARAM_TAG);

		StripesReferencesComponent.registerXmlAttributeReferenceProvider(
			registrar, new PsiReferenceProviderBase() {
				@NotNull
				public PsiReference[] getReferencesByElement(@NotNull PsiElement element, @NotNull ProcessingContext context) {
					if (PsiTreeUtil.getChildOfType(element, ELExpressionHolder.class) != null) {
						return PsiReference.EMPTY_ARRAY;
					}

					final PsiClass actionBeanPsiClass = StripesReferenceUtil.getBeanClassFromParentTag(
						(XmlTag) element.getParent().getParent(), StripesConstants.FORM_TAG
					);

					if (null != actionBeanPsiClass) {
						List<String> arr = StringUtil.split(ElementManipulators.getValueText(element), ",");

						List<PsiReference> retval = new LinkedList<PsiReference>();
						for (int i = 0, offset = 1; i < arr.size(); i++) {
							Collections.addAll(retval, new SetterReferenceExSet(arr.get(i), element, offset, '.', actionBeanPsiClass, false).getPsiReferences());
							offset += (arr.get(i).length() + 1);
						}

						return retval.toArray(new PsiReference[retval.size()]);
					}

					return PsiReference.EMPTY_ARRAY;
				}
			}, StripesConstants.FIELDS_ATTR, StripesConstants.FIELD_METADATA_TAG
		);

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
					: new SetterReferenceExSet(element, 1, '.', cls, false).getPsiReferences();
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
				return null == cls ? PsiReference.EMPTY_ARRAY : new SetterReferenceExSet(element, 1, '.', cls, false) {
					@NotNull
					@Override
					protected SetterReferenceEx<PsiElement> createReferenceWithBraces(TextRange range, int index, boolean hasBraces) {
						return new StrictBindingReference(range, this.isSupportBraces(), this, index);
					}
				}.getPsiReferences();
			}
		});

		registrar.registerReferenceProvider(PsiJavaPatterns.literalExpression().and(new FilterPattern(
			new AnnotationParameterFilter(PsiLiteralExpression.class, StripesConstants.URL_BINDING_ANNOTATION, StripesConstants.VALUE_ATTR)
		)), new PsiReferenceProviderBase() {
			@NotNull
			public PsiReference[] getReferencesByElement(@NotNull PsiElement element, @NotNull ProcessingContext context) {
				PsiClass actionBeanPsiClass = PsiTreeUtil.getParentOfType(element, PsiClass.class);
				String str = ElementManipulators.getValueText(element);

				if (null != actionBeanPsiClass && str.startsWith("/")) {
					final List<PsiReference> retval = new LinkedList<PsiReference>();
					for (int i = 0, eqInd = -1, lBraceInd = -1, braceStack = 0; i < str.length(); i++) {
						if (str.charAt(i) == '{') {
							braceStack++;
							lBraceInd = i;
							eqInd = -1;
						} else if (str.charAt(i) == '}') {// we found closing brace and need to retrieve references if possible
							braceStack--;
							if (braceStack != 0) continue;// braces are unbalanced - we should not try to parse

							int endInd = eqInd != -1
								? eqInd // there's '=' sign within curly braces bounded part of string. processign only part of text located within curl braces
								: i; // no '=' sign found. process whole text from curly braces;

							String txt = str.substring(1 + lBraceInd, endInd);
							if ("$event".equals(txt) && eqInd == -1) {
								retval.add(new UrlBindingReference(element, new TextRange(1 + lBraceInd + 1, 1 + endInd), actionBeanPsiClass));
							} else {
								Collections.addAll(retval,
									new SetterReferenceExSet(txt, element, 1 + lBraceInd + 1, '.', actionBeanPsiClass, true).getPsiReferences()
								);
							}

							retval.add(new UrlBindingReference(element, new TextRange(1 + lBraceInd + 1, 1 + lBraceInd + 1), actionBeanPsiClass));
						} else if (str.charAt(i) == '=') {
							eqInd = i;
						}
					}
					return retval.toArray(new PsiReference[retval.size()]);
				}

				return PsiReference.EMPTY_ARRAY;
			}
		});

		registrar.registerReferenceProvider(PsiJavaPatterns.literalExpression().methodCallParameter(0,
			PsiJavaPatterns
				.psiMethod()
				.definedInClass(StripesConstants.VALIDATION_ERRORS)
				.withName(StandardPatterns.string().oneOf(
				StripesConstants.ADD_METHOD, StripesConstants.ADD_ALL_METHOD, StripesConstants.PUT_METHOD, StripesConstants.PUT_ALL_METHOD))
		), new PsiReferenceProviderBase() {
			@NotNull
			@Override
			public PsiReference[] getReferencesByElement(@NotNull PsiElement element, @NotNull ProcessingContext context) {
				PsiClass cls = PsiTreeUtil.getParentOfType(element, PsiClass.class);
				return null == cls ? PsiReference.EMPTY_ARRAY : new SetterReferenceExSet(element, 1, '.', cls, true).getPsiReferences();
			}
		});
	}
}