<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<fmt:setBundle basename="com.liteoc.i18n.notes" var="restext"/>
<fmt:setBundle basename="com.liteoc.i18n.words" var="resword"/>
<fmt:setBundle basename="com.liteoc.i18n.workflow" var="resworkflow"/>
<fmt:setBundle basename="com.liteoc.i18n.format" var="resformat"/>

<c:choose>
<c:when test="${userBean.sysAdmin && module=='admin'}">
 <c:import url="../include/admin-header.jsp"/>
</c:when>
<c:otherwise>
 <c:import url="../include/managestudy-header.jsp"/>
</c:otherwise>
</c:choose>


<%-- move the alert message to the sidebar --%>

<jsp:include page="../include/sideInfo.jsp"/>

<jsp:useBean scope='session' id='userBean' class='com.liteoc.bean.login.UserAccountBean'/>
<jsp:useBean scope='session' id='userRole' class='com.liteoc.bean.login.StudyUserRoleBean' />
<jsp:useBean scope='request' id='table' class='com.liteoc.web.bean.EntityBeanTable'/>
<c:choose>
<c:when test="${userBean.sysAdmin && module=='admin'}">
	<h1><span class="title_manage"><fmt:message key="administer_CRFs2" bundle="${resworkflow}"/>
</span></h1>
</c:when>
<c:otherwise>
	<h1><span class="title_manage"><fmt:message key="manage_CRFs2" bundle="${resworkflow}"/>
</span></h1>
</c:otherwise>
</c:choose>

<c:import url="../include/showTable.jsp"><c:param name="rowURL" value="showCRFRow.jsp" /></c:import>


<jsp:include page="../include/footer.jsp"/>
