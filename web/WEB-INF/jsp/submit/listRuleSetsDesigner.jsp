<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<fmt:setBundle basename="com.liteoc.i18n.notes" var="restext"/>
<fmt:setBundle basename="com.liteoc.i18n.words" var="resword"/>
<fmt:setBundle basename="com.liteoc.i18n.workflow" var="resworkflow"/>
<fmt:setBundle basename="com.liteoc.i18n.format" var="resformat"/>

<link type="text/css" href="includes/jmesa/jmesa.css"  rel="stylesheet"> 
<link rel="stylesheet" href="includes/styles.css" type="text/css">

<script type="text/JavaScript" language="JavaScript" src="includes/global_functions_javascript.js"></script>
<script type="text/JavaScript" language="JavaScript" src="includes/global_functions_javascript2.js"></script>
<script type="text/JavaScript" language="JavaScript" src="includes/Tabs.js"></script>
<script type="text/JavaScript" language="JavaScript" src="includes/CalendarPopup.js"></script>
<!-- Added for the new Calender -->

<link rel="stylesheet" type="text/css" media="all" href="includes/new_cal/skins/aqua/theme.css" title="Aqua" />
<script type="text/javascript" src="includes/new_cal/calendar.js"></script>
<script type="text/javascript" src="includes/new_cal/lang/<fmt:message key="jscalendar_language_file" bundle="${resformat}"/>"></script>
<script type="text/javascript" src="includes/new_cal/calendar-setup.js"></script>   

<script type="text/javascript" language="JavaScript" src="includes/jmesa/jquery-1.3.2.min.js"></script>
<script type="text/JavaScript" language="JavaScript" src="includes/jmesa/jquery.jmesa.js"></script>
<script type="text/JavaScript" language="JavaScript" src="includes/jmesa/jmesa.js"></script>
<script type="text/javascript" language="JavaScript" src="includes/jmesa/jquery.blockUI.js"></script>
<script type="text/javascript" language="JavaScript" src="includes/jmesa/jquery-ui-1.8.2.custom.min.js"></script>



<script type="text/javascript">
    function onInvokeAction(id,action) {
        if(id.indexOf('ruleAssignments') == -1)  {
        setExportToLimit(id, '');
        }
        createHiddenInputFieldsForLimitAndSubmit(id);
    }
    function onInvokeExportAction(id) {
        var parameterString = createParameterStringForLimit(id);
        location.href = '${pageContext.request.contextPath}/ViewRuleAssignment?'+ parameterString;
    }
</script>

<jsp:useBean scope='session' id='userBean' class='com.liteoc.bean.login.UserAccountBean'/>
<jsp:useBean scope='session' id='userRole' class='com.liteoc.bean.login.StudyUserRoleBean' />
<jsp:useBean scope='session' id='study' class='com.liteoc.bean.managestudy.StudyBean'/>
<jsp:useBean scope='request' id='table' class='com.liteoc.web.domain.EntityBeanTable'/>
<c:choose>
<c:when test="${userBean.sysAdmin && module=='admin'}">
 <h1><span class="title_manage"><fmt:message key="rule_manage_rule_assignment" bundle="${resworkflow}"/> <c:out value="${study.name}" />
</c:when>
<c:otherwise>
 <h1><span class="title_manage"><fmt:message key="rule_manage_rule_assignment" bundle="${resworkflow}"/> <c:out value="${study.name}" />
</c:otherwise>
</c:choose>
</span></h1>



<div id="ruleAssignmentsDiv">
    <form  action="${pageContext.request.contextPath}/ViewRuleAssignment">
        <input type="hidden" name="module" value="admin">
        ${ruleAssignmentsHtml}
    </form>
</div>

<script>$j("img[title*='PDF']").attr('title', '<fmt:message key="view_rules_download_xml" bundle="${resword}"/>' );</script>
