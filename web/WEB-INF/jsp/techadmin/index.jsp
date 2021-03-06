<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<fmt:setBundle basename="com.liteoc.i18n.words" var="resword"/>
<fmt:setBundle basename="com.liteoc.i18n.notes" var="restext"/>

<jsp:include page="../include/tech-admin-header.jsp"/>

<%-- move the alert message to the sidebar --%>

<jsp:include page="../include/sideInfo.jsp"/>


<h1><span class="title_manage">
<fmt:message key="technical_administrator" bundle="${resword}"/>
</span></h1>


<p><fmt:message key="As_a_Technical_Administrator_you_have_privileges_to" bundle="${restext}"/></p>


<jsp:include page="../include/footer.jsp"/>
