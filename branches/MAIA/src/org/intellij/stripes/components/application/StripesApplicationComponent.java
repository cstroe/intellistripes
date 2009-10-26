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

package org.intellij.stripes.components.application;

import com.intellij.facet.FacetTypeRegistry;
import com.intellij.openapi.components.ApplicationComponent;
import org.intellij.stripes.facet.StripesFacetType;
import org.jetbrains.annotations.NotNull;

/**
 * Created by IntelliJ IDEA. User: Mario Arias Date: 2/07/2007 Time: 01:57:05 AM
 */
public class StripesApplicationComponent implements ApplicationComponent {
// ------------------------ INTERFACE METHODS ------------------------

// --------------------- Interface BaseComponent ---------------------

    @NotNull
    public String getComponentName() {
        return "Stripes Application Component";
    }

    public void initComponent() {
        //Register a new Facet Type
        FacetTypeRegistry.getInstance().registerFacetType(StripesFacetType.INSTANCE);

//		final TemplateSettings settings = TemplateSettings.getInstance();
//		try {
//			final Document document = JDOMUtil.loadDocument(getClass().getResourceAsStream("/liveTemplates/stripes.xml"));
//			settings.readHiddenTemplateFile(document);
//		}
//		catch (Exception e) {
//			e.printStackTrace();
//		}
    }

    public void disposeComponent() {

    }

// --------------------- Interface IconProvider ---------------------


}