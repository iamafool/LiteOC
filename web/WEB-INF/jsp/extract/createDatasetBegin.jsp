<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<fmt:setBundle basename="com.liteoc.i18n.notes" var="restext"/>
<fmt:setBundle basename="com.liteoc.i18n.words" var="resword"/>


<jsp:include page="../include/extract-header.jsp"/>

<jsp:include page="../include/sideInfo.jsp"/>


<jsp:useBean scope='session' id='userBean' class='com.liteoc.bean.login.UserAccountBean'/>


<h1>
	<span class="title_manage"> <c:out value="${study.name}" />: <fmt:message key="create_dataset" bundle="${resword}" /> </span>
</h1>

<p><fmt:message key="steps_to_extract_or_filter_dataset" bundle="${restext}"/>
<ol>
<li><fmt:message key="select_events_CRF_items_apply_dataset" bundle="${restext}"/>
<li><fmt:message key="specify_study_longitudinal_scope" bundle="${restext}"/>
<li><fmt:message key="specify_filtering_choosing_filters" bundle="${restext}"/>
<li><fmt:message key="specify_metadata_for_the_dataset" bundle="${restext}"/>
<li><fmt:message key="save_and_export_desired_format" bundle="${restext}"/>
</ol></p>
<form action="CreateDataset" method="post">
<input type="hidden" name="action" value="begin"/>
<input type="submit" name="Submit" value="<fmt:message key="proceed_to_create_a_dataset" bundle="${restext}"/>" class="button_xlong"/>
<input type="button" onclick="confirmCancel('ViewDatasets');"  name="cancel" value="   <fmt:message key="cancel" bundle="${resword}"/>   " class="button_medium"/>
</form>

<jsp:include page="../include/footer.jsp"/>
