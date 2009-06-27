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
import com.intellij.facet.autodetecting.FacetDetector;
import com.intellij.facet.autodetecting.FacetDetectorRegistry;
import com.intellij.facet.impl.autodetecting.FacetDetectorRegistryEx;
import com.intellij.j2ee.web.WebUtilImpl;
import com.intellij.javaee.model.common.JavaeeCommonConstants;
import com.intellij.javaee.web.facet.WebFacet;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleType;
import com.intellij.openapi.module.StdModuleTypes;
import com.intellij.openapi.util.io.StreamUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.xml.NanoXmlUtil;
import net.n3.nanoxml.IXMLBuilder;
import org.intellij.stripes.util.StripesConstants;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.io.Reader;
import java.util.Collection;
import java.util.Iterator;

//TODO implement framework support
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
		return moduleType == StdModuleTypes.JAVA;
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

	@Override
	public void registerDetectors(FacetDetectorRegistry<StripesFacetConfiguration> stripesFacetConfigurationFacetDetectorRegistry) {
		((FacetDetectorRegistryEx) stripesFacetConfigurationFacetDetectorRegistry).registerUniversalDetectorByFileNameAndRootTag("web.xml", JavaeeCommonConstants.WEB_XML_ROOT_TAG,
			new StripesFacetDetector("stripes-detector"), WebUtilImpl.BY_PARENT_WEB_ROOT_SELECTOR
		);
	}

	private final static class StripesFacetDetectorHelper extends NanoXmlUtil.BaseXmlBuilder {
		private Boolean isFilterConfigured = null;

		public void addPCData(Reader reader, String systemID, int lineNr) throws Exception {
			if (getLocation().endsWith("filter-class")
				&& StripesConstants.STRIPES_FILTER_CLASS.equals(StreamUtil.readTextFrom(reader))) {
				isFilterConfigured = true;
				stop();
			}
		}

		@Nullable
		public Object getResult() throws Exception {
			return isFilterConfigured;
		}
	}


	private final static class StripesFacetDetector extends FacetDetector<VirtualFile, StripesFacetConfiguration> {

		private StripesFacetDetector(@NotNull String id) {
			super(id);
		}

		public StripesFacetConfiguration detectFacet(VirtualFile source, Collection<StripesFacetConfiguration> existentFacetConfigurations) {
			final Iterator<StripesFacetConfiguration> iterator = existentFacetConfigurations.iterator();
			if (iterator.hasNext()) return iterator.next();

			IXMLBuilder builder = new StripesFacetDetectorHelper();
			try {
				NanoXmlUtil.parse(source.getInputStream(), builder);
				return builder.getResult() != null ? new StripesFacetConfiguration() : null;
			} catch (Exception e) {
				e.printStackTrace(System.err);
			}

			return null;
		}
	}

}
