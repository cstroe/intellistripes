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
  Date: 31/05/2008
  Time: 09:44:33 AM  
--%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="s" uri="http://stripes.sourceforge.net/stripes.tld" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="f" uri="http://java.sun.com/jstl/fmt_rt" %>
<%--@elvariable id="actionBean" type="com.simpletasks.web.tasks.DetailTaskActionBean"--%>
<c:set var="task" value="${actionBean.task}"/>
<s:layout-render name="/WEB-INF/layouts/simple.jsp" title="${task.title}">
    <s:layout-component name="content">
        <table>
            <tr>
                <td>Title</td>
                <td><c:out value="${task.title}"/></td>
            </tr>
            <tr>
                <td>Detail</td>
                <td><c:out value="${task.detail}"/></td>
            </tr>
            <tr>
                <td>Start Date:</td>
                <td><f:formatDate value="${task.createDate}"/></td>
            </tr>
        </table>
    </s:layout-component>
    <s:layout-component name="links">
        <s:link beanclass="com.simpletasks.web.tasks.DisplayTasksActionBean">
            HOME            
        </s:link>
    </s:layout-component>
</s:layout-render>