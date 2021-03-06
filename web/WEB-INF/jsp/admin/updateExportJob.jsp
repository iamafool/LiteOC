<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<fmt:setBundle basename="com.liteoc.i18n.notes" var="restext"/>
<fmt:setBundle basename="com.liteoc.i18n.words" var="resword"/>
<fmt:setBundle basename="com.liteoc.i18n.format" var="resformat"/>
<fmt:setBundle basename="com.liteoc.i18n.workflow" var="resworkflow"/>

<jsp:include page="../include/admin-header.jsp"/>


<%-- move the alert message to the sidebar --%>


<jsp:include page="../include/sideInfo.jsp"/>

<jsp:useBean scope='session' id='userBean' class='com.liteoc.bean.login.UserAccountBean'/>
<jsp:useBean scope='request' id='jobName' class='java.lang.String'/>
<jsp:useBean scope='request' id='jobDesc' class='java.lang.String'/>

<jsp:useBean scope='request' id='periodToRun' class='java.lang.String'/>
<jsp:useBean scope='request' id='tab' class='java.lang.String'/>
<jsp:useBean scope='request' id='cdisc' class='java.lang.String'/>
<jsp:useBean scope='request' id='cdisc12' class='java.lang.String'/>
<jsp:useBean scope='request' id='cdisc13' class='java.lang.String'/>
<jsp:useBean scope='request' id='cdisc13oc' class='java.lang.String'/>
<jsp:useBean scope='request' id='spss' class='java.lang.String'/>
<jsp:useBean scope='request' id='contactEmail' class='java.lang.String'/>

<h1><span class="title_manage"><fmt:message key="update_scheduled_job" bundle="${resword}"/>: <fmt:message key="export_dataset" bundle="${resword}"/></span></h1>
<p>
<c:set var="dtetmeFormat"><fmt:message key="date_time_format_string" bundle="${resformat}"/></c:set>
<jsp:useBean id="now" class="java.util.Date" />
<P><I><fmt:message key="note_that_job_is_set" bundle="${resword}"/> <fmt:formatDate value="${now}" pattern="${dtetmeFormat}"/>.</I></P>
<form action="UpdateJobExport" method="post">
<input type="hidden" name="action" value="confirmall" />
<input type="hidden" name="tname" value="<c:out value="${jobName}"/>" />

<table>
<tr>
		<td class="text"><b><fmt:message key="job_name" bundle="${resword}"/>:</b><br/><jsp:include page="../showMessage.jsp"><jsp:param name="key" value="jobName"/></jsp:include></td>
		<td class="text">
			<input type="text" name="jobName" size="30" value="<c:out value="${jobName}"/>"/>
		</td>
	</tr>
	<tr>
		<td class="text"><b><fmt:message key="description" bundle="${resword}"/>:</b><br/><jsp:include page="../showMessage.jsp"><jsp:param name="key" value="jobDesc"/></jsp:include></td>
		<td class="text"><input type="text" name="jobDesc" size="60" value="<c:out value="${jobDesc}"/>"/>
		</td>
	</tr>
	<tr>
		<td class="text"><b><fmt:message key="please_pick_a_dataset_to_export" bundle="${resword}"/>:</b></td>
		<td class="text"><select name="dsId">
			<c:forEach var="dataset" items="${datasets}">
				<option value="<c:out value="${dataset.id}"/>"
					<c:if test="${dsId == dataset.id}">
						selected
					</c:if>
				><c:out value="${dataset.name}" /></option>
			</c:forEach>
		</select></td>
	</tr>
	<tr>
		<td class="text"><b><fmt:message key="period_to_run" bundle="${resword}"/>:</b>
		&nbsp; <jsp:include page="../showMessage.jsp"><jsp:param name="key" value="periodToRun"/></jsp:include></td>
		<td class="text">
		<table border="0" cellpadding="0" cellspacing="0">
		<tr>
		<td class="text"><fmt:message key="daily" bundle="${resword}"/></td>
		<td class="text"><input type="radio" name="periodToRun" value="daily"
			<c:if test="${periodToRun == 'daily'}">
				checked
			</c:if>
			/></td>
		</tr>
		<tr>
			<td class="text"><fmt:message key="weekly" bundle="${resword}"/></td>
			<td class="text"><input type="radio" name="periodToRun" value="weekly"
			<c:if test="${periodToRun == 'weekly'}">
				checked
			</c:if>
			/></td>
		</tr>
		<tr>
			<td class="text"><fmt:message key="monthly" bundle="${resword}"/></td>
			<td class="text"><input type="radio" name="periodToRun" value="monthly"
			<c:if test="${periodToRun == 'monthly'}">
				checked
			</c:if>
			/>
		</td>
		</tr>
		</table>
		</td>
	</tr>


	<tr>
		<td class="text"><b><fmt:message key="start_date_time" bundle="${resword}"/>:</b></td>
		<td class="text">
			<table border="0" cellpadding="0" cellspacing="0">
			<tr>
				<jsp:include page="../showMessage.jsp"><jsp:param name="key" value="jobDate"/></jsp:include><br/>
				<c:import url="../include/showDateTimeInput.jsp"><c:param name="prefix" value="job"/><c:param name="count" value="1"/></c:import>
				<td>(<fmt:message key="date_time_format" bundle="${resformat}"/>) </td>
			</tr>
			<%-- <tr>
				<td colspan="7">
				<jsp:include page="../showMessage.jsp"><jsp:param name="key" value="jobDate"/></jsp:include>
				</td>
			</tr> --%>
			</table>
		</td>
	</tr>

	<tr>
		<td class="text"><b><fmt:message key="file_formats" bundle="${resword}"/>:</b>
		&nbsp;<jsp:include page="../showMessage.jsp"><jsp:param name="key" value="tab"/></jsp:include>
		</td>
		<td class="text">
			<table border="0" cellpadding="0" cellspacing="0">
            <c:forEach var="extract" items="${extractProperties}">
            <tr>
                <td class="text">
                    <c:choose>
                        <c:when test="${fn:startsWith(extract.filedescription, '&')==true}">
                            <fmt:message key="${fn:substringAfter(extract.filedescription, '&')}" bundle="${restext}"/>&nbsp;
                        </c:when>
                        <c:otherwise>
                            <c:out value="${extract.filedescription}"/>&nbsp;
                        </c:otherwise>
                    </c:choose>
                </td>
                <td class="text"><input type="radio" name="formatId" value="<c:out value="${extract.id}"/>"
                    <c:if test="${formatId == extract.id}">
						checked
					</c:if>
				/></td>
			</tr>
            </c:forEach>    
			</table>
		</td>
	</tr>



	<tr>
		<td class="text"><b><fmt:message key="contact_email" bundle="${resword}"/>:</b><br/>
		<jsp:include page="../showMessage.jsp"><jsp:param name="key" value="contactEmail"/></jsp:include></td>
		<td class="text"><input type="text" name="contactEmail" size="60" value="<c:out value="${contactEmail}"/>"/>
		</td>
	</tr>
	<tr>

	</tr>

	<tr>
		<td align="left">
		  <input type="submit" name="btnSubmit" value="<fmt:message key="confirm_and_save" bundle="${resword}"/>" class="button_xlong"/>
		</td>
		</form>
		<form action='ViewJob' method="POST">
		<td>
			<input type="submit" name="submit" value="<fmt:message key="cancel" bundle="${resword}"/>" class="button">
		</td>
		</form>
	</tr>

</table>



<jsp:include page="../include/footer.jsp"/>
