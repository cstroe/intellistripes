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
  Time: 09:35:16 PM  
--%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
        "http://www.w3.org/TR/html4/loose.dtd">
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="s" uri="http://stripes.sourceforge.net/stripes.tld" %>
<s:layout-render name="/WEB-INF/layouts/simple.jsp" title="Welcome">
    <s:layout-component name="content">
        <s:form beanclass="com.simpletasks.web.user.LoginActionBean">
            <table class="stForm">
                <tr>
                    <td align="right">Email :</td>
                    <td>
                        <s:text name="user.email"/>
                        &nbsp;
                        <s:errors field="user.email"/>
                    </td>
                </tr>
                <tr>
                    <td align="right">Password :</td>
                    <td>
                        <s:password name="user.password"/>
                        &nbsp;
                        <s:errors field="user.password"/>
                    </td>
                </tr>
                <tr>
                    <td align="right" colspan="2">
                        <s:submit name="login" value="Login" class="stButton"/>                        
                    </td>
                </tr>
            </table>
        </s:form>
        <s:link beanclass="com.simpletasks.web.user.NewAccountActionBean">
            Create a New Account
        </s:link>
    </s:layout-component>
</s:layout-render>