<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<fmt:setBundle basename="com.liteoc.i18n.format" var="resformat"/>
<fmt:setBundle basename="com.liteoc.i18n.words" var="resword"/>
<fmt:setBundle basename="com.liteoc.i18n.workflow" var="resworkflow"/>

<jsp:include page="../include/managestudy-header.jsp"/>


<%-- move the alert message to the sidebar --%>


<jsp:include page="../include/sideInfo.jsp"/>

<jsp:useBean scope="request" id="user" class="com.liteoc.bean.login.UserAccountBean"/>
<jsp:useBean scope="request" id="uRole" class="com.liteoc.bean.login.StudyUserRoleBean"/>
<jsp:useBean scope="request" id="uStudy" class="com.liteoc.bean.managestudy.StudyBean"/>

<h1><span class="title_manage"><fmt:message key="remove_user_role" bundle="${resword}"/></span></h1>

<form action="RemoveStudyUserRole" method="post">
<input type="hidden" name="action" value="submit">
<input type="hidden" name="name" value="<c:out value="${user.name}"/>">
<input type="hidden" name="studyId" value="<c:out value="${uRole.studyId}"/>">
<input type="hidden" name="roleId" value="<c:out value="${uRole.role.id}"/>">
<div style="width: 600px">
<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

<div class="tablebox_center">
<table border="0" cellpadding="0" cellspacing="0" width="100%">
  <tr><td class="table_header_column_top"><fmt:message key="first_name" bundle="${resword}"/>:</td><td class="table_cell"><c:out value="${user.firstName}"/></td></tr>
  <tr><td class="table_header_column_top"><fmt:message key="last_name" bundle="${resword}"/>:</td><td class="table_cell"><c:out value="${user.lastName}"/></td></tr>
  <tr><td class="table_header_column_top"><fmt:message key="role" bundle="${resword}"/>:</td><td class="table_cell"><c:out value="${uRole.role.description}"/></td>  
  </tr>  
  
 
</table>
</div>
</div></div></div></div></div></div></div></div>

</div>
<input type="submit" name="Submit" value="<fmt:message key="confirm" bundle="${resword}"/>" class="button_medium">
    &nbsp;
<input type="button" onclick="confirmCancel('ListStudyUser');"  name="cancel" value="   <fmt:message key="cancel" bundle="${resword}"/>   " class="button_medium"/>

</form>
<br/><br/>
<jsp:include page="../include/footer.jsp"/>
