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

import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.validation.ValidateNestedProperties;
import net.sourceforge.stripes.validation.Validate;
import net.sourceforge.stripes.validation.EmailTypeConverter;
import com.simpletasks.domain.User;

/**
 * Created by IntelliJ IDEA.
 * User: Mario Arias
 * Date: 30/05/2008
 * Time: 11:57:50 PM
 */
public class NewAccountActionBean extends UserActionBean {
// ------------------------------ FIELDS ------------------------------

    @ValidateNestedProperties({
    @Validate(field = "email", required = true, on = "insert", converter = EmailTypeConverter.class),
    @Validate(field = "password", required = true, on = "insert", minlength = 6, maxlength = 16),
    @Validate(field = "name", required = true, on = "insert"),
    @Validate(field = "lastName", required = true, on = "insert")})
    public void setUser(User user) {
        this.user = user;
    }

    // -------------------------- STATIC METHODS --------------------------

    @DefaultHandler
    public static Resolution init() {
        return new ForwardResolution("/WEB-INF/jsp/CreateAccount.jsp");
    }

// -------------------------- OTHER METHODS --------------------------

    public Resolution insert() {
        userService.insert(user);
        return new ForwardResolution(LoginActionBean.class);
    }
}
