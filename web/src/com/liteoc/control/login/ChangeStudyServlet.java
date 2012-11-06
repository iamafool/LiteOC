/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package com.liteoc.control.login;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import com.liteoc.bean.core.Role;
import com.liteoc.bean.core.Status;
import com.liteoc.bean.login.StudyUserRoleBean;
import com.liteoc.bean.managestudy.StudyBean;
import com.liteoc.bean.service.StudyParameterValueBean;
import com.liteoc.control.SpringServletAccess;
import com.liteoc.control.admin.EventStatusStatisticsTableFactory;
import com.liteoc.control.admin.SiteStatisticsTableFactory;
import com.liteoc.control.admin.StudyStatisticsTableFactory;
import com.liteoc.control.admin.StudySubjectStatusStatisticsTableFactory;
import com.liteoc.control.core.SecureController;
import com.liteoc.control.form.FormProcessor;
import com.liteoc.control.form.Validator;
import com.liteoc.control.submit.ListStudySubjectTableFactory;
import com.liteoc.core.form.StringUtil;
import com.liteoc.dao.login.UserAccountDAO;
import com.liteoc.dao.managestudy.DiscrepancyNoteDAO;
import com.liteoc.dao.managestudy.EventDefinitionCRFDAO;
import com.liteoc.dao.managestudy.StudyDAO;
import com.liteoc.dao.managestudy.StudyEventDAO;
import com.liteoc.dao.managestudy.StudyEventDefinitionDAO;
import com.liteoc.dao.managestudy.StudyGroupClassDAO;
import com.liteoc.dao.managestudy.StudyGroupDAO;
import com.liteoc.dao.managestudy.StudySubjectDAO;
import com.liteoc.dao.service.StudyConfigService;
import com.liteoc.dao.service.StudyParameterValueDAO;
import com.liteoc.dao.submit.EventCRFDAO;
import com.liteoc.dao.submit.SubjectDAO;
import com.liteoc.dao.submit.SubjectGroupMapDAO;
import com.liteoc.view.Page;
import com.liteoc.web.InsufficientPermissionException;
import com.liteoc.web.table.sdv.SDVUtil;

/**
 * @author jxu
 *
 * Processes the request of changing current study
 */
public class ChangeStudyServlet extends SecureController {
    /**
     * Checks whether the user has the correct privilege
     */

    Locale locale;
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
    private DiscrepancyNoteDAO discrepancyNoteDAO;

    // < ResourceBundlerestext;

    @Override
    public void mayProceed() throws InsufficientPermissionException {

        locale = request.getLocale();
        // < restext =
        // ResourceBundle.getBundle("com.liteoc.i18n.notes",locale);

    }

    @Override
    public void processRequest() throws Exception {

        String action = request.getParameter("action");// action sent by user
        UserAccountDAO udao = new UserAccountDAO(sm.getDataSource());
        StudyDAO sdao = new StudyDAO(sm.getDataSource());

        ArrayList studies = udao.findStudyByUser(ub.getName(), (ArrayList) sdao.findAll());
        request.setAttribute("siteRoleMap", Role.siteRoleMap);
        request.setAttribute("studyRoleMap", Role.studyRoleMap);
        if(request.getAttribute("label")!=null) {
            String label = (String) request.getAttribute("label");
            if(label.length()>0) {
                request.setAttribute("label", label);
            }
        }

        ArrayList validStudies = new ArrayList();
        for (int i = 0; i < studies.size(); i++) {
            StudyUserRoleBean sr = (StudyUserRoleBean) studies.get(i);
            StudyBean study = (StudyBean) sdao.findByPK(sr.getStudyId());
            if (study != null && study.getStatus().equals(Status.PENDING)) {
                sr.setStatus(study.getStatus());
            }
            validStudies.add(sr);
        }

        
        if (StringUtil.isBlank(action)) {
            request.setAttribute("studies", validStudies);

            forwardPage(Page.CHANGE_STUDY);
        } else {
            if ("confirm".equalsIgnoreCase(action)) {
                logger.info("confirm");
                confirmChangeStudy(studies);

            } else if ("submit".equalsIgnoreCase(action)) {
                logger.info("submit");
                changeStudy();
            }
        }

    }

