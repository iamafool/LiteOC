<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<fmt:setBundle basename="com.liteoc.i18n.words" var="resword"/>
<fmt:setBundle basename="com.liteoc.i18n.format" var="resformat"/>
<fmt:setBundle basename="com.liteoc.i18n.notes" var="restext"/>
<fmt:setBundle basename="com.liteoc.i18n.page_messages" var="resmessages"/>


<jsp:include page="include/managestudy_top_pages.jsp"/>

<%-- move the alert message to the sidebar --%>

<jsp:include page="include/sideInfo.jsp"/>
<link rel="stylesheet" href="../includes/jmesa/jmesa.css" type="text/css">
<script type="text/JavaScript" language="JavaScript" src="../includes/jmesa/jquery-1.3.2.min.js"></script>
<script type="text/JavaScript" language="JavaScript" src="../includes/jmesa/jmesa.js"></script>
<script type="text/JavaScript" language="JavaScript" src="../includes/jmesa/jquery.jmesa.js"></script>
<script type="text/javascript">
    function onInvokeAction(id,action) {
        setExportToLimit(id, '');
        createHiddenInputFieldsForLimitAndSubmit(id);
    }
    function onInvokeExportAction(id) {
        var parameterString = createParameterStringForLimit(id);
        //location.href = '${pageContext.request.contextPath}/ViewCRF?module=manage&crfId=' + '${crf.id}&' + parameterString;
    }
</script>

<h1><span class="title_manage">
<fmt:message key="currently_executing_data_export_jobs" bundle="${resword}"/>
</span></h1>


<script type="text/javascript">
    function prompt(formObj,theStudySubjectId){
        var bool = confirm(
                "<fmt:message key="uncheck_sdv" bundle="${resmessages}"/>");
        if(bool){
            formObj.action='${pageContext.request.contextPath}/pages/unSdvStudySubject';
            formObj.theStudySubjectId.value=theStudySubjectId;
            formObj.submit();
        }
    }
</script>
<div id="subjectSDV">
    <form name='scheduledJobsForm' action="${pageContext.request.contextPath}/pages/listCurrentScheduledJobs">
        <%--<fmt:message key="select_all_on_page" bundle="${resword}"/> <input type=checkbox name='checkSDVAll' onclick='selectAllChecks(this.form)'/>
        <br />--%>
        <input type="hidden" name="studyId" value="${param.studyId}">
        
        <%--This value will be set by an onclick handler associated with an SDV button --%>
        <input type="hidden" name="theJobName" value="0">
            <input type="hidden" name="theJobGroupName" value="0">
			<input type="hidden" name="theTriggerGroupName" value="0">
            <input type="hidden" name="theTriggerName" value="0">
        <%-- the destination JSP page after removal or adding SDV for an eventCRF --%>
        <input type="hidden" name="redirection" value="listCurrentScheduledJobs">

  ${scheduledTableAttribute}
        <br />
       
        <%--<input type="submit" name="sdvAllFormCancel" class="button_medium" value="Cancel" onclick="this.form.action='${pageContext.request.contextPath}/pages/viewSubjectAggregate';this.form.submit();"/>
    </form>--%>
    <script type="text/javascript">hideCols('s_sdv',[2,3,4])</script>

</div>
</body>
</html>
