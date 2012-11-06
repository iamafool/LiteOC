/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package com.liteoc.control.admin;


import com.liteoc.bean.admin.CRFBean;
import com.liteoc.bean.core.Role;
import com.liteoc.bean.core.Status;
import com.liteoc.bean.managestudy.EventDefinitionCRFBean;
import com.liteoc.bean.managestudy.StudyEventBean;
import com.liteoc.bean.managestudy.StudyEventDefinitionBean;
import com.liteoc.bean.submit.CRFVersionBean;
import com.liteoc.bean.submit.EventCRFBean;
import com.liteoc.bean.submit.ItemDataBean;
import com.liteoc.bean.submit.SectionBean;
import com.liteoc.control.core.SecureController;
import com.liteoc.control.form.FormProcessor;
import com.liteoc.dao.admin.CRFDAO;
import com.liteoc.dao.managestudy.EventDefinitionCRFDAO;
import com.liteoc.dao.managestudy.StudyEventDAO;
import com.liteoc.dao.managestudy.StudyEventDefinitionDAO;
import com.liteoc.dao.submit.CRFVersionDAO;
import com.liteoc.dao.submit.EventCRFDAO;
import com.liteoc.dao.submit.ItemDataDAO;
import com.liteoc.dao.submit.SectionDAO;
import com.liteoc.view.Page;
import com.liteoc.web.InsufficientPermissionException;

import java.util.ArrayList;
import java.util.Date;

/**
 * Removes a crf
 *
 * @author jxu
 */
public class RemoveCRFServlet extends SecureController {
    /**
     *
     */
    @Override
    public void mayProceed() throws InsufficientPermissionException {
        if (ub.isSysAdmin()) {
            return;
        }
        if (currentRole.getRole().equals(Role.STUDYDIRECTOR) || currentRole.getRole().equals(Role.COORDINATOR)) {
            return;
        }

        addPageMessage(respage.getString("no_have_correct_privilege_current_study") + respage.getString("change_study_contact_sysadmin"));
        throw new InsufficientPermissionException(Page.CRF_LIST_SERVLET, resexception.getString("not_admin"), "1");

    }

    @Override
    public void processRequest() throws Exception {

        CRFDAO cdao = new CRFDAO(sm.getDataSource());
        CRFVersionDAO cvdao = new CRFVersionDAO(sm.getDataSource());
        FormProcessor fp = new FormProcessor(request);

        // checks which module the requests are from
        String module = fp.getString(MODULE);
        request.setAttribute(MODULE, module);

        int crfId = fp.getInt("id", true);

        String action = request.getParameter("action");
        if (crfId == 0) {
            addPageMessage(respage.getString("please_choose_a_CRF_to_remove"));
            forwardPage(Page.CRF_LIST_SERVLET);
        } else {
            CRFBean crf = (CRFBean) cdao.findByPK(crfId);
            ArrayList versions = cvdao.findAllByCRFId(crfId);
            crf.setVersions(versions);
            EventDefinitionCRFDAO edcdao = new EventDefinitionCRFDAO(sm.getDataSource());
            ArrayList edcs = (ArrayList) edcdao.findAllByCRF(crfId);

            SectionDAO secdao = new SectionDAO(sm.getDataSource());

            EventCRFDAO evdao = new EventCRFDAO(sm.getDataSource());
            ArrayList eventCRFs = evdao.findAllByCRF(crfId);
            StudyEventDAO seDao = new StudyEventDAO(sm.getDataSource());
            StudyEventDefinitionDAO sedDao = new StudyEventDefinitionDAO(sm.getDataSource());
            for (Object ecBean: eventCRFs) {
                StudyEventBean seBean = (StudyEventBean) seDao.findByPK(((EventCRFBean)ecBean).getStudyEventId());
                StudyEventDefinitionBean sedBean = (StudyEventDefinitionBean)sedDao.findByPK(seBean.getStudyEventDefinitionId());
                ((EventCRFBean)ecBean).setEventName(sedBean.getName());
            }
            if ("confirm".equalsIgnoreCase(action)) {
                request.setAttribute("crfToRemove", crf);
                request.setAttribute("eventCRFs", eventCRFs);
                forwardPage(Page.REMOVE_CRF);
            } else {
                logger.info("submit to remove the crf");
                crf.setStatus(Status.DELETED);
                crf.setUpdater(ub);
                crf.setUpdatedDate(new Date());
                cdao.update(crf);

                for (int i = 0; i < versions.size(); i++) {
                    CRFVersionBean version = (CRFVersionBean) versions.get(i);
                    if (!version.getStatus().equals(Status.DELETED)) {
                        version.setStatus(Status.AUTO_DELETED);
                        version.setUpdater(ub);
                        version.setUpdatedDate(new Date());
                        cvdao.update(version);

                        ArrayList sections = secdao.findAllByCRFVersionId(version.getId());
                        for (int j = 0; j < sections.size(); j++) {
                            SectionBean section = (SectionBean) sections.get(j);
                            if (!section.getStatus().equals(Status.DELETED)) {
                                section.setStatus(Status.AUTO_DELETED);
                                section.setUpdater(ub);
                                section.setUpdatedDate(new Date());
                                secdao.update(section);
                            }
                        }
                    }
                }

                for (int i = 0; i < edcs.size(); i++) {
                    EventDefinitionCRFBean edc = (EventDefinitionCRFBean) edcs.get(i);
                    if (!edc.getStatus().equals(Status.DELETED)) {
                        edc.setStatus(Status.AUTO_DELETED);
                        edc.setUpdater(ub);
                        edc.setUpdatedDate(new Date());
                        edcdao.update(edc);
                    }
                }

                ItemDataDAO idao = new ItemDataDAO(sm.getDataSource());
                for (int i = 0; i < eventCRFs.size(); i++) {
                    EventCRFBean eventCRF = (EventCRFBean) eventCRFs.get(i);
                    if (!eventCRF.getStatus().equals(Status.DELETED)) {
                        eventCRF.setStatus(Status.AUTO_DELETED);
                        eventCRF.setUpdater(ub);
                        eventCRF.setUpdatedDate(new Date());
                        evdao.update(eventCRF);

                        ArrayList items = idao.findAllByEventCRFId(eventCRF.getId());
                        for (int j = 0; j < items.size(); j++) {
                            ItemDataBean item = (ItemDataBean) items.get(j);
                            if (!item.getStatus().equals(Status.DELETED)) {
                                item.setStatus(Status.AUTO_DELETED);
                                item.setUpdater(ub);
                                item.setUpdatedDate(new Date());
                                idao.update(item);
                            }
                        }
                    }
                }

                addPageMessage(respage.getString("the_CRF") + crf.getName() + " " + respage.getString("has_been_removed_succesfully"));
                forwardPage(Page.CRF_LIST_SERVLET);

            }
        }

    }

    @Override
    protected String getAdminServlet() {
        return SecureController.ADMIN_SERVLET_CODE;
    }

}
