<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<fmt:setBundle basename="com.liteoc.i18n.words" var="resword"/>
<fmt:setBundle basename="com.liteoc.i18n.format" var="resformat"/>
<c:set var="dteFormat"><fmt:message key="date_format_string" bundle="${resformat}"/></c:set>

<jsp:include page="../include/admin-header.jsp"/>


<%-- move the alert message to the sidebar --%>

<jsp:include page="../include/sideInfo.jsp"/>

<jsp:useBean scope="session" id="subjectToUpdate" class="com.liteoc.bean.submit.SubjectBean" />


<h1><span class="title_manage"><fmt:message key="confirm_subject_details" bundle="${resword}"/></span></h1>
<P><fmt:message key="field_required" bundle="${resword}"/></P>
<form action="UpdateSubject" method="post">
<input type="hidden" name="action" value="submit">
<input type="hidden" name="id" value="<c:out value="${subjectToUpdate.id}"/>">
<input type="hidden" name="studySubId" value="<c:out value="${studySubId}"/>">
<!-- These DIVs define shaded box borders -->
<div style="width: 600px">
<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

<div class="textbox_center">
<table border="0" cellpadding="0" cellspacing="0" width="100%">
	<tr valign="top">
	  	<td class="table_header_column"><fmt:message key="person_ID" bundle="${resword}"/>:</td>
		<td class="table_cell">
		  <c:out value="${subjectToUpdate.uniqueIdentifier}"/>
		</td>
	</tr>

	<tr valign="top">
		<td class="table_header_column"><fmt:message key="gender" bundle="${resword}"/>:</td>
		<td class="table_cell">
		 <c:choose>
		 <c:when test="${subjectToUpdate.gender == 109}">
          <fmt:message key="male" bundle="${resword}"/>
         </c:when>
         <c:when test="${subjectToUpdate.gender == 102}">
          <fmt:message key="female" bundle="${resword}"/>
         </c:when>
         <c:otherwise>
         <fmt:message key="not_specified" bundle="${resword}"/>
        </c:otherwise>
        </c:choose>
		</td>
	</tr>

	<c:choose>
	<c:when test="${subjectToUpdate.dobCollected}">
	<tr valign="top">
		<td class="table_header_column"><fmt:message key="date_of_birth" bundle="${resword}"/>:</td>
	  	<td class="table_cell">
		  <fmt:formatDate value="${subjectToUpdate.dateOfBirth}" pattern="${dteFormat}"/>
	  	</td>
	</tr>
    </c:when>
    <c:otherwise>
      <tr valign="top">
		<td class="table_header_column"><fmt:message key="year_of_birth" bundle="${resword}"/>:</td>
	  	<td class="table_cell">
		  <c:out value="${yearOfBirth}"/>
	  	</td>
	</tr>
    </c:otherwise>
    </c:choose>
</table>
</div>
</div></div></div></div></div></div></div></div>

</div>
 <input type="submit" name="Submit" value="<fmt:message key="submit" bundle="${resword}"/>" class="button_medium">
 <input type="button" onclick="confirmCancel('ListSubject');"  name="cancel" value="   <fmt:message key="cancel" bundle="${resword}"/>   " class="button_medium"/>
</form>
<jsp:include page="../include/footer.jsp"/>
