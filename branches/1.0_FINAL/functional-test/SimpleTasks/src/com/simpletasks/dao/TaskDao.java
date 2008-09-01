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

package com.simpletasks.dao;

import com.simpletasks.domain.Task;
import com.simpletasks.domain.User;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Mario Arias
 * Date: 30/05/2008
 * Time: 10:59:58 PM
 */
public interface TaskDao {
// -------------------------- OTHER METHODS --------------------------

    List<Task> retrieveTaskByUser(User user);

    void insert(Task task, User user);

    Task retrieve(int taskID);

    void delete(Task task);
}
