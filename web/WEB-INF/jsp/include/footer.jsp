<%@ page contentType="text/html; charset=UTF-8" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
 
<fmt:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>
 
<!-- END MAIN CONTENT AREA -->
</td>
            </tr>
        </table>
        </td>
    </tr>
    <tr>
        <td valign="bottom">

<!-- Footer -->

<script type="text/javascript" src="includes/wz_tooltip/wz_tooltip.js"></script>
<SCRIPT LANGUAGE="JavaScript">

document.write('<table border="0" cellpadding=0" cellspacing="0" width="' + document.body.clientWidth + '">');

</script>
            <tr>
                <td class="footer" align="center"><fmt:message key="footer.license.1" bundle="${resword}"/> </td>
                </td>
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
        

</body>

</html>
