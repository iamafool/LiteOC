<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<fmt:setBundle basename="com.liteoc.i18n.notes" var="restext"/>
<fmt:setBundle basename="com.liteoc.i18n.terms" var="resterm"/>
<fmt:setBundle basename="com.liteoc.i18n.words" var="resword"/>
<fmt:setBundle basename="com.liteoc.i18n.format" var="resformat"/>


<jsp:include page="../include/extract-header.jsp"/>


<%-- move the alert message to the sidebar --%>


<jsp:include page="../include/createDatasetSideInfo.jsp"/>

<jsp:useBean scope='session' id='userBean' class='com.liteoc.bean.login.UserAccountBean'/>
<jsp:useBean scope='request' id='statuses' class='java.util.ArrayList' />
<jsp:useBean scope="request" id="presetValues" class="java.util.HashMap" />

<c:set var="dsName" value="${newDataset.name}" />
<c:set var="dsDesc" value="${newDataset.description}" />
<c:set var="itemStatusId" value="${newDataset.datasetItemStatus.id}"/>
<c:set var="dsStatusId" value="${0}" />
<c:set var="mdvOID" value="${mdvOID}"/>
<c:set var="mdvName" value="${mdvName}"/>
<c:set var="mdvPrevStudy" value="${mdvPrevStudy}"/>
<c:set var="mdvPrevOID" value="${mdvPrevOID}"/>

<c:forEach var="presetValue" items="${presetValues}">
	<c:if test='${presetValue.key == "dsName"}'>
		<c:set var="dsName" value="${presetValue.value}" />
	</c:if>
	<c:if test='${presetValue.key == "dsDesc"}'>
		<c:set var="dsDesc" value="${presetValue.value}" />
	</c:if>
	<c:if test='${presetValue.key == "dsStatusId"}'>
		<c:set var="dsStatusId" value="${presetValue.value}" />
	</c:if>
	<c:if test='${presetValue.key == "mdvOID"}'>
		<c:set var="mdvOID" value="${presetValue.value}"/>
	</c:if>
	<c:if test='${presetValue.key == "mdvName"}'>
		<c:set var="mdvName" value="${presetValue.value}"/>
	</c:if>
	<c:if test='${presetValue.key == "mdvPrevStudy"}'>
		<c:set var="mdvPrevStudy" value="${presetValue.value}"/>
	</c:if>
	<c:if test='${presetValue.key == "mdvPrevOID"}'>
		<c:set var="mdvPrevOID" value="${presetValue.value}"/>
	</c:if>
</c:forEach>

<c:choose>
<c:when test="${newDataset.id>0}">
<h1><span class="title_manage"><fmt:message key="edit_dataset" bundle="${resword}"/> - <fmt:message key="specify_dataset_properties" bundle="${resword}"/>
: <c:out value="${newDataset.name}"/></span></h1>
</c:when>
<c:otherwise><h1><span class="title_manage"><fmt:message key="create_dataset" bundle="${resword}"/>: <fmt:message key="specify_dataset_properties" bundle="${resword}"/></span></h1>
</c:otherwise>
</c:choose>


<c:if test="${newDataset.id<=0}"><fmt:message key="enter_dataset_properties_be_descriptive" bundle="${restext}"/> <font color="red"><fmt:message key="name_description_required" bundle="${restext}"/></font></c:if>

<form action="CreateDataset" method="post">
<input type="hidden" name="action" value="specifysubmit"/>

<table>
	<tr>

		<td><fmt:message key="name" bundle="${resword}"/>:</td>

		<td><input type="text" name="dsName" size="30" value="<c:out value='${dsName}' />"/>
		<c:if test="${newDataset.id>0}"><fmt:message key="change_dataset_name_create_copy" bundle="${restext}"/></c:if>

		<br/><jsp:include page="../showMessage.jsp"><jsp:param name="key" value="dsName"/></jsp:include>

		</td>

	</tr>

	<tr>

		<td><fmt:message key="description" bundle="${resword}"/>:</td>

		<td><textarea name="dsDesc" cols="40" rows="4"><c:out value="${dsDesc}" /></textarea>

		<br/><jsp:include page="../showMessage.jsp"><jsp:param name="key" value="dsDesc"/></jsp:include>

		</td>

	</tr>

