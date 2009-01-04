package org.intellij.stripes.actions;

import org.intellij.stripes.util.StripesConstants;

/**
 * Created by IntelliJ IDEA.
 * User: Mario Arias
 * Date: 4/01/2009
 * Time: 11:25:44 AM
 */
public class StripesNewInterceptorAction extends StripesBaseNewClassAction {
    protected String getClassName() {
        return "Interceptor";
    }

    protected String getTemplateName() {
        return StripesConstants.INTERCEPTOR_TEMPLATE;
    }
}
