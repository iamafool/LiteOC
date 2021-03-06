/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).
 * For details see: http://www.openclinica.org/license
 *
 * Copyright 2003-2008 Akaza Research
 */
package com.liteoc.control.submit;

import javax.servlet.http.HttpServletRequest;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.liteoc.bean.admin.CRFBean;
import com.liteoc.bean.core.Role;
import com.liteoc.bean.login.UserAccountBean;
import com.liteoc.bean.managestudy.StudyBean;
import com.liteoc.bean.managestudy.StudyEventDefinitionBean;
import com.liteoc.bean.rule.XmlSchemaValidationHelper;
import com.liteoc.control.SpringServletAccess;
import com.liteoc.control.core.SecureController;
import com.liteoc.control.form.FormProcessor;
import com.liteoc.dao.admin.CRFDAO;
import com.liteoc.dao.core.CoreResources;
import com.liteoc.dao.managestudy.StudyEventDAO;
import com.liteoc.dao.managestudy.StudyEventDefinitionDAO;
import com.liteoc.dao.submit.EventCRFDAO;
import com.liteoc.dao.submit.ItemDAO;
import com.liteoc.dao.submit.ItemFormMetadataDAO;
import com.liteoc.service.crfdata.HideCRFManager;
import com.liteoc.service.rule.RuleSetServiceInterface;
import com.liteoc.service.rule.RulesPostImportContainerService;
import com.liteoc.view.Page;
import com.liteoc.web.InsufficientPermissionException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;

/**
 * Verify the Rule import , show records that have Errors as well as records that will be saved.
 * 
 * @author Krikor krumlian
 */
public class ViewRuleAssignmentNewServlet extends SecureController {

    private static final long serialVersionUID = 9116068126651934226L;
    protected final Logger log = LoggerFactory.getLogger(ViewRuleAssignmentNewServlet.class);

    Locale locale;
    XmlSchemaValidationHelper schemaValidator = new XmlSchemaValidationHelper();
    RuleSetServiceInterface ruleSetService;
    RulesPostImportContainerService rulesPostImportContainerService;
    ItemFormMetadataDAO itemFormMetadataDAO;

    private boolean showMoreLink;
    private boolean isDesigner;

    @Override
    public void processRequest() throws Exception {
        FormProcessor fp = new FormProcessor(request);
        if (fp.getString("designer").equals("")) {
            isDesigner = false;
        } else {
            isDesigner = Boolean.parseBoolean(fp.getString("designer"));
        }
        if (fp.getString("showMoreLink").equals("")) {
            showMoreLink = true;
        } else {
            showMoreLink = Boolean.parseBoolean(fp.getString("showMoreLink"));
        }
        createTable();

    }

    private void createStudyEventForInfoPanel() {

        StudyEventDAO sedao = new StudyEventDAO(sm.getDataSource());
        StudyEventDefinitionDAO seddao = new StudyEventDefinitionDAO(sm.getDataSource());
        EventCRFDAO ecdao = new EventCRFDAO(sm.getDataSource());
        ItemDAO itemdao = new ItemDAO(sm.getDataSource());
        StudyBean studyWithEventDefinitions = currentStudy;
        if (currentStudy.getParentStudyId() > 0) {
            studyWithEventDefinitions = new StudyBean();
            studyWithEventDefinitions.setId(currentStudy.getParentStudyId());

        }
        CRFDAO crfdao = new CRFDAO(sm.getDataSource());
        ArrayList seds = seddao.findAllActiveByStudy(studyWithEventDefinitions);

        HashMap events = new LinkedHashMap();
        for (int i = 0; i < seds.size(); i++) {
            StudyEventDefinitionBean sed = (StudyEventDefinitionBean) seds.get(i);
            ArrayList<CRFBean> crfs = (ArrayList<CRFBean>) crfdao.findAllActiveByDefinition(sed);

            if (currentStudy.getParentStudyId() > 0) {
                // sift through these CRFs and see which ones are hidden
                HideCRFManager hideCRFs = HideCRFManager.createHideCRFManager();
                crfs = hideCRFs.removeHiddenCRFBeans(studyWithEventDefinitions, sed, crfs, sm.getDataSource());
            }

            if (!crfs.isEmpty()) {
                events.put(sed, crfs);
            }
        }
        request.setAttribute("eventlist", events);
        request.setAttribute("crfCount", crfdao.getCountofActiveCRFs());
        request.setAttribute("itemCount", itemdao.getCountofActiveItems());
        request.setAttribute("ruleSetCount", getRuleSetService().getRuleSetDao().count(currentStudy));

    }