<tr>
	<td><fmt:message key="item_status" bundle="${resword}"/>:</td>
	<td>
	<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">
	<div class="textbox_center" align="center">
	<table border="0" cellpadding="0" cellspacing="0" width="100%">
		<c:choose>
		<c:when test="${newDataset.id<=0 || itemStatusId==1}">
 		<tr><td class="table_cell"><input type="radio" name="itemStatus" value="1" checked></td>
   		    <td class="table_cell"><fmt:message key="completed_items" bundle="${resterm}"/></td></tr>
   		<tr><td class="table_cell"><input type="radio" name="itemStatus" value="2"></td>
   		    <td class="table_cell"><fmt:message key="non_completed_items" bundle="${resterm}"/></td></tr>
   		<tr><td class="table_cell"><input type="radio" name="itemStatus" value="3"></td>
   			<td class="table_cell"><fmt:message key="completed_and_non_completed_items" bundle="${resterm}"/></td></tr>
   		</c:when>
   			<c:when test="${itemStatusId==2}">
   		<tr><td class="table_cell"><input type="radio" name="itemStatus" value="1"></td>
   		    <td class="table_cell"><fmt:message key="completed_items" bundle="${resterm}"/></td></tr>
   		<tr><td class="table_cell"><input type="radio" name="itemStatus" value="2" checked></td>
   		    <td class="table_cell"><fmt:message key="non_completed_items" bundle="${resterm}"/></td></tr>
   		<tr><td class="table_cell"><input type="radio" name="itemStatus" value="3"></td>
   			<td class="table_cell"><fmt:message key="completed_and_non_completed_items" bundle="${resterm}"/></td></tr>
   		</c:when>
   			<c:when test="${itemStatusId==3}">
   		<tr><td class="table_cell"><input type="radio" name="itemStatus" value="1"></td>
   		    <td class="table_cell"><fmt:message key="completed_items" bundle="${resterm}"/></td></tr>
   		<tr><td class="table_cell"><input type="radio" name="itemStatus" value="2"></td>
   		    <td class="table_cell"><fmt:message key="non_completed_items" bundle="${resterm}"/></td></tr>
   		<tr><td class="table_cell"><input type="radio" name="itemStatus" value="3" checked></td>
   			<td class="table_cell"><fmt:message key="completed_and_non_completed_items" bundle="${resterm}"/></td></tr>
   		</c:when>
   		</c:choose>
   	</table></div></div></div></div></div></div></div></div>
	</td>
	</tr>





	<tr>

	<td colspan="2">

	<br/><br/><br/><br/>

 <fmt:message key="long_note1" bundle="${restext}"/>

 <fmt:message key="long_note2" bundle="${restext}"/>

 <fmt:message key="long_note3" bundle="${restext}"/>

 <fmt:message key="long_note4" bundle="${restext}"/>

 <fmt:message key="long_note5" bundle="${restext}"/>

 <fmt:message key="long_note6" bundle="${restext}"/>

	<br/><br/>

	</td>

	<br/><br/>

	</tr>



	<tr>

		<td><fmt:message key="metadataversion_ODM_ID" bundle="${resword}"/>:          </td>

		<td>

		<input type="text" name="mdvOID" size="25" value="<c:out value='${mdvOID}' />"/>

		<br/><jsp:include page="../showMessage.jsp"><jsp:param name="key" value="mdvOID"/></jsp:include>

		</td>

	</tr>

	<tr>

		<td><fmt:message key="metadataversion_name" bundle="${resword}"/>:            </td>

		<td>

		<input type="text" name="mdvName" size="25" value="<c:out value='${mdvName}' />"/>

		<br/><jsp:include page="../showMessage.jsp"><jsp:param name="key" value="mdvName"/></jsp:include>

		</td>

	</tr>

	<tr>

		<td><fmt:message key="previous_study_ODM_ID" bundle="${resword}"/>:            </td>

		<td>

		<input type="text" name="mdvPrevStudy" size="25" value="<c:out value='${mdvPrevStudy}' />"/>

		<br/><jsp:include page="../showMessage.jsp"><jsp:param name="key" value="mdvPrevStudy"/></jsp:include>

		</td>

	</tr>

	<tr>

		<td><fmt:message key="previous_metadataversion_ODM_ID" bundle="${resword}"/>:   </td>

		<td>

		<input type="text" name="mdvPrevOID" size="25" value="<c:out value='${mdvPrevOID}' />"/>

		<br/><jsp:include page="../showMessage.jsp"><jsp:param name="key" value="mdvPrevOID"/></jsp:include>

		</td>

	</tr>



	<tr>

		<td class="text" colspan="2" align="left">

		<input type="hidden" name="dsStatus" value="1"/>

			<input type="submit" name="remove" value=" <fmt:message key="continue" bundle="${resword}"/>" class="button_xlong"/>
            <input type="button" onclick="confirmCancel('ViewDatasets');"  name="cancel" value="   <fmt:message key="cancel" bundle="${resword}"/>   " class="button_medium"/>

        </td>

	</tr>

</table>

</form>

<br/><br/>


<jsp:include page="../include/footer.jsp"/>
