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
import com.simpletasks.service.TaskService;
import com.simpletasks.web.SimpleTasksActionBean;
import net.sourceforge.stripes.integration.spring.SpringBean;

/**
 * Created by IntelliJ IDEA.
 * User: Mario Arias
 * Date: 31/05/2008
 * Time: 12:42:57 AM
 */
public abstract class TaskActionBean extends SimpleTasksActionBean {
// ------------------------------ FIELDS ------------------------------

    protected TaskService taskService;

    protected Task task;

// --------------------- GETTER / SETTER METHODS ---------------------

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

// -------------------------- OTHER METHODS --------------------------

    @SpringBean("taskService")
    public void injectTaskService(TaskService taskService) {
        this.taskService = taskService;
    }
}
