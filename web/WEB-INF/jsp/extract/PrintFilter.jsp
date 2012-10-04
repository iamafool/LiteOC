<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql"%>
<fmt:setBundle basename="org.akaza.openclinica.i18n.format" var="resformat"/>
<fmt:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>

<c:set var="dteFormat"><fmt:message key="date_format_string" bundle="${resformat}"/></c:set>

<jsp:include page="../include/extract-header.jsp"/>


<jsp:include page="../include/sidebar.jsp"/>
<jsp:useBean scope='session' id='userBean' class='org.akaza.openclinica.bean.login.UserAccountBean'/>
<jsp:useBean scope="request" id="idb" class="org.akaza.openclinica.bean.submit.ItemDataBean"/>
<jsp:useBean scope="request" id="fdb" class="com.yanxiaoguang.bean.FilterDataBean"/>

<h1><span class="title_manage"><fmt:message key="view_filter_details" bundle="${resword}"/>: <c:out value="${filter.name}"/></span></h1>

<P><jsp:include page="../showInfo.jsp"/></P>   

<table border=1>
<tr>
<td><fmt:message key="subject_ID" bundle="${resword}"/></td>
<td><fmt:message key="study_event" bundle="${resword}"/></td>
<td><fmt:message key="value" bundle="${resword}"/></td>
</tr>
<c:forEach var="itemidb" items="${filteridb}">
	<tr>
	<td><c:out value="${itemidb.subjectId}" /></td>
	<td><c:out value="${itemidb.studyEventName}" /></td>
	<td><c:out value="${itemidb.value}" /></td>
	</tr>
</c:forEach>
</table>


  


<%--<c:import url="../include/showTable.jsp"><c:param name="rowURL" value="showFilterRow.jsp" /></c:import> --%>


<jsp:include page="../include/footer.jsp"/>

