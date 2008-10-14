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

package org.intellij.stripes.components.project;

import com.intellij.javaee.web.WebRoot;
import com.intellij.javaee.web.WebUtil;
import com.intellij.javaee.web.facet.WebFacet;
import com.intellij.lang.injection.InjectedLanguageManager;
import com.intellij.lang.injection.MultiHostInjector;
import com.intellij.lang.injection.MultiHostRegistrar;
import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.css.impl.util.CssInHtmlClassOrIdReferenceProvider;
import com.intellij.psi.filters.*;
import com.intellij.psi.filters.position.NamespaceFilter;
import com.intellij.psi.filters.position.ParentElementFilter;
import com.intellij.psi.impl.source.jsp.WebDirectoryUtil;
import com.intellij.psi.impl.source.jsp.el.ELLanguage;
import com.intellij.psi.impl.source.resolve.reference.PsiReferenceProviderBase;
import com.intellij.psi.impl.source.resolve.reference.ReferenceProvidersRegistry;
import com.intellij.psi.impl.source.resolve.reference.impl.providers.FileReferenceSet;
import com.intellij.psi.impl.source.resolve.reference.impl.providers.JavaClassReferenceProvider;
import com.intellij.psi.impl.source.resolve.reference.impl.providers.WebPathReferenceProvider;
import com.intellij.psi.jsp.el.ELExpressionHolder;
import com.intellij.psi.xml.XmlTag;
import com.intellij.spring.references.SpringBeanNamesReferenceProvider;
import com.intellij.util.Function;
import com.intellij.util.ProcessingContext;
import com.intellij.xml.util.XmlUtil;
import org.intellij.lang.regexp.RegExpLanguage;
import org.intellij.stripes.reference.MimeTypeReference;
import org.intellij.stripes.reference.StripesReferenceUtil;
import org.intellij.stripes.reference.filters.NewStreamingResolutionFilter;
import org.intellij.stripes.reference.filters.OnwardResolutionConstructorFilter;
import org.intellij.stripes.reference.filters.SpringBeanAnnotationFilter;
import org.intellij.stripes.reference.providers.*;
import org.intellij.stripes.util.StripesConstants;
import org.intellij.stripes.util.StripesMultiHostInjector;
import org.intellij.stripes.util.StripesUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
//TODO move all reference initializations to PsiReferenceContributors
public class StripesReferencesComponent implements ProjectComponent {
	// ------------------------------ FIELDS ------------------------------
	private ReferenceProvidersRegistry registry;
	private Project project;

	final public static NamespaceFilter STRIPES_NAMESPACE_FILTER = new NamespaceFilter(StripesConstants.STRIPES_TLDS);
	final private static ElementFilter VALIDATE_ANNOTATION_EXPRESSION_ATTR = new AnnotationParameterFilter(PsiLiteralExpression.class,
		StripesConstants.VALIDATE_ANNOTATION, StripesConstants.EXPRESSION_ATTR
	);
	final private static ElementFilter VALIDATE_ANNOTATION_MASK_ATTR = new AnnotationParameterFilter(PsiLiteralExpression.class,
		StripesConstants.VALIDATE_ANNOTATION, StripesConstants.MASK_ATTR
	);
	// --------------------------- CONSTRUCTORS ---------------------------

