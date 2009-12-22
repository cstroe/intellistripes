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

package com.simpletasks.web.user;

import com.simpletasks.domain.User;
import com.simpletasks.service.UserService;
import com.simpletasks.web.SimpleTasksActionBean;
import net.sourceforge.stripes.integration.spring.SpringBean;

/**
 * Created by IntelliJ IDEA.
 * User: Mario Arias
 * Date: 30/05/2008
 * Time: 11:19:11 PM
 */
public abstract class UserActionBean extends SimpleTasksActionBean {
// ------------------------------ FIELDS ------------------------------

    protected UserService userService;

    protected User user;

// --------------------- GETTER / SETTER METHODS ---------------------

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

// -------------------------- OTHER METHODS --------------------------

    @SpringBean("userService")
    public void injectUserService(UserService userService) {
        this.userService = userService;
    }
}
