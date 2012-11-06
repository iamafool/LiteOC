/* 
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).
 * For details see: http://www.openclinica.org/license
 *
 * Copyright 2003-2008 Akaza Research 
 */
package com.liteoc.control.submit;


import com.liteoc.bean.core.Role;
import com.liteoc.bean.login.UserAccountBean;
import com.liteoc.control.SpringServletAccess;
import com.liteoc.control.core.SecureController;
import com.liteoc.dao.hibernate.RuleSetDao;
import com.liteoc.dao.hibernate.RuleSetRuleAuditDao;
import com.liteoc.dao.hibernate.RuleSetRuleDao;
import com.liteoc.domain.Status;
import com.liteoc.domain.rule.RuleSetBean;
import com.liteoc.domain.rule.RuleSetRuleAuditBean;
import com.liteoc.domain.rule.RuleSetRuleBean;
import com.liteoc.service.rule.RuleSetServiceInterface;
import com.liteoc.view.Page;
import com.liteoc.web.InsufficientPermissionException;

/**
 * @author Krikor Krumlian
 *
 */
public class RemoveRuleSetServlet extends SecureController {

    private static final long serialVersionUID = 1L;
    RuleSetDao ruleSetDao;
    RuleSetServiceInterface ruleSetService;
    RuleSetRuleAuditDao ruleSetRuleAuditDao;
    RuleSetRuleDao ruleSetRuleDao;

    private static String RULESET_ID = "ruleSetId";
    private static String RULESET = "ruleSet";
    private static String ACTION = "action";

    @Override
    public void mayProceed() throws InsufficientPermissionException {
        if (ub.isSysAdmin()) {
            return;
        }

        if (currentRole.getRole().equals(Role.STUDYDIRECTOR) || currentRole.getRole().equals(Role.COORDINATOR)) {
            return;
        }

        addPageMessage(respage.getString("no_have_correct_privilege_current_study") + respage.getString("change_study_contact_sysadmin"));
        throw new InsufficientPermissionException(Page.LIST_DEFINITION_SERVLET, resexception.getString("not_study_director"), "1");

    }

    @Override
    public void processRequest() throws Exception {
        String ruleSetId = request.getParameter(RULESET_ID);
        String action = request.getParameter(ACTION);
        if (ruleSetId == null) {
            addPageMessage(respage.getString("please_choose_a_CRF_to_view"));
            forwardPage(Page.CRF_LIST);
        } else {
            RuleSetBean ruleSetBean = null;
            ruleSetBean = getRuleSetService().getRuleSetById(currentStudy, ruleSetId);
            if (action != null && action.equals("confirm")) {
                request.setAttribute(RULESET, ruleSetBean);
                forwardPage(Page.REMOVE_RULE_SET);
            } else {
                for (RuleSetRuleBean ruleSetRuleBean : ruleSetBean.getRuleSetRules()) {
                    if (ruleSetRuleBean.getStatus() != Status.DELETED) {
                        ruleSetRuleBean.setStatus(Status.DELETED);
                        ruleSetRuleBean.setUpdater(ub);
                        ruleSetRuleBean = getRuleSetRuleDao().saveOrUpdate(ruleSetRuleBean);
                        createRuleSetRuleAuditBean(ruleSetRuleBean, ub, Status.DELETED);
                    }
                }
                forwardPage(Page.LIST_RULE_SETS_SERVLET);
            }
        }
    }

    private void createRuleSetRuleAuditBean(RuleSetRuleBean ruleSetRuleBean, UserAccountBean ub, Status status) {
        RuleSetRuleAuditBean ruleSetRuleAuditBean = new RuleSetRuleAuditBean();
        ruleSetRuleAuditBean.setRuleSetRuleBean(ruleSetRuleBean);
        ruleSetRuleAuditBean.setUpdater(ub);
        ruleSetRuleAuditBean.setStatus(status);
        getRuleSetRuleAuditDao().saveOrUpdate(ruleSetRuleAuditBean);
    }

    /**
     * @return the ruleSetDao
     */
    public RuleSetDao getRuleSetDao() {
        return ruleSetDao;
    }

    /**
     * @param ruleSetDao the ruleSetDao to set
     */
    public void setRuleSetDao(RuleSetDao ruleSetDao) {
        this.ruleSetDao = ruleSetDao;
    }

    private RuleSetRuleAuditDao getRuleSetRuleAuditDao() {
        ruleSetRuleAuditDao =
            this.ruleSetRuleAuditDao != null ? ruleSetRuleAuditDao : (RuleSetRuleAuditDao) SpringServletAccess.getApplicationContext(context).getBean(
                    "ruleSetRuleAuditDao");
        return ruleSetRuleAuditDao;
    }

    private RuleSetRuleDao getRuleSetRuleDao() {
        ruleSetRuleDao =
            this.ruleSetRuleDao != null ? ruleSetRuleDao : (RuleSetRuleDao) SpringServletAccess.getApplicationContext(context).getBean("ruleSetRuleDao");
        return ruleSetRuleDao;
    }

    private RuleSetServiceInterface getRuleSetService() {
        ruleSetService =
            this.ruleSetService != null ? ruleSetService : (RuleSetServiceInterface) SpringServletAccess.getApplicationContext(context).getBean(
                    "ruleSetService");
        // TODO: Add getRequestURLMinusServletPath(),getContextPath()
        return ruleSetService;
    }

}
