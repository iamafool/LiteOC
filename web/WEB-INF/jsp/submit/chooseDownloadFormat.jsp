<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<fmt:setBundle basename="com.liteoc.i18n.words" var="resword"/>
<fmt:setBundle basename="com.liteoc.i18n.terms" var="resterm"/>
<html>
<head><title><fmt:message key="choose_download_format" bundle="${resword}"/></title></head>
<link rel="stylesheet" href="includes/styles.css" type="text/css">
<script type="text/JavaScript" language="JavaScript" src="includes/global_functions_javascript.js"></script>

<body style="margin: 5px">
<h2><fmt:message key="choose_download_format" bundle="${resword}"/></h2>

<div id="downloadDiv">
    <form action="DiscrepancyNoteOutputServlet" name="downloadForm">
        <fmt:message key="format" bundle="${resword}"/>: <select id="fmt" name="fmt">
           <option value="csv"><fmt:message key="comma_separated_values" bundle="${resword}"/></option>
           <option value="pdf"><fmt:message key="portable_document_format" bundle="${resword}"/></option>
        </select><br /><br />

        <input type="hidden" name="list" value="y"/>
        <input type="hidden" name="subjectId" value="${subjectId}"/>
        <input type="hidden" name="fileName" value="dnotes${subjectId}_${studyIdentifier}"/>
        <input type="hidden" name="studyIdentifier" value="${studyIdentifier}"/>        
        <input type="hidden" name="eventId" value="${param.eventId}"/>
        <input type="hidden" name="resolutionStatus" value="${param.resolutionStatus}"/>
        <input type="hidden" name="discNoteType" value="${param.discNoteType}"/>        
        <input type="submit" name="submitFormat" value="<fmt:message key="download_notes" bundle="${resword}"/>" class=
                                  "button_medium" />

        <br />
         <input type="button" name="clsWin" value="<fmt:message key="close_window" bundle="${resword}"/>" class=
                                  "button_medium" onclick="window.close()"/>
    </form>


</div>

</body>
</html>