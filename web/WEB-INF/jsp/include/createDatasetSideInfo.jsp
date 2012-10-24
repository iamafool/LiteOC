<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<fmt:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/> 

<table border="0" cellpadding="0" cellspacing="0">
  <tr>
    <td valign="top">
      <table border="0" cellpadding="0" cellspacing="0">

<c:choose>
 <c:when test="${userBean != null && userBean.id>0}">	
	<tr id="sidebar_Info_open">
		<td class="sidebar_tab">

		<a href="javascript:leftnavExpand('sidebar_Info_open'); leftnavExpand('sidebar_Info_closed');"><img src="images/sidebar_collapse.gif" border="0" align="right" hspace="10"></a>

		<b><fmt:message key="info" bundle="${resword}"/></b>
   
		<div class="sidebar_tab">

			<span style="color: #789EC5">

	  <c:if test="${panel.createDataset}">   

        <c:import url="../include/createDatasetSide.jsp"/>
        <br/><br/>
      </c:if>  
      <br/>
      <c:if test="${newDataset.id>0}">
      	<c:forEach var='line' items="${panel.data}">
			<b><c:out value="${line.key}" escapeXml="false"/>:</b>&nbsp;
			<c:out value="${line.value}" escapeXml="false"/>
			<br/>
		</c:forEach> 
      </c:if>
 <script language="JavaScript">
       <!--
         function leftnavExpand(strLeftNavRowElementName){

	       var objLeftNavRowElement;

           objLeftNavRowElement = MM_findObj(strLeftNavRowElementName);
           if (objLeftNavRowElement != null) {
             if (objLeftNavRowElement.style) { objLeftNavRowElement = objLeftNavRowElement.style; } 
	           objLeftNavRowElement.display = (objLeftNavRowElement.display == "none" ) ? "" : "none";		
	         }
           }

       //-->
     </script>  
     
   	</div>

	</td>
	</tr>
	<tr id="sidebar_Info_closed" style="display: none">
		<td class="sidebar_tab">

		<a href="javascript:leftnavExpand('sidebar_Info_open'); leftnavExpand('sidebar_Info_closed');"><img src="images/sidebar_expand.gif" border="0" align="right" hspace="10"></a>

		<b><fmt:message key="info" bundle="${resword}"/></b>

		</td>
	</tr>   
  
  
  
  <c:if test="${panel.iconInfoShown}">
	 <c:import url="../include/sideIcons.jsp"/>
	</c:if>
</table>	
 	
</c:when>
</c:choose>


<!-- End Sidebar Contents -->

				<br/><img src="images/spacer.gif" width="120" height="1">

				</td>
				<td class="content" valign="top">

