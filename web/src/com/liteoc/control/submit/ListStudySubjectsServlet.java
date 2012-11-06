/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2009 Akaza Research
 */
package com.liteoc.control.submit;

import com.liteoc.bean.managestudy.StudySubjectBean;
import com.liteoc.control.core.CoreSecureController;
import com.liteoc.control.core.SecureController;
import com.liteoc.control.form.FormDiscrepancyNotes;
import com.liteoc.control.form.FormProcessor;
import com.liteoc.dao.managestudy.EventDefinitionCRFDAO;
import com.liteoc.dao.managestudy.StudyDAO;
import com.liteoc.dao.managestudy.StudyEventDAO;
import com.liteoc.dao.managestudy.StudyEventDefinitionDAO;
import com.liteoc.dao.managestudy.StudyGroupClassDAO;
import com.liteoc.dao.managestudy.StudyGroupDAO;
import com.liteoc.dao.managestudy.StudySubjectDAO;
import com.liteoc.dao.submit.EventCRFDAO;
import com.liteoc.dao.submit.SubjectDAO;
import com.liteoc.dao.submit.SubjectGroupMapDAO;
import com.liteoc.view.Page;
import com.liteoc.web.InsufficientPermissionException;

import java.util.Locale;
import java.util.Date;
import java.util.HashMap;

/**
 * Servlet for creating a table.
 * 
 * @author Krikor Krumlian
 */
public class ListStudySubjectsServlet extends SecureController {

    // Shaoyu Su
    private static final long serialVersionUID = 1L;
    private StudyEventDefinitionDAO studyEventDefinitionDAO;
    private SubjectDAO subjectDAO;
    private StudySubjectDAO studySubjectDAO;
    private StudyEventDAO studyEventDAO;
    private StudyGroupClassDAO studyGroupClassDAO;
    private SubjectGroupMapDAO subjectGroupMapDAO;
    private StudyDAO studyDAO;
    private EventCRFDAO eventCRFDAO;
    private EventDefinitionCRFDAO eventDefintionCRFDAO;
    private StudyGroupDAO studyGroupDAO;
    private boolean showMoreLink; 
    Locale locale;

    /*
     * (non-Javadoc)
     * 
     * @see com.liteoc.control.core.SecureController#mayProceed()
     */
    @Override
    protected void mayProceed() throws InsufficientPermissionException {

        locale = request.getLocale();

        if (ub.isSysAdmin()) {
            return;
        }

        if (SubmitDataServlet.mayViewData(ub, currentRole)) {
            return;
        }

        addPageMessage(respage.getString("no_have_correct_privilege_current_study") + respage.getString("change_study_contact_sysadmin"));
        throw new InsufficientPermissionException(Page.MENU_SERVLET, resexception.getString("may_not_submit_data"), "1");
    }

    @Override
    protected void processRequest() throws Exception {
        CoreSecureController.removeLockedCRF(ub.getId());
        FormProcessor fp = new FormProcessor(request);
        if(fp.getString("showMoreLink").equals("")){
            showMoreLink = true;
        }else {
            showMoreLink = Boolean.parseBoolean(fp.getString("showMoreLink"));
        }
        String idSetting = currentStudy.getStudyParameterConfig().getSubjectIdGeneration();
        // set up auto study subject id
        if (idSetting.equals("auto editable") || idSetting.equals("auto non-editable")) {
            //Shaoyu Su
            // int nextLabel = getStudySubjectDAO().findTheGreatestLabel() + 1;
            // request.setAttribute("label", new Integer(nextLabel).toString());
            request.setAttribute("label", resword.getString("id_generated_Save_Add"));
            fp.addPresetValue("label", resword.getString("id_generated_Save_Add"));
        }

        if (fp.getRequest().getParameter("subjectOverlay") == null){
            Date today = new Date(System.currentTimeMillis());
            String todayFormatted = local_df.format(today);
            if (request.getAttribute(PRESET_VALUES) != null) {
                fp.setPresetValues((HashMap)request.getAttribute(PRESET_VALUES));
            }
            fp.addPresetValue(AddNewSubjectServlet.INPUT_ENROLLMENT_DATE, todayFormatted);
            fp.addPresetValue(AddNewSubjectServlet.INPUT_EVENT_START_DATE, todayFormatted);
            setPresetValues(fp.getPresetValues());
        }

        request.setAttribute("closeInfoShowIcons", true);
        if (fp.getString("navBar").equals("yes") && fp.getString("findSubjects_f_studySubject.label").trim().length() > 0) {
            StudySubjectBean studySubject = getStudySubjectDAO().findByLabelAndStudy(fp.getString("findSubjects_f_studySubject.label"), currentStudy);
            if (studySubject.getId() > 0) {
                request.setAttribute("id", Integer.toString(studySubject.getId()));
                forwardPage(Page.VIEW_STUDY_SUBJECT_SERVLET);
            } else {
                createTable();
            }
        } else {
            createTable();
        }

    }

