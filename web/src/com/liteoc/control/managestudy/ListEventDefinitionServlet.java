/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package com.liteoc.control.managestudy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

import com.liteoc.bean.admin.CRFBean;
import com.liteoc.bean.core.Role;
import com.liteoc.bean.core.Status;
import com.liteoc.bean.managestudy.EventDefinitionCRFBean;
import com.liteoc.bean.managestudy.StudyEventBean;
import com.liteoc.bean.managestudy.StudyEventDefinitionBean;
import com.liteoc.bean.submit.CRFVersionBean;
import com.liteoc.bean.submit.EventCRFBean;
import com.liteoc.bean.submit.ItemDataBean;
import com.liteoc.control.core.SecureController;
import com.liteoc.control.form.FormProcessor;
import com.liteoc.dao.admin.CRFDAO;
import com.liteoc.dao.login.UserAccountDAO;
import com.liteoc.dao.managestudy.EventDefinitionCRFDAO;
import com.liteoc.dao.managestudy.StudyEventDAO;
import com.liteoc.dao.managestudy.StudyEventDefinitionDAO;
import com.liteoc.dao.submit.CRFVersionDAO;
import com.liteoc.dao.submit.EventCRFDAO;
import com.liteoc.dao.submit.ItemDataDAO;
import com.liteoc.view.Page;
import com.liteoc.web.InsufficientPermissionException;
import com.liteoc.web.bean.EntityBeanTable;
import com.liteoc.web.bean.StudyEventDefinitionRow;

/**
 * Processes user reuqest to generate study event definition list
 *
 * @author jxu
 *
 */
public class ListEventDefinitionServlet extends SecureController {

    Locale locale;

    // < ResourceBundleresword, resworkflow, respage,resexception;

    /**
     * Checks whether the user has the correct privilege
     */
    @Override
    public void mayProceed() throws InsufficientPermissionException {
        session.removeAttribute("tmpCRFIdMap");
        session.removeAttribute("crfsWithVersion");
        session.removeAttribute("eventDefinitionCRFs");

        locale = request.getLocale();
        // < resword =
        // ResourceBundle.getBundle("com.liteoc.i18n.words",locale);
        // <
        // resexception=ResourceBundle.getBundle(
        // "com.liteoc.i18n.exceptions",locale);
        // < respage =
        // ResourceBundle.getBundle("com.liteoc.i18n.page_messages",
        // locale);
        // < resworkflow =
        //ResourceBundle.getBundle("com.liteoc.i18n.workflow",locale)
        // ;

        if (ub.isSysAdmin()) {
            return;
        }

        if (currentRole.getRole().equals(Role.STUDYDIRECTOR) || currentRole.getRole().equals(Role.COORDINATOR)) {
            return;
        }

        addPageMessage(respage.getString("no_have_correct_privilege_current_study") + respage.getString("change_study_contact_sysadmin"));
        throw new InsufficientPermissionException(Page.MANAGE_STUDY_SERVLET, resexception.getString("not_study_director"), "1");

    }