    private void confirmChangeStudy(ArrayList studies) throws Exception {
        Validator v = new Validator(request);
        FormProcessor fp = new FormProcessor(request);
        v.addValidation("studyId", Validator.IS_AN_INTEGER);

        errors = v.validate();

        if (!errors.isEmpty()) {
            request.setAttribute("studies", studies);
            forwardPage(Page.CHANGE_STUDY);
        } else {
            int studyId = fp.getInt("studyId");
            logger.info("new study id:" + studyId);
            for (int i = 0; i < studies.size(); i++) {
                StudyUserRoleBean studyWithRole = (StudyUserRoleBean) studies.get(i);
                if (studyWithRole.getStudyId() == studyId) {
                    request.setAttribute("studyId", new Integer(studyId));
                    session.setAttribute("studyWithRole", studyWithRole);
                    request.setAttribute("currentStudy", currentStudy);
                    forwardPage(Page.CHANGE_STUDY_CONFIRM);
                    return;
                    
                }
            }
            addPageMessage(restext.getString("no_study_selected"));
            
            forwardPage(Page.CHANGE_STUDY);
        }
    }

    private void changeStudy() throws Exception {
        Validator v = new Validator(request);
        FormProcessor fp = new FormProcessor(request);
        int studyId = fp.getInt("studyId");
        int prevStudyId = currentStudy.getId();

        StudyDAO sdao = new StudyDAO(sm.getDataSource());
        StudyBean current = (StudyBean) sdao.findByPK(studyId);

        // reset study parameters -jxu 02/09/2007
        StudyParameterValueDAO spvdao = new StudyParameterValueDAO(sm.getDataSource());

        ArrayList studyParameters = spvdao.findParamConfigByStudy(current);
        current.setStudyParameters(studyParameters);
        int parentStudyId = currentStudy.getParentStudyId()>0?currentStudy.getParentStudyId():currentStudy.getId();
        StudyParameterValueBean parentSPV = spvdao.findByHandleAndStudy(parentStudyId, "subjectIdGeneration");
        current.getStudyParameterConfig().setSubjectIdGeneration(parentSPV.getValue());
        String idSetting = current.getStudyParameterConfig().getSubjectIdGeneration();
        if (idSetting.equals("auto editable") || idSetting.equals("auto non-editable")) {
            int nextLabel = this.getStudySubjectDAO().findTheGreatestLabel() + 1;
            request.setAttribute("label", Integer.toString(nextLabel));
        }

        StudyConfigService scs = new StudyConfigService(sm.getDataSource());
        if (current.getParentStudyId() <= 0) {// top study
            scs.setParametersForStudy(current);

        } else {
            // YW <<
            if (current.getParentStudyId() > 0) {
                current.setParentStudyName(((StudyBean) sdao.findByPK(current.getParentStudyId())).getName());

            }
            // YW 06-12-2007>>
            scs.setParametersForSite(current);

        }
        if (current.getStatus().equals(Status.DELETED) || current.getStatus().equals(Status.AUTO_DELETED)) {
            session.removeAttribute("studyWithRole");
            addPageMessage(restext.getString("study_choosed_removed_restore_first"));
        } else {
            session.setAttribute("study", current);
            currentStudy = current;
            // change user's active study id
            UserAccountDAO udao = new UserAccountDAO(sm.getDataSource());
            ub.setActiveStudyId(current.getId());
            ub.setUpdater(ub);
            ub.setUpdatedDate(new java.util.Date());
            udao.update(ub);

            if (current.getParentStudyId() > 0) {
                /*
                 * The Role decription will be set depending on whether the user
                 * logged in at study lever or site level. issue-2422
                 */
                List roles = Role.toArrayList();
                for (Iterator it = roles.iterator(); it.hasNext();) {
                    Role role = (Role) it.next();
                    switch (role.getId()) {
                    case 2:
                        role.setDescription("site_Study_Coordinator");
                        break;
                    case 3:
                        role.setDescription("site_Study_Director");
                        break;
                    case 4:
                        role.setDescription("site_investigator");
                        break;
                    case 5:
                        role.setDescription("site_Data_Entry_Person");
                        break;
                    case 6:
                        role.setDescription("site_monitor");
                        break;
                    default:
                        // logger.info("No role matched when setting role description");
                    }
                }
            } else {
                /*
                 * If the current study is a site, we will change the role
                 * description. issue-2422
                 */
                List roles = Role.toArrayList();
                for (Iterator it = roles.iterator(); it.hasNext();) {
                    Role role = (Role) it.next();
                    switch (role.getId()) {
                    case 2:
                        role.setDescription("Study_Coordinator");
                        break;
                    case 3:
                        role.setDescription("Study_Director");
                        break;
                    case 4:
                        role.setDescription("investigator");
                        break;
                    case 5:
                        role.setDescription("Data_Entry_Person");
                        break;
                    case 6:
                        role.setDescription("monitor");
                        break;
                    default:
                        // logger.info("No role matched when setting role description");
                    }
                }
            }

            currentRole = (StudyUserRoleBean) session.getAttribute("studyWithRole");
            session.setAttribute("userRole", currentRole);
            session.removeAttribute("studyWithRole");
            addPageMessage(restext.getString("current_study_changed_succesfully"));
        }
        ub.incNumVisitsToMainMenu();
        // YW 2-18-2008, if study has been really changed <<
        if (prevStudyId != studyId) {
            session.removeAttribute("eventsForCreateDataset");
        }
        request.setAttribute("studyJustChanged", "yes");
        // YW >>

        //Integer assignedDiscrepancies = getDiscrepancyNoteDAO().countAllItemDataByStudyAndUser(currentStudy, ub);
        Integer assignedDiscrepancies = getDiscrepancyNoteDAO().getViewNotesCountWithFilter(" AND dn.assigned_user_id ="
                + ub.getId() + " AND (dn.resolution_status_id=1 OR dn.resolution_status_id=2 OR dn.resolution_status_id=3)", currentStudy);
        request.setAttribute("assignedDiscrepancies", assignedDiscrepancies == null ? 0 : assignedDiscrepancies);

        if (currentRole.isInvestigator() || currentRole.isResearchAssistant()) {
            setupListStudySubjectTable();
        }
        if (currentRole.isMonitor()) {
            setupSubjectSDVTable();
        } else if (currentRole.isCoordinator() || currentRole.isDirector()) {
            if (currentStudy.getStatus().isPending()) {
                response.sendRedirect(request.getContextPath() + Page.MANAGE_STUDY_MODULE);
                return;
            }
            setupStudySiteStatisticsTable();
            setupSubjectEventStatusStatisticsTable();
            setupStudySubjectStatusStatisticsTable();
            if (currentStudy.getParentStudyId() == 0) {
                setupStudyStatisticsTable();
            }

        }

        forwardPage(Page.MENU);

    }

