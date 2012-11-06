package com.liteoc.dao.hibernate;

import org.hibernate.criterion.Example;
import org.hibernate.criterion.Projections;

import com.liteoc.domain.rule.action.RuleActionRunLogBean;

public class RuleActionRunLogDao extends AbstractDomainDao<RuleActionRunLogBean> {

    @Override
    public Class<RuleActionRunLogBean> domainClass() {
        return RuleActionRunLogBean.class;
    }

    public Integer findCountByRuleActionRunLogBean(RuleActionRunLogBean ruleActionRunLog) {
        Long k =
            (Long) getCurrentSession().createCriteria(domainClass()).add(Example.create(ruleActionRunLog)).setProjection(Projections.rowCount()).list().get(0);
        return k.intValue();
    }

}
