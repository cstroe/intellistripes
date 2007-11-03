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

package org.intellij.stripes.support;


import com.intellij.javaee.model.xml.Listener;
import com.intellij.javaee.model.xml.ParamValue;
import com.intellij.javaee.model.xml.web.*;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.ide.fileTemplates.FileTemplate;
import com.intellij.ide.fileTemplates.FileTemplateManager;
import com.intellij.ide.fileTemplates.FileTemplateUtil;
import org.intellij.stripes.facet.StripesFacet;
import org.intellij.stripes.facet.StripesFacetConfiguration;
import org.intellij.stripes.util.StripesConstants;

import java.util.List;

/**
 * Created by IntelliJ IDEA. User: Mario Arias Date: 3/07/2007 Time: 12:53:34 AM
 */
public class StripesSupportUtil
{

    private StripesSupportUtil()
    {
    }


    /**
     * Configure web.xml to run a Stripes Application
     *
     * @param facet StripesFacet
     */
    public static void addSupport(StripesFacet facet)
    {
        WebApp app = facet.getWebFacet().getRoot();

        try
        {
            installStripes(app);
            StripesFacetConfiguration stripesFacetConfiguration = facet.getConfiguration();
            if (stripesFacetConfiguration.isSpringIntegration())
            {
                installSpringIntegration(app);
            }
            if (stripesFacetConfiguration.isStripesResources())
            {
                installStripesResources(app);
            }
            if (stripesFacetConfiguration.isLogging())
            {
                installLogging(app, stripesFacetConfiguration);
            }
            if(stripesFacetConfiguration.isActionResolverUrlFilters())
            {
                addActionResolverUrlFilter(app, stripesFacetConfiguration);                
            }

        }
        catch (Throwable throwable)
        {
            //
        }
    }

