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
package com.simpletasks.dao.impl.jdbc;

import org.springframework.jdbc.core.simple.SimpleJdbcDaoSupport;
import com.simpletasks.dao.TaskDao;
import com.simpletasks.domain.Task;
import com.simpletasks.domain.User;

import java.util.List;
import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: Mario Arias
 * Date: 30/05/2008
 * Time: 11:00:19 PM
 */
public class TaskJdbcDaoImpl extends SimpleJdbcDaoSupport implements TaskDao {
// ------------------------------ FIELDS ------------------------------

    private final static String SELECT = "select * from tasks ";
    private final static String SELECT_BY_USER = SELECT + "where user_id = ?";
    private static final String WHERE_TASK_ID = "where task_id = ?";
    private final static String SELECT_BY_ID = SELECT + WHERE_TASK_ID;
    private final static String INSERT = "insert into tasks(user_id, task_title, task_detail,task_create_date) values(?,?,?,?)";
    private final static String DELETE = "delete from tasks " + WHERE_TASK_ID;

// ------------------------ INTERFACE METHODS ------------------------


// --------------------- Interface TaskDao ---------------------

    public List<Task> retrieveTaskByUser(User user) {
        return getSimpleJdbcTemplate().query(SELECT_BY_USER, new TaskMapper(), user.getId());
    }

    public void insert(Task task, User user) {
        getSimpleJdbcTemplate().update(INSERT, user.getId(),
                task.getTitle(),
                task.getDetail(),
                new Date());
    }

    public Task retrieve(int taskID){
        return getSimpleJdbcTemplate().queryForObject(SELECT_BY_ID, new TaskMapper(), taskID);
    }

    public void delete(Task task){
        getSimpleJdbcTemplate().update(DELETE, task.getId());
    }
}
