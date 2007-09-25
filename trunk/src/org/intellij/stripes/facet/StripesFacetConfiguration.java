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
import com.intellij.facet.ui.FacetEditorTab;
import com.intellij.facet.ui.FacetEditorContext;
import com.intellij.facet.ui.FacetValidatorsManager;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.WriteExternalException;
import com.intellij.openapi.util.JDOMExternalizer;
import org.jdom.Element;
import org.intellij.stripes.facet.tabs.StripesConfigurationTab;
import org.intellij.stripes.util.StripesConstants;

/**
 * Created by IntelliJ IDEA. User: Mario Arias Date: 2/07/2007 Time: 10:53:00 PM
 */
public class StripesFacetConfiguration implements FacetConfiguration
{
    private boolean springIntegration;
    private boolean logging;
    private boolean stripesResources;
    private String log4jFile;

    public String getLog4jFile()
    {
        return log4jFile;
    }

    public void setLog4jFile(String log4jFile)
    {
        this.log4jFile = log4jFile;
    }

    public boolean isLogging()
    {
        return logging;
    }

    public void setLogging(boolean logging)
    {
        this.logging = logging;
    }

    public boolean isStripesResources()
    {
        return stripesResources;
    }

    public void setStripesResources(boolean stripesResources)
    {
        this.stripesResources = stripesResources;
    }

    public boolean isSpringIntegration()
    {
        return springIntegration;
    }

    public void setSpringIntegration(boolean springIntegration)
    {
        this.springIntegration = springIntegration;
    }

    public FacetEditorTab[] createEditorTabs(final FacetEditorContext editorContext, final FacetValidatorsManager validatorsManager)
    {
        return new FacetEditorTab[]{new StripesConfigurationTab(editorContext, this)};        
    }

    public void readExternal(Element element) throws InvalidDataException
    {
        springIntegration = JDOMExternalizer.readBoolean(element, StripesConstants.SPRING_INTEGRATION);
        logging = JDOMExternalizer.readBoolean(element, StripesConstants.LOGGING);
        stripesResources = JDOMExternalizer.readBoolean(element, StripesConstants.STRIPES_RESOURCES);
        log4jFile = JDOMExternalizer.readString(element, StripesConstants.LOG4J_FILE);

    }

    public void writeExternal(Element element) throws WriteExternalException
    {
        JDOMExternalizer.write(element, StripesConstants.SPRING_INTEGRATION,springIntegration);
        JDOMExternalizer.write(element, StripesConstants.LOGGING,logging);
        JDOMExternalizer.write(element, StripesConstants.STRIPES_RESOURCES,stripesResources);
        JDOMExternalizer.write(element, StripesConstants.LOG4J_FILE,log4jFile);
    }
}
