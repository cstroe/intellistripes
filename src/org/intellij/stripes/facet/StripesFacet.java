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

package org.intellij.stripes.facet;

import com.intellij.facet.Facet;
import com.intellij.facet.FacetType;
import com.intellij.facet.FacetTypeId;
import com.intellij.javaee.web.facet.WebFacet;
import com.intellij.openapi.module.Module;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;

public class StripesFacet extends Facet<StripesFacetConfiguration> {
// ------------------------------ FIELDS ------------------------------

	public final static FacetTypeId<StripesFacet> FACET_TYPE_ID = new FacetTypeId<StripesFacet>();

// --------------------------- CONSTRUCTORS ---------------------------

	public StripesFacet(@NotNull FacetType facetType, @NotNull Module module, String name, @NotNull StripesFacetConfiguration configuration, Facet underlyingFacet) {
		super(facetType, module, name, configuration, underlyingFacet);
	}

// -------------------------- OTHER METHODS --------------------------

	public PsiFile getWebXmlPsiFile() {
		return getWebFacet().getWebXmlDescriptor().getPsiFile();
	}

	public WebFacet getWebFacet() {
		return (WebFacet) getUnderlyingFacet();
	}

}
