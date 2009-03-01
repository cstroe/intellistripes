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

package org.intellij.stripes.support;


import com.intellij.ide.fileTemplates.FileTemplate;
import com.intellij.ide.fileTemplates.FileTemplateManager;
import com.intellij.ide.fileTemplates.FileTemplateUtil;
import com.intellij.javaee.model.xml.ParamValue;
import com.intellij.javaee.model.xml.web.*;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import org.apache.commons.lang.StringUtils;
import org.intellij.stripes.facet.StripesFacet;
import org.intellij.stripes.facet.StripesFacetConfiguration;
import org.intellij.stripes.util.StripesConstants;

import java.util.List;

public final class StripesSupportUtil {
// -------------------------- STATIC METHODS --------------------------

	/**
	 * Configure web.xml to run a Stripes Application
	 *
	 * @param facet StripesFacet
	 */
	public static void addSupport(StripesFacet facet) {
		WebApp app = facet.getWebFacet().getRoot();

		try {
			if (!facet.getConfiguration().isNeverModifyWebXml()) {
				installStripes(app, facet.getConfiguration());
				if (facet.getConfiguration().isSpringIntegration()) {
					addSpringInterceptor(findStripesFilter(app));
				}
			}

			if (facet.getConfiguration().isStripesResources()) {
				installStripesResources(app, facet.getConfiguration());
			}

			if (facet.getConfiguration().isLogging()) {
				installLogging(app, facet.getConfiguration());
			}
		} catch (Throwable throwable) {
			//
		}
	}

	/**
	 * Install Stripes Configuration
	 *
	 * @param app      Web Application
	 * @param facetCfg
	 */
	private static void installStripes(WebApp app, StripesFacetConfiguration facetCfg) {
		if (!isStripesServletInstalled(app)) {
			addDispatcherServlet(app);
		}

		Filter filter = findStripesFilter(app);
		if (filter == null) {
			filter = addStripesFilter(app);
		}

		ParamValue paramValue = findInitParam(filter, StripesConstants.ACTION_RESOLVER_PACKAGES);
		if (null == paramValue) {
			addInitParam(filter, StripesConstants.ACTION_RESOLVER_PACKAGES, facetCfg.getActionResolverPackages());
		} else {
			paramValue.getParamValue().setStringValue(facetCfg.getActionResolverPackages());
		}
	}

	/**
	 * Is Stripes Dispatcher Servlet Installed
	 *
	 * @param app Web Application
	 * @return boolean, do'h
	 */
	public static boolean isStripesServletInstalled(WebApp app) {
		List<Servlet> servlets = app.getServlets();
		for (Servlet servlet : servlets) {
			try {
				if (StripesConstants.STRIPES_SERVLET_CLASS.equals(servlet.getServletClass().getStringValue())) {
					return true;
				}
			} catch (Exception e) {
				//
			}
		}
		return false;
	}

	/**
	 * Add Stripes Dispatcher Servlet
	 *
	 * @param app Web Application
	 */
	private static void addDispatcherServlet(WebApp app) {
		Servlet dispatcherServlet = app.addServlet();
		dispatcherServlet.getServletName().setValue(StripesConstants.STRIPES_SERVLET_NAME);
		dispatcherServlet.getServletClass().setStringValue(StripesConstants.STRIPES_SERVLET_CLASS);
		dispatcherServlet.getLoadOnStartup().setValue(StripesConstants.LOAD_ON_STARTUP);
		//Mapping
		ServletMapping mapping = app.addServletMapping();
		mapping.getServletName().setValue(dispatcherServlet);
		mapping.addUrlPattern().setValue(StripesConstants.DEFAULT_STRIPES_MAPPING);
	}

	private static void addInitParam(Filter filter, String name, String value) {
		ParamValue initParam = filter.addInitParam();
		initParam.getParamName().setStringValue(name);
		initParam.getParamValue().setStringValue(value);
	}