    /**
     * Install Logging support
     *
     * @param app           Web Application
     * @param configuration Stripes Configuration
     */
    private static void installLogging(WebApp app, StripesFacetConfiguration configuration)
    {
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
     * Install StripesResources.properties
     *
     * @param app Web Application
     */
    private static void installStripesResources(WebApp app)
    {
        //get Src Directory
        PsiDirectory srcPsiDirectory = getSrcDirectory(app);
        //get a PsiFile for a StripesResources.properties
        PsiFile stripesResources = srcPsiDirectory.findFile(StripesConstants.STRIPES_RESOURCES_PROPERTIES);
        //Write the StripesResources.properties
        installFile(stripesResources, srcPsiDirectory, StripesConstants.STRIPES_RESOURCES_PROPERTIES);
    }

    /**
     * Write a File from a FileTemplate in a specific directory
     *
     * @param psiFile PsiFle
     * @param directory PsiDirectory
     * @param fileName file's name
     */
    private static void installFile(PsiFile psiFile, PsiDirectory directory, final String fileName)
    {
        //Write the file if don't exist        
        if (psiFile == null)
        {
            //Get template
            FileTemplate stripesTemplate = FileTemplateManager.getInstance().getJ2eeTemplate(fileName);
            try
            {
                //Write the file
                FileTemplateUtil.createFromTemplate(stripesTemplate, fileName, null, directory);
            }
            catch (Exception e)
            {
                //
            }
        }
    }

    /**Write log4j file
     *
     * @param psiFile PsiFile
     * @param directory PsiDirectory
     * @param fileName file's name
     */
    private static void installLog4jFile(PsiFile psiFile, PsiDirectory directory, final String fileName)
    {
        //Write file if don't exists
        if (psiFile == null)
        {
            /*IntelliJ have a "bug" (or undocumented feataure) when you have FileTemplates with the same name
            but different extension...
            So we have to manage this in 2 one for the file's names and another to the template name
            */
            String templateName = "";
            if (fileName.equals(StripesConstants.LOG4J_PROPERTIES))
            {
                templateName = StripesConstants.LOG4J_PROPERTIES_TEMPLATE;
            }
            else if (fileName.equals(StripesConstants.LOG4J_XML))
            {
                templateName = StripesConstants.LOG4J_XML_TEMPLATE;
            }
            FileTemplate stripesTemplate = FileTemplateManager.getInstance().getJ2eeTemplate(templateName);
            try
            {
                FileTemplateUtil.createFromTemplate(stripesTemplate, fileName, null, directory);
            }
            catch (Exception e)
            {
                //
            }
        }
    }

    /**Get the Src Directory
     *
     * @param app Web Application
     * @return PsiDirectory
     */
    private static PsiDirectory getSrcDirectory(WebApp app)
    {
        //get the Module Root Manager
        ModuleRootManager manager = ModuleRootManager.getInstance(app.getModule());
        //get the first Src
        VirtualFile src = manager.getSourceRoots()[0];
        PsiDirectory srcPsiDirectory = null;
        try
        {
            //get the PsiDirectory
            srcPsiDirectory = PsiManager.getInstance(app.getModule().getProject()).findDirectory(src);
        }
        catch (NullPointerException e)
        {
            //
        }
        return srcPsiDirectory;
    }

    /**Install Spring Integration
     *
     * @param app Web Application
     */
    private static void installSpringIntegration(WebApp app)
    {
        if (!isSpringListenerInstalled(app))
        {
            addSpringListener(app);
        }
        if (!isContextConfigLocation(app))
        {
            addContextConfigLocation(app);
        }
        Filter filter = getStripesFilter(app);
        addSpringInterceptor(filter);
    }

    /**add Context Param "contextConfigLocation" in the web.xml
     *
     * @param app Web Application
     */
    private static void addContextConfigLocation(WebApp app)
    {
        //Get the ParamValue Object (this is an abstraction for represent a <param-value> tag)
        ParamValue paramValue = app.addContextParam();
        paramValue.getParamName().setValue(StripesConstants.SPRING_CONTEXT_PARAM);
        paramValue.getParamValue().setValue("\n");
    }

    /**Add Spring Listener
     *
     * @param app Web Application
     */
    private static void addSpringListener(WebApp app)
    {
        //get a Listener Object (this is an abstraction for represent a <listener> tag)
        Listener listener = app.addListener();
        listener.getListenerClass().setStringValue(StripesConstants.SPRING_LISTENER);
    }

    /**Install Stripes Configuration
     *
     * @param app Web Applicatio
     * @throws Throwable some to throw
     */
    private static void installStripes(WebApp app) throws Throwable
    {
        if (!isStripesServletInstalled(app))
        {
            addDispatcherServlet(app);
        }
        if (!isStripesFilterInstalled(app))
        {
            addStripesFilter(app);

        }

    }

    /**Add Spring Interceptor
     *
     * @param filter Filter Object
     */
    private static void addSpringInterceptor(Filter filter)
    {
        if (isFilterInitParamConfigured(filter, StripesConstants.INTERCEPTOR_CLASSES))
        {
            //get the init-param
            ParamValue initParam = getFilterInitParam(filter, StripesConstants.INTERCEPTOR_CLASSES);
            //get the value
            String value = initParam.getParamValue().getValue();
            assert value != null;
            //get the Interceptors
            String[] interceptors = value.split(",");
            for (String interceptor : interceptors)
            {
                interceptor = interceptor.replaceAll("\n", "");
                //is Spring Interceptor already installed?
                if (interceptor.equals(StripesConstants.SPRING_INTERCEPTOR_CLASS))
                {
                    return;
                }
            }
            boolean beforeAfterInterceptorPresent = false;
            for (String interceptor : interceptors)
            {
                interceptor = interceptor.replaceAll("\n", "");
                //is BeforeAfterMethodInterceptor already installed?
                if (interceptor.equals(StripesConstants.BEFORE_AFTER_METHOD_INTERCEPTOR_CLASS))
                {
                    beforeAfterInterceptorPresent = true;
                }
            }
            if (beforeAfterInterceptorPresent)
            {
                //add Spring Filter before Other Interceptors
                initParam.getParamValue().setValue(StripesConstants.SPRING_INTERCEPTOR_CLASS + ',' + value);
            }
            {
                //add Spring and BeforeAfter Interceptors
                initParam.getParamValue().setValue(StripesConstants.SPRING_INTERCEPTOR_CLASS + ',' + StripesConstants.BEFORE_AFTER_METHOD_INTERCEPTOR_CLASS);
            }
        }
        else
        {
            addSpringInterceptorValue(filter);
        }
    }

    /**Add ActionResolver.UrlFilter to Stripes Filter
     *
     * @param webApp Web Application
     * @param configuration Facet Configuration
     */
    private static void addActionResolverUrlFilter(WebApp webApp,StripesFacetConfiguration configuration)
    {
        Filter filter = getStripesFilter(webApp);
        if(isFilterInitParamConfigured(filter, StripesConstants.ACTION_RESOLVER_URL_FILTER))
        {
            ParamValue initParam = getFilterInitParam(filter, StripesConstants.ACTION_RESOLVER_URL_FILTER);
            initParam.getParamValue().setValue(configuration.getUrlFiltersValue());
        }
        else
        {
            ParamValue paramValue = filter.addInitParam();
            paramValue.getParamName().setValue(StripesConstants.ACTION_RESOLVER_URL_FILTER);
            paramValue.getParamValue().setValue(configuration.getUrlFiltersValue());
        }
    }

    /**Add Spring and BeforeAfter interceptors
     *
     * @param filter Filter Object
     */
    private static void addSpringInterceptorValue(Filter filter)
    {
        ParamValue paramValue = filter.addInitParam();
        paramValue.getParamName().setValue(StripesConstants.INTERCEPTOR_CLASSES);
        paramValue.getParamValue().setValue(StripesConstants.SPRING_INTERCEPTOR_CLASS + ',' + StripesConstants.BEFORE_AFTER_METHOD_INTERCEPTOR_CLASS);
    }

    /**get A Specific initParam for a given Filter
     *
     * @param filter Filter Object
     * @param paramName initParam name
     * @return a Param Value Object, null if don't exists
     */
    private static ParamValue getFilterInitParam(Filter filter, String paramName)
    {
        List<ParamValue> initParams = filter.getInitParams();

        for (ParamValue initParam : initParams)
        {
            try
            {
                if (initParam.getParamName().getValue().equals(paramName))
                {
                    return initParam;
                }
            }
            catch (NullPointerException e)
            {
                //
            }
        }
        return null;
    }

    /**Get the Stripes Filter in web.xml
     * 
     * @param app Web Application
     * @return Filter Object, null if don't exist
     */
    private static Filter getStripesFilter(WebApp app)
    {
        List<Filter> filters = app.getFilters();       
        for (Filter filter : filters)
        {
            try
            {
                if (filter.getFilterClass().getValue().getQualifiedName().equals(StripesConstants.STRIPES_FILTER_CLASS))
                {
                    return filter;
                }
            }
            catch (NullPointerException e)
            {
                //
            }
        }

        return null;
    }

    /**Add Stripes Filter
     *
     * @param app Web Application
     */
    private static void addStripesFilter(WebApp app)
    {
        //Add Filter to the Web Application
        Filter filter = app.addFilter();
        filter.getFilterName().setValue(StripesConstants.STRIPES_FILTER_NAME);
        filter.getFilterClass().setStringValue(StripesConstants.STRIPES_FILTER_CLASS);
        //Mappings
        FilterMapping mapping = app.addFilterMapping();
        mapping.getFilterName().setValue(filter);
        mapping.addUrlPattern().setValue(StripesConstants.STRIPES_FILTER_MAPPING);
        mapping.addDispatcher().setStringValue(StripesConstants.REQUEST);

        FilterMapping servletFilterMapping = app.addFilterMapping();
        servletFilterMapping.getFilterName().setValue(filter);
        servletFilterMapping.addServletName().setStringValue(StripesConstants.STRIPES_SERVLET_NAME);
        servletFilterMapping.addDispatcher().setStringValue(StripesConstants.REQUEST);
    }

    /**Add Stripes Dispatcher Servlet
     *
     * @param app Web Application
     */
    private static void addDispatcherServlet(WebApp app)
    {
        Servlet dispatcherServlet = app.addServlet();
        dispatcherServlet.getServletName().setValue(StripesConstants.STRIPES_SERVLET_NAME);
        dispatcherServlet.getServletClass().setStringValue(StripesConstants.STRIPES_SERVLET_CLASS);
        dispatcherServlet.getLoadOnStartup().setValue(StripesConstants.LOAD_ON_STARTUP);
        //Mapping
        ServletMapping mapping = app.addServletMapping();
        mapping.getServletName().setValue(dispatcherServlet);
        mapping.addUrlPattern().setValue(StripesConstants.DEFAULT_STRIPES_MAPPING);
    }

    /**Is Stripes Dispatcher Servlet Installed
     *
     * @param app Web Application
     * @return boolean, do'h
     */
    public static boolean isStripesServletInstalled(WebApp app)
    {
        List<Servlet> servlets = app.getServlets();
        for (Servlet servlet : servlets)
        {
            boolean b = false;
            try
            {
                b = servlet.getServletClass().getValue().getQualifiedName().equals(StripesConstants.STRIPES_SERVLET_CLASS);
            }
            catch (NullPointerException e)
            {
                //
            }
            if (b)
            {
                return true;
            }
        }
        return false;
    }

    /**Is Stripes Dispatcher Servlet Installed
     *
     * @param app Web Application
     * @return boolean, do'h
     */
    public static boolean isSpringListenerInstalled(WebApp app)
    {
        List<Listener> listeners = app.getListeners();
        for (Listener listener : listeners)
        {
            boolean b = false;
            try
            {
                b = listener.getListenerClass().getValue().getQualifiedName().equals(StripesConstants.SPRING_LISTENER);
            }
            catch (NullPointerException e)
            {
                //
            }
            if (b)
            {
                return true;
            }
        }
        return false;
    }

    /**Is Spring Context Config Location Configured
     *
     * @param app Web Application
     * @return boolean, do'h
     */
    public static boolean isContextConfigLocation(WebApp app)
    {
        List<ParamValue> contextParams = app.getContextParams();
        for (ParamValue contextParam : contextParams)
        {
            boolean b = false;
            try
            {
                b = contextParam.getParamName().getValue().equals(StripesConstants.SPRING_CONTEXT_PARAM);
            }
            catch (NullPointerException e)
            {
                //
            }
            if (b)
            {
                return true;
            }
        }
        return false;
    }

    /**Is Param Configured configured
     *
     * @param filter Stripes Filter
     * @param paramName Parameter name
     * @return boolean, do'h
     */
    public static boolean isFilterInitParamConfigured(Filter filter, String paramName)
    {
        List<ParamValue> initParams = filter.getInitParams();
        for (ParamValue initParam : initParams)
        {
            boolean b = false;
            try
            {
                b = initParam.getParamName().getValue().equals(paramName);
            }
            catch (NullPointerException e)
            {
                //
            }
            if (b)
            {
                return true;
            }
        }
        return false;
    }

    /**Is Stripes Filter Installed
     *
     * @param app Web Application
     * @return boolean, do'h
     */
    public static boolean isStripesFilterInstalled(WebApp app)
    {
        List<Filter> filters = app.getFilters();
        for (Filter filter : filters)
        {
            boolean b = false;
            try
            {
                b = filter.getFilterClass().getValue().getQualifiedName().equals(StripesConstants.STRIPES_FILTER_CLASS);
            }
            catch (NullPointerException e)
            {
                //
            }
            if (b)
            {
                return true;
            }
        }
        return false;
    }
}
