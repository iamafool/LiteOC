<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<fmt:setBundle basename="com.liteoc.i18n.notes" var="restext"/>
<fmt:setBundle basename="com.liteoc.i18n.words" var="resword"/>
<fmt:setBundle basename="com.liteoc.i18n.workflow" var="resworkflow"/>

<jsp:include page="../include/admin-header.jsp"/>

<link rel="stylesheet" href="includes/jmesa/jmesa.css" type="text/css">
<script type="text/JavaScript" language="JavaScript" src="includes/jmesa/jquery-1.3.2.min.js"></script>
<script type="text/JavaScript" language="JavaScript" src="includes/jmesa/jquery.jmesa.js"></script>
<script type="text/JavaScript" language="JavaScript" src="includes/jmesa/jmesa.js"></script>

<script type="text/javascript">
    function onInvokeAction(id,action) {
        if(id.indexOf('listSubjects') == -1)  {
        setExportToLimit(id, '');
        }
        createHiddenInputFieldsForLimitAndSubmit(id);
    }
    function onInvokeExportAction(id) {
        var parameterString = createParameterStringForLimit(id);
        location.href = '${pageContext.request.contextPath}/ListSubject?'+ parameterString;
    }
</script>


<%-- move the alert message to the sidebar --%>

<jsp:include page="../include/sideInfo.jsp"/>

<jsp:useBean scope='session' id='userBean' class='com.liteoc.bean.login.UserAccountBean'/>
<jsp:useBean scope='session' id='userRole' class='com.liteoc.bean.login.StudyUserRoleBean' />
<jsp:useBean scope='request' id='table' class='com.liteoc.web.bean.EntityBeanTable'/>
<h1><span class="title_manage"><fmt:message key="administer_subjects" bundle="${resworkflow}"/></span></h1>

<form  action="${pageContext.request.contextPath}/ListSubject">
        <input type="hidden" name="module" value="submit">
        ${listSubjectsHtml}
    </form>

<jsp:include page="../include/footer.jsp"/>
