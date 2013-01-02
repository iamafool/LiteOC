<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<fmt:setBundle basename="com.liteoc.i18n.workflow" var="resworkflow"/>
<fmt:setBundle basename="com.liteoc.i18n.notes" var="restext"/>
<fmt:setBundle basename="com.liteoc.i18n.words" var="resword"/>

<jsp:include page="../include/extract-header.jsp"/>


<%-- move the alert message to the sidebar --%>


<jsp:include page="../include/sideInfo.jsp"/>


<jsp:useBean scope='session' id='userBean' class='com.liteoc.bean.login.UserAccountBean'/>
<jsp:useBean scope="request" id="datasets" class="java.util.ArrayList"/>


<h1><span class="title_manage"><c:out value="${study.name}" />: <fmt:message key="view_dataset" bundle="${resworkflow}"/></span></h1>


<%--<p><center><a href="ViewDatasets?action=owner&ownerId=<c:out value="${userBean.id}"/>">Show Only My Datasets</a> |
<a href="ViewDatasets">Show All Datasets</a></center></p>
--%>

<c:import url="../include/showTable.jsp"><c:param name="rowURL" value="showDatasetRow.jsp" /></c:import>

<jsp:include page="../include/footer.jsp"/>
