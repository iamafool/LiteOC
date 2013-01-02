<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<fmt:setBundle basename="com.liteoc.i18n.notes" var="restext"/>
<fmt:setBundle basename="com.liteoc.i18n.words" var="resword"/>
<jsp:include page="../include/admin-header.jsp"/>


<%-- move the alert message to the sidebar --%>

<jsp:include page="../include/sideInfo.jsp"/>

<jsp:useBean scope='session' id='userBean' class='com.liteoc.bean.login.UserAccountBean'/>
<jsp:useBean scope='request' id='table' class='com.liteoc.web.bean.EntityBeanTable'/>
<jsp:useBean scope='request' id='message' class='java.lang.String'/>

<h1><span class="title_manage"><fmt:message key="administer_users" bundle="${resword}"/></span></h1>

<div class="homebox_bullets"><a href="CreateUserAccount"><fmt:message key="create_a_new_user" bundle="${resword}"/></a></div><br/>
<div class="homebox_bullets"><a href="AuditUserActivity?restore=true"><fmt:message key="audit_user_activity" bundle="${resword}"/></a></div><br/>
<div class="homebox_bullets"><a href="Configure"><fmt:message key="lock_out_configuration" bundle="${resword}"/></a></div>
<p></p>

<c:import url="../include/showTable.jsp">
	<c:param name="rowURL" value="showUserAccountRow.jsp" />
</c:import>

<jsp:include page="../include/footer.jsp"/>
