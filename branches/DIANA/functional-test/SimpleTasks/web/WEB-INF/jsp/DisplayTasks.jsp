<%--
  ~ Copyright 2000-2007 JetBrains s.r.o.
  ~
  ~ Licensed under the Apache License, Version 2.0 (the "License");
  ~ you may not use this file except in compliance with the License.
  ~ You may obtain a copy of the License at
  ~
  ~ 	http://www.apache.org/licenses/LICENSE-2.0
  ~
  ~ Unless required by applicable law or agreed to in writing, software
  ~ distributed under the License is distributed on an "AS IS" BASIS,
  ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  ~ See the License for the specific language governing permissions and
  ~ limitations under the License.
  ~
  --%>

<%--
  Created by IntelliJ IDEA.
  User: Mario Arias
  Date: 30/05/2008
  Time: 11:27:11 PM  
--%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib prefix="s" uri="http://stripes.sourceforge.net/stripes.tld" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%--@elvariable id="actionBean" type="com.simpletasks.web.tasks.DisplayTasksActionBean"--%>
<c:set var="user" value="${actionBean.context.user}"/>
<s:layout-render name="/WEB-INF/layouts/simple.jsp" title="Task List">
    <s:layout-component name="content">
        Welcome <c:out value="${user.name}"/>
        <br/>
        <s:link beanclass="com.simpletasks.web.tasks.NewTaskActionBean">
            Add New Task
        </s:link>
        <table>            
            <c:forEach var="task" items="${actionBean.tasks}">
                <tr>
                    <td>
                        <s:link beanclass="com.simpletasks.web.tasks.DetailTaskActionBean">
                            <s:param name="task.id" value="${task.id}"/>
                            <c:out value="${task.detail}"/>
                        </s:link>
                    </td>
                    <td>
                        <s:url var="delete" beanclass="com.simpletasks.web.tasks.DisplayTasksActionBean" event="delete">
                            <s:param name="task.id" value="${task.id}"/>
                        </s:url>
                        <button onclick="location.href = '${delete}'" class="stButton">Delete</button>
                    </td>
                </tr>
            </c:forEach>
        </table>
    </s:layout-component>
</s:layout-render>