	public StripesReferencesComponent(Project project) {
		this.registry = ReferenceProvidersRegistry.getInstance(project);
		this.project = project;

		InjectedLanguageManager.getInstance(project).registerMultiHostInjector(StripesMultiHostInjector.getCSSInstance());
		InjectedLanguageManager.getInstance(project).registerMultiHostInjector(StripesMultiHostInjector.getJSInstance());
		InjectedLanguageManager.getInstance(project).registerMultiHostInjector(new MultiHostInjector() {
			public void getLanguagesToInject(@NotNull MultiHostRegistrar registrar, @NotNull PsiElement context) {
				if (VALIDATE_ANNOTATION_EXPRESSION_ATTR.isAcceptable(context, context)) {
					final TextRange range = new TextRange(1, context.getTextLength() - 1);
					registrar.startInjecting(ELLanguage.INSTANCE)
						.addPlace(null, null, (PsiLanguageInjectionHost) context, range)
						.doneInjecting();
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
		});

	}
// ------------------------ INTERFACE METHODS ------------------------

// --------------------- Interface BaseComponent ---------------------


	@NotNull
	public String getComponentName() {
		return "Stripes References Provider";
	}

	public void initComponent() {

		for (String tag : StripesConstants.ACTION_BEAN_TAGS) {
//            all stripes tags with beanclass parameter add Reference provider for implementations od Stripes ActionBean
			registerSubclass(tag);
		}

//errors tag add Reference Provider for Setters Method on parameter field
		registerTags(new SetterMethodsReferenceProvider(new String[]{StripesConstants.FORM_TAG}),
			STRIPES_NAMESPACE_FILTER, StripesConstants.FIELD_ATTR, StripesConstants.ERRORS_TAG
		);
//all stripes tags for input form add Reference Provider for Setters Method
		registerTags(new SetterMethodsReferenceProvider(new String[]{StripesConstants.FORM_TAG}),
			STRIPES_NAMESPACE_FILTER, StripesConstants.NAME_ATTR, StripesConstants.INPUT_TAGS
		);
//file upload input
		registerTags(new FileBeanSetterMethodsReferenceProvider(),
			STRIPES_NAMESPACE_FILTER, StripesConstants.NAME_ATTR, StripesConstants.FILE_TAG
		);
//param and link-param tag add Reference Provider for Setter Methods
		registerTags(new SetterMethodsReferenceProvider(new String[]{StripesConstants.LINK_TAG,
			StripesConstants.URL_TAG}
		), STRIPES_NAMESPACE_FILTER, StripesConstants.NAME_ATTR, StripesConstants.PARAMS_TAGS
		);
//all stripes tags for submit form add Reference Provider for Event(Resolution Method)
		registerTags(new TagResolutionMethodsReferenceProvider(), STRIPES_NAMESPACE_FILTER,
			StripesConstants.NAME_ATTR, StripesConstants.RESOLUTION_TAGS
		);
//all stripes special tags with event parameter add Reference Provider for Event(Resolution Method)
		registerTags(new EventAttrResolutionMethodsReferenceProvider(), STRIPES_NAMESPACE_FILTER,
			StripesConstants.EVENT_ATTR, StripesConstants.ACTION_BEAN_TAGS_WITH_EVENT
		);

//layout-render
		registerTags(new WebPathReferenceProvider(), STRIPES_NAMESPACE_FILTER, StripesConstants.NAME_ATTR, StripesConstants.LAYOUT_RENDER_TAG);
//layout-component
		registerTags(new LayoutComponentReferenceProvider(), STRIPES_NAMESPACE_FILTER, StripesConstants.NAME_ATTR, StripesConstants.LAYOUT_COMPONENT_TAG);
//css
		registerTags(new CssInHtmlClassOrIdReferenceProvider(), STRIPES_NAMESPACE_FILTER, StripesConstants.CLASS_ATTR, StripesConstants.CLASS_TAGS);
//src on stripes:image
		registerTags(new WebPathReferenceProvider(), STRIPES_NAMESPACE_FILTER, StripesConstants.SRC_ATTR, StripesConstants.IMAGE_TAG);

		JavaClassReferenceProvider provider = new JavaClassReferenceProvider(GlobalSearchScope.projectScope(this.project));
		provider.setOption(JavaClassReferenceProvider.EXTEND_CLASS_NAMES, new String[]{"java.lang.Enum"});
		registerTags(provider, STRIPES_NAMESPACE_FILTER, StripesConstants.ENUM_ATTR, StripesConstants.OPTIONS_ENUMERATION_TAG);

		registerSpringBeanReference();
		registerStreamingResolutionReference();

		registry
			.registerReferenceProvider(new OnwardResolutionConstructorFilter(1), PsiLiteralExpression.class, new WebPathReferenceProvider(true, false, false) {
				@NotNull
				public PsiReference[] getReferencesByElement(@NotNull final PsiElement element, @NotNull ProcessingContext context) {

					FileReferenceSet set = FileReferenceSet.createSet(element, false, false, false);
					set.addCustomization(FileReferenceSet.DEFAULT_PATH_EVALUATOR_OPTION, new Function<PsiFile, Collection<PsiFileSystemItem>>() {
						public Collection<PsiFileSystemItem> fun(PsiFile psiFile) {
							WebFacet webFacet = WebUtil.getWebFacet(element);
							if (null == webFacet) return new ArrayList<PsiFileSystemItem>(0);

							Collection<PsiFileSystemItem> retval = new ArrayList<PsiFileSystemItem>();
							for (WebRoot webRoot : webFacet.getWebRoots(true)) {
								retval.add(WebDirectoryUtil.getWebDirectoryUtil(element.getProject()).findWebDirectoryElementByPath(webRoot.getRelativePath(),
									webFacet
								));
							}
							return retval;
						}
					});

//TODO add references to servlets declared in web.xml
					return set.getAllReferences();

//				PsiDynaReference retval = new PsiDynaReference(element, false);
//
//				List<PsiReference> l = new ArrayList<PsiReference>();
//				new ServletPathReferenceProvider().createReferences(element, l, false);
//				for (PsiReference psiReference : l) {
//					retval.addReference(psiReference);
//				}
//
//				for (FileReference fileReference : set.getAllReferences()) {
//					retval.addReference(fileReference);
//				}
//
//				PsiReference[] rrr = super.getReferencesByElement(element, context);
//
//				return new PsiReference[]{retval};
				}
			}
			);


	}

	private void registerStreamingResolutionReference() {
		registry
			.registerReferenceProvider(new ParentElementFilter(new NewStreamingResolutionFilter()), PsiLiteralExpression.class, new PsiReferenceProviderBase() {
				@NotNull
				public PsiReference[] getReferencesByElement(@NotNull PsiElement element, @NotNull ProcessingContext context) {
					return new PsiReference[]{new MimeTypeReference((PsiLiteralExpression) element)};
				}
			}
			);
	}

	public void disposeComponent() {
		StripesReferenceUtil.URL_BINDING_SEARCHER = null;
		StripesUtil.PSI_CLASS_MAP.clear();
	}

// --------------------- Interface ProjectComponent ---------------------

	public void projectOpened() {
	}

	public void projectClosed() {
	}

// -------------------------- OTHER METHODS --------------------------

	private void registerSpringBeanReference() {
		registry.registerReferenceProvider(new ParentElementFilter(new SpringBeanAnnotationFilter()),
			PsiLiteralExpression.class,
			new SpringBeanNamesReferenceProvider()
		);
	}

	private void registerSubclass(String tagName) {
//TODO if package filter is configured in web.xml pass it as GlobalSearchScope instead of whole project scope
		JavaClassReferenceProvider provider = new JavaClassReferenceProvider(GlobalSearchScope.projectScope(this.project)) {
			public PsiReference[] getReferencesByElement(@NotNull PsiElement psiElement) {
				if (psiElement.getChildren().length > 1 && psiElement.getChildren()[1] instanceof ELExpressionHolder) {
					return PsiReference.EMPTY_ARRAY;
				}
				return super.getReferencesByElement(psiElement);
			}
		};
		provider.setOption(JavaClassReferenceProvider.EXTEND_CLASS_NAMES, new String[]{StripesConstants.ACTION_BEAN});
		provider.setOption(JavaClassReferenceProvider.INSTANTIATABLE, true);
		registerTags(provider, STRIPES_NAMESPACE_FILTER, StripesConstants.BEANCLASS_ATTR, tagName);
	}

	private void registerTags(PsiReferenceProvider provider, NamespaceFilter namespaceFilter, String attributeName, String... tagNames) {
		XmlUtil.registerXmlAttributeValueReferenceProvider(registry, StripesUtil.makeArray(attributeName), getTagsFilter(namespaceFilter, tagNames), provider);
	}

	private static ScopeFilter getTagsFilter(ElementFilter elementFilter, String... tagsNames) {
		return new ScopeFilter(new ParentElementFilter(new AndFilter(elementFilter, new ClassFilter(XmlTag.class), new TextFilter(tagsNames)), 2));
	}
}
