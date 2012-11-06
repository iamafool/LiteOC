<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>


<fmt:setBundle basename="com.liteoc.i18n.format" var="resformat"/>
<fmt:setBundle basename="com.liteoc.i18n.notes" var="restext"/>    
<fmt:setBundle basename="com.liteoc.i18n.words" var="resword"/> 
<fmt:setBundle basename="com.liteoc.i18n.workflow" var="resworkflow"/> 



<jsp:include page="../include/managestudy-header.jsp"/>


<%-- move the alert message to the sidebar --%>



<jsp:include page="../include/sideInfo.jsp"/>

<jsp:useBean scope="request" id="newStudy" class="com.liteoc.bean.managestudy.StudyBean"/>
<jsp:useBean scope="request" id="subject" class="com.liteoc.bean.submit.SubjectBean"/>
<table>
<tr>
    <td>
<h1><span class="title_manage">
<fmt:message key="confirm_reassign_study_subject" bundle="${restext}"/>
</span></h1>

<form action="ReassignStudySubject" method="post">
<input type="hidden" name="action" value="submit">
<input type="hidden" name="id" value="<c:out value="${studySub.id}"/>">
<input type="hidden" name="studyId" value="<c:out value="${newStudy.id}"/>">
<p><fmt:message key="you_choose_to_reassign_subject2" bundle="${restext}"/> <b><c:out value="${subject.uniqueIdentifier}"/></b> <fmt:message key="to_study" bundle="${restext}"/>  <b><c:out value="${newStudy.name}"/></b>.</p>
<br/>
<input type="submit" name="Submit" value="<fmt:message key="submit" bundle="${resword}"/>" class="button_medium"></td></tr>

</form>

<br/><br/>
   </td></tr>
</table>
   <jsp:include page="../include/footer.jsp"/>
