package com.liteoc.domain.rule.action;

import org.springframework.mail.javamail.JavaMailSenderImpl;

import com.liteoc.dao.hibernate.RuleActionRunLogDao;
import com.liteoc.domain.rule.RuleSetBean;
import com.liteoc.domain.rule.RuleSetRuleBean;
import com.liteoc.exception.OpenClinicaSystemException;
import com.liteoc.service.crfdata.DynamicsMetadataService;

import javax.sql.DataSource;

public class ActionProcessorFacade {

    public static ActionProcessor getActionProcessor(ActionType actionType, DataSource ds, JavaMailSenderImpl mailSender,
            DynamicsMetadataService itemMetadataService, RuleSetBean ruleSet, RuleActionRunLogDao ruleActionRunLogDao, RuleSetRuleBean ruleSetRule)
            throws OpenClinicaSystemException {
        switch (actionType) {
        case FILE_DISCREPANCY_NOTE:
            return new DiscrepancyNoteActionProcessor(ds, ruleActionRunLogDao, ruleSetRule);
        case EMAIL:
            return new EmailActionProcessor(ds, mailSender, ruleActionRunLogDao, ruleSetRule);
        case SHOW:
            return new ShowActionProcessor(ds, itemMetadataService, ruleSet);
        case HIDE:
            return new HideActionProcessor(ds, itemMetadataService, ruleSet);
        case INSERT:
            return new InsertActionProcessor(ds, itemMetadataService, ruleActionRunLogDao, ruleSet, ruleSetRule);
        default:
            throw new OpenClinicaSystemException("actionType", "Unrecognized action type!");
        }
    }
}
