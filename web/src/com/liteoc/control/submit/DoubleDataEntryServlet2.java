/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package com.liteoc.control.submit;

import com.liteoc.bean.core.DataEntryStage;
import com.liteoc.bean.core.ResolutionStatus;
import com.liteoc.bean.core.Status;
import com.liteoc.bean.login.UserAccountBean;
import com.liteoc.bean.managestudy.DiscrepancyNoteBean;
import com.liteoc.bean.managestudy.EventDefinitionCRFBean;
import com.liteoc.bean.managestudy.StudyBean;
import com.liteoc.bean.submit.DisplayItemBean;
import com.liteoc.bean.submit.DisplayItemGroupBean;
import com.liteoc.bean.submit.EventCRFBean;
import com.liteoc.bean.submit.ItemDataBean;
import com.liteoc.bean.submit.ResponseOptionBean;
import com.liteoc.bean.submit.SectionBean;
import com.liteoc.control.form.DiscrepancyValidator;
import com.liteoc.control.form.FormProcessor;
import com.liteoc.control.form.RuleValidator;
import com.liteoc.control.form.ScoreItemValidator;
import com.liteoc.control.form.Validator;
import com.liteoc.core.form.StringUtil;
import com.liteoc.dao.managestudy.DiscrepancyNoteDAO;
import com.liteoc.dao.submit.ItemDataDAO;
import com.liteoc.dao.submit.SectionDAO;
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
 * @author ssachs
 */
public class DoubleDataEntryServlet2 extends DataEntryServlet {

    Locale locale;

    public static final String COUNT_VALIDATE = "countValidate";
    public static final String DDE_ENTERED = "ddeEntered";
    public static final String DDE_PROGESS = "doubleDataProgress";

    /*
     * (non-Javadoc)
     * 
     * @see com.liteoc.control.core.SecureController#mayProceed()
     */
    @Override
    protected void mayProceed(HttpServletRequest request, HttpServletResponse response) throws InsufficientPermissionException {
        checkStudyLocked(Page.LIST_STUDY_SUBJECTS, respage.getString("current_study_locked"), request, response);
        checkStudyFrozen(Page.LIST_STUDY_SUBJECTS, respage.getString("current_study_frozen"), request, response);

        HttpSession session = request.getSession();
        locale = request.getLocale();

        
        getInputBeans(request);
        EventCRFBean ecb = (EventCRFBean)request.getAttribute(INPUT_EVENT_CRF);
        FormProcessor fp = new FormProcessor(request);
        SectionBean sb = (SectionBean)request.getAttribute(SECTION_BEAN);

        // BWP 12/2/07>> The following COUNT_VALIDATE session attribute is not
        // accessible,
        // for unknown reasons (threading problems?), when
        // double-data entry displays error messages; it's value is always 0; so
        // I have to create my
        // own session variable here to keep track of DDE stages

        // We'll go by the SectionBean's ordinal first
        int tabNumber = 1;
        if (sb != null) {
            tabNumber = sb.getOrdinal();
        }
        // if tabNumber still isn't valid, check the "tab" parameter
        if (tabNumber < 1) {
            String tab = fp.getString("tab");
            if (tab == null || tab.length() < 1) {
                tabNumber = 1;
            } else {
                tabNumber = fp.getInt("tab");
            }
        }
        SectionDAO sectionDao = new SectionDAO(getDataSource());
        int crfVersionId = ecb.getCRFVersionId();
        int eventCRFId = ecb.getId();
        ArrayList sections = sectionDao.findAllByCRFVersionId(crfVersionId);
        int sectionSize = sections.size();

        HttpSession mySession = request.getSession();
        DoubleDataProgress doubleDataProgress = (DoubleDataProgress) mySession.getAttribute(DDE_PROGESS);
        if (doubleDataProgress == null || doubleDataProgress.getEventCRFId() != eventCRFId) {
            doubleDataProgress = new DoubleDataProgress(sectionSize, eventCRFId);
            mySession.setAttribute(DDE_PROGESS, doubleDataProgress);
        }
        boolean hasVisitedSection = doubleDataProgress.getSectionVisited(tabNumber, eventCRFId);

        // setting up one-time validation here
        // admit that it's an odd place to put it, but where else?
        // placing it in dataentryservlet is creating too many counts
        int keyId = ecb.getId();
        Integer count = (Integer) session.getAttribute(COUNT_VALIDATE + keyId);
        if (count != null) {
            count++;
            session.setAttribute(COUNT_VALIDATE + keyId, count);
            logger.info("^^^just set count to session: " + count);
        } else {
            count = 0;
            session.setAttribute(COUNT_VALIDATE + keyId, count);
            logger.info("***count not found, set to session: " + count);
        }

        DataEntryStage stage = ecb.getStage();
        if (stage.equals(DataEntryStage.INITIAL_DATA_ENTRY_COMPLETE) && !hasVisitedSection) {
            // if the user has not entered this section yet in Double Data
            // Entry, then
            // set a flag that default values should be shown in the form
            request.setAttribute(DDE_ENTERED, true);

        }
        // Now update the session attribute
        doubleDataProgress.setSectionVisited(eventCRFId, tabNumber, true);
        mySession.setAttribute("doubleDataProgress", doubleDataProgress);
        // StudyEventStatus status =
        session.setAttribute("mayProcessUploading", "true");

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
        return false;
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
        return Status.PENDING;
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
        
        return ecb.getValidatorAnnotations();
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
        
        ecb.setValidatorAnnotations(annotations);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.liteoc.control.submit.DataEntryServlet#getJSPPage()
     */
    @Override
    protected Page getJSPPage() {
        return Page.DOUBLE_DATA_ENTRY2;
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
            return Page.DOUBLE_DATA_ENTRY_SERVLET2;
        } else {
            Page target = Page.DOUBLE_DATA_ENTRY_SERVLET2;
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
        // my understanding-jxu:
        // if the status is pending, should not load the db value
        // if the status is UNAVAILABLE,load DB value
        // interesting bug here: some fields load, some don't
        // remove a session value here:
        // int keyId = ecb.getId();
        // session.removeAttribute(COUNT_VALIDATE+keyId);
        // logger.info("^^^removed count_validate here");
        // wonky place to do it, but no other place at the moment, tbh
        if (dib.getData().getStatus() == null || dib.getData().getStatus().equals(Status.UNAVAILABLE)) {
            return true;
        }

        // how about this instead:
        // if it's pending, return false
        // otherwise return true?
        if (dib.getData().getStatus().equals(Status.PENDING)) {
            // logger.info("status was pending...");
            return false;
            // return true;
        }

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

	@Override
	protected DisplayItemBean validateDisplayItemBean(DiscrepancyValidator v,
			DisplayItemBean dib, String inputName, HttpServletRequest request) {
		return null;
	}

	@Override
	protected List<DisplayItemGroupBean> validateDisplayItemGroupBean(
			DiscrepancyValidator v, DisplayItemGroupBean dib,
			List<DisplayItemGroupBean> digbs,
			List<DisplayItemGroupBean> formGroups, HttpServletRequest request,
			HttpServletResponse response) {
		return null;
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
