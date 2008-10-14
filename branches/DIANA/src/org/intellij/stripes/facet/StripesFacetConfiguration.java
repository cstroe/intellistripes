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

import com.intellij.facet.FacetConfiguration;
import com.intellij.facet.ui.FacetEditorContext;
import com.intellij.facet.ui.FacetEditorTab;
import com.intellij.facet.ui.FacetValidatorsManager;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.JDOMExternalizer;
import com.intellij.openapi.util.WriteExternalException;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.intellij.stripes.facet.tabs.FacetConfigurationTab;
import org.intellij.stripes.facet.tabs.StripesConfigurationTab;
import org.intellij.stripes.util.StripesConstants;
import org.jdom.Element;

public class StripesFacetConfiguration implements FacetConfiguration, PersistentStateComponent<StripesFacetConfiguration> {
// ------------------------------ FIELDS ------------------------------

    private boolean springIntegration = false;
    private boolean logging = false;
    private boolean stripesResources;
    private String log4jFile;
    private boolean actionResolverUrlFilters = false;
    private String urlFiltersValue = "WEB-INF/classes";
    private boolean changeIcons = false;

// --------------------- GETTER / SETTER METHODS ---------------------

    public String getLog4jFile() {
        return log4jFile;
    }

    public void setLog4jFile(String log4jFile) {
        this.log4jFile = log4jFile;
    }

    public String getUrlFiltersValue() {
        return urlFiltersValue;
    }

    public void setUrlFiltersValue(String urlFiltersValue) {
        this.urlFiltersValue = urlFiltersValue;
    }

    public boolean isActionResolverUrlFilters() {
        return actionResolverUrlFilters;
    }

    public void setActionResolverUrlFilters(boolean actionResolverUrlFilters) {
        this.actionResolverUrlFilters = actionResolverUrlFilters;
    }

    public boolean isChangeIcons() {
        return changeIcons;
    }

    public void setChangeIcons(boolean changeIcons) {
        this.changeIcons = changeIcons;
    }

    public boolean isLogging() {
        return logging;
    }

    public void setLogging(boolean logging) {
        this.logging = logging;
    }

    public boolean isSpringIntegration() {
        return springIntegration;
    }

    public void setSpringIntegration(boolean springIntegration) {
        this.springIntegration = springIntegration;
    }

    public boolean isStripesResources() {
        return stripesResources;
    }

    public void setStripesResources(boolean stripesResources) {
        this.stripesResources = stripesResources;
    }

// ------------------------ CANONICAL METHODS ------------------------

    @Override
    public String toString() {
        return "StripesFacetConfiguration{" +
                "actionResolverUrlFilters=" + actionResolverUrlFilters +
                ", springIntegration=" + springIntegration +
                ", logging=" + logging +
                ", stripesResources=" + stripesResources +
                ", log4jFile='" + log4jFile + '\'' +
                ", urlFiltersValue='" + urlFiltersValue + '\'' +
                ", changeIcons=" + changeIcons +
                '}';
    }

// ------------------------ INTERFACE METHODS ------------------------

// --------------------- Interface FacetConfiguration ---------------------

    public FacetEditorTab[] createEditorTabs(final FacetEditorContext editorContext, final FacetValidatorsManager validatorsManager) {
        return new FacetEditorTab[]{new StripesConfigurationTab(editorContext, this, validatorsManager),
                new FacetConfigurationTab(this)};
    }

// --------------------- Interface JDOMExternalizable ---------------------
	@Deprecated
    public void readExternal(Element element) throws InvalidDataException {
    }

	@Deprecated
    public void writeExternal(Element element) throws WriteExternalException {
    }

	public StripesFacetConfiguration getState() {
		return this;
	}

	public void loadState(StripesFacetConfiguration state) {
		XmlSerializerUtil.copyBean(state, this);
	}
}
