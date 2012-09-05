<%@ include file="/include-internal.jsp"%>
<%@ taglib prefix="props" tagdir="/WEB-INF/tags/props" %>
<jsp:useBean id="keys" class="jetbrains.teamcilty.github.ui.UpdateChangesConstants"/>

<tr>
  <th>GitHub User Name:<l:star/></th>
  <td><props:textProperty name="${keys.userNameKey}"/></td>
</tr>
<tr>
  <th>GitHub Password:<l:star/></th>
  <td><props:passwordProperty name="${keys.passwordKey}"/></td>
</tr>

<!-- this page supports .jsp resources resolving -->
