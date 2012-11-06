<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<fmt:setBundle basename="com.liteoc.i18n.notes" var="restext"/>
<fmt:setBundle basename="com.liteoc.i18n.words" var="resword"/>
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
<jsp:useBean scope='session' id='crfName' class='java.lang.String'/>

<h1><span class="title_manage"><fmt:message key="create_a_CRF_version_for" bundle="${restext}"/> <c:out value="${crfName}"/>
</span></h1>

<p>
<fmt:message key="please_click_browse_button" bundle="${restext}"/>:
</p>
<form action="CreateCRFVersion?action=submit&crfId=<c:out value="${version.crfId}"/>&name=<c:out value="${version.name}"/>" method="post" ENCTYPE="multipart/form-data">
<div style="width: 600px">
<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

<div class="textbox_center" align="center">
<table border="0" cellpadding="0" cellspacing="0" width="100%">
<tr><td class="formlabel"><fmt:message key="version_name" bundle="${resword}"/>:</td>
<td><div class="formfieldXL_BG">&nbsp;<c:out value="${version.name}"/></div></td>
<tr>
<td class="formlabel"><fmt:message key="ms_excel_file_to_upload" bundle="${resword}"/>:</td>
<td><div class="formfieldFile_BG"><input type="file" name="excel_file" class="formfieldM"></div></td>
</tr>
</table>
 </div>
</div></div></div></div></div></div></div></div>
</div>
<input type="submit" name="submit" value="<fmt:message key="upload_excel_file" bundle="${resword}"/>" class="button_xlong">
</form>


<jsp:include page="../include/footer.jsp"/>
