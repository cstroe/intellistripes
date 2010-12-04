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

import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import com.simpletasks.domain.Task;
import com.simpletasks.dao.DaoUtil;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by IntelliJ IDEA.
 * User: Mario Arias
 * Date: 30/05/2008
 * Time: 11:01:56 PM
 */
class TaskMapper implements ParameterizedRowMapper<Task> {
// ------------------------ INTERFACE METHODS ------------------------


// --------------------- Interface RowMapper ---------------------

    public Task mapRow(ResultSet resultSet, int i) throws SQLException {
        Task task = new Task();
        task.setId(resultSet.getInt(1));
        task.setTitle(resultSet.getString(3));
        task.setDetail(resultSet.getString(4));
        task.setFinished(resultSet.getBoolean(5));
        task.setCreateDate(DaoUtil.getDate(resultSet.getTimestamp(6)));
        task.setEndDate(DaoUtil.getDate(resultSet.getTimestamp(7)));
        return task;
    }
}
