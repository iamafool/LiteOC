/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package com.liteoc.control.submit;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.liteoc.bean.core.Status;
import com.liteoc.bean.managestudy.EventDefinitionCRFBean;
import com.liteoc.bean.submit.DisplayItemBean;
import com.liteoc.bean.submit.DisplayItemGroupBean;
import com.liteoc.bean.submit.EventCRFBean;
import com.liteoc.bean.submit.ItemBean;
import com.liteoc.control.form.DiscrepancyValidator;
import com.liteoc.control.form.FormProcessor;
import com.liteoc.control.form.RuleValidator;
import com.liteoc.core.form.StringUtil;
import com.liteoc.view.Page;
import com.liteoc.web.InsufficientPermissionException;

/**
 * @author ssachs
 */
@Controller
@RequestMapping(value="/InitialDataEntry")
public class InitialDataEntryServlet extends DataEntryServlet {
    protected final Logger logger = LoggerFactory.getLogger(getClass().getName());
    Locale locale;

    /*
     * (non-Javadoc)
     * 
     * @see com.liteoc.control.core.SecureController#mayProceed()
     */

    @Override
    protected void mayProceed(HttpServletRequest request, HttpServletResponse response) throws InsufficientPermissionException {
        mayAccess(request);
        checkStudyLocked(Page.LIST_STUDY_SUBJECTS, respage.getString("current_study_locked"), request, response);
        checkStudyFrozen(Page.LIST_STUDY_SUBJECTS, respage.getString("current_study_frozen"), request, response);
        HttpSession session = request.getSession();
        locale = request.getLocale();
        
        session.setAttribute("mayProcessUploading", "true");

        getInputBeans(request);


        return;
    }

    /*
     * (non-Javadoc)
     * 
     * @seecom.liteoc.control.submit.DataEntryServlet#
     * validateInputOnFirstRound()
     */
    @Override
    protected boolean validateInputOnFirstRound() {
        return true;
    }

    @Override
    protected DisplayItemBean validateDisplayItemBean(DiscrepancyValidator v, DisplayItemBean dib, String inputName, RuleValidator rv,
            HashMap<String, ArrayList<String>> groupOrdinalPLusItemOid, Boolean fireRuleValidation, ArrayList<String> messages, HttpServletRequest request) {

        // note that this step sets us up both for
        // displaying the data on the form again, in the event of an error
        // and sending the data to the database, in the event of no error
        if (StringUtil.isBlank(inputName)) {// not an item from group, doesn't
            // need to get data from form again
            dib = loadFormValue(dib, request);
        }

        if (groupOrdinalPLusItemOid.containsKey(dib.getItem().getOid()) || fireRuleValidation) {
            messages = messages == null ? groupOrdinalPLusItemOid.get(dib.getItem().getOid()) : messages;
            dib = validateDisplayItemBeanSingleCV(rv, dib, inputName, messages);
        }
        // I_AGEN_DOSEDATE64
        return dib;
    }