    private void setupSubjectSDVTable() {

        request.setAttribute("studyId", currentStudy.getId());
        String sdvMatrix = getSDVUtil().renderEventCRFTableWithLimit(request, currentStudy.getId(), "");
        request.setAttribute("sdvMatrix", sdvMatrix);
    }

    private void setupStudySubjectStatusStatisticsTable() {

        StudySubjectStatusStatisticsTableFactory factory = new StudySubjectStatusStatisticsTableFactory();
        factory.setStudySubjectDao(getStudySubjectDAO());
        factory.setCurrentStudy(currentStudy);
        factory.setStudyDao(getStudyDAO());
        String studySubjectStatusStatistics = factory.createTable(request, response).render();
        request.setAttribute("studySubjectStatusStatistics", studySubjectStatusStatistics);
    }

    private void setupSubjectEventStatusStatisticsTable() {

        EventStatusStatisticsTableFactory factory = new EventStatusStatisticsTableFactory();
        factory.setStudySubjectDao(getStudySubjectDAO());
        factory.setCurrentStudy(currentStudy);
        factory.setStudyEventDao(getStudyEventDAO());
        factory.setStudyDao(getStudyDAO());
        String subjectEventStatusStatistics = factory.createTable(request, response).render();
        request.setAttribute("subjectEventStatusStatistics", subjectEventStatusStatistics);
    }

    private void setupStudySiteStatisticsTable() {

        SiteStatisticsTableFactory factory = new SiteStatisticsTableFactory();
        factory.setStudySubjectDao(getStudySubjectDAO());
        factory.setCurrentStudy(currentStudy);
        factory.setStudyDao(getStudyDAO());
        String studySiteStatistics = factory.createTable(request, response).render();
        request.setAttribute("studySiteStatistics", studySiteStatistics);

    }

    private void setupStudyStatisticsTable() {

        StudyStatisticsTableFactory factory = new StudyStatisticsTableFactory();
        factory.setStudySubjectDao(getStudySubjectDAO());
        factory.setCurrentStudy(currentStudy);
        factory.setStudyDao(getStudyDAO());
        String studyStatistics = factory.createTable(request, response).render();
        request.setAttribute("studyStatistics", studyStatistics);

    }

    private void setupListStudySubjectTable() {

        ListStudySubjectTableFactory factory = new ListStudySubjectTableFactory(true);
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

    public DiscrepancyNoteDAO getDiscrepancyNoteDAO() {
        discrepancyNoteDAO = this.discrepancyNoteDAO == null ? new DiscrepancyNoteDAO(sm.getDataSource()) : discrepancyNoteDAO;
        return discrepancyNoteDAO;
    }

    public SDVUtil getSDVUtil() {
        return (SDVUtil) SpringServletAccess.getApplicationContext(context).getBean("sdvUtil");
    }

}
