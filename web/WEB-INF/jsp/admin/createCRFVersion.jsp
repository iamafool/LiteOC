<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<fmt:setBundle basename="com.liteoc.i18n.notes" var="restext"/>
<fmt:setBundle basename="com.liteoc.i18n.words" var="resword"/>
<fmt:setBundle basename="com.liteoc.i18n.workflow" var="resworkflow"/>
<fmt:setBundle basename="com.liteoc.i18n.page_messages" var="respage"/>

<c:choose>
<c:when test="${userBean.sysAdmin && module=='admin'}">
 <c:import url="../include/admin-header.jsp"/>
</c:when>
<c:otherwise>
 <c:import url="../include/managestudy-header.jsp"/>
</c:otherwise>
</c:choose>


<%-- move the alert message to the sidebar --%>

<jsp:include page="../include/sideInfo.jsp"/>


<jsp:useBean scope='session' id='version' class='com.liteoc.bean.submit.CRFVersionBean'/>
<jsp:useBean scope='session' id='userBean' class='com.liteoc.bean.login.UserAccountBean'/>
<jsp:useBean scope='request' id='crfName' class='java.lang.String'/>

<h1>
<span class="title_manage">
 <c:choose>
     <c:when test="${empty crfName}">
         <fmt:message key="create_a_new_CRF_case_report_form" bundle="${resworkflow}"/>
     </c:when>
     <c:otherwise>
        <fmt:message key="create_CRF_version" bundle="${resworkflow}"/> <c:out value="${crfName}"/>
     </c:otherwise>
 </c:choose>

</span>
</h1>

<script type="text/JavaScript" language="JavaScript">
  <!--
 function myCancel() {

    cancelButton=document.getElementById('cancel');
    if ( cancelButton != null) {
      if(confirm('<fmt:message key="sure_to_cancel" bundle="${resword}"/>')) {
       window.location.href="ListCRF?module=" + "<c:out value="${module}"/>";
       return true;
      } else {
        return false;
       }
     }
     return true;

  }

function submitform(){
    var crfUpload = document.getElementById('excel_file_path');
    //Does the user browse or select a file or not
    if (crfUpload.value =='' )
    {
        alert("Select a file to upload!");
        return false;
    }
}

   //-->
</script>

<form action="CreateCRFVersion?action=confirm&crfId=<c:out value="${version.crfId}"/>&name=<c:out value="${version.name}"/>" method="post" ENCTYPE="multipart/form-data">
<div style="width: 400px">
<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

<div class="textbox_center">
<table border="0" cellpadding="0" cellspacing="0">

<tr>
<td class="formlabel"><fmt:message key="ms_excel_file_to_upload" bundle="${resword}"/>:</td>
<td><div class="formfieldFile_BG"><input type="file" name="excel_file" id="excel_file_path"></div>
<br/><jsp:include page="../showMessage.jsp"><jsp:param name="key" value="excel_file"/></jsp:include></td>
</tr>
<input type="hidden" name="crfId" value="<c:out value="${version.crfId}"/>">


</table>

</div>

</div></div></div></div></div></div></div></div>
</div>

<br clear="all">
<table border="0" cellpadding="0" cellspacing="0">
<tr>
<td>
<input type="submit" onclick="return submitform();" value="<fmt:message key="submit" bundle="${resword}"/>" class="button_medium">
</td>
<td>
<input type="button" onclick="confirmExit('ListCRF?module=<c:out value="${module}"/>')" name="exit" value="<fmt:message key="cancel" bundle="${resword}"/>" class="button_medium"/>
</tr></table>
</form>

<jsp:include page="../include/footer.jsp"/>
