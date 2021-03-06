/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package com.liteoc.control.submit;

import com.liteoc.bean.core.DataEntryStage;
import com.liteoc.bean.core.ResolutionStatus;
import com.liteoc.bean.core.Role;
import com.liteoc.bean.core.Status;
import com.liteoc.bean.login.StudyUserRoleBean;
import com.liteoc.bean.login.UserAccountBean;
import com.liteoc.bean.managestudy.DiscrepancyNoteBean;
import com.liteoc.bean.managestudy.EventDefinitionCRFBean;
import com.liteoc.bean.managestudy.StudyBean;
import com.liteoc.bean.submit.DisplayItemBean;
import com.liteoc.bean.submit.DisplayItemGroupBean;
import com.liteoc.bean.submit.EventCRFBean;
import com.liteoc.bean.submit.ItemBean;
import com.liteoc.bean.submit.ItemDataBean;
import com.liteoc.control.form.DiscrepancyValidator;
import com.liteoc.control.form.FormProcessor;
import com.liteoc.control.form.RuleValidator;
import com.liteoc.control.managestudy.ViewNotesServlet;
import com.liteoc.core.form.StringUtil;
import com.liteoc.dao.managestudy.DiscrepancyNoteDAO;
import com.liteoc.dao.submit.ItemDataDAO;
import com.liteoc.view.Page;
import com.liteoc.web.InsufficientPermissionException;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * @author jxu
 * 
 *         Performs 'administrative editing' action for study director/study
 *         coordinator
 */
public class AdministrativeEditingServlet2 extends DataEntryServlet {

    Locale locale;

