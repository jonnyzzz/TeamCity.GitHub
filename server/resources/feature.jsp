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
  <th>GitHub Url:</th>
  <td>
    <props:textProperty name="${keys.serverKey}"/>
    <span class="smallNote">Specify GitHub instance URL</span>
  </td>
</tr>
<tr>
  <th>GitHub User Name:<l:star/></th>
  <td>
    <props:textProperty name="${keys.userNameKey}"/>
    <span class="smallNote">Specify GitHub username</span>
  </td>
</tr>
<tr>
  <th>GitHub Password:<l:star/></th>
  <td>
    <props:passwordProperty name="${keys.passwordKey}"/>
    <span class="smallNote">Specify GitHub password</span>
  </td>
</tr>
<tr>
  <th>GitHub Repository:<l:star/></th>
  <td>
    <props:textProperty name="${keys.repositoryNameKey}"/>
    <span class="smallNote">Specify GitHub repository name to push changes statuses to</span>
  </td>
</tr>

<!-- this page supports .jsp resources resolving -->
