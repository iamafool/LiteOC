/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package com.liteoc.control.rule;

import com.liteoc.bean.core.Role;
import com.liteoc.bean.rule.RuleExecutionBusinessObject;
import com.liteoc.control.core.SecureController;
import com.liteoc.view.Page;
import com.liteoc.web.InsufficientPermissionException;

import java.util.Locale;

/**
 * Execute a Cross Edit Check
 * 
 * @author Krikor Krumlian
 * 
 */
public class ExecuteCrossEditCheckServlet extends SecureController {

    Locale locale;

    public static final String DIS_TYPES = "discrepancyTypes";
    public static final String RES_STATUSES = "resolutionStatuses";
    public static final String ENTITY_ID = "id";
    public static final String PARENT_ID = "parentId";// parent note id
    public static final String ENTITY_TYPE = "name";
    public static final String ENTITY_COLUMN = "column";
    public static final String ENTITY_FIELD = "field";
    public static final String FORM_DISCREPANCY_NOTES_NAME = "fdnotes";
    public static final String DIS_NOTE = "discrepancyNote";
    public static final String WRITE_TO_DB = "writeToDB";
    public static final String PRESET_RES_STATUS = "strResStatus";

    /*
     * (non-Javadoc)
     * 
     * @see com.liteoc.control.core.SecureController#mayProceed()
     */
    @Override
    public void mayProceed() throws InsufficientPermissionException {

        locale = request.getLocale();
        if (ub.isSysAdmin()) {
            return;
        }

        Role r = currentRole.getRole();
        if (r.equals(Role.STUDYDIRECTOR) || r.equals(Role.COORDINATOR) || r.equals(Role.INVESTIGATOR) || r.equals(Role.RESEARCHASSISTANT)) {
            return;
        }

        addPageMessage(respage.getString("no_have_correct_privilege_current_study") + respage.getString("change_study_contact_sysadmin"));
        throw new InsufficientPermissionException(Page.MENU_SERVLET, resexception.getString("may_not_submit_data"), "1");
    }

    @Override
    protected void processRequest() throws Exception {
        // FormProcessor fp = new FormProcessor(request);

        String eventCrfId = request.getParameter("eventCrfId");
        RuleExecutionBusinessObject ruleExecutionBusinessObject = new RuleExecutionBusinessObject(sm, currentStudy, ub);
        ruleExecutionBusinessObject.runRule(Integer.parseInt(eventCrfId));
        // forwardPage(Page.SUBMIT_DATA_SERVLET);
        forwardPage(Page.LIST_STUDY_SUBJECTS_SERVLET);
        // >> changed tbh, 06/2009

    }
}
