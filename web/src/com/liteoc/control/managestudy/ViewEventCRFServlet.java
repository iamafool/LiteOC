/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package com.liteoc.control.managestudy;


import com.liteoc.bean.admin.CRFBean;
import com.liteoc.bean.managestudy.StudySubjectBean;
import com.liteoc.bean.submit.DisplayItemBean;
import com.liteoc.bean.submit.EventCRFBean;
import com.liteoc.bean.submit.ItemBean;
import com.liteoc.bean.submit.ItemDataBean;
import com.liteoc.bean.submit.ItemFormMetadataBean;
import com.liteoc.bean.submit.SectionBean;
import com.liteoc.control.core.SecureController;
import com.liteoc.control.form.FormProcessor;
import com.liteoc.dao.admin.CRFDAO;
import com.liteoc.dao.managestudy.StudySubjectDAO;
import com.liteoc.dao.submit.EventCRFDAO;
import com.liteoc.dao.submit.ItemDAO;
import com.liteoc.dao.submit.ItemDataDAO;
import com.liteoc.dao.submit.ItemFormMetadataDAO;
import com.liteoc.dao.submit.SectionDAO;
import com.liteoc.view.Page;
import com.liteoc.web.InsufficientPermissionException;

import java.util.ArrayList;

/**
 * @author jxu
 *
 * Views the detail of an event CRF
 */
public class ViewEventCRFServlet extends SecureController {
    /**
     *
     */
    @Override
    public void mayProceed() throws InsufficientPermissionException {

    }

    @Override
    public void processRequest() throws Exception {
        FormProcessor fp = new FormProcessor(request);
        int eventCRFId = fp.getInt("id", true);
        int studySubId = fp.getInt("studySubId", true);

        StudySubjectDAO subdao = new StudySubjectDAO(sm.getDataSource());
        EventCRFDAO ecdao = new EventCRFDAO(sm.getDataSource());
        ItemDataDAO iddao = new ItemDataDAO(sm.getDataSource());
        ItemDAO idao = new ItemDAO(sm.getDataSource());
        ItemFormMetadataDAO ifmdao = new ItemFormMetadataDAO(sm.getDataSource());
        CRFDAO cdao = new CRFDAO(sm.getDataSource());
        SectionDAO secdao = new SectionDAO(sm.getDataSource());

        if (eventCRFId == 0) {
            addPageMessage(respage.getString("please_choose_an_event_CRF_to_view"));
            forwardPage(Page.LIST_STUDY_SUBJECTS);
        } else {
            StudySubjectBean studySub = (StudySubjectBean) subdao.findByPK(studySubId);
            request.setAttribute("studySub", studySub);

            EventCRFBean eventCRF = (EventCRFBean) ecdao.findByPK(eventCRFId);
            CRFBean crf = cdao.findByVersionId(eventCRF.getCRFVersionId());
            request.setAttribute("crf", crf);

            ArrayList sections = secdao.findAllByCRFVersionId(eventCRF.getCRFVersionId());
            for (int j = 0; j < sections.size(); j++) {
                SectionBean section = (SectionBean) sections.get(j);
                ArrayList itemData = iddao.findAllByEventCRFId(eventCRFId);

                ArrayList displayItemData = new ArrayList();
                for (int i = 0; i < itemData.size(); i++) {
                    ItemDataBean id = (ItemDataBean) itemData.get(i);
                    DisplayItemBean dib = new DisplayItemBean();
                    ItemBean item = (ItemBean) idao.findByPK(id.getItemId());
                    ItemFormMetadataBean ifm = ifmdao.findByItemIdAndCRFVersionId(item.getId(), eventCRF.getCRFVersionId());

                    item.setItemMeta(ifm);
                    dib.setItem(item);
                    dib.setData(id);
                    dib.setMetadata(ifm);
                    displayItemData.add(dib);
                }
                section.setItems(displayItemData);
            }

            request.setAttribute("sections", sections);
            request.setAttribute("studySubId", Integer.toString(studySubId));
            forwardPage(Page.VIEW_EVENT_CRF);
        }
    }

}
