<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<fmt:setBundle basename="com.liteoc.i18n.notes" var="restext"/>
<fmt:setBundle basename="com.liteoc.i18n.words" var="resword"/>
<fmt:setBundle basename="com.liteoc.i18n.workflow" var="resworkflow"/>


<jsp:include page="../include/submit-header.jsp"/>


<%-- move the alert message to the sidebar --%>

<jsp:include page="../include/sideInfo.jsp"/>

	<h1><span class="title_manage"><fmt:message key="add_subject" bundle="${resword}"/>- <fmt:message key="instructions" bundle="${restext}"/> <a href="javascript:openDocWindow('help/2_2_enrollSubject_Help.html')"><img src="images/bt_Help_Manage.gif" border="0" alt="H<fmt:message key="help" bundle="${restext}"/>" title="<fmt:message key="help" bundle="${restext}"/>"></a></span></h1>



	<br/>


	<fmt:message key="to_add_subject_enter_ID" bundle="${restext}"/>
	<br/><fmt:message key="should_be_able_to_add_subject" bundle="${restext}"/>

	<br/> <fmt:message key="can_then_add_study_events_going" bundle="${restext}"/>

	<ol>
	<li> <fmt:message key="study_event_overview" bundle="${resworkflow}"/>
	<li> <fmt:message key="event_CRF_data_submission" bundle="${resworkflow}"/>
	<li> <fmt:message key="data_entry" bundle="${resworkflow}"/>
	<li> <fmt:message key="mark_event_CRF_complete" bundle="${resworkflow}"/>
	</ol>

<form method="POST" action="AddNewSubject">
	<input type="submit" value="<fmt:message key="proceed_to_add_subject" bundle="${resword}"/>" class="button_long">
</form>
<br/>

<br/>

<jsp:include page="../include/footer.jsp"/>
