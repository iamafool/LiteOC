<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<fmt:setBundle basename="com.liteoc.i18n.notes" var="restext"/>
<fmt:setBundle basename="com.liteoc.i18n.words" var="resword"/>

<jsp:include page="../include/extract-header.jsp"/>


<%-- move the alert message to the sidebar --%>


<jsp:include page="../include/createDatasetSideInfo.jsp"/>

<jsp:useBean scope='session' id='userBean' class='com.liteoc.bean.login.UserAccountBean'/>
<jsp:useBean scope='request' id='months' class='java.util.ArrayList' />
<jsp:useBean scope='request' id='years' class='java.util.ArrayList' />
<jsp:useBean scope="request" id="presetValues" class="java.util.HashMap" />

<c:set var="firstMonth" value="${0}" />
<c:set var="firstYear" value="${0}" />
<c:set var="lastMonth" value="${0}" />
<c:set var="lastYear" value="${0}" />

<c:forEach var="presetValue" items="${presetValues}">
	<c:if test='${presetValue.key == "firstmonth"}'>
		<c:set var="firstMonth" value="${presetValue.value}" />
	</c:if>
	<c:if test='${presetValue.key == "firstyear"}'>
		<c:set var="firstYear" value="${presetValue.value}" />
	</c:if>
	<c:if test='${presetValue.key == "lastmonth"}'>
		<c:set var="lastMonth" value="${presetValue.value}" />
	</c:if>
	<c:if test='${presetValue.key == "lastyear"}'>
		<c:set var="lastYear" value="${presetValue.value}" />
	</c:if>
</c:forEach>

<c:choose>
<c:when test="${newDataset.id>0}">
<h1><span class="title_manage"><fmt:message key="edit_dataset" bundle="${resword}"/> - <fmt:message key="define_temporal_scope" bundle="${resword}"/>
: <c:out value="${newDataset.name}"/></span></h1>
</c:when>
<c:otherwise>
<h1><span class="title_manage"><fmt:message key="create_dataset" bundle="${resword}"/>: <fmt:message key="define_temporal_scope" bundle="${resword}"/></span></h1>
</c:otherwise>
</c:choose>

<p><fmt:message key="filter_data_selecting_range" bundle="${restext}"/></p>

<p><fmt:message key="select_event_start_end_however" bundle="${restext}"/></p>

<p><fmt:message key="not_filter_study_by_enrollment_leave_blank" bundle="${restext}"/></p>

<form action="CreateDataset" method="post">
<input type="hidden" name="action" value="scopesubmit"/>

<%--<jsp:include page="../showMessage.jsp"><jsp:param name="key" value="firstmonth"/></jsp:include>--%>

<table>
	<tr>
		<td class="text"><fmt:message key="beginning_date" bundle="${resword}"/>:</td>
		<td class="text" valign="top">
			<select name="firstmonth">
				<option value="0">-<fmt:message key="select" bundle="${resword}"/>-</option>
			<c:set var="monthNum" value="${1}" />
			<c:forEach var="month" items="${months}">
				<c:choose>
					<c:when test="${firstMonth == monthNum}">
						<option value='<c:out value="${monthNum}" />' selected><c:out value="${month}" /></option>
					</c:when>
					<c:otherwise>
						<option value='<c:out value="${monthNum}" />'><c:out value="${month}" /></option>
					</c:otherwise>
				</c:choose>
				<c:set var="monthNum" value="${monthNum + 1}" />
			</c:forEach>
			</select>
			<br/><jsp:include page="../showMessage.jsp"><jsp:param name="key" value="firstmonth"/></jsp:include>
		</td>
		<td class="text" valign="top">
			<select name="firstyear">
				<option value="1900">-<fmt:message key="select" bundle="${resword}"/>-</option>
			<c:set var="yearNum" value="${1980}" />
			<c:forEach var="year" items="${years}">
				<c:choose>
					<c:when test="${firstYear == yearNum}">
						<option value='<c:out value="${year}" />' selected><c:out value="${year}" /></option>
					</c:when>
					<c:otherwise>
						<option value='<c:out value="${year}" />'><c:out value="${year}" /></option>
					</c:otherwise>
				</c:choose>
				<c:set var="yearNum" value="${yearNum + 1}" />
			</c:forEach>
			</select>
		</td>
	</tr>
	<tr>
		<td class="text" align="right"><fmt:message key="end_date" bundle="${resword}"/>:</td>
		<td class="text" valign="top">
			<select name="lastmonth">
				<option value="0">-<fmt:message key="select" bundle="${resword}"/>-</option>
			<c:set var="monthNum" value="${1}" />
			<c:forEach var="month" items="${months}">
				<c:choose>
					<c:when test="${lastMonth == monthNum}">
						<option value='<c:out value="${monthNum}" />' selected><c:out value="${month}" /></option>
					</c:when>
					<c:otherwise>
						<option value='<c:out value="${monthNum}" />'><c:out value="${month}" /></option>
					</c:otherwise>
				</c:choose>
				<c:set var="monthNum" value="${monthNum + 1}" />
			</c:forEach>
			</select>
			<br/><jsp:include page="../showMessage.jsp"><jsp:param name="key" value="lastmonth"/></jsp:include>
		</td>
		<td class="text" valign="top">
			<select name="lastyear">
				<option value="2100">-<fmt:message key="select" bundle="${resword}"/>-</option>
			<c:set var="yearNum" value="${1980}" />
			<c:forEach var="year" items="${years}">
				<c:choose>
					<c:when test="${lastYear == yearNum}">
						<option value='<c:out value="${year}" />' selected><c:out value="${year}" /></option>
					</c:when>
					<c:otherwise>
						<option value='<c:out value="${year}" />'><c:out value="${year}" /></option>
					</c:otherwise>
				</c:choose>
				<c:set var="yearNum" value="${yearNum + 1}" />
			</c:forEach>
			</select>
		</td>
	</tr>

</table>
<p><fmt:message key="save_export_dataset_or_apply_filter" bundle="${restext}"/></p>
<table>
  <tr>
		<td>
			<input type="submit" name="submit" value='<fmt:message key="continue_to_apply_filter" bundle="${resword}"/>' class="button_xlong"/>
			<input type="submit" name="submit" value='<fmt:message key="skip_apply_filter_and_save" bundle="${resword}"/>' class="button_xlong"/>
			<input type="submit" name="submit" value='<fmt:message key="continue" bundle="${resword}"/>' class="button_xlong"/>
            <input type="button" onclick="confirmCancel('ViewDatasets');"  name="cancel" value='<fmt:message key="cancel" bundle="${resword}"/>'  class="button_medium"/>


        </td>
	</tr>
</table>
</form>

<jsp:include page="../include/footer.jsp"/>
