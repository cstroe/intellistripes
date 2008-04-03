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

package org.intellij.stripes.util;

import com.intellij.facet.ui.libraries.LibraryInfo;
import com.intellij.facet.ui.libraries.MavenLibraryUtil;
import com.intellij.openapi.util.IconLoader;

import javax.swing.*;

/**
 * Created by IntelliJ IDEA. User: Mario Arias Date: 2/07/2007 Time: 02:07:44 AM
 */
public interface StripesConstants {
// ------------------------------ FIELDS ------------------------------

    //Icons
    Icon ACTION_BEAN_ICON = IconLoader.findIcon("/org/intellij/stripes/icons/ActionBean.png");
    Icon STRIPES_JSP_ICON = IconLoader.findIcon("/org/intellij/stripes/icons/JspStripes.png");
    Icon STRIPES_ICON = IconLoader.findIcon("/org/intellij/stripes/icons/StripesIcon.png");
    Icon FIELD_ICON = IconLoader.findIcon("/org/intellij/stripes/icons/Field.png");
    Icon RESOLUTION_ICON = IconLoader.findIcon("/org/intellij/stripes/icons/Resolution.png");
    Icon LAYOUT_COMPONENT_ICON = IconLoader.findIcon("/org/intellij/stripes/icons/LayoutComponent.png");
    //Strings
    //Class Name
    String STRIPES_SERVLET_CLASS = "net.sourceforge.stripes.controller.DispatcherServlet";
    String STRIPES_FILTER_CLASS = "net.sourceforge.stripes.controller.StripesFilter";
    String STRIPES_RESOLUTION_CLASS = "net.sourceforge.stripes.action.Resolution";
    String SPRING_LISTENER = "org.springframework.web.context.ContextLoaderListener";
    String SPRING_INTERCEPTOR_CLASS = "net.sourceforge.stripes.integration.spring.SpringInterceptor";
    String BEFORE_AFTER_METHOD_INTERCEPTOR_CLASS = "net.sourceforge.stripes.controller.BeforeAfterMethodInterceptor";
    String STRIPES_ACTION_BEAN_CLASS = "net.sourceforge.stripes.action.ActionBean";
    String STRIPES_ACTION_BEAN_CONTEXT = "net.sourceforge.stripes.action.ActionBeanContext";
    String SPRING_BEAN = "net.sourceforge.stripes.integration.spring.SpringBean";
    String STRIPES_HANDLES_EVENT_ANNOTATION = "net.sourceforge.stripes.action.HandlesEvent";
    String FORWARD_RESOLUTION = "net.sourceforge.stripes.action.ForwardResolution";
    String REDIRECT_RESOLUTION = "net.sourceforge.stripes.action.RedirectResolution";
    //Others
    String STRIPES_SERVLET_NAME = "StripesDispatcher";
    String DEFAULT_STRIPES_MAPPING = "*.action";
    String STRIPES_FILTER_NAME = "StripesFilter";
    String SPRING_CONTEXT_PARAM = "contextConfigLocation";
    String INTERCEPTOR_CLASSES = "Interceptor.Classes";
    String ACTION_RESOLVER_URL_FILTER = "ActionResolver.UrlFilters";
    String STRIPES_FILTER_MAPPING = "*.jsp";
    String REQUEST = "REQUEST";
    String STRIPES_TLD = "http://stripes.sourceforge.net/stripes.tld";
    String STRIPES_DYNAMIC_TLD = "http://stripes.sourceforge.net/stripes-dynattr.tld";
    String[] STRIPES_TLDS = {STRIPES_DYNAMIC_TLD, STRIPES_TLD};
    String CLASS_ATTRIBUTE = "class";
    String[] CLASS_TAGS = {"button", "checkbox", "file", "form", "image", "label", "link", "hidden", "option", "options-collection",
            "options-enumeration", "password", "radio", "reset", "select", "submit", "text", "textarea"};
    String FORM_TAG = "form";
    String LINK_TAG = "link";
    String ERRORS_TAG = "errors";
    String LINK_PARAM_TAG = "link-param";
    String PARAM_TAG = "param";
    String BEAN_CLASS_ATTRIBUTE = "beanclass";
    String LAYOUT_RENDER_TAG = "layout-render";
    String URL_TAG = "url";
    String FIELD_ATTRIBUTE = "field";
    String[] ACTION_BEAN_TAGS = {"form", "errors", "link", "url", "useActionBean"};
    String[] ACTION_BEAN_TAGS_WITH_EVENT = {"link", "url", "useActionBean"};
    String[] PARAMS_TAGS = {"param", "link-param"};
    String EVENT = "event";
    String[] RESOLUTION_TAGS = {"button", "image", "submit"};
    String[] INPUT_TAGS = {"checkbox", "file", "hidden", "password", "radio", "select", "text", "textarea"};
    String NAME_ATTRIBUTE = "name";

    //facet configuration
    String SPRING_INTEGRATION = "springIntegration";
    String LOGGING = "logging";
    String STRIPES_RESOURCES = "stripesResources";
    String LOG4J_FILE = "log4jFile";
    String URL_FILTER = "urlFilter";
    String FILTER_VALUE = "filterValue";
    String CHANGE_ICONS = "changeIcons";
    //Integers

    Integer LOAD_ON_STARTUP = 1;

    String STRIPES_TEMPLATES = "Stripes Templates";
    String COMMONS_LOGGING_PROPERTIES = "commons-logging.properties";
    String STRIPES_RESOURCES_PROPERTIES = "StripesResources.properties";
    String ACTION_BEAN_TEMPLATE = "ActionBean.java";
    String LOG4J_XML_TEMPLATE = "log4j xml configuration.xml";
    String LOG4J_PROPERTIES_TEMPLATE = "log4j properties configuration.properties";
    String LOG4J_XML = "log4j.xml";
    String LOG4J_PROPERTIES = "log4j.properties";


    String STRIPES = "Stripes";
    //layouts
    String LAYOUT_RENDER = "layout-render";
    String LAYOUT_COMPONENT = "layout-component";
    String LAYOUT_DEFINITION = "layout-definition";

    LibraryInfo[] STRIPES_LIBRARY_INFO = {MavenLibraryUtil.createMavenJarInfo("stripes", "1.4.3", "net.sourceforge.stripes.action.ActionBean"),
            MavenLibraryUtil.createMavenJarInfo("commons-logging", "1.0", "org.apache.commons.logging.Log"),
            MavenLibraryUtil.createMavenJarInfo("cos", "1.3.1", "com.oreilly.servlet.multipart.FilePart")};
}
