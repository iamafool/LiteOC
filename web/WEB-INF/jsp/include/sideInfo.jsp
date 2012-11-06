<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<fmt:setBundle basename="com.liteoc.i18n.words" var="resword"/>
<fmt:setBundle basename="com.liteoc.i18n.notes" var="restext"/> 

<script language="JavaScript">
  function leftnavExpand(strLeftNavRowElementName){
    var objLeftNavRowElement;
    objLeftNavRowElement = MM_findObj(strLeftNavRowElementName);
    if (objLeftNavRowElement != null) {
      if (objLeftNavRowElement.style) { objLeftNavRowElement = objLeftNavRowElement.style; } 
      objLeftNavRowElement.display = (objLeftNavRowElement.display == "none" ) ? "" : "none";		
    }
  }
</script>  

<table border="0" cellpadding="0" cellspacing="0">
  <tr>
	<td valign="top">
	  <table border="0" cellpadding="0" cellspacing="0">
        <tr id="sidebar_Info_closed" style="display: none">
		  <td class="sidebar_tab">
		    <a href="javascript:leftnavExpand('sidebar_Info_open'); leftnavExpand('sidebar_Info_closed');"><img src="${pageContext.request.contextPath}/images/sidebar_expand.gif" border="0" align="right" hspace="10"></a>
		    <b><fmt:message key="info" bundle="${restext}"/></b>
          </td>
	    </tr>
      	
        <tr id="sidebar_Info_open" style="display: all">
          <td class="sidebar_tab">
            <a href="javascript:leftnavExpand('sidebar_Info_open'); leftnavExpand('sidebar_Info_closed');"><img src="${pageContext.request.contextPath}/images/sidebar_collapse.gif" border="0" align="right" hspace="10"></a>
            <b><fmt:message key="info" bundle="${resword}"/></b>
	  
			<%-- Side alert, only show the content after user logs in --%>
			<c:if test="${userBean != null && userBean.id>0}">	 
			  <c:choose>
				<c:when test="${!empty pageMessages || param.message == 'authentication_failed'}">
				  <div class="sidebar_tab_content">
					<i>	
					  <c:choose>
						<c:when test="${userBean!= null && userBean.id>0}">             
						  <jsp:include page="../include/showSideMessage.jsp" />
						</c:when>
						<c:otherwise>             
						  <fmt:message key="have_logged_out_application" bundle="${resword}"/><a href="MainMenu"><fmt:message key="login_page" bundle="${resword}"/></a> <fmt:message key="in_order_to_re_enter_openclinica" bundle="${resword}"/>         
						</c:otherwise>
					  </c:choose>
					</i>
					<br/>
					<span style="color: #789EC5"></span>
				  </div>
				</c:when>
			  </c:choose>	

 	          <c:if test="${iconInfoShown}">
	 			<c:import url="/WEB-INF/jsp/include/sideIconsSubject.jsp"/>
	          </c:if>

              <c:if test="${(!panel.iconInfoShown && panel.manageSubject) || closeInfoShowIcons}">
				<c:import url="/WEB-INF/jsp/include/sideIconsSubject.jsp"/>
	          </c:if>
	
              <c:if test="${panel.submitDataModule}">      
                <c:import url="/WEB-INF/jsp/include/submitDataSide.jsp"/>
              </c:if> 
			</c:if>

      </table>
      <br/>
      <img src="images/spacer.gif" width="120" height="1">
    </td>
    <td class="aka_revised_content" valign="top">
  

