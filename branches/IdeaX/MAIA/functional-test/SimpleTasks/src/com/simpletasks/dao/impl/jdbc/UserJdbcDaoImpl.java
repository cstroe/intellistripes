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

import com.simpletasks.dao.TaskDao;
import com.simpletasks.dao.UserDao;
import com.simpletasks.domain.User;
import com.simpletasks.domain.exceptions.UserNotFoundException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.simple.SimpleJdbcDaoSupport;

/**
 * Created by IntelliJ IDEA.
 * User: Mario Arias
 * Date: 30/05/2008
 * Time: 10:48:43 PM
 */
public class UserJdbcDaoImpl extends SimpleJdbcDaoSupport implements UserDao {
// ------------------------------ FIELDS ------------------------------

    private static final String SELECT = "select * from users ";
    private final static String SELECT_BY_EMAIL_PASSWORD = SELECT + "where user_email = ? and user_password = ?";
    private final static String INSERT = "insert into users(user_email, user_password, user_name, user_last_name) values(?,?,?,?)";
    private TaskDao taskDao;

// --------------------- GETTER / SETTER METHODS ---------------------

    public void setTaskDao(TaskDao taskDao) {
        this.taskDao = taskDao;
    }

// ------------------------ INTERFACE METHODS ------------------------


// --------------------- Interface UserDao ---------------------

    public User retrieveUser(String email, String password) throws UserNotFoundException {
        try {
            return getSimpleJdbcTemplate().queryForObject(SELECT_BY_EMAIL_PASSWORD, new UserMapper(), email, password);
        } catch (EmptyResultDataAccessException e) {
            throw new UserNotFoundException();
        }
    }

    public void insert(User user) {
        getSimpleJdbcTemplate().update(INSERT, user.getEmail(),
                user.getPassword(),
                user.getName(),
                user.getLastName());
    }
}
