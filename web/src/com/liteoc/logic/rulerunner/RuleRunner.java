package com.liteoc.logic.rulerunner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import com.liteoc.bean.admin.CRFBean;
import com.liteoc.bean.managestudy.StudyBean;
import com.liteoc.bean.managestudy.StudyEventBean;
import com.liteoc.bean.managestudy.StudySubjectBean;
import com.liteoc.bean.submit.CRFVersionBean;
import com.liteoc.bean.submit.EventCRFBean;
import com.liteoc.bean.submit.ItemBean;
import com.liteoc.bean.submit.ItemGroupBean;
import com.liteoc.bean.submit.SectionBean;
import com.liteoc.dao.admin.CRFDAO;
import com.liteoc.dao.hibernate.RuleActionRunLogDao;
import com.liteoc.dao.managestudy.StudyDAO;
import com.liteoc.dao.managestudy.StudyEventDAO;
import com.liteoc.dao.managestudy.StudySubjectDAO;
import com.liteoc.dao.rule.RuleSetDAO;
import com.liteoc.dao.rule.RuleSetRuleDAO;
import com.liteoc.dao.rule.action.RuleActionDAO;
import com.liteoc.dao.submit.CRFVersionDAO;
import com.liteoc.dao.submit.EventCRFDAO;
import com.liteoc.dao.submit.ItemDataDAO;
import com.liteoc.dao.submit.ItemFormMetadataDAO;
import com.liteoc.dao.submit.SectionDAO;
import com.liteoc.domain.rule.RuleBulkExecuteContainer;
import com.liteoc.domain.rule.RuleBulkExecuteContainerTwo;
import com.liteoc.domain.rule.RuleSetBean;
import com.liteoc.domain.rule.RuleSetRuleBean;
import com.liteoc.domain.rule.action.RuleActionBean;
import com.liteoc.i18n.util.ResourceBundleProvider;
import com.liteoc.service.crfdata.DynamicsMetadataService;
import com.liteoc.service.rule.expression.ExpressionService;

import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.Set;

import javax.sql.DataSource;

public class RuleRunner {

    protected final Logger logger = LoggerFactory.getLogger(getClass().getName());
    private RuleSetDAO ruleSetDao;
    private RuleSetRuleDAO ruleSetRuleDao;
    private RuleActionDAO ruleActionDao;
    private CRFDAO crfDao;
    private CRFVersionDAO crfVersionDao;
    private StudyEventDAO studyEventDao;
    private ItemDataDAO itemDataDao;
    private ExpressionService expressionService;
    private EventCRFDAO eventCrfDao;
    private StudySubjectDAO studySubjectDao;
    private StudyDAO studyDao;
    private ItemFormMetadataDAO itemFormMetadataDao;
    private SectionDAO sectionDao;
    private final JavaMailSenderImpl mailSender;
    protected RuleRunnerMode ruleRunnerMode;
    protected DynamicsMetadataService dynamicsMetadataService;
    protected RuleActionRunLogDao ruleActionRunLogDao;
    DataSource ds;

    String requestURLMinusServletPath;
    String contextPath;

    public enum RuleRunnerMode {
        DATA_ENTRY, CRF_BULK, RULSET_BULK
    };

    public RuleRunner(DataSource ds, String requestURLMinusServletPath, String contextPath, JavaMailSenderImpl mailSender) {
        this.ds = ds;
        this.requestURLMinusServletPath = requestURLMinusServletPath;
        this.contextPath = contextPath;
        this.mailSender = mailSender;
    }

    String curateMessage(RuleActionBean ruleAction, RuleSetRuleBean ruleSetRule) {

        String message = ruleAction.getSummary();
        String ruleOid = ruleSetRule.getRuleBean().getOid();
        return ruleOid + " " + message;
    }

