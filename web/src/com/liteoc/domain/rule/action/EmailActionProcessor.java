package com.liteoc.domain.rule.action;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;

import com.liteoc.bean.login.UserAccountBean;
import com.liteoc.bean.managestudy.StudyBean;
import com.liteoc.bean.submit.ItemDataBean;
import com.liteoc.core.EmailEngine;
import com.liteoc.dao.hibernate.RuleActionRunLogDao;
import com.liteoc.domain.rule.RuleSetRuleBean;
import com.liteoc.exception.OpenClinicaSystemException;
import com.liteoc.logic.rulerunner.ExecutionMode;
import com.liteoc.logic.rulerunner.RuleRunner.RuleRunnerMode;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.StringTokenizer;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.sql.DataSource;

public class EmailActionProcessor implements ActionProcessor {

    protected final Logger logger = LoggerFactory.getLogger(getClass().getName());
    DataSource ds;
    EmailEngine emailEngine;
    JavaMailSenderImpl mailSender;
    RuleActionRunLogDao ruleActionRunLogDao;
    RuleSetRuleBean ruleSetRule;

    public EmailActionProcessor(DataSource ds, JavaMailSenderImpl mailSender, RuleActionRunLogDao ruleActionRunLogDao, RuleSetRuleBean ruleSetRule) {
        this.ds = ds;
        this.mailSender = mailSender;
        this.ruleSetRule = ruleSetRule;
        this.ruleActionRunLogDao = ruleActionRunLogDao;
    }

    public RuleActionBean execute(RuleRunnerMode ruleRunnerMode, ExecutionMode executionMode, RuleActionBean ruleAction, ItemDataBean itemDataBean,
            String itemData, StudyBean currentStudy, UserAccountBean ub, Object... arguments) {
        switch (executionMode) {
        case DRY_RUN: {
            return ruleAction;
        }

        case SAVE: {
            HashMap<String, String> arg0 = (HashMap<String, String>) arguments[0];
            sendEmail(ruleAction, ub, arg0.get("body"), arg0.get("subject"));
            RuleActionRunLogBean ruleActionRunLog =
                new RuleActionRunLogBean(ruleAction.getActionType(), itemDataBean, itemDataBean.getValue(), ruleSetRule.getRuleBean().getOid());
            ruleActionRunLogDao.saveOrUpdate(ruleActionRunLog);
            return null;
        }
        default:
            return null;
        }
    }

    private void sendEmail(RuleActionBean ruleAction, UserAccountBean ub, String body, String subject) throws OpenClinicaSystemException {

        logger.info("Sending email...");
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();

            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);
            helper.setFrom(EmailEngine.getAdminEmail());
            helper.setTo(processMultipleImailAddresses(((EmailActionBean) ruleAction).getTo().trim()));
            helper.setSubject(subject);
            helper.setText(body);

            mailSender.send(mimeMessage);
            logger.debug("Email sent successfully on {}", new Date());
        } catch (MailException me) {
            logger.error("Email could not be sent");
            throw new OpenClinicaSystemException(me.getMessage());
        } catch (MessagingException me) {
            logger.error("Email could not be sent");
            throw new OpenClinicaSystemException(me.getMessage());
        }
    }

    private InternetAddress[] processMultipleImailAddresses(String to) throws MessagingException {
        ArrayList<String> recipientsArray = new ArrayList<String>();
        StringTokenizer st = new StringTokenizer(to, ",");
        while (st.hasMoreTokens()) {
            recipientsArray.add(st.nextToken());
        }

        int sizeTo = recipientsArray.size();
        InternetAddress[] addressTo = new InternetAddress[sizeTo];
        for (int i = 0; i < sizeTo; i++) {
            addressTo[i] = new InternetAddress(recipientsArray.get(i).toString());
        }
        return addressTo;

    }

}