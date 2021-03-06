<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<fmt:setBundle basename="com.liteoc.i18n.words" var="resword"/>
<fmt:setBundle basename="com.liteoc.i18n.format" var="resformat"/>
<fmt:setBundle basename="com.liteoc.i18n.workflow" var="resworkflow"/>

<jsp:include page="../include/managestudy-header.jsp"/>


<%-- move the alert message to the sidebar --%>

<jsp:include page="../include/sideInfo.jsp"/>

<jsp:useBean scope='request' id='table' class='com.liteoc.web.bean.EntityBeanTable'/>
<jsp:useBean scope="request" id="roles" class="java.util.ArrayList"/>
<script type="text/JavaScript" language="JavaScript">
  <!--
 function myCancel() {
 
    cancelButton=document.getElementById('cancel');
    if ( cancelButton != null) {
      if(confirm('<fmt:message key="sure_to_cancel" bundle="${resword}"/>')) {
        window.location.href="ListStudyUser";
       return true;
      } else {
        return false;
       }
     }
     return true;    
   
  }
   //-->
</script>
<h1><span class="title_manage">
    <c:choose>
        <c:when test="${study.parentStudyId > 0}">
            <fmt:message key="assign_users_to_current_site" bundle="${resword}"/>
        </c:when>
        <c:otherwise>
            <fmt:message key="assign_users_to_current_study" bundle="${resword}"/>
        </c:otherwise>
    </c:choose>
</span>
</h1>

<c:choose>
    <c:when test="${study.parentStudyId > 0}">
        <fmt:message key="assign_site_user_note" bundle="${resword}"/>
        <a href="${pageContext.request.contextPath}/ChangeStudy"><fmt:message key="that_study" bundle="${resword}"/></a>
    </c:when>
    <c:otherwise>
        <fmt:message key="assign_study_user_note" bundle="${resword}"/>
        <a href="${pageContext.request.contextPath}/ChangeStudy"><fmt:message key="that_site" bundle="${resword}"/></a>
    </c:otherwise>
</c:choose>

<br/><br/>

<form name="userForm" action="AssignUserToStudy" method="post">
<input type="hidden" name="action" value="submit">
 <c:set var="count" value="0"/>
<c:import url="../include/showTableForStudyUserList.jsp">
<c:param name="rowURL" value="showStudyUserRow.jsp" />
<c:param name="outerFormName" value="userForm" />
</c:import>
<br/>
<table border="0" cellpadding="0" cellspacing="0">
<tr>
<td>
<input type="submit" name="Submit" value="<fmt:message key="submit" bundle="${resword}"/>" class="button_medium">
</td>
<td>
<input type="button" name="Cancel" id="cancel" value="<fmt:message key="cancel" bundle="${resword}"/>" class="button_medium" onClick="javascript:myCancel();"/></td>
</tr></table>

</form>
<br/><br/>

<jsp:include page="../include/footer.jsp"/>
