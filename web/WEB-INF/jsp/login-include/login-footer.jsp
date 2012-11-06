<%@ include file="/WEB-INF/jsp/taglibs.jsp" %>

<fmt:setBundle basename="com.liteoc.i18n.words" var="resword"/>

<script type="text/javascript" src="<c:url value='/includes/wz_tooltip/wz_tooltip.js'/>"></script>
<!-- END MAIN CONTENT AREA -->
</td>
			</tr>
		</table>
		</td>
	</tr>
	<tr>
		<td valign="bottom">

<!-- Footer -->

		<table border="0" cellpadding="0" cellspacing="0" width="100%">
			 <tr>
                <td class="footer" align="center"><fmt:message key="footer.license.1" bundle="${resword}"/> </td>
             </tr>
            <tr>
                 <td class="footer" align="center"> <fmt:message key="footer.license.2" bundle="${resword}"/></td>
            </tr>
            <tr>
                <td class="footer" align="center"><fmt:message key="footer.license.3" bundle="${resword}"/></td>
            </tr>
		</table>

<!-- End Footer -->

		</td>
	</tr>
</table>

<script type="text/javascript">
        jQuery(document).ready(function() {
            jQuery('#cancel').click(function() {
                jQuery.unblockUI();
                return false;
            });

            jQuery('#Contact').click(function() {
                jQuery.blockUI({ message: jQuery('#contactForm'), css:{left: "200px", top:"180px" } });
            });
        });

    </script>


        <div id="contactForm" style="display:none;">
              <%-- <c:import url="contactPop.jsp">
              </c:import> --%>
        </div>
</body>

</html>