    private void createTable() {

        ViewRuleAssignmentTableFactory factory = new ViewRuleAssignmentTableFactory(showMoreLink, getCoreResources().getField("designer.url")+"access?host="+getHostPath(request)+"&app="+getContextPath(request), isDesigner);
        factory.setRuleSetService(getRuleSetService());
        factory.setItemFormMetadataDAO(getItemFormMetadataDAO());
        factory.setCurrentStudy(currentStudy);
        factory.setCurrentUser(((UserAccountBean)request.getSession().getAttribute(USER_BEAN_NAME)));
        String ruleAssignmentsHtml = factory.createTable(request, response).render();
        request.getSession().setAttribute("ruleDesignerUrl",factory.getDesingerLink());
        request.setAttribute("ruleAssignmentsHtml", ruleAssignmentsHtml);
        createStudyEventForInfoPanel();
        if (ruleAssignmentsHtml != null) {
            if (isDesigner) {
                forwardPage(Page.VIEW_RULE_SETS_DESIGNER);
            } else {
                forwardPage(Page.VIEW_RULE_SETS2);
            }

        }

    }
    public String getContextPath(HttpServletRequest request) {
        String contextPath = request.getContextPath().replaceAll("/", "");
        return contextPath;
    }
    public String getHostPath(HttpServletRequest request) {
        String requestURLMinusServletPath = getRequestURLMinusServletPath(request);
        String hostPath = "";
        if (null != requestURLMinusServletPath) {
             hostPath = requestURLMinusServletPath.substring(0, requestURLMinusServletPath.lastIndexOf("/"));
           // hostPath = tmpPath.substring(0, tmpPath.lastIndexOf("/"));
        }
        return hostPath;
    }
    public String getRequestURLMinusServletPath(HttpServletRequest request) {
        String requestURLMinusServletPath = request.getRequestURL().toString().replaceAll(request.getServletPath(), "");
        return requestURLMinusServletPath;
    }

    @Override
    protected String getAdminServlet() {
        if (ub.isSysAdmin()) {
            return SecureController.ADMIN_SERVLET_CODE;
        } else {
            return "";
        }
    }

    @Override
    public void mayProceed() throws InsufficientPermissionException {
        locale = request.getLocale();
        if (ub.isSysAdmin()) {
            return;
        }
        Role r = currentRole.getRole();
       if (r.equals(Role.STUDYDIRECTOR) || r.equals(Role.COORDINATOR)) {
            return;
        }
        addPageMessage(respage.getString("no_have_correct_privilege_current_study") + respage.getString("change_study_contact_sysadmin"));
        throw new InsufficientPermissionException(Page.MENU_SERVLET, resexception.getString("may_not_submit_data"), "1");
    }

    private RuleSetServiceInterface getRuleSetService() {
        ruleSetService =
            this.ruleSetService != null ? ruleSetService : (RuleSetServiceInterface) SpringServletAccess.getApplicationContext(context).getBean(
                    "ruleSetService");
        // TODO: Add getRequestURLMinusServletPath(),getContextPath()
        return ruleSetService;
    }

    public ItemFormMetadataDAO getItemFormMetadataDAO() {
        itemFormMetadataDAO = this.itemFormMetadataDAO == null ? new ItemFormMetadataDAO(sm.getDataSource()) : itemFormMetadataDAO;
        return itemFormMetadataDAO;
    }

    private CoreResources getCoreResources() {
        return (CoreResources) SpringServletAccess.getApplicationContext(context).getBean("coreResources");
    }

}