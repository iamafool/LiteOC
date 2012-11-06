<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<fmt:setBundle basename="com.liteoc.i18n.words" var="resword"/>	
<fmt:setBundle basename="com.liteoc.i18n.format" var="resformat"/>
<fmt:setBundle basename="com.liteoc.i18n.terms" var="resterm"/>


<jsp:include page="../include/managestudy-header.jsp"/>


<%-- move the alert message to the sidebar --%>


<jsp:include page="../include/sideInfo.jsp"/>

<jsp:useBean scope='session' id='userBean' class='com.liteoc.bean.login.UserAccountBean'/>
<jsp:useBean scope='session' id='definition' class='com.liteoc.bean.managestudy.StudyEventDefinitionBean'/>
<jsp:useBean scope='request' id='eventDefinitionCRFs' class='java.util.ArrayList'/>
<h1><span class="title_manage"><fmt:message key="confirm_event_definition_creation"  bundle="${resword}"/></span></h1>

<%-- removed since it repeats at the bottom, tbh 09112007 --%>
<%--
<table border="0" cellpadding="0" cellspacing="0">
			<tr>
				<td>

				<!-- These DIVs define shaded box borders -->
					<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

						<div class="textbox_center" align="center">

						<span class="title_manage">
						<fmt:message key="enter_definition_name_and_description" bundle="${resword}"/><br/><br/>
						</span>

						</div>

					</div></div></div></div></div></div></div></div>

				</td>
				<td><img src="images/arrow.gif"></td>
				<td>

				<!-- These DIVs define shaded box borders -->
					<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

						<div class="textbox_center" align="center">

						<span class="title_manage">
						<fmt:message key="add_CRFs_to_definition" bundle="${resword}"/><br/><br/>
						</span>

						</div>

					</div></div></div></div></div></div></div></div>

				</td>
				<td><img src="images/arrow.gif"></td>
				<td>

				<!-- These DIVs define shaded box borders -->
					<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

						<div class="textbox_center" align="center">

						<span class="title_manage">
						<fmt:message key="edit_properties_for_each_CRF" bundle="${resword}"/><br/><br/>
						</span>

						</div>

					</div></div></div></div></div></div></div></div>

				</td>
				<td><img src="images/arrow.gif"></td>
				<td>

				<!-- These DIVs define shaded box borders -->
					<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

						<div class="textbox_center" align="center">

						<span class="title_manage">
						<b><fmt:message key="confirm_and_submit_definition" bundle="${resword}"/><br/><br/></b>
						</span>

						</div>

					</div></div></div></div></div></div></div></div>

				</td>				
			
				
				<td></td>
			</tr>
		</table>
--%>
		<br/>
<div style="width: 600px">
<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

<div class="tablebox_center">
<table border="0" cellpadding="0" cellspacing="0" width="100%">
  <tr valign="top"><td class="table_header_column_top"><fmt:message key="name" bundle="${resword}"/>:</td><td class="table_cell">  
  <c:out value="${definition.name}"/>
   </td></tr>
  <tr valign="top"><td class="table_header_column"><fmt:message key="description" bundle="${resword}"/>:</td><td class="table_cell">  
  <c:out value="${definition.description}"/>&nbsp;
  </td></tr>
 
 <tr valign="top"><td class="table_header_column"><fmt:message key="repeating" bundle="${resword}"/>:</td><td class="table_cell">
  <c:choose>
   <c:when test="${definition.repeating == true}"> <fmt:message key="yes" bundle="${resword}"/> </c:when>
   <c:otherwise> <fmt:message key="no" bundle="${resword}"/> </c:otherwise>
  </c:choose>
  </td></tr>
  
  <tr valign="top"><td class="table_header_column"><fmt:message key="type" bundle="${resword}"/>:</td><td class="table_cell">
    <fmt:message key="${definition.type}" bundle="${resword}"/>
   </td></tr>
  
  <tr valign="top"><td class="table_header_column"><fmt:message key="category" bundle="${resword}"/>:</td><td class="table_cell">  
  <c:out value="${definition.category}"/>&nbsp;
  </td></tr>
  </table>
 </div>
</div></div></div></div></div></div></div></div>

</div>
<br/>
 
 <span class="table_title_manage"><fmt:message key="CRFs" bundle="${resword}"/></span>
 <div style="width: 600px">
 <div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