    /**
     * Processes the request
     */
    @Override
    public void processRequest() throws Exception {

        StudyEventDefinitionDAO edao = new StudyEventDefinitionDAO(sm.getDataSource());
        UserAccountDAO sdao = new UserAccountDAO(sm.getDataSource());
        EventDefinitionCRFDAO edcdao = new EventDefinitionCRFDAO(sm.getDataSource());
        CRFDAO crfDao = new CRFDAO(sm.getDataSource());
        CRFVersionDAO crfVersionDao = new CRFVersionDAO(sm.getDataSource());
        ArrayList seds = edao.findAllByStudy(currentStudy);

        // request.setAttribute("seds", seds);

        StudyEventDAO sedao = new StudyEventDAO(sm.getDataSource());
        EventCRFDAO ecdao = new EventCRFDAO(sm.getDataSource());
        ItemDataDAO iddao = new ItemDataDAO(sm.getDataSource());
        for (int i = 0; i < seds.size(); i++) {
            StudyEventDefinitionBean sed = (StudyEventDefinitionBean) seds.get(i);
            Collection eventDefinitionCRFlist = edcdao.findAllParentsByDefinition(sed.getId());
            Map crfWithDefaultVersion = new LinkedHashMap();
            for (Iterator it = eventDefinitionCRFlist.iterator(); it.hasNext();) {
                EventDefinitionCRFBean edcBean = (EventDefinitionCRFBean) it.next();
                CRFBean crfBean = (CRFBean) crfDao.findByPK(edcBean.getCrfId());
                CRFVersionBean crfVersionBean = (CRFVersionBean) crfVersionDao.findByPK(edcBean.getDefaultVersionId());
                logger.info("ED[" + sed.getName() + "]crf[" + crfBean.getName() + "]dv[" + crfVersionBean.getName() + "]");
                crfWithDefaultVersion.put(crfBean.getName(), crfVersionBean.getName());
            }
            sed.setCrfsWithDefaultVersion(crfWithDefaultVersion);
            logger.info("CRF size [" + sed.getCrfs().size() + "]");
            if (sed.getUpdater().getId() == 0) {
                sed.setUpdater(sed.getOwner());
                sed.setUpdatedDate(sed.getCreatedDate());
            }
            if (isPopulated(sed, sedao)) {
                sed.setPopulated(true);
            }
        }

        FormProcessor fp = new FormProcessor(request);
        EntityBeanTable table = fp.getEntityBeanTable();
        ArrayList allStudyRows = StudyEventDefinitionRow.generateRowsFromBeans(seds);

        String[] columns =
            { resword.getString("order"), resword.getString("name"), resword.getString("OID"), resword.getString("repeating"), resword.getString("type"),
                resword.getString("category"), resword.getString("populated"), resword.getString("date_created"), resword.getString("date_updated"),
                resword.getString("CRFs"), resword.getString("default_version"), resword.getString("actions") };
        table.setColumns(new ArrayList(Arrays.asList(columns)));
        // >> tbh #4169 09/2009
        table.hideColumnLink(2);
        table.hideColumnLink(3);
        table.hideColumnLink(4);
        table.hideColumnLink(6);
        table.hideColumnLink(7);
        table.hideColumnLink(8);
        table.hideColumnLink(9);
        table.hideColumnLink(10); // crfs, tbh
        table.hideColumnLink(11);
        table.hideColumnLink(12);
        // << tbh 09/2009
        table.setQuery("ListEventDefinition", new HashMap());
        // if (!currentStudy.getStatus().isLocked()) {
        // table.addLink(resworkflow.getString(
        // "create_a_new_study_event_definition"), "DefineStudyEvent");
        // }

        table.setRows(allStudyRows);
        
        table.setPaginated(false);
        table.computeDisplay();

        request.setAttribute("table", table);
        request.setAttribute("defSize", new Integer(seds.size()));

        if (request.getParameter("read") != null && request.getParameter("read").equals("true")) {
            request.setAttribute("readOnly", true);
        }

        forwardPage(Page.STUDY_EVENT_DEFINITION_LIST);
    }

    /**
     * Checked whether a definition is available to be locked
     *
     * @param sed
     * @return
     */
    private boolean isPopulated(StudyEventDefinitionBean sed, StudyEventDAO sedao) {
        /*
        // checks study event
        ArrayList events = (ArrayList) sedao.findAllByDefinition(sed.getId());
        for (int j = 0; j < events.size(); j++) {
            StudyEventBean event = (StudyEventBean) events.get(j);
            if (!event.getStatus().equals(Status.DELETED) && !event.getStatus().equals(Status.AUTO_DELETED)) {
                return true;
            }
        }
        return false;
        */
        if(sedao.countNotRemovedEvents(sed.getId())>0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Checked whether a definition is available to be locked
     *
     * @param sed
     * @return
     */
    private boolean isLockable(StudyEventDefinitionBean sed, StudyEventDAO sedao, EventCRFDAO ecdao, ItemDataDAO iddao) {

        // checks study event
        ArrayList events = (ArrayList) sedao.findAllByDefinition(sed.getId());
        for (int j = 0; j < events.size(); j++) {
            StudyEventBean event = (StudyEventBean) events.get(j);
            if (!(event.getStatus().equals(Status.AVAILABLE) || event.getStatus().equals(Status.DELETED))) {
                return false;
            }

            ArrayList eventCRFs = ecdao.findAllByStudyEvent(event);

            for (int k = 0; k < eventCRFs.size(); k++) {
                EventCRFBean eventCRF = (EventCRFBean) eventCRFs.get(k);
                if (!(eventCRF.getStatus().equals(Status.UNAVAILABLE) || eventCRF.getStatus().equals(Status.DELETED))) {
                    return false;
                }

                ArrayList itemDatas = iddao.findAllByEventCRFId(eventCRF.getId());
                for (int a = 0; a < itemDatas.size(); a++) {
                    ItemDataBean item = (ItemDataBean) itemDatas.get(a);
                    if (!(item.getStatus().equals(Status.UNAVAILABLE) || item.getStatus().equals(Status.DELETED))) {
                        return false;
                    }

                }
            }
        }

        return true;
    }

}
