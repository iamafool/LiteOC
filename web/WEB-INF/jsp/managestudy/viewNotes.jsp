<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<fmt:setBundle basename="com.liteoc.i18n.words" var="resword"/>
<fmt:setBundle basename="com.liteoc.i18n.format" var="resformat"/>
<fmt:setBundle basename="com.liteoc.i18n.notes" var="restext"/>
<fmt:setBundle basename="com.liteoc.i18n.terms" var="resterm"/>

<c:choose>
    <c:when test="${module eq 'manage'}">
        <jsp:include page="../include/managestudy-header.jsp"/>
    </c:when>
    <c:otherwise><jsp:include page="../include/submit-header.jsp"/>
    </c:otherwise>
</c:choose>

<link rel="stylesheet" href="includes/jmesa/jmesa.css" type="text/css">
<script type="text/JavaScript" language="JavaScript" src="includes/jmesa/jquery-1.3.2.min.js"></script>
<script type="text/JavaScript" language="JavaScript" src="includes/jmesa/jquery.jmesa.js"></script>
<script type="text/JavaScript" language="JavaScript" src="includes/jmesa/jmesa.js"></script>

<script type="text/javascript">
    function onInvokeAction(id,action) {
        if(id.indexOf('listNotes') == -1)  {
        setExportToLimit(id, '');
        }
        createHiddenInputFieldsForLimitAndSubmit(id);
    }
    function onInvokeExportAction(id) {
        var parameterString = createParameterStringForLimit(id);
        location.href = '${pageContext.request.contextPath}/ViewNotes?'+ parameterString;
    }
    function openPopup() {
        openDocWindow(window.location.href +'&print=yes')
    }
</script>


<%-- move the alert message to the sidebar --%>

<jsp:include page="../include/sideInfo.jsp"/>

<jsp:useBean scope='session' id='userBean' class='com.liteoc.bean.login.UserAccountBean'/>
<jsp:useBean scope='request' id='table' class='com.liteoc.web.bean.EntityBeanTable'/>
<jsp:useBean scope='request' id='message' class='java.lang.String'/>

<h1><c:choose>
        <c:when test="${module eq 'manage'}"><span class="title_manage"></c:when>
        <c:otherwise><span class="title_manage"></c:otherwise>
        </c:choose>
	<fmt:message key="view_discrepancy_notes" bundle="${resword}"/>
</span></h1>


<div><a id="sumBoxParent" href="javascript:void(0)"
        onclick="showSummaryBox('sumBox',document.getElementById('sumBoxParent'),
        '<fmt:message key="show_summary_statistics" bundle="${resword}"/>',
        '<fmt:message key="hide_summary_statistics" bundle="${resword}"/>')">
    <img name="ExpandGroup1" src="images/bt_Collapse.gif" border="0">
    <fmt:message key="hide_summary_statistics" bundle="${resword}"/></a>
</div>
<div id="sumBox" style="display:block; width:600px;">
    <%--<h3>Summary statistics</h3>--%>
    <c:if test="${empty summaryMap}"><fmt:message key="There_are_no_discrepancy_notes" bundle="${resword}"/></c:if>
    <!-- NEW Summary-->
    <table cellspacing="0" class="summaryTable" style="width:600px;">
        <tr><td>&nbsp;</td>
            <c:forEach var="typeName"  items="${typeNames}">
                <td align="center"><strong>${typeName.name}</strong></td>
            </c:forEach>
            <td align="center"><strong><fmt:message key="total" bundle="${resword}"/></strong></td>
        </tr>
            <c:forEach var="status" items="${mapKeys}">
                <tr>
                    <td><strong>${status.name}</strong><img src="${status.iconFilePath}" border="0" align="right"></td>
                    <c:forEach var="typeName" items="${typeNames}">
                        <td align="center">${summaryMap[status.name][typeName.name]}</td>
                    </c:forEach>
                    <td align="center"> ${summaryMap[status.name]['Total']}</td>
                </tr>
            </c:forEach>
        <tr><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td></tr>
        <tr><td><strong><fmt:message key="total" bundle="${resword}"/></strong></td>
            <c:forEach var="typeName"  items="${typeNames}">
                <td align="center">${typeKeys[typeName]}</td>
            </c:forEach>
            <td align="center">${grandTotal}</td>
        </tr>
    </table>
    <!-- End Of New Summary -->
</div>

<form  action="${pageContext.request.contextPath}/ViewNotes" style="clear:left; float:left;">
        <input type="hidden" name="module" value="submit">
        ${viewNotesHtml}
    </form>

<jsp:include page="../include/footer.jsp"/>
