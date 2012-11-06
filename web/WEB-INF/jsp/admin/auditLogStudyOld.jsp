<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<fmt:setBundle basename="com.liteoc.i18n.words" var="resword"/>

<jsp:include page="../include/managestudy-header.jsp"/>


<jsp:include page="../include/sidebar.jsp"/>

<jsp:useBean scope='session' id='userBean' class='com.liteoc.bean.login.UserAccountBean'/>
<jsp:useBean scope='session' id='study' class='com.liteoc.bean.managestudy.StudyBean' />

<jsp:useBean scope='request' id='table' class='com.liteoc.web.bean.EntityBeanTable'/>

<h1><span class="title_manage">
<fmt:message key="view_study_log_for" bundle="${resword}">
	<fmt:param value="${study.name}"/>
</fmt:message> 
</span></h1>

<jsp:include page="../include/alertbox.jsp" />
<c:import url="../include/showTable.jsp"><c:param name="rowURL" value="showAuditEventStudyRow.jsp" /></c:import>

<jsp:include page="../include/footer.jsp"/>
