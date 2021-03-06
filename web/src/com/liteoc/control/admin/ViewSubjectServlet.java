/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package com.liteoc.control.admin;


import com.liteoc.bean.submit.SubjectBean;
import com.liteoc.control.core.SecureController;
import com.liteoc.control.form.FormProcessor;
import com.liteoc.dao.managestudy.StudySubjectDAO;
import com.liteoc.dao.submit.SubjectDAO;
import com.liteoc.view.Page;
import com.liteoc.web.InsufficientPermissionException;

import java.util.ArrayList;

/**
 * @author jxu
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class ViewSubjectServlet extends SecureController {
    /**
     *
     */
    @Override
    public void mayProceed() throws InsufficientPermissionException {
        if (ub.isSysAdmin()) {
            return;
        }

        addPageMessage(respage.getString("no_have_correct_privilege_current_study") + respage.getString("change_study_contact_sysadmin"));
        throw new InsufficientPermissionException(Page.SUBJECT_LIST_SERVLET, resexception.getString("not_admin"), "1");

    }

    @Override
    public void processRequest() throws Exception {

        SubjectDAO sdao = new SubjectDAO(sm.getDataSource());
        FormProcessor fp = new FormProcessor(request);
        int subjectId = fp.getInt("id");

        if (subjectId == 0) {
            addPageMessage(respage.getString("please_choose_a_subject_to_view"));
            forwardPage(Page.SUBJECT_LIST_SERVLET);
        } else {
            SubjectBean subject = (SubjectBean) sdao.findByPK(subjectId);

            // find all study subjects
            StudySubjectDAO ssdao = new StudySubjectDAO(sm.getDataSource());
            ArrayList studySubs = ssdao.findAllBySubjectId(subjectId);

            request.setAttribute("subject", subject);
            request.setAttribute("studySubs", studySubs);
            forwardPage(Page.VIEW_SUBJECT);

        }

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
