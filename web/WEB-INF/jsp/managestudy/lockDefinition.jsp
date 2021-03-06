<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<fmt:setBundle basename="com.liteoc.i18n.words" var="resword"/>
<fmt:setBundle basename="com.liteoc.i18n.format" var="resformat"/>
<c:set var="dteFormat"><fmt:message key="date_format_string" bundle="${resformat}"/></c:set>

<jsp:include page="../include/managestudy-header.jsp"/>


<%-- move the alert message to the sidebar --%>


<jsp:include page="../include/sideInfo.jsp"/>

<jsp:useBean scope='session' id='userBean' class='com.liteoc.bean.login.UserAccountBean'/>
<jsp:useBean scope='request' id='definitionToLock' class='com.liteoc.bean.managestudy.StudyEventDefinitionBean'/>
<jsp:useBean scope='request' id='eventDefinitionCRFs' class='java.util.ArrayList'/>
<jsp:useBean scope='request' id='events' class='java.util.ArrayList'/>

<h1><span class="title_manage"><fmt:message key="confirm_locking_event_definition"  bundle="${resword}"/> </span></h1>

<div style="width: 600px"> 
<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

<div class="textbox_center">
<table border="0" cellpadding="0" cellspacing="0" width="100%">  
  <tr valign="top"><td class="table_header_column"><fmt:message key="name" bundle="${resword}"/>:</td><td class="table_cell">  
  <c:out value="${definitionToLock.name}"/>
   </td></tr>
  <tr valign="top"><td class="table_header_column"><fmt:message key="description" bundle="${resword}"/>:</td><td class="table_cell">  
  <c:out value="${definitionToLock.description}"/>
  </td></tr>
 
 <tr valign="top"><td class="table_header_column"><fmt:message key="repeating" bundle="${resword}"/>:</td><td class="table_cell">
  <c:choose>
   <c:when test="${definitionToLock.repeating == true}"> <fmt:message key="yes" bundle="${resword}"/> </c:when>
   <c:otherwise> <fmt:message key="no" bundle="${resword}"/> </c:otherwise>
  </c:choose>
  </td></tr>
  
  <tr valign="top"><td class="table_header_column"><fmt:message key="type" bundle="${resword}"/>:</td><td class="table_cell">
    <c:out value="${definitionToLock.type}"/>
   </td></tr>
  
  <tr valign="top"><td class="table_header_column"><fmt:message key="category" bundle="${resword}"/>:</td><td class="table_cell">  
  <c:out value="${definitionToLock.category}"/>
  </td></tr>
  </table> 
 </div>
</div></div></div></div></div></div></div></div>
 </div>
<br/>
<span class="table_title_manage"><fmt:message key="CRFs" bundle="${resword}"/>: </span>
<div style="width: 600px">
<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

<div class="textbox_center">
<table border="0" cellpadding="0" cellspacing="0" width="100%">  
 <c:forEach var ="crf" items="${eventDefinitionCRFs}">   
   <tr valign="top" bgcolor="#F5F5F5">             
    <td class="table_header_column" colspan="4"><c:out value="${crf.crfName}"/></td> 
    <td class="table_header_column" colspan="1"><c:out value="${crf.status.name}"/></td>      
  </tr> 
   <tr valign="top">   
     
    <td class="table_cell"><fmt:message key="required" bundle="${resword}"/>:
    <c:choose>
    <c:when test="${crf.requiredCRF == true}"> <fmt:message key="yes" bundle="${resword}"/> </c:when>
     <c:otherwise> <fmt:message key="no" bundle="${resword}"/> </c:otherwise>
    </c:choose>
   </td>
     
    <td class="table_cell"><fmt:message key="double_data_entry" bundle="${resword}"/>:
     <c:choose>
      <c:when test="${crf.doubleEntry == true}"> <fmt:message key="yes" bundle="${resword}"/> </c:when>
      <c:otherwise> <fmt:message key="no" bundle="${resword}"/> </c:otherwise>
     </c:choose>
    </td>         
         
    <td class="table_cell"><fmt:message key="enforce_decision_conditions" bundle="${resword}"/>:
     <c:choose>
      <c:when test="${crf.decisionCondition == true}"> <fmt:message key="yes" bundle="${resword}"/> </c:when>
      <c:otherwise> <fmt:message key="no" bundle="${resword}"/> </c:otherwise>
     </c:choose>
   </td>
  
   <td class="table_cell"><fmt:message key="default_version" bundle="${resword}"/>:    
    <c:out value="${crf.defaultVersionId}"/>     
   </td>
   <td class="table_cell"><fmt:message key="null_values" bundle="${resword}"/>:    
    <c:out value="${crf.nullValues}"/>     
  </td>
  </tr>             
 
 </c:forEach>
 
 </table>
 </div>
</div></div></div></div></div></div></div></div>
</div> 
<br/>
<span class="table_title_manage"><fmt:message key="SE" bundle="${resword}"/></span> 
<div style="width: 600px">
<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

<div class="textbox_center">
<table border="0" cellpadding="0" cellspacing="0" width="100%"> 
  <tr valign="top"><td class="table_header_column_top"><fmt:message key="study_subject_ID" bundle="${resword}"/></td>   
   <td class="table_header_column_top"><fmt:message key="start_date" bundle="${resword}"/></td>
   <td class="table_header_column_top"><fmt:message key="end_date" bundle="${resword}"/></td>
   <td class="table_header_column_top"><fmt:message key="status" bundle="${resword}"/></td>
  </tr>   
  <c:forEach var="event" items="${events}">
  <tr valign="top">
   <td class="table_cell">
    <c:out value="${event.studySubjectId}"/>
   </td>
   <td class="table_cell">
    <fmt:formatDate value="${event.dateStarted}" pattern="${dteFormat}"/>&nbsp;
   </td>
   <td class="table_cell">
    <fmt:formatDate value="${event.dateEnded}" pattern="${dteFormat}"/>&nbsp;
   </td>
   <td class="table_cell">
    <c:out value="${event.status.name}"/>
   </td>
  </tr>  
  </c:forEach>  
 </table>
 </div>
</div></div></div></div></div></div></div></div>
</div> 
<br/>
<form action='LockEventDefinition?action=submit&id=<c:out value="${definitionToLock.id}"/>' method="POST">
<input type="submit" name="submit" value="<fmt:message key="lock_event_definition" bundle="${resword}"/>" class="button_xlong" onClick='return confirm("<fmt:message key="if_you_lock_this_definition" bundle="${resword}"/>");'>
</form>

<jsp:include page="../include/footer.jsp"/>
