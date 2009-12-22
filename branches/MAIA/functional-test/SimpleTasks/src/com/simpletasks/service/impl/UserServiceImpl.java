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

import com.simpletasks.dao.UserDao;
import com.simpletasks.domain.User;
import com.simpletasks.domain.exceptions.UserNotFoundException;
import com.simpletasks.service.UserService;

/**
 * Created by IntelliJ IDEA.
 * User: Mario Arias
 * Date: 30/05/2008
 * Time: 11:14:31 PM
 */
public class UserServiceImpl implements UserService {
// ------------------------------ FIELDS ------------------------------

    private UserDao userDao;

// --------------------- GETTER / SETTER METHODS ---------------------

    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

// ------------------------ INTERFACE METHODS ------------------------


// --------------------- Interface UserService ---------------------

    public User getUser(User user) throws UserNotFoundException {
        return userDao.retrieveUser(user.getEmail(), user.getPassword());
    }

    public void insert(User user) {
        userDao.insert(user);
    }
}
