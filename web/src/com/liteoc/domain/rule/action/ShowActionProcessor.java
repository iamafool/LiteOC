package com.liteoc.domain.rule.action;


import com.liteoc.bean.login.UserAccountBean;
import com.liteoc.bean.managestudy.StudyBean;
import com.liteoc.bean.submit.ItemDataBean;
import com.liteoc.domain.rule.RuleSetBean;
import com.liteoc.logic.rulerunner.ExecutionMode;
import com.liteoc.logic.rulerunner.RuleRunner.RuleRunnerMode;
import com.liteoc.service.crfdata.DynamicsMetadataService;

import javax.sql.DataSource;

public class ShowActionProcessor implements ActionProcessor {

    DataSource ds;
    DynamicsMetadataService itemMetadataService;
    RuleSetBean ruleSet;

    public ShowActionProcessor(DataSource ds, DynamicsMetadataService itemMetadataService, RuleSetBean ruleSet) {
        this.itemMetadataService = itemMetadataService;
        this.ruleSet = ruleSet;
        this.ds = ds;
    }

    public RuleActionBean execute(RuleRunnerMode ruleRunnerMode, ExecutionMode executionMode, RuleActionBean ruleAction, ItemDataBean itemDataBean,
            String itemData, StudyBean currentStudy, UserAccountBean ub, Object... arguments) {

        switch (executionMode) {
        case DRY_RUN: {
            if (ruleRunnerMode == RuleRunnerMode.DATA_ENTRY) {
                return null;
            } else {
                dryRun(ruleAction, itemDataBean, itemData, currentStudy, ub);
            }
        }
        case SAVE: {
            if (ruleRunnerMode == RuleRunnerMode.DATA_ENTRY) {
                return saveAndReturnMessage(ruleAction, itemDataBean, itemData, currentStudy, ub);
            } else {
                return save(ruleAction, itemDataBean, itemData, currentStudy, ub);
            }
        }
        default:
            return null;
        }
    }

    private RuleActionBean save(RuleActionBean ruleAction, ItemDataBean itemDataBean, String itemData, StudyBean currentStudy, UserAccountBean ub) {
        getItemMetadataService().showNew(itemDataBean.getId(), ((ShowActionBean) ruleAction).getProperties(), ub, ruleSet);
        return ruleAction;
    }

    private RuleActionBean saveAndReturnMessage(RuleActionBean ruleAction, ItemDataBean itemDataBean, String itemData, StudyBean currentStudy,
            UserAccountBean ub) {
        getItemMetadataService().showNew(itemDataBean.getId(), ((ShowActionBean) ruleAction).getProperties(), ub, ruleSet);
        return ruleAction;
    }

    private RuleActionBean dryRun(RuleActionBean ruleAction, ItemDataBean itemDataBean, String itemData, StudyBean currentStudy, UserAccountBean ub) {
        return ruleAction;
    }

    private DynamicsMetadataService getItemMetadataService() {
        return itemMetadataService;
    }

}
