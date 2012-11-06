<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<fmt:setBundle basename="com.liteoc.i18n.words" var="resword"/>


<jsp:include page="../include/home-header.jsp"/>


<%-- move the alert message to the sidebar --%>

<jsp:include page="../include/sideInfo.jsp"/>
<jsp:useBean scope="session" id="newRole" class="com.liteoc.bean.login.StudyUserRoleBean"/>
<h1><span class="title_manage"><fmt:message key="confirm_your_study_acces_request" bundle="${resword}"/></span></h1>

<form action="RequestStudy?action=submit" method="post">
<!-- These DIVs define shaded box borders -->
<div style="width: 600px">
<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

<div class="textbox_center">
<table border="0" cellpadding="0">

  <tr valign="top"><td class="table_header_column"><fmt:message key="first_name" bundle="${resword}"/>:</td><td class="table_cell"><c:out value="${userBean.firstName}"/></td></tr>
  <tr valign="top"><td class="table_header_column"><fmt:message key="last_name" bundle="${resword}"/>:</td><td class="table_cell"><c:out value="${userBean.lastName}"/></td></tr>
  <tr valign="top"><td class="table_header_column"><fmt:message key="email" bundle="${resword}"/>:</td><td class="table_cell"><c:out value="${userBean.email}"/></td></tr>
  <tr valign="top"><td class="table_header_column"><fmt:message key="study_requested" bundle="${resword}"/></td><td class="table_cell"><c:out value="${newRole.studyName}"/></td></tr>
  <tr><td class="table_header_column"><fmt:message key="role_of_access_requested" bundle="${resword}"/>:</td>
  <td class="table_cell"><c:out value="${newRole.role.description}"/></td></tr>

  </td></tr>
</table>
</div>

</div></div></div></div></div></div></div></div>

</div>
 <input type="submit" name="Submit" value="<fmt:message key="submit_request" bundle="${resword}"/>" class="button_long">
 <input type="button" onclick="confirmCancel('MainMenu');"  name="cancel" value="   <fmt:message key="cancel" bundle="${resword}"/>   " class="button_medium"/>
</form>
<jsp:include page="../include/footer.jsp"/>
