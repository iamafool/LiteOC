/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package com.liteoc.control.admin;

import com.liteoc.bean.admin.CRFBean;
import com.liteoc.bean.managestudy.DiscrepancyNoteBean;
import com.liteoc.bean.managestudy.EventDefinitionCRFBean;
import com.liteoc.bean.managestudy.StudyBean;
import com.liteoc.bean.managestudy.StudyEventBean;
import com.liteoc.bean.managestudy.StudyEventDefinitionBean;
import com.liteoc.bean.managestudy.StudySubjectBean;
import com.liteoc.bean.submit.CRFVersionBean;
import com.liteoc.bean.submit.DisplayEventCRFBean;
import com.liteoc.bean.submit.EventCRFBean;
import com.liteoc.bean.submit.ItemDataBean;
import com.liteoc.control.core.SecureController;
import com.liteoc.control.form.FormProcessor;
import com.liteoc.dao.admin.CRFDAO;
import com.liteoc.dao.managestudy.DiscrepancyNoteDAO;
import com.liteoc.dao.managestudy.EventDefinitionCRFDAO;
import com.liteoc.dao.managestudy.StudyDAO;
import com.liteoc.dao.managestudy.StudyEventDAO;
import com.liteoc.dao.managestudy.StudyEventDefinitionDAO;
import com.liteoc.dao.managestudy.StudySubjectDAO;
import com.liteoc.dao.submit.CRFVersionDAO;
import com.liteoc.dao.submit.EventCRFDAO;
import com.liteoc.dao.submit.ItemDataDAO;
import com.liteoc.view.Page;
import com.liteoc.web.InsufficientPermissionException;

import java.util.ArrayList;

/**
 * @author jxu
 * 
 *         TODO To change the template for this generated type comment go to Window - Preferences - Java - Code Style - Code Templates
 */
public class DeleteEventCRFServlet extends SecureController {
    public static String STUDY_SUB_ID = "ssId";

    public static String EVENT_CRF_ID = "ecId";

    /**
     * 
     */
    @Override
    public void mayProceed() throws InsufficientPermissionException {
        if (ub.isSysAdmin()) {
            return;
        }
        addPageMessage(respage.getString("no_have_correct_privilege_current_study") + respage.getString("change_study_contact_sysadmin"));
        throw new InsufficientPermissionException(Page.LIST_STUDY_SUBJECTS, resexception.getString("not_admin"), "1");

    }

    @Override
    public void processRequest() throws Exception {
        FormProcessor fp = new FormProcessor(request);
        int studySubId = fp.getInt(STUDY_SUB_ID, true);
        int eventCRFId = fp.getInt(EVENT_CRF_ID);

        String action = request.getParameter("action");

        StudyEventDAO sedao = new StudyEventDAO(sm.getDataSource());
        StudySubjectDAO subdao = new StudySubjectDAO(sm.getDataSource());
        EventCRFDAO ecdao = new EventCRFDAO(sm.getDataSource());
        StudyDAO sdao = new StudyDAO(sm.getDataSource());

        if (eventCRFId == 0) {
            addPageMessage(respage.getString("please_choose_an_event_CRF_to_delete"));
            request.setAttribute("id", Integer.toString(studySubId));
            forwardPage(Page.VIEW_STUDY_SUBJECT_SERVLET);
        } else {
            EventCRFBean eventCRF = (EventCRFBean) ecdao.findByPK(eventCRFId);

            StudySubjectBean studySub = (StudySubjectBean) subdao.findByPK(studySubId);
            request.setAttribute("studySub", studySub);

            // construct info needed on view event crf page
            CRFDAO cdao = new CRFDAO(sm.getDataSource());
            CRFVersionDAO cvdao = new CRFVersionDAO(sm.getDataSource());

            int crfVersionId = eventCRF.getCRFVersionId();
            CRFBean cb = cdao.findByVersionId(crfVersionId);
            eventCRF.setCrf(cb);

            CRFVersionBean cvb = (CRFVersionBean) cvdao.findByPK(crfVersionId);
            eventCRF.setCrfVersion(cvb);

            // then get the definition so we can call
            // DisplayEventCRFBean.setFlags
            int studyEventId = eventCRF.getStudyEventId();

            StudyEventBean event = (StudyEventBean) sedao.findByPK(studyEventId);

            int studyEventDefinitionId = sedao.getDefinitionIdFromStudyEventId(studyEventId);
            StudyEventDefinitionDAO seddao = new StudyEventDefinitionDAO(sm.getDataSource());
            StudyEventDefinitionBean sed = (StudyEventDefinitionBean) seddao.findByPK(studyEventDefinitionId);
            event.setStudyEventDefinition(sed);
            request.setAttribute("event", event);

            EventDefinitionCRFDAO edcdao = new EventDefinitionCRFDAO(sm.getDataSource());

            StudyBean study = (StudyBean) sdao.findByPK(studySub.getStudyId());
            EventDefinitionCRFBean edc = edcdao.findByStudyEventDefinitionIdAndCRFId(study, studyEventDefinitionId, cb.getId());

            DisplayEventCRFBean dec = new DisplayEventCRFBean();
            dec.setEventCRF(eventCRF);
            dec.setFlags(eventCRF, ub, currentRole, edc.isDoubleEntry());

            // find all item data
            ItemDataDAO iddao = new ItemDataDAO(sm.getDataSource());
            DiscrepancyNoteDAO dnDao = new DiscrepancyNoteDAO(sm.getDataSource());
            ArrayList itemData = iddao.findAllByEventCRFId(eventCRF.getId());
            request.setAttribute("items", itemData);

            if ("confirm".equalsIgnoreCase(action)) {

                request.setAttribute("displayEventCRF", dec);

                forwardPage(Page.DELETE_EVENT_CRF);
            } else {
                logger.info("submit to delete the event CRF from event");
                // delete all the item data first
                for (int a = 0; a < itemData.size(); a++) {
                    ItemDataBean item = (ItemDataBean) itemData.get(a);
                    ArrayList discrepancyList = dnDao.findExistingNotesForItemData(item.getId());
                    iddao.deleteDnMap(item.getId());
                    for (int b = 0; b < discrepancyList.size(); b++) {
                        DiscrepancyNoteBean noteBean = (DiscrepancyNoteBean) discrepancyList.get(b);
                        dnDao.deleteNotes(noteBean.getId());
                    }
                    item.setUpdater(ub);
                    iddao.updateUser(item);
                    iddao.delete(item.getId());
                }
                // delete event crf
                ecdao.delete(eventCRF.getId());

                String emailBody =
                    respage.getString("the_event_CRF") + cb.getName() + respage.getString("has_been_deleted_from_the_event")
                        + event.getStudyEventDefinition().getName() + ".";

                addPageMessage(emailBody);
                // sendEmail(emailBody);
                request.setAttribute("id", Integer.toString(studySubId));
                forwardPage(Page.VIEW_STUDY_SUBJECT_SERVLET);
            }

        }
    }
}
