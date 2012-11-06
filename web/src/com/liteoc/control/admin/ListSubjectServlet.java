/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package com.liteoc.control.admin;

import com.liteoc.control.core.SecureController;
import com.liteoc.dao.login.UserAccountDAO;
import com.liteoc.dao.managestudy.StudyDAO;
import com.liteoc.dao.managestudy.StudySubjectDAO;
import com.liteoc.dao.submit.SubjectDAO;
import com.liteoc.view.Page;
import com.liteoc.web.InsufficientPermissionException;

import java.util.Locale;

/**
 * Processes user request and generate subject list
 * 
 * @author jxu
 */
public class ListSubjectServlet extends SecureController {
    Locale locale;

    /**
     *
     */
    @Override
    public void mayProceed() throws InsufficientPermissionException {

        locale = request.getLocale();

        if (ub.isSysAdmin()) {
            return;
        }

        addPageMessage(respage.getString("no_have_correct_privilege_current_study") + respage.getString("change_study_contact_sysadmin"));
        throw new InsufficientPermissionException(Page.ADMIN_SYSTEM_SERVLET, resexception.getString("not_admin"), "1");

    }

    @Override
    public void processRequest() throws Exception {
        SubjectDAO sdao = new SubjectDAO(sm.getDataSource());

        StudySubjectDAO subdao = new StudySubjectDAO(sm.getDataSource());
        StudyDAO studyDao = new StudyDAO(sm.getDataSource());
        UserAccountDAO uadao = new UserAccountDAO(sm.getDataSource());

        ListSubjectTableFactory factory = new ListSubjectTableFactory();
        factory.setSubjectDao(sdao);
        factory.setStudySubjectDao(subdao);
        factory.setUserAccountDao(uadao);
        factory.setStudyDao(studyDao);
        factory.setCurrentStudy(currentStudy);
        

        String auditLogsHtml = factory.createTable(request, response).render();
        request.setAttribute("listSubjectsHtml", auditLogsHtml);

        forwardPage(Page.SUBJECT_LIST);
    }

    @Override
    protected String getAdminServlet() {
        if (ub.isSysAdmin()) {
            return SecureController.ADMIN_SERVLET_CODE;
        } else {
            return "";
        }
    }

}
