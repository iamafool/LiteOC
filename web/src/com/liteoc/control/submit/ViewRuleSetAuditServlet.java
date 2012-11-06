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
import com.liteoc.dao.hibernate.RuleSetAuditDao;
import com.liteoc.dao.hibernate.RuleSetRuleAuditDao;
import com.liteoc.dao.login.UserAccountDAO;
import com.liteoc.domain.rule.RuleSetAuditBean;
import com.liteoc.domain.rule.RuleSetBean;
import com.liteoc.domain.rule.RuleSetRuleAuditBean;
import com.liteoc.service.rule.RuleSetServiceInterface;
import com.liteoc.view.Page;
import com.liteoc.web.InsufficientPermissionException;

import java.util.List;

/**
 * @author Krikor Krumlian
 */
public class ViewRuleSetAuditServlet extends SecureController {

    private static String RULESET_ID = "ruleSetId";
    private static String RULESET = "ruleSet";
    private static String RULESETAUDITS = "ruleSetAudits";
    private static String RULESETRULEAUDITS = "ruleSetRuleAudits";
    private RuleSetServiceInterface ruleSetService;
    private RuleSetAuditDao ruleSetAuditDao;
    private RuleSetRuleAuditDao ruleSetRuleAuditDao;
    private UserAccountDAO userAccountDAO;

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
        throw new InsufficientPermissionException(Page.MENU_SERVLET, resexception.getString("not_study_director"), "1");

    }

    @Override
    public void processRequest() throws Exception {

        String ruleSetId = request.getParameter(RULESET_ID);
        if (ruleSetId == null) {
            addPageMessage(respage.getString("please_choose_a_CRF_to_view"));
            forwardPage(Page.CRF_LIST);
        } else {
            RuleSetBean ruleSetBean = getRuleSetService().getRuleSetById(currentStudy, ruleSetId);
            List<RuleSetAuditBean> ruleSetAudits = getRuleSetAuditDao().findAllByRuleSet(ruleSetBean);
            List<RuleSetRuleAuditBean> ruleSetRuleAudits = getRuleSetRuleAuditDao().findAllByRuleSet(ruleSetBean);
            for (RuleSetRuleAuditBean ruleSetRuleAuditBean : ruleSetRuleAudits) {
                ruleSetRuleAuditBean.setUpdater((UserAccountBean) getUserAccountDAO().findByPK(ruleSetRuleAuditBean.getUpdaterId()));
            }
            for (RuleSetAuditBean ruleSetAudit : ruleSetAudits) {
                ruleSetAudit.setUpdater((UserAccountBean) getUserAccountDAO().findByPK(ruleSetAudit.getUpdaterId()));
            }
            request.setAttribute(RULESET, ruleSetBean);
            request.setAttribute(RULESETAUDITS, ruleSetAudits);
            request.setAttribute(RULESETRULEAUDITS, ruleSetRuleAudits);
            forwardPage(Page.VIEW_RULESET_AUDITS);
        }
    }

    @Override
    protected String getAdminServlet() {
        if (ub.isSysAdmin()) {
            return SecureController.ADMIN_SERVLET_CODE;
        } else {
            return "";
        }
    }

    private RuleSetServiceInterface getRuleSetService() {
        ruleSetService =
            this.ruleSetService != null ? ruleSetService : (RuleSetServiceInterface) SpringServletAccess.getApplicationContext(context).getBean(
                    "ruleSetService");
        return ruleSetService;
    }

    private RuleSetAuditDao getRuleSetAuditDao() {
        ruleSetAuditDao =
            this.ruleSetAuditDao != null ? ruleSetAuditDao : (RuleSetAuditDao) SpringServletAccess.getApplicationContext(context).getBean("ruleSetAuditDao");
        return ruleSetAuditDao;
    }

    private RuleSetRuleAuditDao getRuleSetRuleAuditDao() {
        ruleSetRuleAuditDao =
            this.ruleSetRuleAuditDao != null ? ruleSetRuleAuditDao : (RuleSetRuleAuditDao) SpringServletAccess.getApplicationContext(context).getBean(
                    "ruleSetRuleAuditDao");
        return ruleSetRuleAuditDao;
    }

    private UserAccountDAO getUserAccountDAO() {
        return userAccountDAO = this.userAccountDAO != null ? userAccountDAO : new UserAccountDAO(sm.getDataSource());
    }

}
