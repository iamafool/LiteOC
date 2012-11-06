package com.liteoc.bean.rule.action;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.liteoc.bean.login.UserAccountBean;
import com.liteoc.bean.managestudy.StudyBean;
import com.liteoc.core.EmailEngine;
import com.liteoc.exception.OpenClinicaSystemException;

import java.util.HashMap;

import javax.mail.MessagingException;
import javax.sql.DataSource;

public class EmailActionProcessor implements ActionProcessor {

    protected final Logger logger = LoggerFactory.getLogger(getClass().getName());
    DataSource ds;
    EmailEngine emailEngine;

    public EmailActionProcessor(DataSource ds) {
        this.ds = ds;
    }

    public void execute(RuleActionBean ruleAction, int itemDataBeanId, String itemData, StudyBean currentStudy, UserAccountBean ub, Object... arguments) {
        HashMap<String, String> arg0 = (HashMap<String, String>) arguments[0];
        sendEmail(ruleAction, ub, arg0.get("body"), arg0.get("subject"));
    }

    private void sendEmail(RuleActionBean ruleAction, UserAccountBean ub, String body, String subject) throws OpenClinicaSystemException {

        logger.info("Sending email...");
        try {
            getEmailEngine().process(((EmailActionBean) ruleAction).getTo().trim(), EmailEngine.getAdminEmail(), subject, body);
            logger.info("Sending email done..");
        } catch (MessagingException me) {
            logger.error("Email could not be sent");
            throw new OpenClinicaSystemException(me.getMessage());
        }
    }

    private EmailEngine getEmailEngine() {
        emailEngine = emailEngine != null ? emailEngine : new EmailEngine(EmailEngine.getSMTPHost(), "5");
        return emailEngine;
    }
}
