/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package com.liteoc.control.admin;






import com.liteoc.bean.managestudy.StudyBean;
import com.liteoc.bean.managestudy.StudyEventDefinitionBean;
import com.liteoc.control.core.SecureController;
import com.liteoc.control.form.FormProcessor;
import com.liteoc.control.submit.SubmitDataServlet;
import com.liteoc.dao.login.UserAccountDAO;
import com.liteoc.dao.managestudy.EventDefinitionCRFDAO;
import com.liteoc.dao.managestudy.StudyDAO;
import com.liteoc.dao.managestudy.StudyEventDefinitionDAO;
import com.liteoc.dao.managestudy.StudySubjectDAO;
import com.liteoc.dao.service.StudyConfigService;
import com.liteoc.view.Page;
import com.liteoc.web.InsufficientPermissionException;

import java.util.ArrayList;

/**
 * @author jxu
 * 
 * Processes the reuqest of 'view study details'
 */
public class ViewStudyServlet extends SecureController {
    /**
     * Checks whether the user has the correct privilege
     */
    @Override
    public void mayProceed() throws InsufficientPermissionException {
        if (ub.isSysAdmin()) {
            return;
        }

        if (SubmitDataServlet.mayViewData(ub, currentRole)) {
            return;
        }

        addPageMessage(respage.getString("no_have_correct_privilege_current_study") + respage.getString("change_study_contact_sysadmin"));
        throw new InsufficientPermissionException(Page.MENU_SERVLET, resexception.getString("not_admin"), "1");

    }

    @Override
    public void processRequest() throws Exception {

        StudyDAO sdao = new StudyDAO(sm.getDataSource());
        FormProcessor fp = new FormProcessor(request);
        int studyId = fp.getInt("id");
        if (studyId == 0) {
            addPageMessage(respage.getString("please_choose_a_study_to_view"));
            forwardPage(Page.STUDY_LIST_SERVLET);
        } else {
            if (currentStudy.getId() != studyId && currentStudy.getParentStudyId() != studyId) {
                checkRoleByUserAndStudy(ub, studyId, 0);
            }

            String viewFullRecords = fp.getString("viewFull");
            StudyBean study = (StudyBean) sdao.findByPK(studyId);


            StudyConfigService scs = new StudyConfigService(sm.getDataSource());
            study = scs.setParametersForStudy(study);

            request.setAttribute("studyToView", study);
            if ("yes".equalsIgnoreCase(viewFullRecords)) {
                UserAccountDAO udao = new UserAccountDAO(sm.getDataSource());
                StudySubjectDAO ssdao = new StudySubjectDAO(sm.getDataSource());
                ArrayList sites = new ArrayList();
                ArrayList userRoles = new ArrayList();
                ArrayList subjects = new ArrayList();
                if (this.currentStudy.getParentStudyId() > 0 && this.currentRole.getRole().getId() > 3) {
                    sites.add(this.currentStudy);
                    userRoles = udao.findAllUsersByStudy(currentStudy.getId());
                    subjects = ssdao.findAllByStudy(currentStudy);
                } else {
                    sites = (ArrayList) sdao.findAllByParent(studyId);
                    userRoles = udao.findAllUsersByStudy(studyId);
                    subjects = ssdao.findAllByStudy(study);
                }

                // find all subjects in the study, include ones in sites
                StudyEventDefinitionDAO seddao = new StudyEventDefinitionDAO(sm.getDataSource());
                EventDefinitionCRFDAO edcdao = new EventDefinitionCRFDAO(sm.getDataSource());
                // StudyEventDAO sedao = new StudyEventDAO(sm.getDataSource());

//                ArrayList displayStudySubs = new ArrayList();
//                for (int i = 0; i < subjects.size(); i++) {
//                    StudySubjectBean studySub = (StudySubjectBean) subjects.get(i);
//                    // find all events
//                    ArrayList events = sedao.findAllByStudySubject(studySub);
//
//                    // find all eventcrfs for each event
//                    EventCRFDAO ecdao = new EventCRFDAO(sm.getDataSource());
//
//                    DisplayStudySubjectBean dssb = new DisplayStudySubjectBean();
//                    dssb.setStudyEvents(events);
//                    dssb.setStudySubject(studySub);
//                    displayStudySubs.add(dssb);
//                }

                // find all events in the study, include ones in sites
                ArrayList definitions = seddao.findAllByStudy(study);

                for (int i = 0; i < definitions.size(); i++) {
                    StudyEventDefinitionBean def = (StudyEventDefinitionBean) definitions.get(i);
                    ArrayList crfs = (ArrayList) edcdao.findAllActiveParentsByEventDefinitionId(def.getId());
                    def.setCrfNum(crfs.size());

                }

                request.setAttribute("sitesToView", sites);
                request.setAttribute("siteNum", sites.size() + "");

                request.setAttribute("userRolesToView", userRoles);
                request.setAttribute("userNum", userRoles.size() + "");

                // request.setAttribute("subjectsToView", displayStudySubs);
                // request.setAttribute("subjectNum", subjects.size() + "");

                request.setAttribute("definitionsToView", definitions);
                request.setAttribute("defNum", definitions.size() + "");
                forwardPage(Page.VIEW_FULL_STUDY);

            } else {
                forwardPage(Page.VIEW_STUDY);
            }
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
