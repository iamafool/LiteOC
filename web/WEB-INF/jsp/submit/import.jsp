<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<fmt:setBundle basename="com.liteoc.i18n.notes" var="restext"/>
<fmt:setBundle basename="com.liteoc.i18n.words" var="resword"/>
<fmt:setBundle basename="com.liteoc.i18n.workflow" var="resworkflow"/>
<fmt:setBundle basename="com.liteoc.i18n.terms" var="resterms"/>
<c:choose>
<c:when test="${userBean.sysAdmin && module=='admin'}">
 <c:import url="../include/admin-header.jsp"/>
</c:when>
<c:otherwise>
 <c:import url="../include/submit-header.jsp"/>
</c:otherwise>
</c:choose>


<%-- move the alert message to the sidebar --%>





<jsp:include page="../include/sideInfo.jsp"/>


<jsp:useBean scope='session' id='version' class='com.liteoc.bean.submit.CRFVersionBean'/>
<jsp:useBean scope='session' id='userBean' class='com.liteoc.bean.login.UserAccountBean'/>
<jsp:useBean scope='session' id='crfName' class='java.lang.String'/>

 <c:out value="${crfName}"/>

<c:choose>
	<c:when test="${userBean.sysAdmin && module=='admin'}">
		<h1><span class="title_manage">
	</c:when>
	<c:otherwise>
		<h1>
		<span class="title_submit">
	</c:otherwise>
</c:choose>

<fmt:message key="import_crf_data" bundle="${resworkflow}"/></h1>
<p><fmt:message key="import_instructions" bundle="${restext}"/></p>



<form action="ImportCRFData?action=confirm&crfId=<c:out value="${version.crfId}"/>&name=<c:out value="${version.name}"/>" method="post" ENCTYPE="multipart/form-data">
<div style="width: 400px">

<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">
<div class="textbox_center">
<table border="0" cellpadding="0" cellspacing="0">

<tr>
	<td class="formlabel"><fmt:message key="xml_file_to_upload" bundle="${resterms}"/>:</td>
	<td>
		<div class="formfieldFile_BG"><input type="file" name="xml_file" > </div>
		<br/><jsp:include page="../showMessage.jsp"><jsp:param name="key" value="xml_file"/></jsp:include>
	</td>
</tr>
<input type="hidden" name="crfId" value="<c:out value="${version.crfId}"/>">


</table>
</div>
</div></div></div></div></div></div></div></div>
</div>

<br clear="all">
<input type="submit" value="<fmt:message key="continue" bundle="${resword}"/>" class="button_long">
<input type="button" onclick="goBack()"  name="cancel" value="<fmt:message key="cancel" bundle="${resword}"/>" class="button_medium"/>

</form>


<jsp:include page="../include/footer.jsp"/>
