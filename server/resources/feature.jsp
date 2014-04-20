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


  <tr>
    <th><label for="${keys.useCommentsKey}">Use comments:</label></th>
    <td>
      <props:checkboxProperty name="${keys.useCommentsKey}"/>
      <label for="${keys.useCommentsKey}">Comment pull request with build details</label>
      <span class="error" id="error_${keys.useCommentsKey}"></span>
      <span class="smallNote">Adds comments with build details to GitHub change</span>
    </td>
  </tr>

</l:settingsGroup>

<l:settingsGroup title="Authentication">
  <tr>
    <th>Authentication Type</th>
    <td>
      <props:selectProperty name="${keys.authenticationTypeKey}" id="authenticationType">
        <props:option value="${keys.authenticationTypePasswordValue}">Password</props:option>
        <props:option value="${keys.authenticationTypeTokenValue}">Personal Access Token</props:option>
      </props:selectProperty>
    </td>
  </tr>

  <tr id="authUsernameRow">
    <th>User Name:<l:star/></th>
    <td>
      <props:textProperty name="${keys.userNameKey}" className="longField"/>
      <span class="error" id="error_${keys.userNameKey}"></span>
      <span class="smallNote">Specify GitHub user name</span>
    </td>
  </tr>
  <tr id="authPasswordRow">
    <th>Password:<l:star/></th>
    <td>
      <props:passwordProperty name="${keys.passwordKey}" className="longField"/>
      <span class="error" id="error_${keys.passwordKey}"></span>
      <span class="smallNote">Specify GitHub password</span>
    </td>
  </tr>

  <tr id="authTokenRow">
    <th>Personal Access Token:</th>
    <td>
      <props:passwordProperty name="${keys.accessTokenKey}" className="longField"/>
      <span class="error" id="error_${keys.accessTokenKey}"></span>
      <span class="smallNote">Specify Access Token      </span>

    </td>
  </tr>

</l:settingsGroup>


<tr>
  <td colspan="2">
    <div class="attentionComment">
      TeamCity Server URL<bs:help file="Configuring+Server+URL"/> will be used in GitHub status.
      Make sure this URL is specified correctly. To change it use
      <a href="<c:url value='/admin/admin.html?item=serverConfigGeneral'/>" target="_blank">Server Configuration
        page</a>.
    </div>
  </td>
</tr>
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


<script type="text/javascript">
  (function () {
    var update = function () {
      var authType = $j("#authenticationType").val();
      if (authType == "${keys.authenticationTypeTokenValue}") {
        BS.Util.show('authTokenRow');
        BS.Util.hide('authUsernameRow','authPasswordRow');
      } else {
        BS.Util.hide('authTokenRow');
        BS.Util.show('authUsernameRow','authPasswordRow');
      }
    };

    $j("#authenticationType").change(update);
    $j(document).ready(update);
  })();
</script>
