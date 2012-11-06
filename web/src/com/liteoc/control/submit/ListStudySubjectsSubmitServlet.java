/*
 * Created on Jan 21, 2005
 */
package com.liteoc.control.submit;


import com.liteoc.control.managestudy.ListStudySubjectServlet;
import com.liteoc.view.Page;
import com.liteoc.web.InsufficientPermissionException;

import java.util.Locale;

/**
 * @author ssachs
 */
public class ListStudySubjectsSubmitServlet extends ListStudySubjectServlet {

    Locale locale;

    // < ResourceBundleresexception,respage;

    /*
     * (non-Javadoc)
     *
     * @see com.liteoc.control.managestudy.ListStudySubjectServlet#getJSP()
     */
    @Override
    protected Page getJSP() {
        return Page.SUBMIT_DATA;
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

        if (ub.isSysAdmin()) {
            return;
        }

        if (SubmitDataServlet.mayViewData(ub, currentRole)) {
            return;
        }

        addPageMessage(respage.getString("no_have_correct_privilege_current_study") + respage.getString("change_study_contact_sysadmin"));
        throw new InsufficientPermissionException(Page.MENU_SERVLET, resexception.getString("may_not_submit_data"), "1");
    }

    /*
     * (non-Javadoc)
     *
     * @see com.liteoc.control.managestudy.ListStudySubjectServlet#getBaseURL()
     */
    @Override
    protected String getBaseURL() {
        return "ListStudySubjectsSubmit";
    }
}
