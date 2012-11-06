package com.liteoc.domain.rule.action;

import com.liteoc.bean.login.UserAccountBean;
import com.liteoc.bean.managestudy.StudyBean;
import com.liteoc.bean.submit.ItemDataBean;
import com.liteoc.logic.rulerunner.ExecutionMode;
import com.liteoc.logic.rulerunner.RuleRunner.RuleRunnerMode;

public interface ActionProcessor {

    public RuleActionBean execute(RuleRunnerMode ruleRunnerMode, ExecutionMode executionMode, RuleActionBean ruleAction, ItemDataBean itemDataBean, String itemData,
            StudyBean currentStudy, UserAccountBean ub, Object... arguments);
}
