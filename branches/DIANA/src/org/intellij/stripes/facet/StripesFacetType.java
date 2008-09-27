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

package org.intellij.stripes.facet;

import com.intellij.facet.Facet;
import com.intellij.facet.FacetType;
import com.intellij.javaee.web.facet.WebFacet;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleType;
import com.intellij.openapi.module.JavaModuleType;
import org.intellij.stripes.util.StripesConstants;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * Created by IntelliJ IDEA. User: Mario Arias Date: 2/07/2007 Time: 10:52:09 PM
 */
public class StripesFacetType extends FacetType<StripesFacet, StripesFacetConfiguration> {
// ------------------------------ FIELDS ------------------------------

    public final static StripesFacetType INSTANCE = new StripesFacetType();

// --------------------------- CONSTRUCTORS ---------------------------

    private StripesFacetType() {
        super(StripesFacet.FACET_TYPE_ID, "Stripes", "Stripes", WebFacet.ID);
    }

// -------------------------- OTHER METHODS --------------------------

    @Override
    public boolean isOnlyOneFacetAllowed() {
        return true;
    }

    @Override
    public boolean isSuitableModuleType(ModuleType moduleType) {
    	return moduleType instanceof JavaModuleType;
    }

    @Override
    public Icon getIcon() {
        return StripesConstants.STRIPES_ICON;
    }

    public StripesFacet createFacet(@NotNull Module module, String name, @NotNull StripesFacetConfiguration configuration, @Nullable Facet underlyingFacet) {
        return new StripesFacet(this, module, name, configuration, underlyingFacet);
    }

    public StripesFacetConfiguration createDefaultConfiguration() {
        return new StripesFacetConfiguration();
    }

//TODO create auto detect capability

//TODO implement framework support	
}
