<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<fmt:setBundle basename="com.liteoc.i18n.words" var="resword"/>
<fmt:setBundle basename="com.liteoc.i18n.notes" var="restext"/>

<jsp:include page="../include/managestudy-header.jsp"/>


<%-- move the alert message to the sidebar --%>


<jsp:include page="../include/sideInfo.jsp"/>

<jsp:useBean scope='session' id='userBean' class='com.liteoc.bean.login.UserAccountBean'/>
<jsp:useBean scope='request' id='table' class='com.liteoc.web.bean.EntityBeanTable'/>

<h1><span class="title_manage">
<fmt:message key="manage_all_groups_in_study" bundle="${restext}"/>
    <c:out value="${study.name}"/>
    <a href="javascript:openDocWindow('https://docs.openclinica.com/3.1/openclinica-user-guide/monitor-and-manage-data/manage-all-groups')">
        <img src="images/bt_Help_Manage.gif" border="0" alt="<fmt:message key="help" bundle="${restext}"/>" title="<fmt:message key="help" bundle="${restext}"/>"></a>
</span></h1>

<%-- 
<c:if test="${(isParentStudy == true) && (!study.status.locked)}" >
    <div class="homebox_bullets"><a href="CreateSubjectGroupClass"><fmt:message key="create_a_subject_group_class" bundle="${resword}"/></a></div>
</c:if>
--%>

<p></p>
<c:import url="../include/showTable.jsp"><c:param name="rowURL" value="showSubjectGroupClassRow.jsp" /></c:import>
<br/>
<div class="homebox_bullets"><a href="pages/studymodule"><fmt:message key="go_back_build_study_page" bundle="${resword}"/></a></div>

<jsp:include page="../include/footer.jsp"/>
