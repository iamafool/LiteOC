/*
 *
 */
package com.liteoc.control.techadmin;

//
// import java.util.ArrayList;


import com.liteoc.control.core.SecureController;
import com.liteoc.dao.admin.CRFDAO;
import com.liteoc.dao.login.UserAccountDAO;
import com.liteoc.dao.managestudy.StudyDAO;
import com.liteoc.dao.submit.SubjectDAO;
import com.liteoc.view.Page;
import com.liteoc.web.InsufficientPermissionException;

import java.util.ArrayList;

/**
 * @author thickerson
 *
 *
 */
public class TechAdminServlet extends SecureController {

    /*
     * (non-Javadoc)
     *
     * @see com.liteoc.control.core.SecureController#processRequest()
     */
    @Override
    protected void processRequest() throws Exception {
        // find last 5 modifed studies
        StudyDAO sdao = new StudyDAO(sm.getDataSource());
        // ArrayList studies = (ArrayList) sdao.findAllByLimit(true);
        // request.setAttribute("studies", studies);
        ArrayList allStudies = (ArrayList) sdao.findAll();
        // request.setAttribute("allStudyNumber", new
        // Integer(allStudies.size()));

        UserAccountDAO udao = new UserAccountDAO(sm.getDataSource());
        // ArrayList users = (ArrayList) udao.findAllByLimit(true);
        // request.setAttribute("users", users);
        ArrayList allUsers = (ArrayList) udao.findAll();
        // request.setAttribute("allUserNumber", new Integer(allUsers.size()));

        SubjectDAO subdao = new SubjectDAO(sm.getDataSource());
        // ArrayList subjects = (ArrayList) subdao.findAllByLimit(true);
        // request.setAttribute("subjects", subjects);
        ArrayList allSubjects = (ArrayList) subdao.findAll();
        // request.setAttribute("allSubjectNumber", new
        // Integer(allSubjects.size()));

        CRFDAO cdao = new CRFDAO(sm.getDataSource());
        // ArrayList crfs = (ArrayList) cdao.findAllByLimit(true);
        // request.setAttribute("crfs", subjects);
        ArrayList allCrfs = (ArrayList) cdao.findAll();
        // request.setAttribute("allCrfNumber", new Integer(allCrfs.size()));

        resetPanel();

        panel.setStudyInfoShown(false);
        panel.setOrderedData(true);
        setToPanel(resword.getString("in_the_application"), "");
        if (allSubjects.size() > 0) {
            setToPanel(resword.getString("subjects"), Integer.toString(allSubjects.size()));
        }
        if (allUsers.size() > 0) {
            setToPanel(resword.getString("users"), Integer.toString(allUsers.size()));
        }
        if (allStudies.size() > 0) {
            setToPanel(resword.getString("studies"), Integer.toString(allStudies.size()));
        }
        if (allCrfs.size() > 0) {
            setToPanel(resword.getString("CRFs"), Integer.toString(allCrfs.size()));
        }
        forwardPage(Page.TECH_ADMIN_SYSTEM);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.liteoc.control.core.SecureController#mayProceed()
     */
    @Override
    protected void mayProceed() throws InsufficientPermissionException {

        if (!ub.isTechAdmin()) {
            throw new InsufficientPermissionException(Page.MENU, resexception.getString("you_may_not_perform_technical_admin_functions"), "1");
        }

        return;
    }

    @Override
    protected String getAdminServlet() {
        return SecureController.ADMIN_SERVLET_CODE;
    }
}
