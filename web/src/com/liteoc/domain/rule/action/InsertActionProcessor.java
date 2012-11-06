package com.liteoc.domain.rule.action;

import com.liteoc.bean.login.UserAccountBean;
import com.liteoc.bean.managestudy.StudyBean;
import com.liteoc.bean.submit.ItemDataBean;
import com.liteoc.dao.hibernate.RuleActionRunLogDao;
import com.liteoc.domain.rule.RuleSetBean;
import com.liteoc.domain.rule.RuleSetRuleBean;
import com.liteoc.logic.rulerunner.ExecutionMode;
import com.liteoc.logic.rulerunner.RuleRunner.RuleRunnerMode;
import com.liteoc.service.crfdata.DynamicsMetadataService;

import javax.sql.DataSource;

public class InsertActionProcessor implements ActionProcessor {

    DataSource ds;
    DynamicsMetadataService itemMetadataService;
    RuleActionRunLogDao ruleActionRunLogDao;
    RuleSetBean ruleSet;
    RuleSetRuleBean ruleSetRule;

    public InsertActionProcessor(DataSource ds, DynamicsMetadataService itemMetadataService, RuleActionRunLogDao ruleActionRunLogDao, RuleSetBean ruleSet,
            RuleSetRuleBean ruleSetRule) {
        this.itemMetadataService = itemMetadataService;
        this.ruleSet = ruleSet;
        this.ruleSetRule = ruleSetRule;
        this.ruleActionRunLogDao = ruleActionRunLogDao;
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
                save(ruleAction, itemDataBean, itemData, currentStudy, ub);
            } else {
                save(ruleAction, itemDataBean, itemData, currentStudy, ub);
            }
        }
        default:
            return null;
        }
    }

    private RuleActionBean save(RuleActionBean ruleAction, ItemDataBean itemDataBean, String itemData, StudyBean currentStudy, UserAccountBean ub) {
        getItemMetadataService().insert(itemDataBean.getId(), ((InsertActionBean) ruleAction).getProperties(), ub, ruleSet);
        RuleActionRunLogBean ruleActionRunLog =
            new RuleActionRunLogBean(ruleAction.getActionType(), itemDataBean, itemDataBean.getValue(), ruleSetRule.getRuleBean().getOid());
        if (ruleActionRunLogDao.findCountByRuleActionRunLogBean(ruleActionRunLog) > 0) {
        } else {
            ruleActionRunLogDao.saveOrUpdate(ruleActionRunLog);
        }
        return null;
    }

    private RuleActionBean saveAndReturnMessage(RuleActionBean ruleAction, ItemDataBean itemDataBean, String itemData, StudyBean currentStudy,
            UserAccountBean ub) {
        //
        return ruleAction;
    }

    private RuleActionBean dryRun(RuleActionBean ruleAction, ItemDataBean itemDataBean, String itemData, StudyBean currentStudy, UserAccountBean ub) {
        return ruleAction;
    }

    private DynamicsMetadataService getItemMetadataService() {
        return itemMetadataService;
    }

}
