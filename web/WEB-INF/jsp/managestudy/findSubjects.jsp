<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<fmt:setBundle basename="com.liteoc.i18n.words" var="resword"/>
<fmt:setBundle basename="com.liteoc.i18n.notes" var="restext"/>


<jsp:include page="../include/submit-header.jsp"/>
<%-- move the alert message to the sidebar --%>


<link rel="stylesheet" href="includes/jmesa/jmesa.css" type="text/css">

<script type="text/JavaScript" language="JavaScript" src="includes/jmesa/jquery-1.3.2.min.js"></script>
<script type="text/JavaScript" language="JavaScript" src="includes/jmesa/jquery.jmesa.js"></script>
<script type="text/JavaScript" language="JavaScript" src="includes/jmesa/jmesa.js"></script>
<script type="text/javascript" language="JavaScript" src="includes/jmesa/jquery.blockUI.js"></script>



<script type="text/javascript">
    function onInvokeAction(id,action) {
        if(id.indexOf('findSubjects') == -1)  {
        setExportToLimit(id, '');
        }
        createHiddenInputFieldsForLimitAndSubmit(id);
    }
    function onInvokeExportAction(id) {
        var parameterString = createParameterStringForLimit(id);
        location.href = '${pageContext.request.contextPath}/ListStudySubjects?'+ parameterString;
    }
</script>

<jsp:include page="../include/sideInfo.jsp"/>

<jsp:useBean scope='session' id='userBean' class='com.liteoc.bean.login.UserAccountBean'/>
<jsp:useBean scope='request' id='crf' class='com.liteoc.bean.admin.CRFBean'/>


<h1><span class="title_manage">
<fmt:message key="view_subjects_in" bundle="${restext}"/>
</span></h1>

<div id="findSubjectsDiv">
    <form  action="${pageContext.request.contextPath}/ListStudySubjects" method="post">
        <input type="hidden" name="module" value="admin">
        ${findSubjectsHtml}
    </form>
</div>


<br/>
<jsp:include page="../include/footer.jsp"/>

