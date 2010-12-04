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

package com.simpletasks.stripes.interceptors;

import net.sourceforge.stripes.controller.Interceptor;
import net.sourceforge.stripes.controller.ExecutionContext;
import net.sourceforge.stripes.controller.Intercepts;
import net.sourceforge.stripes.controller.LifecycleStage;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.RedirectResolution;
import net.sourceforge.stripes.action.ActionBean;
import com.simpletasks.stripes.context.SimpleTasksActionBeanContext;
import com.simpletasks.web.user.LoginActionBean;
import com.simpletasks.web.user.NewAccountActionBean;

/**
 * Created by IntelliJ IDEA.
 * User: Mario Arias
 * Date: 30/05/2008
 * Time: 08:51:50 PM
 */
@Intercepts(LifecycleStage.EventHandling)
public class SecurityInterceptor implements Interceptor {
// -------------------------- STATIC METHODS --------------------------

    private static boolean isLogged(ExecutionContext executionContext) {
        SimpleTasksActionBeanContext context = (SimpleTasksActionBeanContext) executionContext.getActionBeanContext();
        return context.getUser() != null;
    }

    private static boolean isValidWithoutLogin(ExecutionContext executionContext) {
        ActionBean bean = executionContext.getActionBean();
        return bean instanceof LoginActionBean || bean instanceof NewAccountActionBean;
    }

// ------------------------ INTERFACE METHODS ------------------------


// --------------------- Interface Interceptor ---------------------

    public Resolution intercept(ExecutionContext executionContext) throws Exception {
        if (isValidWithoutLogin(executionContext)) {
            return executionContext.proceed();
        } else if (isLogged(executionContext)) {
            return executionContext.proceed();
        } else {
            return new RedirectResolution("/index.jsp");
        }
    }
}
