<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<fmt:setBundle basename="com.liteoc.i18n.notes" var="restext"/>
<fmt:setBundle basename="com.liteoc.i18n.words" var="resword"/>
<fmt:setBundle basename="com.liteoc.i18n.format" var="resformat"/>


<jsp:include page="../include/admin-header.jsp"/>


<%-- move the alert message to the sidebar --%>

<jsp:include page="../include/sideInfo.jsp"/>

<jsp:useBean scope='session' id='userBean' class='com.liteoc.bean.login.UserAccountBean'/>
<jsp:useBean scope='request' id='table' class='com.liteoc.web.bean.EntityBeanTable'/>
<jsp:useBean scope='request' id='message' class='java.lang.String'/>

<h1><span class="title_manage"><fmt:message key="administer_all_jobs" bundle="${resword}"/></span></h1>

<div class="homebox_bullets"><a href="ViewJob"><fmt:message key="view_all_export_data_jobs" bundle="${resword}"/></a></div>
<div class="homebox_bullets"><a href="ViewImportJob"><fmt:message key="view_all_import_data_jobs" bundle="${resword}"/></a></div>
<div  class="homebox_bullets"> <a title="View all Jobs" href='pages/listCurrentScheduledJobs' onclick="javascript:HighlightTab(1);"><fmt:message key="view_currently_executing_data_export_jobs" bundle="${resword}"/></a></div>
<p></p>
<c:set var="dtetmeFormat"><fmt:message key="date_time_format_string" bundle="${resformat}"/></c:set>
<jsp:useBean id="now" class="java.util.Date" />
<P><I><fmt:message key="note_that_job_is_set" bundle="${resword}"/> <fmt:formatDate value="${now}" pattern="${dtetmeFormat}"/>.</I></P>

 <jsp:include page="../include/footer.jsp"/>
