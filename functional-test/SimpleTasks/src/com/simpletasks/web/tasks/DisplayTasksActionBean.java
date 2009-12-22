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

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Mario Arias
 * Date: 31/05/2008
 * Time: 09:03:04 AM
 */
public class DisplayTasksActionBean extends TaskActionBean {
// ------------------------------ FIELDS ------------------------------

    private List<Task> tasks;

// --------------------- GETTER / SETTER METHODS ---------------------

    public List<Task> getTasks() {
        return tasks;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }

// -------------------------- OTHER METHODS --------------------------

    @DefaultHandler
    public Resolution init() {
        tasks = taskService.getTasks(context.getUser());
        return new ForwardResolution("/WEB-INF/jsp/DisplayTasks.jsp");
    }

    public Resolution delete(){
        taskService.deleteTask(task);
        return init();
    }
}
