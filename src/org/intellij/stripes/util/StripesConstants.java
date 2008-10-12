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
    Icon ACTION_BEAN_ICON = IconLoader.findIcon("/resources/icons/ActionBean.png");
    Icon ACTION_BEAN_GUTTER_ICON = IconLoader.findIcon("/resources/icons/ActionBeanGutter.png");
    Icon STRIPES_JSP_ICON = IconLoader.findIcon("/resources/icons/JspStripes.png");
    Icon STRIPES_ICON = IconLoader.findIcon("/resources/icons/StripesIcon.png");
    Icon FIELD_ICON = IconLoader.findIcon("/resources/icons/Field.png");
    Icon RESOLUTION_ICON = IconLoader.findIcon("/resources/icons/Resolution.png");
    Icon LAYOUT_COMPONENT_ICON = IconLoader.findIcon("/resources/icons/LayoutComponent.png");

    //stripes class names
    String STRIPES_SERVLET_CLASS = "net.sourceforge.stripes.controller.DispatcherServlet";
    String STRIPES_FILTER_CLASS = "net.sourceforge.stripes.controller.StripesFilter";
    String STRIPES_RESOLUTION_CLASS = "net.sourceforge.stripes.action.Resolution";
    String SPRING_LISTENER = "org.springframework.web.context.ContextLoaderListener";
    String SPRING_INTERCEPTOR_CLASS = "net.sourceforge.stripes.integration.spring.SpringInterceptor";
    String BEFORE_AFTER_METHOD_INTERCEPTOR_CLASS = "net.sourceforge.stripes.controller.BeforeAfterMethodInterceptor";
    String ACTION_BEAN = "net.sourceforge.stripes.action.ActionBean";
    String FILE_BEAN = "net.sourceforge.stripes.action.FileBean";
    String ACTION_BEAN_CONTEXT = "net.sourceforge.stripes.action.ActionBeanContext";
    String FORWARD_RESOLUTION = "net.sourceforge.stripes.action.ForwardResolution";
    String STREAMING_RESOLUTION = "net.sourceforge.stripes.action.StreamingResolution";
    String REDIRECT_RESOLUTION = "net.sourceforge.stripes.action.RedirectResolution";
    String VALIDATION_ERRORS = "net.sourceforge.stripes.validation.ValidationErrors";

    //stripes annotations names
    String SPRING_BEAN_ANNOTATION = "net.sourceforge.stripes.integration.spring.SpringBean";
    String HANDLES_EVENT_ANNOTATION = "net.sourceforge.stripes.action.HandlesEvent";
    String DEFAULT_HANDLER_ANNOTATION = "net.sourceforge.stripes.action.DefaultHandler";
    String VALIDATE_ANNOTATION = "net.sourceforge.stripes.validation.Validate";
    String VALIDATE_NESTED_PROPERTIES_ANNOTATION = "net.sourceforge.stripes.validation.ValidateNestedProperties";
    String VALIDATION_METHOD_ANNOTATION = "net.sourceforge.stripes.validation.ValidationMethod";
    String URL_BINDING_ANNOTATION = "net.sourceforge.stripes.action.UrlBinding";
    String AFTER_ANNOTATION = "net.sourceforge.stripes.action.After";
    String BEFORE_ANNOTATION = "net.sourceforge.stripes.action.Before";
    String STRICT_BINDING_ANNOTATION = "net.sourceforge.stripes.action.StrictBinding";

    //stripes annotation attribute names
    String EXPRESSION_ATTR = "expression";
    String MASK_ATTR = "mask";
    String DENY_ATTR = "deny";
    String ALLOW_ATTR = "allow";
    String ON_ATTR = "on";

    //stripes configuration
    String STRIPES_SERVLET_NAME = "StripesDispatcher";
    String DEFAULT_STRIPES_MAPPING = "*.action";
    String STRIPES_FILTER_NAME = "StripesFilter";
    String SPRING_CONTEXT_PARAM = "contextConfigLocation";
    String INTERCEPTOR_CLASSES = "Interceptor.Classes";
    String ACTION_RESOLVER_URL_FILTER = "ActionResolver.UrlFilters";
    String STRIPES_FILTER_MAPPING = "*.jsp";
    String REQUEST = "REQUEST";

    //stripes taglibs
    String TAGLIB_PREFIX = "http://stripes.sourceforge.net/stripes";
    String STRIPES_TLD = "http://stripes.sourceforge.net/stripes.tld";
    String STRIPES_DYNAMIC_TLD = "http://stripes.sourceforge.net/stripes-dynattr.tld";
    String[] STRIPES_TLDS = {STRIPES_DYNAMIC_TLD, STRIPES_TLD};

    //stripes tags
    String FORM_TAG = "form";
    String FILE_TAG = "file";
    String URL_TAG = "url";
    String LINK_TAG = "link";
    String ERRORS_TAG = "errors";
    String LINK_PARAM_TAG = "link-param";
    String PARAM_TAG = "param";
    String USE_ACTION_BEAN_TAG = "useActionBean";
    String LAYOUT_RENDER_TAG = "layout-render";
    String LAYOUT_COMPONENT_TAG = "layout-component";
    String LAYOUT_DEFINITION_TAG = "layout-definition";
    String IMAGE_TAG = "image";
    String BUTTON_TAG = "button";
    String SUBMIT_TAG = "submit";
    String PASSWORD_TAG = "password";
    String HIDDEN_TAG = "hidden";
    String RADIO_TAG = "radio";
    String CHECKBOX_TAG = "checkbox";
    String TEXT_TAG = "text";
    String TEXTAREA_TAG = "textarea";
    String SELECT_TAG = "select";
    String LABEL_TAG = "label";
    String RESET_TAG = "reset";
    String OPTION_TAG = "option";
    String OPTIONS_ENUMERATION_TAG = "options-enumeration";
    String OPTIONS_COLLECTION_TAG = "options-collection";

    String[] CLASS_TAGS = {BUTTON_TAG, CHECKBOX_TAG, FILE_TAG, FORM_TAG, IMAGE_TAG, LABEL_TAG, LINK_TAG, HIDDEN_TAG, OPTION_TAG, OPTIONS_COLLECTION_TAG,
            OPTIONS_ENUMERATION_TAG, PASSWORD_TAG, RADIO_TAG, RESET_TAG, SELECT_TAG, SUBMIT_TAG, TEXT_TAG, TEXTAREA_TAG};
    String[] ACTION_BEAN_TAGS = {FORM_TAG, ERRORS_TAG, LINK_TAG, URL_TAG, USE_ACTION_BEAN_TAG};
    String[] ACTION_BEAN_TAGS_WITH_EVENT = {LINK_TAG, URL_TAG, USE_ACTION_BEAN_TAG};
    String[] PARAMS_TAGS = {PARAM_TAG, LINK_PARAM_TAG};
    String[] RESOLUTION_TAGS = {BUTTON_TAG, IMAGE_TAG, SUBMIT_TAG};
    String[] INPUT_TAGS = {CHECKBOX_TAG, HIDDEN_TAG, PASSWORD_TAG, RADIO_TAG, SELECT_TAG, TEXT_TAG, TEXTAREA_TAG};

    //stripes tag attributes
    String ENUM_ATTR = "enum";
    String CLASS_ATTR = "class";
    String BEANCLASS_ATTR = "beanclass";
    String ID_ATTR = "id";
    String VAR_ATTR = "var";
    String FIELD_ATTR = "field";
    String NAME_ATTR = "name";
    String URI_ATTR = "uri";
    String EVENT_ATTR = "event";
    String SRC_ATTR = "src";

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

    //file templates
    String STRIPES_TEMPLATES = "Stripes Templates";
    String COMMONS_LOGGING_PROPERTIES = "commons-logging.properties";
    String STRIPES_RESOURCES_PROPERTIES = "StripesResources.properties";
    String ACTION_BEAN_TEMPLATE = "ActionBean.java";
    String LOG4J_XML_TEMPLATE = "log4j xml configuration.xml";
    String LOG4J_PROPERTIES_TEMPLATE = "log4j properties configuration.properties";
    String LOG4J_XML = "log4j.xml";
    String LOG4J_PROPERTIES = "log4j.properties";


    String STRIPES = "Stripes";
    LibraryInfo[] STRIPES_LIBRARY_INFO = {MavenLibraryUtil.createMavenJarInfo("stripes", "1.4.3", "net.sourceforge.stripes.action.ActionBean"),
            MavenLibraryUtil.createMavenJarInfo("commons-logging", "1.0", "org.apache.commons.logging.Log"),
            MavenLibraryUtil.createMavenJarInfo("cos", "1.3.1", "com.oreilly.servlet.multipart.FilePart")};


}
