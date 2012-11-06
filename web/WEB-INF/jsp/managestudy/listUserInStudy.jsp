<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<fmt:setBundle basename="com.liteoc.i18n.words" var="resword"/>
<fmt:setBundle basename="com.liteoc.i18n.notes" var="restext"/>

<jsp:include page="../include/managestudy-header.jsp"/>


<%-- move the alert message to the sidebar --%>


<jsp:include page="../include/sideInfo.jsp"/>

<jsp:useBean scope='session' id='study' class='com.liteoc.bean.managestudy.StudyBean'/>

<h1><span class="title_manage"><fmt:message key="manage_all_users_in" bundle="${restext}"/> <c:out value="${study.name}"/> <a href="javascript:openDocWindow('https://docs.openclinica.com/3.1/manage-users/manage-user-roles')"><img src="images/bt_Help_Manage.gif" border="0" alt="<fmt:message key="help" bundle="${resword}"/>" title="<fmt:message key="help" bundle="${resword}"/>"></a></span></h1>

<%-- 
<div class="homebox_bullets"><a href="AssignUserToStudy"><fmt:message key="assign_new_user_to_current_study" bundle="${restext}"/></a></div>
--%>
<p>
<c:import url="../include/showTable.jsp"><c:param name="rowURL" value="showUserInStudyRow.jsp" /></c:import>
<p>
<c:if test="${study.parentStudyId == 0}"><%-- is not a site --%>
	<div class="homebox_bullets"><a href="pages/studymodule"><fmt:message key="go_back_build_study_page" bundle="${resword}"/></a></div>
</c:if>
<jsp:include page="../include/footer.jsp"/>
