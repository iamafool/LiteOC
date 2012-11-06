package com.liteoc.control.admin;



import com.liteoc.control.core.SecureController;
import com.liteoc.view.Page;
import com.liteoc.web.InsufficientPermissionException;

public class ViewAllJobsServlet extends SecureController {

    @Override
    protected void mayProceed() throws InsufficientPermissionException {
        // TODO Auto-generated method stub
        if (ub.isSysAdmin() || ub.isTechAdmin()) {
            return;
        }
//        if (currentRole.getRole().equals(Role.STUDYDIRECTOR) || currentRole.getRole().equals(Role.COORDINATOR)) {// ?
//
//            return;
//        }

        addPageMessage(respage.getString("no_have_correct_privilege_current_study") + respage.getString("change_study_contact_sysadmin"));
        throw new InsufficientPermissionException(Page.MENU_SERVLET, resexception.getString("not_allowed_access_extract_data_servlet"), "1");// TODO

    }

    @Override
    protected void processRequest() throws Exception {
        // TODO Auto-generated method stub
        forwardPage(Page.VIEW_ALL_JOBS);
    }

}