	/**
	 * Add Stripes Filter
	 *
	 * @param app Web Application
	 * @return recently created Stripes Filter
	 */
	private static Filter addStripesFilter(WebApp app) {
		//Add Filter to the Web Application
		Filter filter = app.addFilter();
		filter.getFilterName().setValue(StripesConstants.STRIPES_FILTER_NAME);
		filter.getFilterClass().setStringValue(StripesConstants.STRIPES_FILTER_CLASS);

		addInitParam(filter, StripesConstants.ACTION_RESOLVER_PACKAGES, "");

		//Mappings
		FilterMapping mapping = app.addFilterMapping();
		mapping.getFilterName().setValue(filter);
		mapping.addUrlPattern().setValue(StripesConstants.STRIPES_FILTER_MAPPING);
		mapping.addDispatcher().setStringValue(StripesConstants.REQUEST);

		FilterMapping servletFilterMapping = app.addFilterMapping();
		servletFilterMapping.getFilterName().setValue(filter);
		servletFilterMapping.addServletName().setStringValue(StripesConstants.STRIPES_SERVLET_NAME);
		servletFilterMapping.addDispatcher().setStringValue(StripesConstants.REQUEST);

		return filter;
	}

	/**
	 * Get the Stripes Filter in web.xml
	 *
	 * @param app Web Application
	 * @return Filter Object, null if don't exist
	 */
	public static Filter findStripesFilter(WebApp app) {
		for (Filter filter : app.getFilters()) {
			try {
				if (StripesConstants.STRIPES_FILTER_CLASS.equals(filter.getFilterClass().getStringValue())) {
					return filter;
				}
			} catch (Exception e) {
				//
			}
		}

		return null;
	}

	/**
	 * Add Spring Interceptor
	 *
	 * @param filter Filter Object
	 */
	private static void addSpringInterceptor(Filter filter) {
		ParamValue initParam = findInitParam(filter, StripesConstants.INTERCEPTOR_CLASSES);
		if (initParam != null) {
			String value = initParam.getParamValue().getStringValue();
			if (!StringUtils.contains(value, StripesConstants.SPRING_INTERCEPTOR_CLASS)) {
				initParam.getParamValue().setValue(StripesConstants.SPRING_INTERCEPTOR_CLASS + ',' + value);
			}
		} else {
			ParamValue paramValue = filter.addInitParam();
			paramValue.getParamName().setValue(StripesConstants.INTERCEPTOR_CLASSES);
			paramValue.getParamValue().setValue(StripesConstants.SPRING_INTERCEPTOR_CLASS);
		}
	}

	/**
	 * get A Specific initParam for a given Filter
	 *
	 * @param filter    Filter Object
	 * @param paramName initParam name
	 * @return a Param Value Object, null if don't exists
	 */
	private static ParamValue findInitParam(final Filter filter, final String paramName) {
		for (ParamValue initParam : filter.getInitParams()) {
			if (paramName.equals(initParam.getParamName().getStringValue())) {
				return initParam;
			}
		}
		return null;
	}

	/**
	 * Install StripesResources.properties
	 *
	 * @param app      Web Application
	 * @param facetCfg facetCfg
	 */
	private static void installStripesResources(WebApp app, StripesFacetConfiguration facetCfg) {
		//get Src Directory
		PsiDirectory srcPsiDirectory = getSrcDirectory(app);
		//get a PsiFile for a StripesResources.properties
		PsiFile stripesResources = srcPsiDirectory.findFile(StripesConstants.STRIPES_RESOURCES_PROPERTIES);
		//Write the StripesResources.properties
		installFile(stripesResources, srcPsiDirectory, StripesConstants.STRIPES_RESOURCES_PROPERTIES);

		ParamValue paramValue = null;
		for (ParamValue pv : app.getContextParams()) {
			if ("javax.servlet.jsp.jstl.fmt.localizationContext".equals(pv.getParamName().getStringValue())) {
				paramValue = pv;
				break;
			}
		}

		if (facetCfg.isDefaultBundle() && paramValue == null) {
			paramValue = app.addContextParam();
			paramValue.getParamName().setStringValue("javax.servlet.jsp.jstl.fmt.localizationContext");
			paramValue.getParamValue().setStringValue("StripesResources");
		}
//	    else if (!facetCfg.isDefaultBundle() && paramValue != null) {
//		    app.getContextParams().remove(paramValue);
//	    }
	}

