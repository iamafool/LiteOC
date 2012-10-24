<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<fmt:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>
<fmt:setBundle basename="org.akaza.openclinica.i18n.notes" var="restext"/>


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

//     jQuery(document).ready(function() {
//         jQuery('#addSubject').click(function() {
// 			jQuery.blockUI({ message: jQuery('#addSubjectForm'), css:{left: "300px", top:"10px" } });
//         });

//         jQuery('#cancel').click(function() {
//             jQuery.unblockUI();
//             return false;
//         });
//     });
</script>

<jsp:include page="../include/sideInfo.jsp"/>

<jsp:useBean scope='session' id='userBean' class='org.akaza.openclinica.bean.login.UserAccountBean'/>
<jsp:useBean scope='request' id='crf' class='org.akaza.openclinica.bean.admin.CRFBean'/>


<h1><span class="title_manage">
<fmt:message key="view_subjects_in" bundle="${restext}"/> <c:out value="${study.name}"/>
<a href="javascript:openDocWindow('https://docs.openclinica.com/3.1/openclinica-user-guide/submit-data-module-overview/subject-matrix')">
    <img src="images/bt_Help_Manage.gif" border="0" alt="<fmt:message key="help" bundle="${restext}"/>" title="<fmt:message key="help" bundle="${restext}"/>"></a>
</span></h1>

<div id="findSubjectsDiv">
    <form  action="${pageContext.request.contextPath}/ListStudySubjects">
        <input type="hidden" name="module" value="admin">
        ${findSubjectsHtml}
    </form>
</div>
<div id="addSubjectForm" style="display:none;">
      <c:import url="../submit/addNewSubjectExpressNew.jsp">
      </c:import>
</div>

<br/>
<jsp:include page="../include/footer.jsp"/>

<script type="text/javascript">
    <c:if test="${showOverlay}">
        jQuery.blockUI({ message: jQuery('#addSubjectForm'), css:{left: "300px", top:"10px" } });
    </c:if>
</script>
