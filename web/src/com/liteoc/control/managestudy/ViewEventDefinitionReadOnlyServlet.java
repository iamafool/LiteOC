/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package com.liteoc.control.managestudy;


import com.liteoc.bean.admin.CRFBean;
import com.liteoc.bean.core.Status;
import com.liteoc.bean.managestudy.EventDefinitionCRFBean;
import com.liteoc.bean.managestudy.StudyEventDefinitionBean;
import com.liteoc.bean.submit.CRFVersionBean;
import com.liteoc.control.form.FormProcessor;
import com.liteoc.dao.admin.CRFDAO;
import com.liteoc.dao.managestudy.EventDefinitionCRFDAO;
import com.liteoc.dao.managestudy.StudyEventDefinitionDAO;
import com.liteoc.dao.submit.CRFVersionDAO;
import com.liteoc.view.Page;

import java.util.ArrayList;

/**
 * View the details of a study event definition
 *
 * @author jxu
 *
 */
public class ViewEventDefinitionReadOnlyServlet extends ViewEventDefinitionServlet {

    @Override
    public void processRequest() throws Exception {

        StudyEventDefinitionDAO sdao = new StudyEventDefinitionDAO(sm.getDataSource());
        FormProcessor fp = new FormProcessor(request);
        int defId = fp.getInt("id", true);

        if (defId == 0) {
            addPageMessage(respage.getString("please_choose_a_definition_to_view"));
            forwardPage(Page.LIST_DEFINITION_SERVLET);
        } else {
            // definition id
            StudyEventDefinitionBean sed = (StudyEventDefinitionBean) sdao.findByPK(defId);

            EventDefinitionCRFDAO edao = new EventDefinitionCRFDAO(sm.getDataSource());
            ArrayList eventDefinitionCRFs = (ArrayList) edao.findAllByDefinition(this.currentStudy, defId);

            CRFVersionDAO cvdao = new CRFVersionDAO(sm.getDataSource());
            CRFDAO cdao = new CRFDAO(sm.getDataSource());

            for (int i = 0; i < eventDefinitionCRFs.size(); i++) {
                EventDefinitionCRFBean edc = (EventDefinitionCRFBean) eventDefinitionCRFs.get(i);
                ArrayList versions = (ArrayList) cvdao.findAllByCRF(edc.getCrfId());
                edc.setVersions(versions);
                CRFBean crf = (CRFBean) cdao.findByPK(edc.getCrfId());
                // edc.setCrfLabel(crf.getLabel());
                edc.setCrfName(crf.getName());
                // to show/hide edit action on jsp page
                if (crf.getStatus().equals(Status.AVAILABLE)) {
                    edc.setOwner(crf.getOwner());
                }

                CRFVersionBean defaultVersion = (CRFVersionBean) cvdao.findByPK(edc.getDefaultVersionId());
                edc.setDefaultVersionName(defaultVersion.getName());
            }

            request.setAttribute("definition", sed);
            request.setAttribute("eventDefinitionCRFs", eventDefinitionCRFs);
            request.setAttribute("defSize", new Integer(eventDefinitionCRFs.size()));
            // request.setAttribute("eventDefinitionCRFs", new
            // ArrayList(tm.values()));
            forwardPage(Page.VIEW_EVENT_DEFINITION_READONLY);
        }

    }

}
