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

package com.simpletasks.service.impl;

import com.simpletasks.dao.TaskDao;
import com.simpletasks.domain.Task;
import com.simpletasks.domain.User;
import com.simpletasks.service.TaskService;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Mario Arias
 * Date: 31/05/2008
 * Time: 12:35:28 AM
 */
public class TaskServiceImpl implements TaskService {
// ------------------------------ FIELDS ------------------------------

    private TaskDao taskDao;

// --------------------- GETTER / SETTER METHODS ---------------------

    public void setTaskDao(TaskDao taskDao) {
        this.taskDao = taskDao;
    }

// ------------------------ INTERFACE METHODS ------------------------


// --------------------- Interface TaskService ---------------------

    public void insertTask(Task task, User user) {
        taskDao.insert(task, user);
    }

    public List<Task> getTasks(User user) {
        return taskDao.retrieveTaskByUser(user);
    }

    public Task getTask(Task task){
        return taskDao.retrieve(task.getId());
    }

    public void deleteTask(Task task){
        taskDao.delete(task);
    }
}
