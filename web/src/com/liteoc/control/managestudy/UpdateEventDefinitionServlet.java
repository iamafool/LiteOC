/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package com.liteoc.control.managestudy;


import com.liteoc.bean.core.*;
import com.liteoc.bean.managestudy.DiscrepancyNoteBean;
import com.liteoc.bean.managestudy.EventDefinitionCRFBean;
import com.liteoc.bean.managestudy.StudyEventBean;
import com.liteoc.bean.managestudy.StudyEventDefinitionBean;
import com.liteoc.bean.submit.CRFVersionBean;
import com.liteoc.bean.submit.EventCRFBean;
import com.liteoc.bean.submit.ItemDataBean;
import com.liteoc.control.core.SecureController;
import com.liteoc.control.form.FormProcessor;
import com.liteoc.control.form.Validator;
import com.liteoc.core.form.StringUtil;
import com.liteoc.dao.managestudy.DiscrepancyNoteDAO;
import com.liteoc.dao.managestudy.EventDefinitionCRFDAO;
import com.liteoc.dao.managestudy.StudyEventDAO;
import com.liteoc.dao.managestudy.StudyEventDefinitionDAO;
import com.liteoc.dao.submit.CRFVersionDAO;
import com.liteoc.dao.submit.EventCRFDAO;
import com.liteoc.dao.submit.ItemDataDAO;
import com.liteoc.domain.SourceDataVerification;
import com.liteoc.view.Page;
import com.liteoc.web.InsufficientPermissionException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author jxu
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class UpdateEventDefinitionServlet extends SecureController {

    /**
     * 
     */
    @Override
    public void mayProceed() throws InsufficientPermissionException {
        checkStudyLocked(Page.LIST_DEFINITION_SERVLET, respage.getString("current_study_locked"));
        if (ub.isSysAdmin()) {
            return;
        }
        if (currentRole.getRole().equals(Role.STUDYDIRECTOR) || currentRole.getRole().equals(Role.COORDINATOR)) {
            return;
        }
        addPageMessage(respage.getString("no_have_permission_to_update_study_event_definition") + "<br>" + respage.getString("change_study_contact_sysadmin"));
        throw new InsufficientPermissionException(Page.LIST_DEFINITION_SERVLET, resexception.getString("not_study_director"), "1");
    }

    @Override
    public void processRequest() throws Exception {
        String action = request.getParameter("action");
        if (StringUtil.isBlank(action)) {
            forwardPage(Page.UPDATE_EVENT_DEFINITION1);
        } else {
            if ("confirm".equalsIgnoreCase(action)) {
                confirmDefinition();

            } else if ("submit".equalsIgnoreCase(action)) {
                submitDefinition();

            } else {
                addPageMessage(respage.getString("updating_ED_is_cancelled"));
                forwardPage(Page.LIST_DEFINITION_SERVLET);
            }
        }

    }

    /**
     * 
     * @throws Exception
     */
    private void confirmDefinition() throws Exception {
        Validator v = new Validator(request);
        FormProcessor fp = new FormProcessor(request);

        StudyEventDefinitionBean sed = (StudyEventDefinitionBean) session.getAttribute("definition");

        v.addValidation("name", Validator.NO_BLANKS);
        v.addValidation("name", Validator.LENGTH_NUMERIC_COMPARISON, NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, 2000);
        v.addValidation("description", Validator.LENGTH_NUMERIC_COMPARISON, NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, 2000);
        v.addValidation("category", Validator.LENGTH_NUMERIC_COMPARISON, NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, 2000);

        errors = v.validate();

        if (!errors.isEmpty()) {
            logger.info("has errors");
            request.setAttribute("formMessages", errors);
            forwardPage(Page.UPDATE_EVENT_DEFINITION1);

        } else {
            logger.info("no errors");

            sed.setName(fp.getString("name"));
            sed.setRepeating(fp.getBoolean("repeating"));
            sed.setCategory(fp.getString("category"));
            sed.setDescription(fp.getString("description"));
            sed.setType(fp.getString("type"));

            session.setAttribute("definition", sed);
            ArrayList edcs = (ArrayList) session.getAttribute("eventDefinitionCRFs");
            CRFVersionDAO cvdao = new CRFVersionDAO(sm.getDataSource());
            for (int i = 0; i < edcs.size(); i++) {
                EventDefinitionCRFBean edcBean = (EventDefinitionCRFBean) edcs.get(i);
                if (!edcBean.getStatus().equals(Status.DELETED) && !edcBean.getStatus().equals(Status.AUTO_DELETED)) {
                    // only get inputs from web page if AVAILABLE
                    int defaultVersionId = fp.getInt("defaultVersionId" + i);
                    edcBean.setDefaultVersionId(defaultVersionId);
                    CRFVersionBean defaultVersion = (CRFVersionBean) cvdao.findByPK(edcBean.getDefaultVersionId());
                    edcBean.setDefaultVersionName(defaultVersion.getName());

                    String requiredCRF = fp.getString("requiredCRF" + i);
                    String doubleEntry = fp.getString("doubleEntry" + i);
                    String decisionCondition = fp.getString("decisionCondition" + i);
                    String electronicSignature = fp.getString("electronicSignature" + i);
                    String hideCRF = fp.getString("hideCRF" + i);
                    int sdvId = fp.getInt("sdvOption" + i);

                    if (!StringUtil.isBlank(hideCRF) && "yes".equalsIgnoreCase(hideCRF.trim())) {
                        edcBean.setHideCrf(true);
                    } else {
                        edcBean.setHideCrf(false);
                    }

                    if (!StringUtil.isBlank(requiredCRF) && "yes".equalsIgnoreCase(requiredCRF.trim())) {
                        edcBean.setRequiredCRF(true);
                    } else {
                        edcBean.setRequiredCRF(false);
                    }
                    
                    if ("1".equalsIgnoreCase(doubleEntry.trim())) {
                        edcBean.setDoubleEntry(true);
                        edcBean.setDoubleEntryType(1);
                    } else if ("2".equalsIgnoreCase(doubleEntry.trim())){
                    	edcBean.setDoubleEntry(true);
                    	edcBean.setDoubleEntryType(2);
                    } else {
                        edcBean.setDoubleEntry(false);
                    }

                    if (!StringUtil.isBlank(electronicSignature) && "yes".equalsIgnoreCase(electronicSignature.trim())) {
                        edcBean.setElectronicSignature(true);
                    } else {
                        edcBean.setElectronicSignature(false);
                    }

                    if (!StringUtil.isBlank(decisionCondition) && "yes".equalsIgnoreCase(decisionCondition.trim())) {
                        edcBean.setDecisionCondition(true);
                    } else {
                        edcBean.setDecisionCondition(false);
                    }
                    String nullString = "";
                    // process null values
                    ArrayList nulls = NullValue.toArrayList();
                    for (int a = 0; a < nulls.size(); a++) {
                        NullValue n = (NullValue) nulls.get(a);
                        String myNull = fp.getString(n.getName().toLowerCase() + i);
                        if (!StringUtil.isBlank(myNull) && "yes".equalsIgnoreCase(myNull.trim())) {
                            nullString = nullString + n.getName().toUpperCase() + ",";
                        }

                    }

                    if (sdvId > 0 && (edcBean.getSourceDataVerification() == null || sdvId != edcBean.getSourceDataVerification().getCode())) {
                        edcBean.setSourceDataVerification(SourceDataVerification.getByCode(sdvId));
                    }

                    edcBean.setNullValues(nullString);
                    logger.info("found null values: " + nullString);
                }

            }

            session.setAttribute("eventDefinitionCRFs", edcs);
            forwardPage(Page.UPDATE_EVENT_DEFINITION_CONFIRM);
        }

    }

    /**
     * Updates the definition
     * 
     */
    private void submitDefinition() {
        ArrayList edcs = (ArrayList) session.getAttribute("eventDefinitionCRFs");
        StudyEventDefinitionBean sed = (StudyEventDefinitionBean) session.getAttribute("definition");
        StudyEventDefinitionDAO edao = new StudyEventDefinitionDAO(sm.getDataSource());
        logger.info("Definition bean to be updated:" + sed.getName() + sed.getCategory());

        sed.setUpdater(ub);
        sed.setUpdatedDate(new Date());
        sed.setStatus(Status.AVAILABLE);
        edao.update(sed);

        EventDefinitionCRFDAO cdao = new EventDefinitionCRFDAO(sm.getDataSource());

        for (int i = 0; i < edcs.size(); i++) {
            EventDefinitionCRFBean edc = (EventDefinitionCRFBean) edcs.get(i);
            if (edc.getId() > 0) {// need to do update
                edc.setUpdater(ub);
                edc.setUpdatedDate(new Date());
                logger.info("Status:" + edc.getStatus().getName());
                logger.info("version:" + edc.getDefaultVersionId());
                logger.info("Electronic Signature [" + edc.isElectronicSignature() + "]");
                cdao.update(edc);

                if (edc.getStatus().equals(Status.DELETED)
                        || edc.getStatus().equals(Status.AUTO_DELETED)) {
                    removeAllEventsItems(edc, sed);
                }
                if (edc.getOldStatus()!=null && edc.getOldStatus().equals(Status.DELETED)) {
                    restoreAllEventsItems(edc, sed);
                }

            } else { // to insert
                edc.setOwner(ub);
                edc.setCreatedDate(new Date());
                edc.setStatus(Status.AVAILABLE);
                cdao.create(edc);

            }
        }
        session.removeAttribute("definition");
        session.removeAttribute("eventDefinitionCRFs");

        session.removeAttribute("tmpCRFIdMap");
        session.removeAttribute("crfsWithVersion");
        session.removeAttribute("eventDefinitionCRFs");
        
        addPageMessage(respage.getString("the_ED_has_been_updated_succesfully"));
        forwardPage(Page.LIST_DEFINITION_SERVLET);
    }

    public void removeAllEventsItems(EventDefinitionCRFBean edc, StudyEventDefinitionBean sed){
        StudyEventDAO seDao = new StudyEventDAO(sm.getDataSource());
        EventCRFDAO ecrfDao = new EventCRFDAO(sm.getDataSource());
        ItemDataDAO iddao = new ItemDataDAO(sm.getDataSource());
        
        // Getting Study Events
        ArrayList seList = seDao.findAllByStudyEventDefinitionAndCrfOids(sed.getOid(), edc.getCrf().getOid());
        for (int j = 0; j < seList.size(); j++) {
            StudyEventBean seBean = (StudyEventBean) seList.get(j);
            // Getting Event CRFs
            ArrayList ecrfList = ecrfDao.findAllByStudyEventAndCrfOrCrfVersionOid(seBean, edc.getCrf().getOid());
            for (int k = 0; k < ecrfList.size(); k++) {
                EventCRFBean ecrfBean = (EventCRFBean) ecrfList.get(k);
                ecrfBean.setOldStatus(ecrfBean.getStatus());
                ecrfBean.setStatus(Status.AUTO_DELETED);
                ecrfBean.setUpdater(ub);
                ecrfBean.setUpdatedDate(new Date());
                ecrfDao.update(ecrfBean);
                // Getting Item Data
                ArrayList itemData = iddao.findAllByEventCRFId(ecrfBean.getId());
                // remove all the item data
                for (int a = 0; a < itemData.size(); a++) {
                    ItemDataBean item = (ItemDataBean) itemData.get(a);
                    if (!item.getStatus().equals(Status.DELETED)) {
                        item.setOldStatus(item.getStatus());
                        item.setStatus(Status.AUTO_DELETED);
                        item.setUpdater(ub);
                        item.setUpdatedDate(new Date());
                        iddao.update(item);
                        DiscrepancyNoteDAO dnDao = new DiscrepancyNoteDAO(sm.getDataSource());
                        List dnNotesOfRemovedItem = dnDao.findExistingNotesForItemData(item.getId());
                        if (!dnNotesOfRemovedItem.isEmpty()) {
                            DiscrepancyNoteBean itemParentNote = null;
                            for (Object obj : dnNotesOfRemovedItem) {
                                if (((DiscrepancyNoteBean)obj).getParentDnId() == 0) {
                                    itemParentNote = (DiscrepancyNoteBean)obj;
                                }
                            }
                            DiscrepancyNoteBean dnb = new DiscrepancyNoteBean();
                            if (itemParentNote != null) {
                                dnb.setParentDnId(itemParentNote.getId());
                                dnb.setDiscrepancyNoteTypeId(itemParentNote.getDiscrepancyNoteTypeId());
                            }
                            dnb.setResolutionStatusId(ResolutionStatus.CLOSED.getId());
                            dnb.setStudyId(currentStudy.getId());
                            dnb.setAssignedUserId(ub.getId());
                            dnb.setOwner(ub);
                            dnb.setEntityType(DiscrepancyNoteBean.ITEM_DATA);
                            dnb.setEntityId(item.getId());
                            dnb.setColumn("value");
                            dnb.setCreatedDate(new Date());
                            dnb.setDescription("The item has been removed, this Discrepancy Note has been Closed.");
                            dnDao.create(dnb);
                            dnDao.createMapping(dnb);
                            itemParentNote.setResolutionStatusId(ResolutionStatus.CLOSED.getId());
                            dnDao.update(itemParentNote);
                        }
                    }
                }
            }
        }
    }

    public void restoreAllEventsItems(EventDefinitionCRFBean edc, StudyEventDefinitionBean sed){
        StudyEventDAO seDao = new StudyEventDAO(sm.getDataSource());
        EventCRFDAO ecrfDao = new EventCRFDAO(sm.getDataSource());
        ItemDataDAO iddao = new ItemDataDAO(sm.getDataSource());

        // All Study Events
        ArrayList seList = seDao.findAllByStudyEventDefinitionAndCrfOids(sed.getOid(), edc.getCrf().getOid());
        for (int j = 0; j < seList.size(); j++) {
            StudyEventBean seBean = (StudyEventBean) seList.get(j);
            // All Event CRFs
            ArrayList ecrfList = ecrfDao.findAllByStudyEventAndCrfOrCrfVersionOid(seBean, edc.getCrf().getOid());
            for (int k = 0; k < ecrfList.size(); k++) {
                EventCRFBean ecrfBean = (EventCRFBean) ecrfList.get(k);
                ecrfBean.setStatus(ecrfBean.getOldStatus());
                ecrfBean.setUpdater(ub);
                ecrfBean.setUpdatedDate(new Date());
                ecrfDao.update(ecrfBean);
                // All Item Data
                ArrayList itemData = iddao.findAllByEventCRFId(ecrfBean.getId());
                // remove all the item data
                for (int a = 0; a < itemData.size(); a++) {
                    ItemDataBean item = (ItemDataBean) itemData.get(a);
                    if (item.getStatus().equals(Status.DELETED) || item.getStatus().equals(Status.AUTO_DELETED)) {
                        item.setStatus(item.getOldStatus());
                        item.setUpdater(ub);
                        item.setUpdatedDate(new Date());
                        iddao.update(item);
                    }
                }
            }
        }

    }

}