    // < ResourceBundleresexception,respage;

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
            return Page.ADMIN_EDIT_SERVLET2;
        } else {
            Page target = Page.ADMIN_EDIT_SERVLET2;
            String s = target.getFileName().trim();
            if(s.contains("?")) {
                String[] t = s.split("\\?");
                String x = "";
                String y = t[0] + "?";
                if(t.length>1) {
                    if(t[1].contains("&")) {
                        String[] ts = t[1].split("&");
                        for(int i=0; i<ts.length; ++i) {
                            if(ts[i].contains("eventCRFId=")) {
                                ts[i] = "eventCRFId=" + eventCRFId;
                                x += "e";
                            } else if(ts[i].contains("sectionId=")) {
                                ts[i] = "sectionId=" + sectionId;
                                x += "s";
                            } else if(ts[i].contains("tab=")) {
                                ts[i] = "tab=" + tabId;
                                x += "t";
                            }
                            y += ts[i] + "&";
                        }
                    } else {
                        if(t[1].contains("eventCRFId=")) {
                            t[1] = "eventCRFId=" + eventCRFId;
                            x += "e";
                        } else if(t[1].contains("sectionId=")) {
                            t[1] = "sectionId=" + sectionId;
                            x += "s";
                        } else if(t[1].contains("tab=")) {
                            t[1] = "tab=" + tabId;
                            x += "t";
                        }
                        y += t[1]+"&";
                    }
                    if(x.length()<3) {
                        if(!x.contains("e")) {
                            y += "eventCRFId=" + eventCRFId + "&";
                        } 
                        if(!x.contains("s")) {
                            y += "sectionId=" + sectionId + "&";
                        } 
                        if(!x.contains("t")) {
                            y += "tab=" + tabId + "&"; 
                        }
                    }
                    y = y.substring(0,y.length()-1);
                    target.setFileName(y);
                } else {
                    logger.info("It's a wrong servlet page:" + s);
                }
            }else {
                target.setFileName(target.getFileName() + "?eventCRFId=" + eventCRFId + "&sectionId=" + sectionId + "&tab=" + tabId);
            }
            //target.setFileName(target.getFileName() + "?eventCRFId=" + eventCRFId + "&sectionId=" + sectionId + "&tab=" + tabId);
            return target;
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.liteoc.control.submit.DataEntryServlet#getJSPPage()
     */
    @Override
    protected Page getJSPPage() {
        return Page.ADMIN_EDIT2;
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

    /*
     * (non-Javadoc)
     * 
     * @see com.liteoc.control.core.SecureController#mayProceed()
     */
    @Override
    protected void mayProceed(HttpServletRequest request, HttpServletResponse response) throws InsufficientPermissionException {
        mayAccess(request);
        locale = request.getLocale();
        HttpSession session = request.getSession();
        FormProcessor fp = new FormProcessor(request);
        UserAccountBean ub =(UserAccountBean) request.getSession().getAttribute(USER_BEAN_NAME);
        StudyUserRoleBean  currentRole = (StudyUserRoleBean) request.getSession().getAttribute("userRole");
      
        
        getInputBeans(request);
        EventCRFBean ecb = (EventCRFBean)request.getAttribute(INPUT_EVENT_CRF);
        String fromResolvingNotes = fp.getString("fromResolvingNotes", true);

        if (StringUtil.isBlank(fromResolvingNotes)) {
            session.removeAttribute(ViewNotesServlet.WIN_LOCATION);
            session.removeAttribute(ViewNotesServlet.NOTES_TABLE);
            checkStudyLocked(Page.LIST_STUDY_SUBJECTS, respage.getString("current_study_locked"), request, response);
            checkStudyFrozen(Page.LIST_STUDY_SUBJECTS, respage.getString("current_study_frozen"), request, response);
        }
        request.setAttribute("fromResolvingNotes", fromResolvingNotes);
        // System.out.println(" +++++++++++++++++++ " + ecb.getStudyEventId());

        DataEntryStage stage = ecb.getStage();
        Role r = currentRole.getRole();
        session.setAttribute("mayProcessUploading", "true");

        if (!SubmitDataServlet.maySubmitData(ub, currentRole)) {
            session.setAttribute("mayProcessUploading", "false");
            String exceptionName = resexception.getString("no_permission_validation");
            String noAccessMessage = respage.getString("not_perform_administrative_editing_CRF");

            addPageMessage(noAccessMessage, request);
            throw new InsufficientPermissionException(Page.MENU, exceptionName, "1");
        }
        logger.info("stage name:" + stage.getName());
        if (stage.equals(DataEntryStage.DOUBLE_DATA_ENTRY_COMPLETE)) {
            // if (!r.equals(Role.STUDYDIRECTOR) && !r.equals(Role.COORDINATOR))
            // {
            if (r.equals(Role.MONITOR)) {
                session.setAttribute("mayProcessUploading", "false");
                addPageMessage(respage.getString("no_have_correct_privilege_current_study") + respage.getString("change_study_contact_sysadmin"), request);
                throw new InsufficientPermissionException(Page.LIST_STUDY_SUBJECTS_SERVLET, resexception.getString("no_permission_administrative_editing"), "1");
            }
        }
        else {
            session.setAttribute("mayProcessUploading", "false");
            addPageMessage(respage.getString("not_perform_administrative_editing_because"), request);
            throw new InsufficientPermissionException(Page.LIST_STUDY_SUBJECTS_SERVLET, resexception.getString("not_correct_stage"), "1");
        }
        return;
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
     * @see com.liteoc.control.submit.DataEntryServlet#loadDBValues()
     */
    @Override
    protected boolean shouldLoadDBValues(DisplayItemBean dib) {
        // logger.info("dib" + dib.getData().getValue());
        if (dib.getData().getStatus() == null) {
            return true;
        }
        if (!Status.UNAVAILABLE.equals(dib.getData().getStatus())) {
            return false;
        }

        return true;
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

    @Override
    protected DisplayItemBean validateDisplayItemBean(DiscrepancyValidator v, DisplayItemBean dib, String inputName, RuleValidator rv,
            HashMap<String, ArrayList<String>> groupOrdinalPLusItemOid, Boolean fireRuleValidation, ArrayList<String> messages, HttpServletRequest request) {
        if (StringUtil.isBlank(inputName)) {// we pass a blank inputName,which
            // means if not an item from group,
            // doesn't
            // need to get data from form again
            dib = loadFormValue(dib, request);
        }
        if (groupOrdinalPLusItemOid.containsKey(dib.getItem().getOid()) || fireRuleValidation) {
            messages = messages == null ? groupOrdinalPLusItemOid.get(dib.getItem().getOid()) : messages;
            dib = validateDisplayItemBeanSingleCV(rv, dib, inputName, messages);
        }
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
     * com.liteoc.control.submit.DataEntryServlet#getBlankItemStatus
     * ()
     */
    @Override
    protected Status getBlankItemStatus() {
        return Status.UNAVAILABLE;
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
        return Status.UNAVAILABLE;
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

    @Override
    protected boolean shouldRunRules() {
        return false;
    }

    @Override
    protected boolean isAdministrativeEditing() {
        return true;
    }

    @Override
    protected boolean isAdminForcedReasonForChange(HttpServletRequest request) {
        return false;
    }

	@Override
	protected boolean writeToDB(ItemDataBean itemData, DisplayItemBean dib, ItemDataDAO iddao, int ordinal, HttpServletRequest request) {
	    ItemDataBean idb = itemData;
	    UserAccountBean ub =(UserAccountBean) request.getSession().getAttribute(USER_BEAN_NAME);
	    StudyBean currentStudy =    (StudyBean)  request.getSession().getAttribute("study");
	    EventCRFBean ecb = (EventCRFBean)request.getAttribute(INPUT_EVENT_CRF);
	    idb.setItemId(dib.getItem().getId());
	    idb.setEventCRFId(ecb.getId());
	
	    if (idb.getValue().equals("")) {
	        idb.setStatus(getBlankItemStatus());
	    } else {
	        idb.setStatus(getNonBlankItemStatus(request));
	    }
	    if (StringUtil.isBlank(dib.getEditFlag())) {
	
	        if (!idb.isActive()) {
	            idb.setOrdinal(ordinal);
	            idb.setCreatedDate(new Date());
	            idb.setOwner(ub);
	            idb = (ItemDataBean) iddao.create(idb);
	        } else {
	            idb.setUpdater(ub);
	            logger.info("update item update_id " + idb.getUpdater().getId());
	            idb = (ItemDataBean) iddao.updateValue1(idb);
	            idb = (ItemDataBean) iddao.updateValue(idb);
	        }
	    } else {
	        // for the items in group, they have editFlag
	        if ("add".equalsIgnoreCase(dib.getEditFlag())) {
	            idb.setOrdinal(ordinal);
	            idb.setCreatedDate(new Date());
	            idb.setOwner(ub);
	            logger.debug("create a new item data" + idb.getItemId() + idb.getValue());
	            // idb = (ItemDataBean) iddao.create(idb);
	            // >>tbh 08/2008
	            idb.setUpdater(ub);
	            // idb = (ItemDataBean) iddao.updateValue(idb);
	            // instead of two hits to the db, we perform an 'upsert'
	            // combining them into one
	            idb = (ItemDataBean) iddao.upsert(idb);
	            // <<tbh
	        } else if ("edit".equalsIgnoreCase(dib.getEditFlag())) {
	                            idb.setUpdater(ub);
	           
	            // //System.out.println("update an item data - running update value " + idb.getId() + " :" + idb.getValue());
	            logger.info("update item update_id " + idb.getUpdater().getId());
	            // update tbh #5999, #5998; if an item_data was not included in
	            // an import data, it won't exist; we need to check on item_data_id
	            // to make sure we are running the correct command on the db
	            if (idb.getId() != 0) {
	                idb.setUpdatedDate(new Date());
	                idb = (ItemDataBean) iddao.updateValue(idb);
	            } else {
	                idb.setCreatedDate(new Date());
	                idb.setOrdinal(ordinal);
	                idb.setOwner(ub);
	                idb = (ItemDataBean) iddao.upsert(idb);
	                logger.debug("just ran upsert! " + idb.getId());
	            }
	
	        } else if ("remove".equalsIgnoreCase(dib.getEditFlag())) {
	            logger.debug("REMOVE an item data" + idb.getItemId() + idb.getValue());
	            idb.setUpdater(ub);
	            idb.setStatus(Status.DELETED);
	            idb = (ItemDataBean) iddao.updateValueForRemoved(idb);
	
	            DiscrepancyNoteDAO dnDao = new DiscrepancyNoteDAO(getDataSource());
	            List dnNotesOfRemovedItem = dnDao.findExistingNotesForItemData(idb.getId());
	            if (!dnNotesOfRemovedItem.isEmpty()) {
	                DiscrepancyNoteBean itemParentNote = null;
	                for (Object obj : dnNotesOfRemovedItem) {
	                    if (((DiscrepancyNoteBean) obj).getParentDnId() == 0) {
	                        itemParentNote = (DiscrepancyNoteBean) obj;
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
	                dnb.setEntityId(idb.getId());
	                dnb.setCreatedDate(new Date());
	                dnb.setColumn("value");
	                dnb.setDescription("The item has been removed, this Discrepancy Note has been Closed.");
	                dnDao.create(dnb);
	                dnDao.createMapping(dnb);
	                itemParentNote.setResolutionStatusId(ResolutionStatus.CLOSED.getId());
	                dnDao.update(itemParentNote);
	            }
	        }
	
	    }
	
	    return idb.isActive();
	}    
}
