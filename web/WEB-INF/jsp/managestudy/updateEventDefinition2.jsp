<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<fmt:setBundle basename="com.liteoc.i18n.words" var="resword"/>	
<fmt:setBundle basename="com.liteoc.i18n.format" var="resformat"/>


<jsp:include page="../include/managestudy-header.jsp"/>


<%-- move the alert message to the sidebar --%>


<jsp:include page="../include/sideInfo.jsp"/>

<jsp:useBean scope='session' id='userBean' class='com.liteoc.bean.login.UserAccountBean'/>
<%--<jsp:useBean scope="request" id="crfs" class="java.util.ArrayList"/>--%>

<script type="text/JavaScript" language="JavaScript">
  <!--
 function myCancel() {
 
    cancelButton=document.getElementById('cancel');
    if ( cancelButton != null) {
      if(confirm('<fmt:message key="sure_to_cancel" bundle="${resword}"/>')) {
        window.location.href="ListEventDefinition";
       return true;
      } else {
        return false;
       }
     }
     return true;       
  }
   //-->
</script>
<h1><span class="title_manage"><fmt:message key="update_SED" bundle="${resword}"/> - <fmt:message key="add_CRFs" bundle="${resword}"/></span></h1>

<form name="crfForm" action="AddCRFToDefinition"  method="post">
    <input type="hidden" name="actionName" value="next">
    <input type="hidden" name="pageNum" value="2">

    <c:import url="../include/showTableForEventDefinitionCRFList.jsp">
        <c:param name="rowURL" value="showDefineEventCRFRow.jsp" />
        <c:param name="outerFormName" value="crfForm" />
        <%--<c:param name="searchFormOnClickJS" value="document.crfForm.elements['actionName'].value='next';document.crfForm.elements[1].value='1';" />--%>
    </c:import>

<%--<c:import url="../include/showTable.jsp">--%>
<%--<c:param name="rowURL" value="showDefineEventCRFRow.jsp" />--%>
<%--<c:param name="outerFormName" value="crfForm" />--%>
<%--</c:import>--%>
<table border="0" cellpadding="0" cellspacing="0">
<tr>
<td>
 <input type="submit" name="Submit" value="<fmt:message key="add" bundle="${resword}"/>" class="button_medium">
</td>
<td>
<input type="button" name="Cancel" id="cancel" value="<fmt:message key="cancel" bundle="${resword}"/>" class="button_long" onClick="javascript:myCancel();"/></td>
</tr></table>
</form>
<br/><br/>
<jsp:include page="../include/footer.jsp"/>
