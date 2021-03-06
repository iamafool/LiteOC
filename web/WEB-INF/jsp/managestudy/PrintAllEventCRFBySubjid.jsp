<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="com.akazaresearch.tags" prefix="aka_frm" %>

<fmt:setBundle basename="com.liteoc.i18n.words" var="resword"/>
<fmt:setBundle basename="com.liteoc.i18n.format" var="resformat"/>
<c:set var="dteFormat"><fmt:message key="date_format_string" bundle="${resformat}"/></c:set>

<html>
<head>
    <meta http-equiv="content-type" content="text/html; charset=utf-8" />
    <meta http-equiv="X-UA-Compatible" content="IE=8" />
    
    <script type="text/JavaScript" language="JavaScript" src=
      "includes/global_functions_javascript.js"></script>
    <script type="text/javascript"  language="JavaScript" src=
      "includes/repetition-model/repetition-model.js"></script>
    <link rel="stylesheet" href="includes/print.css" type="text/css">

    <style>
        .infotab{border-top:1px #CCCCCC solid; border-left:1px #CCCCCC solid}
        .infotab tr td{border-right:1px #000000 solid;border-bottom:1px #000000 solid; padding:2px 4px 2px 4px}
        .infotab tr td h1{color:#000000}
        thead { display: table-header-group; }
    </style>
</head>
<%-- <jsp:useBean scope="request" id="section" class="com.liteoc.bean.submit.DisplaySectionBean" />
<jsp:useBean scope="session" id="studyEvent" class="com.liteoc.bean.managestudy.StudyEventBean" /> --%>
<jsp:useBean scope="request" id="sedCrfBeans" class="java.util.LinkedHashMap" />
<jsp:useBean scope="request" id="studySubject" class="com.liteoc.bean.managestudy.StudySubjectBean"></jsp:useBean>

<body>
<div style="border:0px #000000 solid;padding-left:0px;width:820px;margin:5px">
<c:forEach var="item" items="${sedCrfBeans}" varStatus="status">
<div style="page-break-after:always" >

<table border=0 align="center" width="100%">

<thead>
    <tr>
<th width=100%>
        <table class="infotab" cellpadding="0" cellspacing="0">
            <tr>
                <td width="30%"><h1><fmt:message key="study_subject_ID" bundle="${resword}"/></h1></td>
                <td width="40%"><h1><fmt:message key="event_definition_name" bundle="${resword}"/></h1></td>
                <td width="30%"><h1><fmt:message key="date_interviewed" bundle="${resword}"/></h1></td>
            </tr>
            <tr>
                <td><h1><c:out value="${studySubject.label}"/></h1></td>
                <td><h1><c:out value="${item.key[1].name}"/></h1></td>
                <%-- <td><h1><c:out value="${item.key[0].dateStarted}"/></h1></td> --%>
                <td><h1><fmt:formatDate value="${item.key[0].dateStarted}" type="date" pattern="yyyy-MM-dd"/></h1></td>
                
            </tr>
        </table>
    <br/>
    <br/>
</thead>
<tbody>
<tr>
<td width="100%">
    <c:forEach var="printCrfBean" items="${item.value}" varStatus="status">
    <div style="padding-left:10px;padding-bottom:20px;padding-top:10px;page-break-after:always" >
<h1>
<c:out value="${printCrfBean.crfBean.name}" /> <c:out value="${printCrfBean.crfVersionBean.name}" />
</h1>
    <c:choose>
    <c:when test="${printCrfBean.grouped}">
        <c:set var="listOfDisplaySectionBeans" value="${printCrfBean.displaySectionBeans}" scope="request"/>
        <c:set var="crfVersionBean" value="${printCrfBean.crfVersionBean}" scope="request"/>
        <c:set var="crfBean" value="${printCrfBean.crfBean}" scope="request"/>
        <c:set var="dataInvolved" value="true" scope="request"/>
        <aka_frm:print_tabletag><c:out value="${dataIsInvolved}"/></aka_frm:print_tabletag>
    </c:when>
    <c:when test="${!printCrfBean.grouped}">
        <c:set var="allSections" value="${printCrfBean.allSections}" scope="request"/>
        <c:set var="section" value="${printCrfBean.displaySectionBean}" scope="request"/>
        <c:set var="annotations" value="${printCrfBean.eventCrfBean.annotations}" scope="request"/>
        <c:set var="EventCRFBean" value="${printCrfBean.eventCrfBean}" scope="request"/>
        <c:set var="crfBean" value="${printCrfBean.crfBean}" scope="request"/>
        <c:set var="allCrfPrint" value="true" scope="request"/>
        <jsp:include page="../submit/showAllSectionPrint.jsp" />
    </c:when>
    </c:choose>
    </div>
    <c:if test="${!status.last}">
        <hr/>
    </c:if>
    </c:forEach>
 </td>
</tr>
</tbody>
 </table>
 </div>
</c:forEach>
 </div>
</body>
</html>
