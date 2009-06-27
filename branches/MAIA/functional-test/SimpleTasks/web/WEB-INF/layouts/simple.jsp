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
  Time: 08:58:40 PM  
--%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="s" uri="http://stripes.sourceforge.net/stripes.tld" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%--@elvariable id="title" type="java.lang.String"--%>
<c:url var="css" value="/css/simpletasks.css"/>
<c:url var="titleImage" value="/images/Title.png"/>
<c:url var="stripes" value="/images/stripesbutton-builtusing.gif"/>
<s:layout-definition>
    <html>
    <head>
        <title><c:out value="SimpleTasks - ${title}"/></title>
        <link rel="stylesheet" type="text/css" href="${css}"/>
    </head>
    <body>
    <div align="center">
        <table width="786" align="center">
            <tr>
                <td align="center">
                    <img src="${titleImage}" alt="SimpleTasks"/>
                </td>
            </tr>
            <tr>
                <td bgcolor="#3054BF" height="1px"/>
            </tr>

            <tr>
                <td>
                    <table width="100%">
                        <tr>
                            <td><s:layout-component name="content"/></td>
                            <td width="15%" valign="top">
                                <s:layout-component name="links"/>
                                <s:link beanclass="com.simpletasks.web.user.LoginActionBean" event="logout">
                                    LOGOUT
                                </s:link>
                            </td>
                        </tr>
                    </table>
                </td>
            </tr>

            <tr>
                <td bgcolor="#3054BF" height="1px"/>
            </tr>
            <tr>
                <td align="right">
                    <a href="http://stripesframework.org"><img src="${stripes}" alt="Built with Stripes"/></a>
                </td>
            </tr>
        </table>
    </div>
    </body>
    </html>
</s:layout-definition>