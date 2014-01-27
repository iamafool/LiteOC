/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package com.liteoc.control.admin;

import com.liteoc.bean.core.EntityAction;
import com.liteoc.bean.core.Status;
import com.liteoc.bean.login.StudyUserRoleBean;
import com.liteoc.bean.login.UserAccountBean;
import com.liteoc.control.core.SecureController;
import com.liteoc.control.form.FormProcessor;
import com.liteoc.dao.login.UserAccountDAO;
import com.liteoc.view.Page;
import com.liteoc.web.InsufficientPermissionException;

// allows both deletion and restoration of a study user role

public class DeleteStudyUserRoleServlet extends SecureController {
    public static final String PATH = "DeleteStudyUserRole";
    public static final String ARG_USERNAME = "userName";
    public static final String ARG_STUDYID = "studyId";
    public static final String ARG_ACTION = "action";

    public static String getLink(String userName, int studyId, EntityAction action) {
        return PATH + "?" + ARG_USERNAME + "=" + userName + "&" + ARG_STUDYID + "=" + studyId + "&" + ARG_ACTION + "=" + action.getId();
    }

    @Override
    protected void mayProceed() throws InsufficientPermissionException {
        if (!ub.isSysAdmin()) {
            addPageMessage(respage.getString("no_have_correct_privilege_current_study") + respage.getString("change_study_contact_sysadmin"));
            throw new InsufficientPermissionException(Page.MENU_SERVLET, resexception.getString("you_may_not_perform_administrative_functions"), "1");
        }

        return;
    }

    @Override
    protected void processRequest() throws Exception {
        UserAccountDAO udao = new UserAccountDAO(sm.getDataSource());

        FormProcessor fp = new FormProcessor(request);
        int studyId = fp.getInt(ARG_STUDYID);
        String uName = fp.getString(ARG_USERNAME);
        UserAccountBean user = (UserAccountBean) udao.findByUserName(uName);
        int action = fp.getInt(ARG_ACTION);

        StudyUserRoleBean s = udao.findRoleByUserNameAndStudyId(uName, studyId);

        String message;
        if (!s.isActive()) {
            message = respage.getString("the_specified_user_role_not_exits_for_study");
        } else if (!EntityAction.contains(action)) {
            message = respage.getString("the_specified_action_is_invalid");
        } else if (!EntityAction.get(action).equals(EntityAction.DELETE) && !EntityAction.get(action).equals(EntityAction.RESTORE)) {
            message = respage.getString("the_specified_action_is_not_allowed");
        } else if (EntityAction.get(action).equals(EntityAction.RESTORE) && user.getStatus().equals(Status.DELETED)) {
            message = respage.getString("the_role_cannot_be_restored_since_user_deleted");
        } else {
            EntityAction desiredAction = EntityAction.get(action);

            if (desiredAction.equals(EntityAction.DELETE)) {
                s.setStatus(Status.DELETED);
                message = respage.getString("the_study_user_role_deleted");
            } else {
                s.setStatus(Status.AVAILABLE);
                message = respage.getString("the_study_user_role_restored");
            }

            s.setUpdater(ub);
            udao.updateStudyUserRole(s, uName);
        }

        addPageMessage(message);
        forwardPage(Page.LIST_USER_ACCOUNTS_SERVLET);
    }

    @Override
    protected String getAdminServlet() {
        return SecureController.ADMIN_SERVLET_CODE;
    }
}
