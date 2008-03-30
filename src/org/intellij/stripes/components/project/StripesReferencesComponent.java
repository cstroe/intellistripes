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

import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiLiteralExpression;
import com.intellij.psi.css.impl.util.CssInHtmlClassOrIdReferenceProvider;
import com.intellij.psi.filters.*;
import com.intellij.psi.filters.position.NamespaceFilter;
import com.intellij.psi.filters.position.ParentElementFilter;
import com.intellij.psi.impl.source.resolve.reference.PsiReferenceProvider;
import com.intellij.psi.impl.source.resolve.reference.ReferenceProvidersRegistry;
import com.intellij.psi.impl.source.resolve.reference.impl.providers.JavaClassReferenceProvider;
import com.intellij.psi.impl.source.resolve.reference.impl.providers.WebPathReferenceProvider;
import com.intellij.psi.xml.XmlTag;
import com.intellij.spring.references.SpringBeanNamesReferenceProvider;
import org.intellij.stripes.reference.filters.SpringBeanAnnotationFilter;
import org.intellij.stripes.reference.providers.*;
import org.intellij.stripes.util.StripesConstants;
import org.intellij.stripes.util.StripesUtil;
import org.jetbrains.annotations.NotNull;

/**
 * Created by IntelliJ IDEA. User: Mario Arias Date: 3/07/2007 Time: 11:14:42 PM
 */
public class StripesReferencesComponent implements ProjectComponent {
// ------------------------------ FIELDS ------------------------------

    private ReferenceProvidersRegistry registry;
    private NamespaceFilter stripesNamespaceFilter;

// --------------------------- CONSTRUCTORS ---------------------------

    public StripesReferencesComponent(Project project) {
        registry = ReferenceProvidersRegistry.getInstance(project);
        stripesNamespaceFilter = new NamespaceFilter(StripesConstants.STRIPES_TLDS);
    }

// ------------------------ INTERFACE METHODS ------------------------

// --------------------- Interface BaseComponent ---------------------


    @NotNull
    public String getComponentName() {
        return "Stripes References Provider";
    }

    public void initComponent() {
        String[] tags = StripesConstants.ACTION_BEAN_TAGS;
        for (String tag : tags) {
            //all stripes tags with beanclass parameter add Reference provider for implementations od Stripes ActionBean
            registerSubclass(stripesNamespaceFilter, tag, StripesConstants.BEAN_CLASS_ATTRIBUTE, StripesConstants.STRIPES_ACTION_BEAN_CLASS);
        }
        //errors tag add Reference Provider for Setters Method on parameter field
        registerTags(new ActionBeanSetterMethodsReferenceProvider(), stripesNamespaceFilter, StripesConstants.FIELD_ATTRIBUTE, StripesConstants.ERRORS_TAG);
        //all stripes tags for input form add Reference Provider for Setters Method
        registerTags(new ActionBeanSetterMethodsReferenceProvider(), stripesNamespaceFilter, StripesConstants.NAME_ATTRIBUTE, StripesConstants.INPUT_TAGS);
        //link-param tag add Reference Provider for Setters Methods
        registerTags(new LinkParamSetterMethodsReferenceProvider(), stripesNamespaceFilter, StripesConstants.NAME_ATTRIBUTE, StripesConstants.LINK_PARAM_TAG);
        //param tag add Reference Provider for Setter Methods
        registerTags(new ParamSetterMethodsReferenceProvider(), stripesNamespaceFilter, StripesConstants.NAME_ATTRIBUTE, StripesConstants.PARAMS_TAGS);
        //all stripes tags for submit form add Reference Provider for Event(Resolution Method)
        registerTags(new ActionBeanResolutionMethodsReferenceProvider(), stripesNamespaceFilter, StripesConstants.NAME_ATTRIBUTE, StripesConstants.RESOLUTION_TAGS);
        //all stripes special tags with event parameter add Reference Provider for Event(Resolution Method)
        registerTags(new TagResolutionMethodsReferenceProvider(), stripesNamespaceFilter, StripesConstants.EVENT, StripesConstants.ACTION_BEAN_TAGS_WITH_EVENT);
        //layout-render
        registerTags(new WebPathReferenceProvider(), stripesNamespaceFilter, StripesConstants.NAME_ATTRIBUTE, StripesConstants.LAYOUT_RENDER_TAG);
        //layout-component
        registerTags(new LayoutComponentReferenceProvider(), stripesNamespaceFilter, StripesConstants.NAME_ATTRIBUTE, StripesConstants.LAYOUT_COMPONENT);
        //css
        registerTags(new CssInHtmlClassOrIdReferenceProvider(), stripesNamespaceFilter, StripesConstants.CLASS_ATTRIBUTE, StripesConstants.CLASS_TAGS);
        registerSpringBeanReference();
    }

    public void disposeComponent() {

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
                new SpringBeanNamesReferenceProvider()//Add <IDEA_HOME>/plugins/Spring/lib/spring.jar to your IDEA JDK
        );
    }

    private void registerSubclass(NamespaceFilter namespaceFilter, String tagName, String attributName, String... classes) {
        JavaClassReferenceProvider provider = new JavaClassReferenceProvider();
        provider.setOption(JavaClassReferenceProvider.EXTEND_CLASS_NAMES, classes);
        provider.setOption(JavaClassReferenceProvider.INSTANTIATABLE, true);
        registerTags(provider, namespaceFilter, attributName, tagName);
    }

    private void registerTags(PsiReferenceProvider provider, NamespaceFilter namespaceFilter, String attributeName, String... tagNames) {
        registry.registerXmlAttributeValueReferenceProvider(StripesUtil.makeArray(attributeName), getTagsFilter(namespaceFilter, tagNames), provider);
    }

    private static ScopeFilter getTagsFilter(ElementFilter elementFilter, String... tagsNames) {
        return new ScopeFilter(new ParentElementFilter(new AndFilter(elementFilter, new ClassFilter(XmlTag.class), new TextFilter(tagsNames)), 2));
    }
}
