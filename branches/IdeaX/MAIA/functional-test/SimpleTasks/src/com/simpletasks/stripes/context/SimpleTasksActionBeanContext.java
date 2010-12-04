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

package com.simpletasks.stripes.context;

import net.sourceforge.stripes.action.ActionBeanContext;

import javax.servlet.http.HttpSession;

import com.simpletasks.domain.User;

/**
 * Created by IntelliJ IDEA.
 * User: Mario Arias
 * Date: 30/05/2008
 * Time: 08:35:14 PM
 */
public class SimpleTasksActionBeanContext extends ActionBeanContext {
// ------------------------------ FIELDS ------------------------------

    private static final String ST_USER = "ST.user";

// -------------------------- OTHER METHODS --------------------------

    public HttpSession getSession() {
        return getRequest().getSession();
    }

    public User getUser() {
        return (User) getSession().getAttribute(ST_USER);
    }

    public void setUser(User user) {
        getSession().setAttribute(ST_USER, user);
    }
}
