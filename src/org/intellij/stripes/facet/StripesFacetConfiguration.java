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

import com.intellij.facet.FacetConfiguration;
import com.intellij.facet.ui.FacetEditorContext;
import com.intellij.facet.ui.FacetEditorTab;
import com.intellij.facet.ui.FacetValidatorsManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.WriteExternalException;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.intellij.stripes.facet.tabs.FacetConfigurationTab;
import org.intellij.stripes.facet.tabs.StripesConfigurationTab;
import org.jdom.Element;

public class StripesFacetConfiguration implements FacetConfiguration, PersistentStateComponent<StripesFacetConfiguration> {
// ------------------------------ FIELDS ------------------------------

    private boolean springIntegration = false;
    private boolean logging = false;
    private boolean stripesResources;
    private String log4jFile;
    private boolean changeIcons = false;
    private boolean neverModifyWebXml = false;
    private String actionResolverPackages = "";

// --------------------- GETTER / SETTER METHODS ---------------------

    public String getLog4jFile() {
        return log4jFile;
    }

    public void setLog4jFile(String log4jFile) {
        this.log4jFile = log4jFile;
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

    public String getActionResolverPackages() {
        return actionResolverPackages;
    }

    public void setActionResolverPackages(String actionResolverPackages) {
        this.actionResolverPackages = actionResolverPackages;
    }

    public boolean isNeverModifyWebXml() {
        return neverModifyWebXml;
    }

    public void setNeverModifyWebXml(boolean neverModifyWebXml) {
        this.neverModifyWebXml = neverModifyWebXml;
    }

    // ------------------------ CANONICAL METHODS ------------------------

    @Override
    public String toString() {
        return "StripesFacetConfiguration{" +
                ", springIntegration=" + springIntegration +
                ", logging=" + logging +
                ", stripesResources=" + stripesResources +
                ", log4jFile='" + log4jFile + '\'' +
                ", changeIcons=" + changeIcons +
                '}';
    }

// ------------------------ INTERFACE METHODS ------------------------

// --------------------- Interface FacetConfiguration ---------------------

    public FacetEditorTab[] createEditorTabs(final FacetEditorContext editorContext, final FacetValidatorsManager validatorsManager) {
        return new FacetEditorTab[]{
                new StripesConfigurationTab(editorContext, this, validatorsManager),
                new FacetConfigurationTab(this)
        };
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
