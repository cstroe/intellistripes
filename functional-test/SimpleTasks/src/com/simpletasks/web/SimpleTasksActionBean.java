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

package com.simpletasks.web;

import net.sourceforge.stripes.action.ActionBeanContext;
import net.sourceforge.stripes.action.ActionBean;
import com.simpletasks.stripes.context.SimpleTasksActionBeanContext;

/**
 * Created by IntelliJ IDEA.
 * User: Mario Arias
 * Date: 30/05/2008
 * Time: 09:30:16 PM
 */
public abstract class SimpleTasksActionBean implements ActionBean {
// ------------------------------ FIELDS ------------------------------

    protected SimpleTasksActionBeanContext context;

// --------------------- GETTER / SETTER METHODS ---------------------

    public SimpleTasksActionBeanContext getContext() {
        return context;
    }

    public void setContext(ActionBeanContext context) {
        this.context = (SimpleTasksActionBeanContext) context;
    }
}
