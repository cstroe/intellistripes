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

package org.intellij.stripes.components.project;

import com.intellij.javaee.web.WebRoot;
import com.intellij.javaee.web.WebUtil;
import com.intellij.javaee.web.facet.WebFacet;
import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.css.impl.util.CssInHtmlClassOrIdReferenceProvider;
import com.intellij.psi.filters.AndFilter;
import com.intellij.psi.filters.ClassFilter;
import com.intellij.psi.filters.ScopeFilter;
import com.intellij.psi.filters.TextFilter;
import com.intellij.psi.filters.position.NamespaceFilter;
import com.intellij.psi.filters.position.ParentElementFilter;
import com.intellij.psi.impl.source.jsp.WebDirectoryUtil;
import com.intellij.psi.impl.source.resolve.reference.ReferenceProvidersRegistry;
import com.intellij.psi.impl.source.resolve.reference.impl.providers.FileReferenceSet;
import com.intellij.psi.impl.source.resolve.reference.impl.providers.JavaClassReferenceProvider;
import com.intellij.psi.impl.source.resolve.reference.impl.providers.WebPathReferenceProvider;
import com.intellij.psi.jsp.el.ELExpressionHolder;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.xml.XmlTag;
import com.intellij.spring.references.SpringBeanNamesReferenceProvider;
import com.intellij.util.Function;
import com.intellij.util.ProcessingContext;
import com.intellij.xml.util.XmlUtil;
import org.intellij.stripes.reference.StripesReferenceUtil;
import org.intellij.stripes.reference.filters.OnwardResolutionConstructorFilter;
import org.intellij.stripes.reference.filters.SpringBeanAnnotationFilter;
import org.intellij.stripes.reference.providers.EventAttrResolutionMethodsReferenceProvider;
import org.intellij.stripes.reference.providers.FileBeanSetterMethodsReferenceProvider;
import org.intellij.stripes.reference.providers.LayoutComponentReferenceProvider;
import org.intellij.stripes.reference.providers.TagResolutionMethodsReferenceProvider;
import org.intellij.stripes.util.StripesConstants;
import org.intellij.stripes.util.StripesUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;

//TODO move all reference initializations to PsiReferenceContributors
public class StripesReferencesComponent implements ProjectComponent {
	// ------------------------------ FIELDS ------------------------------
	private ReferenceProvidersRegistry registry;
	private Project project;

	final public static NamespaceFilter STRIPES_NAMESPACE_FILTER = new NamespaceFilter(StripesConstants.STRIPES_TLDS);
	// --------------------------- CONSTRUCTORS ---------------------------

