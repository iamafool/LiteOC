package com.liteoc.dao.hibernate;

import com.liteoc.domain.rule.RuleSetBean;
import com.liteoc.domain.rule.RuleSetRuleAuditBean;

import java.util.ArrayList;

public class RuleSetRuleAuditDao extends AbstractDomainDao<RuleSetRuleAuditBean> {

    @Override
    public Class<RuleSetRuleAuditBean> domainClass() {
        return RuleSetRuleAuditBean.class;
    }

    @SuppressWarnings("unchecked")
    public ArrayList<RuleSetRuleAuditBean> findAllByRuleSet(RuleSetBean ruleSet) {
        String query = "from " + getDomainClassName() + " ruleSetRuleAudit  where ruleSetRuleAudit.ruleSetRuleBean.ruleSetBean = :ruleSet  ";
        org.hibernate.Query q = getCurrentSession().createQuery(query);
        q.setParameter("ruleSet", ruleSet);
        return (ArrayList<RuleSetRuleAuditBean>) q.list();
    }
}
