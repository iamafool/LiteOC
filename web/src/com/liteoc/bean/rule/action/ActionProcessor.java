package com.liteoc.bean.rule.action;

import com.liteoc.bean.login.UserAccountBean;
import com.liteoc.bean.managestudy.StudyBean;

public interface ActionProcessor {

    public void execute(RuleActionBean ruleAction, int itemDataBeanId, String itemData, StudyBean currentStudy, UserAccountBean ub, Object... arguments);
}
