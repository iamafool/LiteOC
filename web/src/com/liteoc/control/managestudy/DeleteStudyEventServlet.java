package com.liteoc.control.managestudy;

/**
 * Created by IntelliJ IDEA.
 * User: bads
 * Date: Mar 24, 2010
 * Time: 8:33:54 PM
 * To change this template use File | Settings | File Templates.
 */










import com.liteoc.bean.core.Role;
import com.liteoc.bean.core.SubjectEventStatus;
import com.liteoc.bean.managestudy.DisplayStudyEventBean;
import com.liteoc.bean.managestudy.StudyBean;
import com.liteoc.bean.managestudy.StudyEventBean;
import com.liteoc.bean.managestudy.StudyEventDefinitionBean;
import com.liteoc.bean.managestudy.StudySubjectBean;
import com.liteoc.control.core.SecureController;
import com.liteoc.control.form.FormProcessor;
import com.liteoc.core.EmailEngine;
import com.liteoc.dao.managestudy.EventDefinitionCRFDAO;
import com.liteoc.dao.managestudy.StudyDAO;
import com.liteoc.dao.managestudy.StudyEventDAO;
import com.liteoc.dao.managestudy.StudyEventDefinitionDAO;
import com.liteoc.dao.managestudy.StudySubjectDAO;
import com.liteoc.dao.submit.EventCRFDAO;
import com.liteoc.view.Page;
import com.liteoc.web.InsufficientPermissionException;


import java.util.ArrayList;
import java.util.Date;


public class DeleteStudyEventServlet extends SecureController{
    @Override
    public void mayProceed() throws InsufficientPermissionException {
        checkStudyLocked(Page.LIST_STUDY_SUBJECTS, respage.getString("current_study_locked"));
        checkStudyFrozen(Page.LIST_STUDY_SUBJECTS, respage.getString("current_study_frozen"));
        
        if (ub.isSysAdmin()) {
            return;
        }

        if (currentRole.getRole().equals(Role.STUDYDIRECTOR) || currentRole.getRole().equals(Role.COORDINATOR)) {
            return;
        }

        addPageMessage(respage.getString("no_have_correct_privilege_current_study") + respage.getString("change_study_contact_sysadmin"));
        throw new InsufficientPermissionException(Page.MENU_SERVLET, resexception.getString("not_study_director"), "1");

    }

    @Override
    public void processRequest() throws Exception {
        FormProcessor fp = new FormProcessor(request);
        int studyEventId = fp.getInt("id");// studyEventId
        int studySubId = fp.getInt("studySubId");// studySubjectId

        StudyEventDAO sedao = new StudyEventDAO(sm.getDataSource());
        StudySubjectDAO subdao = new StudySubjectDAO(sm.getDataSource());

        if (studyEventId == 0) {
            addPageMessage(respage.getString("please_choose_a_SE_to_remove"));
            request.setAttribute("id", Integer.toString(studySubId));
            forwardPage(Page.VIEW_STUDY_SUBJECT_SERVLET);
        } else {

            StudyEventBean event = (StudyEventBean) sedao.findByPK(studyEventId);

            StudySubjectBean studySub = (StudySubjectBean) subdao.findByPK(studySubId);
            request.setAttribute("studySub", studySub);

            StudyEventDefinitionDAO seddao = new StudyEventDefinitionDAO(sm.getDataSource());
            StudyEventDefinitionBean sed = (StudyEventDefinitionBean) seddao.findByPK(event.getStudyEventDefinitionId());
            event.setStudyEventDefinition(sed);

            StudyDAO studydao = new StudyDAO(sm.getDataSource());
            StudyBean study = (StudyBean) studydao.findByPK(studySub.getStudyId());
            request.setAttribute("study", study);

            String action = request.getParameter("action");
            if ("confirm".equalsIgnoreCase(action)) {

                EventDefinitionCRFDAO edcdao = new EventDefinitionCRFDAO(sm.getDataSource());
                // find all crfs in the definition
                ArrayList eventDefinitionCRFs = (ArrayList) edcdao.findAllByEventDefinitionId(study, sed.getId());

                EventCRFDAO ecdao = new EventCRFDAO(sm.getDataSource());
                // construct info needed on view study event page
                DisplayStudyEventBean de = new DisplayStudyEventBean();
                de.setStudyEvent(event);

                request.setAttribute("displayEvent", de);
//                request.setAttribute("crfs", eventDefinitionCRFs);

                forwardPage(Page.DELETE_STUDY_EVENT);
            } else {
                logger.info("submit to delete the event from study");
                // delete event from study

                event.setSubjectEventStatus(SubjectEventStatus.NOT_SCHEDULED);
                event.setUpdater(ub);
                event.setUpdatedDate(new Date());
                sedao.update(event);
                String emailBody =
                    respage.getString("the_event") + " " + event.getStudyEventDefinition().getName() + " "
                        + respage.getString("has_been_removed_from_the_subject_record_for") + " " + studySub.getLabel() + " "
                        + respage.getString("in_the_study") + " " + study.getName() + ".";

                addPageMessage(emailBody);
//                sendEmail(emailBody);
                request.setAttribute("id", Integer.toString(studySubId));
                forwardPage(Page.VIEW_STUDY_SUBJECT_SERVLET);
            }
        }
    }


    /**
     * Send email to director and administrator
     *
     */
    private void sendEmail(String emailBody) throws Exception {

        logger.info("Sending email...");
        // to study director
        sendEmail(ub.getEmail().trim(), respage.getString("remove_event_from_study"), emailBody, false);
        sendEmail(EmailEngine.getAdminEmail(), respage.getString("remove_event_from_study"), emailBody, false, false);
        logger.info("Sending email done..");
    }
    
}
