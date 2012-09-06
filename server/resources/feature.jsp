<%@ include file="/include-internal.jsp"%>
<%@ taglib prefix="props" tagdir="/WEB-INF/tags/props" %>
<%--
  ~ Copyright 2000-2012 JetBrains s.r.o.
  ~
  ~ Licensed under the Apache License, Version 2.0 (the "License");
  ~ you may not use this file except in compliance with the License.
  ~ You may obtain a copy of the License at
  ~
  ~ http://www.apache.org/licenses/LICENSE-2.0
  ~
  ~ Unless required by applicable law or agreed to in writing, software
  ~ distributed under the License is distributed on an "AS IS" BASIS,
  ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  ~ See the License for the specific language governing permissions and
  ~ limitations under the License.
  --%>

<jsp:useBean id="keys" class="jetbrains.teamcilty.github.ui.UpdateChangesConstants"/>

<tr>
  <td colspan="2">Specify GitHub repository name and credentials to push status updates</td>
</tr>
<tr>
  <th>Url:<l:star/></th>
  <td>
    <props:textProperty name="${keys.serverKey}" className="longField"/>
    <span class="error" id="error_${keys.serverKey}"></span>
    <span class="smallNote">Specify GitHub instance URL</span>
  </td>
</tr>
<tr>
  <th>UserName:<l:star/></th>
  <td>
    <props:textProperty name="${keys.userNameKey}" className="longField"/>
    <span class="error" id="error_${keys.userNameKey}"></span>
    <span class="smallNote">Specify GitHub username</span>
  </td>
</tr>
<tr>
  <th>Password:<l:star/></th>
  <td>
    <props:passwordProperty name="${keys.passwordKey}" className="longField"/>
    <span class="error" id="error_${keys.passwordKey}"></span>
    <span class="smallNote">Specify GitHub password</span>
  </td>
</tr>
<tr>
  <th>Repository:<l:star/></th>
  <td>
    <props:textProperty name="${keys.repositoryNameKey}" className="longField"/>
    <span class="error" id="error_${keys.repositoryNameKey}"></span>
    <span class="smallNote">Specify GitHub repository name to push changes statuses to</span>
  </td>
</tr>