	/**
	 * Install Logging support
	 *
	 * @param app           Web Application
	 * @param configuration Stripes Configuration
	 */
	private static void installLogging(WebApp app, StripesFacetConfiguration configuration) {
		//get Src Directory
		PsiDirectory srcPsiDirectory = getSrcDirectory(app);
		//get a PsiFile for a commons-logging.properties
		PsiFile commons = srcPsiDirectory.findFile(StripesConstants.COMMONS_LOGGING_PROPERTIES);
		//get a PsiFile for a log4j.properties or .xml
		PsiFile log4j = srcPsiDirectory.findFile(configuration.getLog4jFile());
		//Write the commons-logging.properties
		installFile(commons, srcPsiDirectory, StripesConstants.COMMONS_LOGGING_PROPERTIES);
		//Write the log4j file
		installLog4jFile(log4j, srcPsiDirectory, configuration.getLog4jFile());
	}

	/**
	 * Get the Src Directory
	 *
	 * @param app Web Application
	 * @return PsiDirectory
	 */
	private static PsiDirectory getSrcDirectory(WebApp app) {
		//get the Module Root Manager
		ModuleRootManager manager = ModuleRootManager.getInstance(app.getModule());
		//get the first Src
		VirtualFile src = manager.getSourceRoots()[0];
		PsiDirectory srcPsiDirectory = null;
		try {
			//get the PsiDirectory
			srcPsiDirectory = PsiManager.getInstance(app.getModule().getProject()).findDirectory(src);
		} catch (NullPointerException e) {
			//
		}
		return srcPsiDirectory;
	}

	/**
	 * Write a File from a FileTemplate in a specific directory
	 *
	 * @param psiFile   PsiFle
	 * @param directory PsiDirectory
	 * @param fileName  file's name
	 */
	private static void installFile(PsiFile psiFile, PsiDirectory directory, final String fileName) {
		//Write the file if don't exist
		if (psiFile == null) {
			//Get template
			FileTemplate stripesTemplate = FileTemplateManager.getInstance().getJ2eeTemplate(fileName);
			try {
				//Write the file
				FileTemplateUtil.createFromTemplate(stripesTemplate, fileName, null, directory);
			} catch (Exception e) {
				//
			}
		}
	}

	/**
	 * Write log4j file
	 *
	 * @param psiFile   PsiFile
	 * @param directory PsiDirectory
	 * @param fileName  file's name
	 */
	private static void installLog4jFile(PsiFile psiFile, PsiDirectory directory, final String fileName) {
		//Write file if don't exists
		if (psiFile == null) {
			/*IntelliJ have a "bug" (or undocumented feataure) when you have FileTemplates with the same name
						but different extension...
						So we have to manage this in 2 one for the file's names and another to the template name
						*/
			String templateName = "";
			if (fileName.equals(StripesConstants.LOG4J_PROPERTIES)) {
				templateName = StripesConstants.LOG4J_PROPERTIES_TEMPLATE;
			} else if (fileName.equals(StripesConstants.LOG4J_XML)) {
				templateName = StripesConstants.LOG4J_XML_TEMPLATE;
			}
			FileTemplate stripesTemplate = FileTemplateManager.getInstance().getJ2eeTemplate(templateName);
			try {
				FileTemplateUtil.createFromTemplate(stripesTemplate, fileName, null, directory);
			} catch (Exception e) {
				//
			}
		}
	}
}
