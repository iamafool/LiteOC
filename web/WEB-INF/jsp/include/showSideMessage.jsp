<%@ page contentType="text/html; charset=UTF-8" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<fmt:setBundle basename="com.liteoc.i18n.page_messages" var="resmessages"/>

<jsp:useBean scope='request' id='pageMessages' class='java.util.ArrayList'/>

<c:if test="${!empty pageMessages}">
<div class="alert">    
<c:forEach var="message" items="${pageMessages}">
 <c:out value="${message}" escapeXml="false"/> 
</c:forEach>
</div>
</c:if>

<c:if test="${param.message == 'authentication_failed'}">
<div class="alert">
    <fmt:message key="no_have_correct_privilege_current_study" bundle="${resmessages}"/>
    <fmt:message key="change_study_contact_sysadmin" bundle="${resmessages}"/>
 <br/><br/>
</div>
</c:if>

