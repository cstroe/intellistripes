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

package com.simpletasks.web.tasks;

import com.simpletasks.domain.Task;
import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.validation.Validate;
import net.sourceforge.stripes.validation.ValidateNestedProperties;

/**
 * Created by IntelliJ IDEA.
 * User: Mario Arias
 * Date: 31/05/2008
 * Time: 12:48:52 AM
 */
public class NewTaskActionBean extends TaskActionBean {
// -------------------------- STATIC METHODS --------------------------

    @DefaultHandler
    public static Resolution init() {
        return new ForwardResolution("/WEB-INF/jsp/CreateTask.jsp");
    }

// --------------------- GETTER / SETTER METHODS ---------------------

    @ValidateNestedProperties({
    @Validate(field = "title", required = true, on = "create"),
    @Validate(field = "detail", required = true, on = "create")})
    public void setTask(Task task) {
        this.task = task;
    }

// -------------------------- OTHER METHODS --------------------------

    public Resolution create() {
        taskService.insertTask(task, context.getUser());
        return new ForwardResolution(DisplayTasksActionBean.class);
    }
}
