<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<fmt:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>
<fmt:setBundle basename="org.akaza.openclinica.i18n.workflow" var="resworkflow"/>
<fmt:setBundle basename="org.akaza.openclinica.i18n.notes" var="restext"/>

<jsp:include page="../include/extract-header.jsp"/>


<%-- move the alert message to the sidebar --%>


<jsp:include page="../include/sideInfo.jsp"/>



<jsp:useBean scope='session' id='userBean' class='org.akaza.openclinica.bean.login.UserAccountBean'/>
<jsp:useBean scope="request" id="datasets" class="java.util.ArrayList"/>

<h1><span class="title_manage"><c:out value="${study.name}" />: <fmt:message key="extract_datasets" bundle="${resworkflow}"/> <a href="javascript:openDocWindow('help/4_0_extractData_Help.html')"><img src="images/bt_Help_Manage.gif" border="0" alt="<fmt:message key="help" bundle="${resword}"/>" title="<fmt:message key="help" bundle="${resword}"/>"></a></h1>

<OL>
<LI><a href="ViewDatasets"><fmt:message key="view_datasets" bundle="${resworkflow}"/></a>
<LI><a href="CreateDataset"><fmt:message key="create_dataset" bundle="${resword}"/></a>
<!--<LI><a href="CreateFiltersOne">View Filters</a>-->
<!--<LI><a href="CreateFiltersOne?action=begin&submit=Create+New+Filter">Create Filter</a>-->
</OL>


<c:import url="../include/showTable.jsp">
<c:param name="rowURL" value="showDatasetRow.jsp" />
</c:import>

<jsp:include page="../include/footer.jsp"/>