    HashMap<String, String> prepareEmailContents(RuleSetBean ruleSet, RuleSetRuleBean ruleSetRule, StudyBean currentStudy, RuleActionBean ruleAction) {

        // get the Study Event
        StudyEventBean studyEvent =
            (StudyEventBean) getStudyEventDao().findByPK(
                    Integer.valueOf(getExpressionService().getStudyEventDefenitionOrdninalCurated(ruleSet.getTarget().getValue())));
        // get the Study Subject
        StudySubjectBean studySubject = (StudySubjectBean) getStudySubjectDao().findByPK(studyEvent.getStudySubjectId());
        // get Study/Site Associated with Subject
        StudyBean theStudy = (StudyBean) getStudyDao().findByPK(studySubject.getStudyId());
        String theStudyName, theSiteName = "";
        if (theStudy.getParentStudyId() > 0) {
            StudyBean theParentStudy = (StudyBean) getStudyDao().findByPK(theStudy.getParentStudyId());
            theStudyName = theParentStudy.getName() + " / " + theParentStudy.getIdentifier();
            theSiteName = theStudy.getName() + " / " + theStudy.getIdentifier();
        } else {
            theStudyName = theStudy.getName() + " / " + theStudy.getIdentifier();
        }

        // get the eventCrf & subsequently the CRF Version
        //EventCRFBean eventCrf = (EventCRFBean) getEventCrfDao().findAllByStudyEvent(studyEvent).get(0);
        EventCRFBean eventCrf =
            (EventCRFBean) getEventCrfDao().findAllByStudyEventAndCrfOrCrfVersionOid(studyEvent,
                    getExpressionService().getCrfOid(ruleSet.getTarget().getValue())).get(0);

        CRFVersionBean crfVersion = (CRFVersionBean) getCrfVersionDao().findByPK(eventCrf.getCRFVersionId());
        CRFBean crf = (CRFBean) getCrfDao().findByPK(crfVersion.getCrfId());

        String studyEventDefinitionName = getExpressionService().getStudyEventDefinitionFromExpression(ruleSet.getTarget().getValue(), currentStudy).getName();
        studyEventDefinitionName += " [" + studyEvent.getSampleOrdinal() + "]";

        String itemGroupName = getExpressionService().getItemGroupNameAndOrdinal(ruleSet.getTarget().getValue());
        ItemGroupBean itemGroupBean = getExpressionService().getItemGroupExpression(ruleSet.getTarget().getValue());
        ItemBean itemBean = getExpressionService().getItemExpression(ruleSet.getTarget().getValue(), itemGroupBean);
        String itemName = itemBean.getName();

        SectionBean section =
            (SectionBean) getSectionDAO().findByPK(getItemFormMetadataDAO().findByItemIdAndCRFVersionId(itemBean.getId(), crfVersion.getId()).getSectionId());

        StringBuffer sb = new StringBuffer();
        ResourceBundle respage = ResourceBundleProvider.getPageMessagesBundle();

        sb.append(respage.getString("email_header_1"));

        sb.append(" " + contextPath + " ");
        sb.append(respage.getString("email_header_2"));
        sb.append(" '" + currentStudy.getName() + "' ");
        sb.append(respage.getString("email_header_3"));
        sb.append(" \n\n ");

        sb.append(respage.getString("email_body_1") + " " + theStudyName + " \n ");
        sb.append(respage.getString("email_body_1_a") + " " + theSiteName + " \n ");
        sb.append(respage.getString("email_body_2") + " " + studySubject.getName() + " \n ");
        sb.append(respage.getString("email_body_3") + " " + studyEventDefinitionName + " \n ");
        sb.append(respage.getString("email_body_4") + " " + crf.getName() + " " + crfVersion.getName() + " \n ");
        sb.append(respage.getString("email_body_5") + " " + section.getTitle() + " \n ");
        sb.append(respage.getString("email_body_6") + " " + itemGroupName + " \n ");
        sb.append(respage.getString("email_body_7") + " " + itemName + " \n ");
        sb.append(respage.getString("email_body_8") + " " + ruleAction.getCuratedMessage() + " \n ");

        sb.append(" \n\n ");
        sb.append(respage.getString("email_body_9"));
        sb.append(" " + contextPath + " ");
        sb.append(respage.getString("email_body_10"));
        sb.append(" \n");

        requestURLMinusServletPath = requestURLMinusServletPath == null ? "" : requestURLMinusServletPath;

        sb.append(requestURLMinusServletPath + "/ViewSectionDataEntry?ecId=" + eventCrf.getId() + "&sectionId=" + section.getId() + "&tabId="
            + section.getOrdinal());
        // &eventId="+ studyEvent.getId());
        sb.append("\n\n");
        sb.append(respage.getString("email_footer"));

        String subject = contextPath + " - [" + currentStudy.getName() + "] ";
        String ruleSummary = ruleAction.getSummary() != null ? ruleAction.getSummary() : "";
        String message = ruleSummary.length() < 20 ? ruleSummary : ruleSummary.substring(0, 20) + " ... ";
        subject += message;

        HashMap<String, String> emailContents = new HashMap<String, String>();
        emailContents.put("body", sb.toString());
        emailContents.put("subject", subject);

        return emailContents;
    }

