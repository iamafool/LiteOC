<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<fmt:setBundle basename="com.liteoc.i18n.words" var="resword"/>
<fmt:setBundle basename="com.liteoc.i18n.format" var="resformat"/>
<fmt:setBundle basename="com.liteoc.i18n.notes" var="restext"/>

<jsp:include page="../include/admin-header.jsp"/>


<%-- move the alert message to the sidebar --%>

<jsp:include page="../include/sideInfo.jsp"/>

<jsp:useBean scope='session' id='userBean' class='com.liteoc.bean.login.UserAccountBean'/>
<jsp:useBean scope='request' id='table' class='com.liteoc.web.bean.EntityBeanTable'/>

<h1><span class="title_manage"><fmt:message key="administer_studies" bundle="${resword}"/> <a href="javascript:openDocWindow('https://docs.openclinica.com/3.1/administer-study')"><img src="images/bt_Help_Manage.gif" border="0" alt="<fmt:message key="help" bundle="${resword}"/>" title="<fmt:message key="help" bundle="${resword}"/>"></a></span></h1>

<div class="homebox_bullets"><a href="CreateStudy"><fmt:message key="create_a_new_study" bundle="${resword}"/></a></div>
<p>
<fmt:message key="studies_are_indicated_in_bold" bundle="${restext}"/>
</p>

<c:import url="../include/showTable.jsp"><c:param name="rowURL" value="showStudyRow.jsp" /></c:import>
<br/><br/>

<jsp:include page="../include/footer.jsp"/>