    @Override
    protected List<DisplayItemGroupBean> validateDisplayItemGroupBean(DiscrepancyValidator v, DisplayItemGroupBean digb, List<DisplayItemGroupBean> digbs,
            List<DisplayItemGroupBean> formGroups, RuleValidator rv, HashMap<String, ArrayList<String>> groupOrdinalPLusItemOid, HttpServletRequest request, HttpServletResponse response) {

        EventDefinitionCRFBean edcb = (EventDefinitionCRFBean)request.getAttribute(EVENT_DEF_CRF_BEAN);
                
        formGroups = loadFormValueForItemGroup(digb, digbs, formGroups, edcb.getId(), request);
        String inputName = "";
        for (int i = 0; i < formGroups.size(); i++) {
            DisplayItemGroupBean displayGroup = formGroups.get(i);

            List<DisplayItemBean> items = displayGroup.getItems();
            int order = displayGroup.getOrdinal();
            if (displayGroup.isAuto() && displayGroup.getFormInputOrdinal() > 0) {
                order = displayGroup.getFormInputOrdinal();
            }
            for (DisplayItemBean displayItem : items) {
                // int manualcount = 0;
                // tbh trying to set this correctly 01/2010
                if (displayGroup.isAuto()) {
                    inputName = getGroupItemInputName(displayGroup, order, displayItem);
                } else {
                    inputName = getGroupItemManualInputName(displayGroup, order, displayItem);
                    // manualcount++;
                }
                logger.debug("THe oid is " + displayItem.getItem().getOid() + " order : " + order + " inputName : " + inputName);

                if (groupOrdinalPLusItemOid.containsKey(displayItem.getItem().getOid())
                    || groupOrdinalPLusItemOid.containsKey(String.valueOf(displayGroup.getIndex() + 1) + displayItem.getItem().getOid())) {
                    logger.debug("IN : " + String.valueOf(displayGroup.getIndex() + 1) + displayItem.getItem().getOid());
                    validateDisplayItemBean(v, displayItem, inputName, rv, groupOrdinalPLusItemOid, true, groupOrdinalPLusItemOid.get(String
                            .valueOf(displayGroup.getIndex() + 1)
                        + displayItem.getItem().getOid()), request);
                } else {
                    validateDisplayItemBean(v, displayItem, inputName, rv, groupOrdinalPLusItemOid, false, null, request);
                }
            }
        }
        return formGroups;

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.liteoc.control.submit.DataEntryServlet#validateDisplayItemBean
     * (com.liteoc.core.form.Validator,
     * com.liteoc.bean.submit.DisplayItemBean)
     */
    @Override
    protected DisplayItemBean validateDisplayItemBean(DiscrepancyValidator v, DisplayItemBean dib, String inputName, HttpServletRequest request) {

        ItemBean ib = dib.getItem();
        com.liteoc.bean.core.ResponseType rt = dib.getMetadata().getResponseSet().getResponseType();

        // note that this step sets us up both for
        // displaying the data on the form again, in the event of an error
        // and sending the data to the database, in the event of no error
        if (StringUtil.isBlank(inputName)) {// not an item from group, doesn't
            // need to get data from form again
            dib = loadFormValue(dib, request);
        }

        // types TEL and ED are not supported yet
        if (rt.equals(com.liteoc.bean.core.ResponseType.TEXT) || rt.equals(com.liteoc.bean.core.ResponseType.TEXTAREA) || 
                rt.equals(com.liteoc.bean.core.ResponseType.FILE)) {
            dib = validateDisplayItemBeanText(v, dib, inputName, request);
        } else if (rt.equals(com.liteoc.bean.core.ResponseType.RADIO) || rt.equals(com.liteoc.bean.core.ResponseType.SELECT)) {
            dib = validateDisplayItemBeanSingleCV(v, dib, inputName);
        } else if (rt.equals(com.liteoc.bean.core.ResponseType.CHECKBOX) || rt.equals(com.liteoc.bean.core.ResponseType.SELECTMULTI)) {
            dib = validateDisplayItemBeanMultipleCV(v, dib, inputName);
        }

        logger.debug("just ran validate display item bean on " + inputName);
        return dib;
    }

    @Override
    protected List<DisplayItemGroupBean> validateDisplayItemGroupBean(DiscrepancyValidator v, DisplayItemGroupBean digb, List<DisplayItemGroupBean> digbs,
            List<DisplayItemGroupBean> formGroups, HttpServletRequest request, HttpServletResponse response) {
        EventDefinitionCRFBean edcb = (EventDefinitionCRFBean)request.getAttribute(EVENT_DEF_CRF_BEAN);
        formGroups = loadFormValueForItemGroup(digb, digbs, formGroups, edcb.getId(), request);
        String inputName = "";
        for (int i = 0; i < formGroups.size(); i++) {
            DisplayItemGroupBean displayGroup = formGroups.get(i);

            List<DisplayItemBean> items = displayGroup.getItems();
            int order = displayGroup.getOrdinal();
            if (displayGroup.isAuto() && displayGroup.getFormInputOrdinal() > 0) {
                order = displayGroup.getFormInputOrdinal();
            }
            for (DisplayItemBean displayItem : items) {
                if (displayGroup.isAuto()) {
                    inputName = getGroupItemInputName(displayGroup, order, displayItem);
                } else {
                    inputName = getGroupItemManualInputName(displayGroup, order, displayItem);
                }
                validateDisplayItemBean(v, displayItem, inputName, request);
            }
        }
        return formGroups;

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.liteoc.control.submit.DataEntryServlet#getBlankItemStatus
     * ()
     */
    @Override
    protected Status getBlankItemStatus() {
        return Status.AVAILABLE;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.liteoc.control.submit.DataEntryServlet#getNonBlankItemStatus
     * ()
     */
    @Override
    protected Status getNonBlankItemStatus(HttpServletRequest request) {
        EventDefinitionCRFBean edcb = (EventDefinitionCRFBean)request.getAttribute(EVENT_DEF_CRF_BEAN);
        return edcb.isDoubleEntry() ? Status.PENDING : Status.UNAVAILABLE;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.liteoc.control.submit.DataEntryServlet#getEventCRFAnnotations
     * ()
     */
    @Override
    protected String getEventCRFAnnotations(HttpServletRequest request) {
        EventCRFBean ecb = (EventCRFBean)request.getAttribute(INPUT_EVENT_CRF);
        return ecb.getAnnotations();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.liteoc.control.submit.DataEntryServlet#setEventCRFAnnotations
     * (java.lang.String)
     */
    @Override
    protected void setEventCRFAnnotations(String annotations, HttpServletRequest request) {
        EventCRFBean ecb = (EventCRFBean)request.getAttribute(INPUT_EVENT_CRF);
        ecb.setAnnotations(annotations);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.liteoc.control.submit.DataEntryServlet#getJSPPage()
     */
    @Override
    protected Page getJSPPage() {

        return Page.INITIAL_DATA_ENTRY_NW;

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.liteoc.control.submit.DataEntryServlet#getServletPage()
     */
    @Override
    protected Page getServletPage(HttpServletRequest request) {
        FormProcessor fp = new FormProcessor(request);
        String tabId = fp.getString("tab", true);
        String sectionId = fp.getString(DataEntryServlet.INPUT_SECTION_ID, true);
        String eventCRFId = fp.getString(INPUT_EVENT_CRF_ID, true);
        if (StringUtil.isBlank(sectionId) || StringUtil.isBlank(tabId)) {
            return Page.INITIAL_DATA_ENTRY_SERVLET;
        } else {
            Page target = Page.INITIAL_DATA_ENTRY_SERVLET;
            target.setFileName(target.getFileName() + "?eventCRFId=" + eventCRFId + "&sectionId=" + sectionId + "&tab=" + tabId);
            return target;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.liteoc.control.submit.DataEntryServlet#loadDBValues()
     */
    @Override
    protected boolean shouldLoadDBValues(DisplayItemBean dib) {
        return true;
    }

    @Override
    protected boolean shouldRunRules() {
        return true;
    }

    @Override
    protected boolean isAdministrativeEditing() {
        return false;
    }

    @Override
    protected boolean isAdminForcedReasonForChange(HttpServletRequest request) {
        return false;
    }

}
