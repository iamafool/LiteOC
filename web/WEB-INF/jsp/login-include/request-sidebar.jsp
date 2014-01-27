<%@ page contentType="text/html; charset=UTF-8" %>
<%@page import="com.liteoc.bean.core.Status"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<fmt:setBundle basename="com.liteoc.i18n.words" var="resword"/>
<fmt:setBundle basename="com.liteoc.i18n.workflow" var="resworkflow"/>
<script language="JavaScript">
function reportBug(versionNumber) {
 var bugtrack = "https://www.openclinica.org/OpenClinica/bug.php?version=<fmt:message key="version_number" bundle="${resword}"/>&url=";
 bugtrack = bugtrack + window.location.href;
 openDocWindow(bugtrack);

}
</script>
<!-- Breadcrumbs -->

	<div class="breadcrumbs">
	<a class="breadcrumb_completed"
			href="MainMenu">
			<fmt:message key="home" bundle="${resworkflow}"/></a>	
	&nbsp;

	
	</div>

<!-- End Breadcrumbs -->

				</td>



	<td valign="top" align="right">


<!-- User Box -->


<!-- End User Box -->

				</td>
			</tr>
		</table>
<!-- End Header Table -->
<table border="0" cellpadding=0" cellspacing="0">
	<tr>
		<td class="sidebar" valign="top">

<!-- Sidebar Contents -->

	<br/><br/>
	<a href="MainMenu"><fmt:message key="login" bundle="${resword}"/> </a>	

    <c:if test="${userBean != null && userRole != null && !userRole.invalid && passwordExpired == 'no'}">
	<br/><br/>
	<a href="RequestAccount"><fmt:message key="request_an_account" bundle="${resword}"/></a>
	<br/><br/>
	<a href="RequestPassword"><fmt:message key="forgot_password" bundle="${resword}"/></a>
	
    </c:if>

<!-- End Sidebar Contents -->

				<br/><img src="images/spacer.gif" width="120" height="1">

				</td>
				<td class="content" valign="top">
