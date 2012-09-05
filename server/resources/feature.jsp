<%@ include file="/include-internal.jsp"%>
<%@ taglib prefix="props" tagdir="/WEB-INF/tags/props" %>
<jsp:useBean id="keys" class="jetbrains.teamcilty.github.ui.UpdateChangesConstants"/>

<tr>
  <th>GitHub Url:</th>
  <td>
    <props:textProperty name="${keys.serverKey}"/>
    <span class="smallNote">Specify GitHub instance URL. Leave blank if you use GitHub.com</span>
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