    void logCrfViewSpecificOrderedObjects(HashMap<RuleBulkExecuteContainer, HashMap<RuleBulkExecuteContainerTwo, Set<String>>> crfViewSpecificOrderedObjects) {
        for (RuleBulkExecuteContainer key1 : crfViewSpecificOrderedObjects.keySet()) {
            for (RuleBulkExecuteContainerTwo key2 : crfViewSpecificOrderedObjects.get(key1).keySet()) {
                String studySubjects = "";
                for (String studySubjectIds : crfViewSpecificOrderedObjects.get(key1).get(key2)) {
                    studySubjects += studySubjectIds + " : ";
                }
                logger.debug("key1 {} , key2 {} , studySubjectId {}", new Object[] { key1.toString(), key2.toString(), studySubjects });
            }
        }
    }

    ExpressionService getExpressionService() {
        expressionService = this.expressionService != null ? expressionService : new ExpressionService(ds);
        return expressionService;
    }

    RuleSetDAO getRuleSetDao() {
        ruleSetDao = this.ruleSetDao != null ? ruleSetDao : new RuleSetDAO(ds);
        return ruleSetDao;
    }

    CRFDAO getCrfDao() {
        crfDao = this.crfDao != null ? crfDao : new CRFDAO(ds);
        return crfDao;
    }

    RuleSetRuleDAO getRuleSetRuleDao() {
        ruleSetRuleDao = this.ruleSetRuleDao != null ? ruleSetRuleDao : new RuleSetRuleDAO(ds);
        return ruleSetRuleDao;
    }

    RuleActionDAO getRuleActionDao() {
        ruleActionDao = this.ruleActionDao != null ? ruleActionDao : new RuleActionDAO(ds);
        return ruleActionDao;
    }

    StudyEventDAO getStudyEventDao() {
        studyEventDao = this.studyEventDao != null ? studyEventDao : new StudyEventDAO(ds);
        return studyEventDao;
    }

    ItemDataDAO getItemDataDao() {
        itemDataDao = this.itemDataDao != null ? itemDataDao : new ItemDataDAO(ds);
        return itemDataDao;
    }

    EventCRFDAO getEventCrfDao() {
        eventCrfDao = this.eventCrfDao != null ? eventCrfDao : new EventCRFDAO(ds);
        return eventCrfDao;
    }

    CRFVersionDAO getCrfVersionDao() {
        crfVersionDao = this.crfVersionDao != null ? crfVersionDao : new CRFVersionDAO(ds);
        return crfVersionDao;
    }

    StudySubjectDAO getStudySubjectDao() {
        studySubjectDao = this.studySubjectDao != null ? studySubjectDao : new StudySubjectDAO(ds);
        return studySubjectDao;
    }

    ItemFormMetadataDAO getItemFormMetadataDAO() {
        itemFormMetadataDao = this.itemFormMetadataDao != null ? itemFormMetadataDao : new ItemFormMetadataDAO(ds);
        return itemFormMetadataDao;
    }

    SectionDAO getSectionDAO() {
        sectionDao = this.sectionDao != null ? sectionDao : new SectionDAO(ds);
        return sectionDao;
    }

    StudyDAO getStudyDao() {
        studyDao = this.studyDao != null ? studyDao : new StudyDAO(ds);
        return studyDao;
    }

    public JavaMailSenderImpl getMailSender() {
        return mailSender;
    }

    public DynamicsMetadataService getDynamicsMetadataService() {
        return dynamicsMetadataService;
    }

    public void setDynamicsMetadataService(DynamicsMetadataService dynamicsMetadataService) {
        this.dynamicsMetadataService = dynamicsMetadataService;
    }

    public RuleActionRunLogDao getRuleActionRunLogDao() {
        return ruleActionRunLogDao;
    }

    public void setRuleActionRunLogDao(RuleActionRunLogDao ruleActionRunLogDao) {
        this.ruleActionRunLogDao = ruleActionRunLogDao;
    }
}