    private void createTable() {

        ListStudySubjectTableFactory factory = new ListStudySubjectTableFactory(showMoreLink);
        factory.setStudyEventDefinitionDao(getStudyEventDefinitionDao());
        factory.setSubjectDAO(getSubjectDAO());
        factory.setStudySubjectDAO(getStudySubjectDAO());
        factory.setStudyEventDAO(getStudyEventDAO());
        factory.setStudyBean(currentStudy);
        factory.setStudyGroupClassDAO(getStudyGroupClassDAO());
        factory.setSubjectGroupMapDAO(getSubjectGroupMapDAO());
        factory.setStudyDAO(getStudyDAO());
        factory.setCurrentRole(currentRole);
        factory.setCurrentUser(ub);
        factory.setEventCRFDAO(getEventCRFDAO());
        factory.setEventDefintionCRFDAO(getEventDefinitionCRFDAO());
        factory.setStudyGroupDAO(getStudyGroupDAO());
        String findSubjectsHtml = factory.createTable(request, response).render();
        request.setAttribute("findSubjectsHtml", findSubjectsHtml);
        // A. Hamid.
        // For event definitions and group class list in the add subject popup
        request.setAttribute("allDefsArray", super.getEventDefinitionsByCurrentStudy());
        request.setAttribute("studyGroupClasses", super.getStudyGroupClassesByCurrentStudy());
        FormDiscrepancyNotes discNotes = new FormDiscrepancyNotes();
        session.setAttribute(AddNewSubjectServlet.FORM_DISCREPANCY_NOTES_NAME, discNotes);

        forwardPage(Page.LIST_STUDY_SUBJECTS);

    }

    @Override
    protected String getAdminServlet() {
        return SecureController.ADMIN_SERVLET_CODE;
    }

    public StudyEventDefinitionDAO getStudyEventDefinitionDao() {
        studyEventDefinitionDAO = studyEventDefinitionDAO == null ? new StudyEventDefinitionDAO(sm.getDataSource()) : studyEventDefinitionDAO;
        return studyEventDefinitionDAO;
    }

    public SubjectDAO getSubjectDAO() {
        subjectDAO = this.subjectDAO == null ? new SubjectDAO(sm.getDataSource()) : subjectDAO;
        return subjectDAO;
    }

    public StudySubjectDAO getStudySubjectDAO() {
        studySubjectDAO = this.studySubjectDAO == null ? new StudySubjectDAO(sm.getDataSource()) : studySubjectDAO;
        return studySubjectDAO;
    }

    public StudyGroupClassDAO getStudyGroupClassDAO() {
        studyGroupClassDAO = this.studyGroupClassDAO == null ? new StudyGroupClassDAO(sm.getDataSource()) : studyGroupClassDAO;
        return studyGroupClassDAO;
    }

    public SubjectGroupMapDAO getSubjectGroupMapDAO() {
        subjectGroupMapDAO = this.subjectGroupMapDAO == null ? new SubjectGroupMapDAO(sm.getDataSource()) : subjectGroupMapDAO;
        return subjectGroupMapDAO;
    }

    public StudyEventDAO getStudyEventDAO() {
        studyEventDAO = this.studyEventDAO == null ? new StudyEventDAO(sm.getDataSource()) : studyEventDAO;
        return studyEventDAO;
    }

    public StudyDAO getStudyDAO() {
        studyDAO = this.studyDAO == null ? new StudyDAO(sm.getDataSource()) : studyDAO;
        return studyDAO;
    }

    public EventCRFDAO getEventCRFDAO() {
        eventCRFDAO = this.eventCRFDAO == null ? new EventCRFDAO(sm.getDataSource()) : eventCRFDAO;
        return eventCRFDAO;
    }

    public EventDefinitionCRFDAO getEventDefinitionCRFDAO() {
        eventDefintionCRFDAO = this.eventDefintionCRFDAO == null ? new EventDefinitionCRFDAO(sm.getDataSource()) : eventDefintionCRFDAO;
        return eventDefintionCRFDAO;
    }

    public StudyGroupDAO getStudyGroupDAO() {
        studyGroupDAO = this.studyGroupDAO == null ? new StudyGroupDAO(sm.getDataSource()) : studyGroupDAO;
        return studyGroupDAO;
    }

}
