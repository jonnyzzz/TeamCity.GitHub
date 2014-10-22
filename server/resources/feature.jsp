<%@ include file="/include-internal.jsp" %>
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
  <td colspan="2">Specify GitHub repository name and credentials to push status updates to</td>
</tr>
<l:settingsGroup title="Main">
  <tr>
    <th>URL:<l:star/></th>
    <td>
      <props:textProperty name="${keys.serverKey}" className="longField"/>
      <span class="error" id="error_${keys.serverKey}"></span>
    <span class="smallNote">
      Specify GitHub instance URL.
      <br/>
      Use <strong>http(s)://[hostname]/api/v3</strong>
      for <a href="https://support.enterprise.github.com/entries/21391237-Using-the-API" target="_blank">GitHub
      Enterprise</a>
    </span>
    </td>
  </tr>

  <props:selectSectionProperty name="${keys.reportOnKey}" title="Report:">
    <props:selectSectionPropertyContent value="${keys.reportOnStartAndFinishValue}" caption="On Start and Finish">
    </props:selectSectionPropertyContent>
    <props:selectSectionPropertyContent value="${keys.reportOnStartValue}" caption="On Start">
    </props:selectSectionPropertyContent>
    <props:selectSectionPropertyContent value="${keys.reportOnFinishValue}" caption="On Finish">
    </props:selectSectionPropertyContent>
  </props:selectSectionProperty>

  <tr>
    <th><label for="${keys.useCommentsKey}">Use comments:</label></th>
    <td>
      <props:checkboxProperty name="${keys.useCommentsKey}"/>
      <label for="${keys.useCommentsKey}">Comment pull request with build details</label>
      <span class="error" id="error_${keys.useCommentsKey}"></span>
    </td>
  </tr>

  <tr>
    <th><label for="${keys.useGuestUrlsKey}">Use guest links:</label></th>
    <td>
      <props:checkboxProperty name="${keys.useGuestUrlsKey}"/>
      <label for="${keys.useGuestUrlsKey}">Use Guest Links</label>
      <span class="error" id="error_${keys.useGuestUrlsKey}"></span>
      <span class="smallNote">
        Makes generated TeamCity URLs automatically use guest login without the login form</span>
      <div class="attentionComment">
        Guest User should be enabled on the
        <a href="<c:url value='/admin/admin.html?item=auth'/>" target="_blank">Server Configuration page</a>.
      </div>
    </td>
  </tr>

  <tr>
    <th>Context:</th>
    <td>
      <props:textProperty name="${keys.contextKey}" className="longField"/>
      <span class="error" id="error_${keys.contextKey}"></span>
      <span class="smallNote">
        Specify GitHub status context. Leave blank for 'default'
      </span>
    </td>
  </tr>

</l:settingsGroup>

<tr>
  <td colspan="2">
    <div class="attentionComment">
      TeamCity Server URL<bs:help file="Configuring+Server+URL"/> will be used in GitHub status.
      Make sure this URL is specified correctly. To change it use the
      <a href="<c:url value='/admin/admin.html?item=serverConfigGeneral'/>" target="_blank">Server Configuration
        page</a>.
    </div>
  </td>
</tr>

<l:settingsGroup title="Authentication">
  <props:selectSectionProperty name="${keys.authenticationTypeKey}" title="Authentication Type">

    <props:selectSectionPropertyContent value="${keys.authenticationTypePasswordValue}" caption="Password">
      <tr>
        <th>User Name<l:star/>:</th>
        <td>
          <props:textProperty name="${keys.userNameKey}" className="longField"/>
          <span class="error" id="error_${keys.userNameKey}"></span>
          <span class="smallNote">Specify GitHub user name</span>
        </td>
      </tr>
      <tr>
        <th>Password<l:star/>:</th>
        <td>
          <props:passwordProperty name="${keys.passwordKey}" className="longField"/>
          <span class="error" id="error_${keys.passwordKey}"></span>
          <span class="smallNote">Specify GitHub password</span>
        </td>
      </tr>
    </props:selectSectionPropertyContent>

    <props:selectSectionPropertyContent value="${keys.authenticationTypeTokenValue}" caption="Access Token">
      <tr>
        <th>Personal Access Token<l:star/>:</th>
        <td>
          <props:passwordProperty name="${keys.accessTokenKey}" className="longField"/>
          <span class="error" id="error_${keys.accessTokenKey}"></span>
          <span class="smallNote">
            Specify a GitHub <a href="https://github.com/settings/applications" target="_blank">Personal Access Token</a>
            <br />
            It is required to have the following permissions:
            <strong><em>repo:status</em></strong> and
            <strong><em>public_repo</em></strong> or <strong><em>repo</em></strong> depending on the repository type
          </span>
        </td>
      </tr>
    </props:selectSectionPropertyContent>
  </props:selectSectionProperty>
</l:settingsGroup>

<l:settingsGroup title="Repository">
  <tr>
    <th>Owner:<l:star/></th>
    <td>
      <props:textProperty name="${keys.repositoryOwnerKey}" className="longField"/>
      <span class="error" id="error_${keys.repositoryOwnerKey}"></span>
      <span class="smallNote">Specify GitHub repository owner name (user or organization)</span>
    </td>
  </tr>
  <tr>
    <th>Repository:<l:star/></th>
    <td>
      <props:textProperty name="${keys.repositoryNameKey}" className="longField"/>
      <span class="error" id="error_${keys.repositoryNameKey}"></span>
      <span class="smallNote">Specify GitHub repository name to push change statuses to</span>
    </td>
  </tr>
</l:settingsGroup>
