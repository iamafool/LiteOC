/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package com.liteoc.control.submit;


import com.liteoc.bean.core.Role;
import com.liteoc.bean.login.StudyUserRoleBean;
import com.liteoc.bean.login.UserAccountBean;
import com.liteoc.control.core.SecureController;
import com.liteoc.view.Page;
import com.liteoc.web.InsufficientPermissionException;

import java.util.Locale;

/**
 * @author ssachs
 *
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class SubmitDataServlet extends SecureController {

    Locale locale;

    // < ResourceBundleresexception,respage;

    /*
     * (non-Javadoc)
     *
     * @see com.liteoc.control.core.SecureController#processRequest()
     */
    @Override
    protected void processRequest() throws Exception {
        forwardPage(Page.SUBMIT_DATA);
    }

    public static boolean mayViewData(UserAccountBean ub, StudyUserRoleBean currentRole) {
        if (currentRole != null) {
            Role r = currentRole.getRole();
            if (r != null && (r.equals(Role.COORDINATOR) || r.equals(Role.STUDYDIRECTOR) ||
                    r.equals(Role.INVESTIGATOR) || r.equals(Role.RESEARCHASSISTANT) ||r.equals(Role.MONITOR) )) {
                return true;
            }
        }

        return false;
    }
    
    
    public static boolean maySubmitData(UserAccountBean ub, StudyUserRoleBean currentRole) {
        if (currentRole != null) {
            Role r = currentRole.getRole();
            if (r != null && (r.equals(Role.COORDINATOR) || r.equals(Role.STUDYDIRECTOR) ||
                    r.equals(Role.INVESTIGATOR) || r.equals(Role.RESEARCHASSISTANT))) {
                return true;
            }
        }

        return false;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.liteoc.control.core.SecureController#mayProceed()
     */
    @Override
    protected void mayProceed() throws InsufficientPermissionException {

        locale = request.getLocale();
        // <
        // resexception=ResourceBundle.getBundle("com.liteoc.i18n.exceptions",locale);
        // < respage =
        // ResourceBundle.getBundle("com.liteoc.i18n.page_messages",locale);

        String exceptionName = resexception.getString("no_permission_to_submit_data");
        String noAccessMessage = respage.getString("may_not_enter_data_for_this_study") + respage.getString("change_study_contact_sysadmin");

        if (mayViewData(ub, currentRole)) {
            return;
        }

        addPageMessage(noAccessMessage);
        throw new InsufficientPermissionException(Page.MENU, exceptionName, "1");
    }

}