<div class="tablebox_center">
<table border="0" cellpadding="0" cellspacing="0" width="100%">
 <tr valign="top">               
    <td class="table_header_row_left"><fmt:message key="name" bundle="${resword}"/></td>   
    <td class="table_header_row"><fmt:message key="required" bundle="${resword}"/></td>     
    <td class="table_header_row"><fmt:message key="double_data_entry" bundle="${resword}"/></td>
    <td class="table_header_row"><fmt:message key="password_required" bundle="${resword}"/></td>
    <td class="table_header_row"><fmt:message key="hidden_crf" bundle="${resword}"/></td>   
    <!-- <td class="table_header_row"><fmt:message key="enforce_decision_conditions" bundle="${resword}"/></td>-->  
    <td class="table_header_row"><fmt:message key="default_version" bundle="${resword}"/></td>
    <td class="table_header_row"><fmt:message key="sdv_option" bundle="${resword}"/></td>  
    <td class="table_header_row"><fmt:message key="null_values" bundle="${resword}"/></td>    
  </tr>   
 <c:forEach var ="crf" items="${eventDefinitionCRFs}">   
   <tr>            
    <td class="table_cell_left"><c:out value="${crf.crfName}"/></td>      
    <td class="table_cell">
    <c:choose>
    <c:when test="${crf.requiredCRF == true}"> <fmt:message key="yes" bundle="${resword}"/> </c:when>
     <c:otherwise> <fmt:message key="no" bundle="${resword}"/> </c:otherwise>
    </c:choose>
   </td>
     
    <td class="table_cell">
     <c:choose>
      <c:when test="${crf.doubleEntry == true}"> <fmt:message key="yes" bundle="${resword}"/> </c:when>
      <c:otherwise> <fmt:message key="no" bundle="${resword}"/> </c:otherwise>
     </c:choose>
    </td>       

    <td class="table_cell">
     <c:choose>
      <c:when test="${crf.electronicSignature == true}"> <fmt:message key="yes" bundle="${resword}"/> </c:when>
      <c:otherwise> <fmt:message key="no" bundle="${resword}"/> </c:otherwise>
     </c:choose>
    </td>

    <td class="table_cell">
     <c:choose>
      <c:when test="${crf.hideCrf == true}"> <fmt:message key="yes" bundle="${resword}"/> </c:when>
      <c:otherwise> <fmt:message key="no" bundle="${resword}"/> </c:otherwise>
     </c:choose>
    </td>
    <%--<td class="table_cell">
     <c:choose>
      <c:when test="${crf.decisionCondition == true}"> <fmt:message key="yes" bundle="${resword}"/> </c:when>
      <c:otherwise> No </c:otherwise>
     </c:choose>
   </td>--%>
  
   <td class="table_cell">  
    <c:out value="${crf.defaultVersionName}"/>     
  </td>  
  
  <td class="table_cell">   
    <fmt:message key="${crf.sourceDataVerification.description}" bundle="${resterm}"/> 
  </td>   
  
   <td class="table_cell">   
    <c:out value="${crf.nullValues}"/>&nbsp;      
  </td> 
  </tr>            
   
 </c:forEach>
 
</table>
</div>
</div></div></div></div></div></div></div></div>

</div>
<br/>
<table border="0" cellpadding="0" cellspacing="0">
<tr>   
    <td><form action="DefineStudyEvent" method="POST">
         <input type="hidden" name="actionName" value="submit">
         <input type="hidden" name="nextAction" value="2">
         <input type="submit" name="submit" value="<fmt:message key="confirm_and_finish" bundle="${resword}"/>" class="button_long">
        </form>
    </td>
    <td><form action="DefineStudyEvent" method="POST">
         <input type="hidden" name="actionName" value="submit">
         <input type="hidden" name="nextAction" value="3">
         <input type="submit" name="submit" value="<fmt:message key="confirm_and_create_another_definition" bundle="${resword}"/>" class="button_xlong">
        </form>
    </td>
    <td><form action="DefineStudyEvent" method="POST">
         <input type="hidden" name="actionName" value="submit">
         <input type="hidden" name="nextAction" value="1">
         <input type="submit" name="submit" value="<fmt:message key="cancel" bundle="${resword}"/>" class="button_medium">
        </form>
    </td>
    </tr>  
   </table>
   <br/><br/>
<jsp:include page="../include/footer.jsp"/>
