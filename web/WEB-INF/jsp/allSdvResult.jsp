<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<fmt:setBundle basename="com.liteoc.i18n.words" var="resword"/>
<fmt:setBundle basename="com.liteoc.i18n.format" var="resformat"/>
<fmt:setBundle basename="com.liteoc.i18n.notes" var="restext"/>

<jsp:include page="include/managestudy_top_pages.jsp"/>


<%-- move the alert message to the sidebar --%>


<div id="sdvResult">
    allParams: ${allParams}    <br />
    verified: ${verified} 
</div>

<jsp:include page="include/footer.jsp"/>