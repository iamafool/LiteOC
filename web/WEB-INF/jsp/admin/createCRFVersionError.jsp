<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="java.util.*" %>
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


<jsp:useBean scope='session' id='userBean' class='com.liteoc.bean.login.UserAccountBean'/>
<jsp:useBean scope='session' id='excelErrors' class='java.util.ArrayList'/>

<h1><span class="title_manage"><fmt:message key="create_a_new_CRF_version" bundle="${resword}"/> - <fmt:message key="error_encountered" bundle="${resword}"/></span></h1>

<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

<div class="alertbox_center">
<fmt:message key="errors_were_encountered_while_committing" bundle="${restext}"/>
<br/><br/>
</div>

</div></div></div></div></div></div></div></div>
<br/>
<c:forEach var="error" items="${excelErrors}">
<span class="alert"><c:out value="${error}"/></span><br/><hr/>
</c:forEach>




<jsp:include page="../include/footer.jsp"/>
