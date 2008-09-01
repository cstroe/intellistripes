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

package com.simpletasks.domain;

import java.util.Date;

/**
 * Created by IntelliJ IDEA. User: Mario Arias Date: 30/05/2008 Time: 08:28:01 PM
 */
public class Task {
// ------------------------------ FIELDS ------------------------------

    private int id;
    private String title;
    private String detail;
    private boolean finished;
    private Date createDate;
    private Date endDate;

// --------------------- GETTER / SETTER METHODS ---------------------

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isFinished() {
        return finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }

// ------------------------ CANONICAL METHODS ------------------------

    public String toString() {
        return "Task{" +
                "createDate=" + createDate +
                ", id=" + id +
                ", title='" + title + '\'' +
                ", detail='" + detail + '\'' +
                ", finished=" + finished +
                ", endDate=" + endDate +
                '}';
    }
}
