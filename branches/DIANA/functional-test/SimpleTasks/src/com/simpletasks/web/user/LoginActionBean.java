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
import com.simpletasks.domain.exceptions.UserNotFoundException;
import com.simpletasks.web.tasks.DisplayTasksActionBean;
import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.validation.EmailTypeConverter;
import net.sourceforge.stripes.validation.Validate;
import net.sourceforge.stripes.validation.ValidateNestedProperties;

/**
 * Created by IntelliJ IDEA.
 * User: Mario Arias
 * Date: 30/05/2008
 * Time: 09:34:27 PM
 */
public class LoginActionBean extends UserActionBean {
// --------------------- GETTER / SETTER METHODS ---------------------

    @ValidateNestedProperties({
    @Validate(field = "email", required = true, on = "login", converter = EmailTypeConverter.class),
    @Validate(field = "password", required = true, on = "login", minlength = 6, maxlength = 16)})
    public void setUser(User user) {
        this.user = user;
    }

// -------------------------- OTHER METHODS --------------------------

    public Resolution login() {
        try {
            context.setUser(userService.getUser(user));
            return new ForwardResolution(DisplayTasksActionBean.class);
        } catch (UserNotFoundException e) {
            return init();
        }
    }

    @DefaultHandler
    public static Resolution init() {
        return new ForwardResolution("/WEB-INF/jsp/Login.jsp");
    }

    public Resolution logout() {
        context.setUser(null);
        return init();
    }
}