	public StripesReferencesComponent(Project project) {
		this.registry = ReferenceProvidersRegistry.getInstance(project);
		this.project = project;
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

//file upload input
		registerXmlAttributeReferenceProvider(registry, new FileBeanSetterMethodsReferenceProvider(),
			StripesConstants.NAME_ATTR, StripesConstants.FILE_TAG);

//all stripes tags for submit form add Reference Provider for Event(Resolution Method)
		registerXmlAttributeReferenceProvider(registry, new TagResolutionMethodsReferenceProvider(),
			StripesConstants.NAME_ATTR, StripesConstants.RESOLUTION_TAGS);
//all stripes special tags with event parameter add Reference Provider for Event(Resolution Method)
		registerXmlAttributeReferenceProvider(registry, new EventAttrResolutionMethodsReferenceProvider(),
			StripesConstants.EVENT_ATTR, StripesConstants.ACTION_BEAN_TAGS_WITH_EVENT);

//layout-render
		registerXmlAttributeReferenceProvider(registry, new WebPathReferenceProvider(), StripesConstants.NAME_ATTR, StripesConstants.LAYOUT_RENDER_TAG);
//layout-component
		registerXmlAttributeReferenceProvider(registry, new LayoutComponentReferenceProvider(), StripesConstants.NAME_ATTR, StripesConstants.LAYOUT_COMPONENT_TAG);
//css
		registerXmlAttributeReferenceProvider(registry, new CssInHtmlClassOrIdReferenceProvider(), StripesConstants.CLASS_ATTR, StripesConstants.CLASS_TAGS);
//src on stripes:image
		registerXmlAttributeReferenceProvider(registry, new WebPathReferenceProvider(), StripesConstants.SRC_ATTR, StripesConstants.IMAGE_TAG);

		JavaClassReferenceProvider provider = new JavaClassReferenceProvider(GlobalSearchScope.allScope(this.project), this.project);
		provider.setOption(JavaClassReferenceProvider.EXTEND_CLASS_NAMES, new String[]{Enum.class.getName()});
		provider.setOption(JavaClassReferenceProvider.ADVANCED_RESOLVE, Boolean.TRUE);
		provider.setOption(JavaClassReferenceProvider.RESOLVE_QUALIFIED_CLASS_NAME, Boolean.TRUE);
		registerXmlAttributeReferenceProvider(registry, provider, StripesConstants.ENUM_ATTR, StripesConstants.OPTIONS_ENUMERATION_TAG);

		registerSpringBeanReference();

		registry
			.registerReferenceProvider(new OnwardResolutionConstructorFilter(1), PsiLiteralExpression.class, new WebPathReferenceProvider(true, false, false) {
				@NotNull
				public PsiReference[] getReferencesByElement(@NotNull final PsiElement element, @NotNull ProcessingContext context) {

					FileReferenceSet set = FileReferenceSet.createSet(element, false, false, false);
					set.addCustomization(FileReferenceSet.DEFAULT_PATH_EVALUATOR_OPTION, new Function<PsiFile, Collection<PsiFileSystemItem>>() {
						public Collection<PsiFileSystemItem> fun(PsiFile psiFile) {
							WebFacet webFacet = WebUtil.getWebFacet(element);
							if (null == webFacet) return new ArrayList<PsiFileSystemItem>(0);

							Collection<PsiFileSystemItem> retval = new ArrayList<PsiFileSystemItem>(8);
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
		//Register Provider
		registry.registerReferenceProvider(new ParentElementFilter(new SpringBeanAnnotationFilter()),//Our Filter
			PsiLiteralExpression.class,// Only in Strings
			new SpringBeanNamesReferenceProvider()
		);
	}

	private void registerSubclass(String tagName) {
//TODO if ActionResolver.Packages is configured in web.xml pass it as GlobalSearchScope instead of whole project scope
		JavaClassReferenceProvider provider = new JavaClassReferenceProvider(GlobalSearchScope.projectScope(this.project), this.project) {
			public PsiReference[] getReferencesByElement(@NotNull PsiElement psiElement) {
				if (PsiTreeUtil.getChildOfType(psiElement, ELExpressionHolder.class) != null) {
					return PsiReference.EMPTY_ARRAY;
				}
				return super.getReferencesByElement(psiElement);
			}
		};

		provider.setOption(JavaClassReferenceProvider.EXTEND_CLASS_NAMES, new String[]{StripesConstants.ACTION_BEAN});
		provider.setOption(JavaClassReferenceProvider.INSTANTIATABLE, true);
		registerXmlAttributeReferenceProvider(registry, provider, StripesConstants.BEANCLASS_ATTR, tagName);
	}

	public static void registerXmlAttributeReferenceProvider(PsiReferenceRegistrar psiReferenceRegistrar, PsiReferenceProvider provider, String attributeName, String... tagNames) {
		XmlUtil.registerXmlAttributeValueReferenceProvider(
			psiReferenceRegistrar,
			new String[]{attributeName},
			new ScopeFilter(new ParentElementFilter(new AndFilter(STRIPES_NAMESPACE_FILTER, new ClassFilter(XmlTag.class), new TextFilter(tagNames)), 2)),
			provider
		);
	}

	public static void registerXmlAttributeReferenceProviderAttr(PsiReferenceRegistrar psiReferenceRegistrar, PsiReferenceProvider provider, String tagName, String... attributeNames) {
		XmlUtil.registerXmlAttributeValueReferenceProvider(
			psiReferenceRegistrar,
			attributeNames,
			new ScopeFilter(new ParentElementFilter(new AndFilter(STRIPES_NAMESPACE_FILTER, new ClassFilter(XmlTag.class), new TextFilter(tagName)), 2)),
			provider
		);
	}
}