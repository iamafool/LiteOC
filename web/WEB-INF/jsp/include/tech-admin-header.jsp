<%@ page contentType="text/html; charset=UTF-8" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<fmt:setBundle basename="com.liteoc.i18n.words" var="resword"/>
<fmt:setBundle basename="com.liteoc.i18n.workflow" var="resworkflow"/>  
<fmt:setBundle basename="com.liteoc.i18n.format" var="resformat"/> 

<jsp:useBean scope='session' id='userBean' class='com.liteoc.bean.login.UserAccountBean'/>
<jsp:useBean scope='session' id='study' class='com.liteoc.bean.managestudy.StudyBean' />
<jsp:useBean scope='session' id='userRole' class='com.liteoc.bean.login.StudyUserRoleBean' />
<jsp:useBean scope='request' id='isAdminServlet' class='java.lang.String' />

<html>

<head>

<title><fmt:message key="openclinica" bundle="${resword}"/></title>

<link rel="stylesheet" href="includes/styles.css" type="text/css">

<link rel="stylesheet" href="includes/NewNavStyles.css" type="text/css" />
<script type="text/JavaScript" language="JavaScript" src="includes/global_functions_javascript.js"></script>
<script type="text/JavaScript" language="JavaScript" src="includes/global_functions_javascript2.js"></script>
<script language="JavaScript" src="includes/Tabs.js"></script>
<script language="JavaScript" src="includes/CalendarPopup.js"></script>
    <!-- Added for the new Calender -->

    <link rel="stylesheet" type="text/css" media="all" href="includes/new_cal/skins/aqua/theme.css" title="Aqua" />
    <script type="text/javascript" src="includes/new_cal/calendar.js"></script>
    <script type="text/javascript" src="includes/new_cal/lang/<fmt:message key="jscalendar_language_file" bundle="${resformat}"/>"></script>
    <script type="text/javascript" src="includes/new_cal/calendar-setup.js"></script>
<!-- End -->
    
</head>

<body class="main_BG" topmargin="0" leftmargin="0" marginwidth="0" marginheight="0"
	<jsp:include page="../include/showPopUp.jsp"/>
>

<table border="0" cellpadding="0" cellspacing="0" width="100%" height="100%" class="background">
	<tr>
		<td valign="top">
<%-- Header Table --%>

<SCRIPT LANGUAGE="JavaScript">

document.write('<table border="0" cellpadding="0" cellspacing="0" width="' + document.body.clientWidth + '" class="header">');

</script>
			<tr>
				<td valign="top">

<!-- Logo -->

<!-- 	<div class="logo"><img src="images/Logo.gif"></div> -->

<!-- Main Navigation -->

	<jsp:include page="../include/navBar.jsp"/>
<%-- End Main Navigation --%>
