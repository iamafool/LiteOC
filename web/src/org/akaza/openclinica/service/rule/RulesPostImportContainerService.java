/* 
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).
 * For details see: http://www.openclinica.org/license
 *
 * Copyright 2003-2008 Akaza Research 
 */
package org.akaza.openclinica.service.rule;

import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.EventDefinitionCRFBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.oid.GenericOidGenerator;
import org.akaza.openclinica.bean.oid.OidGenerator;
import org.akaza.openclinica.dao.hibernate.RuleDao;
import org.akaza.openclinica.dao.hibernate.RuleSetDao;
import org.akaza.openclinica.domain.Status;
import org.akaza.openclinica.domain.rule.AuditableBeanWrapper;
import org.akaza.openclinica.domain.rule.RuleBean;
import org.akaza.openclinica.domain.rule.RuleSetBean;
import org.akaza.openclinica.domain.rule.RuleSetRuleBean;
import org.akaza.openclinica.domain.rule.RulesPostImportContainer;
import org.akaza.openclinica.domain.rule.RuleSetRuleBean.RuleSetRuleBeanImportStatus;
import org.akaza.openclinica.domain.rule.action.EmailActionBean;
import org.akaza.openclinica.domain.rule.action.HideActionBean;
import org.akaza.openclinica.domain.rule.action.InsertActionBean;
import org.akaza.openclinica.domain.rule.action.PropertyBean;
import org.akaza.openclinica.domain.rule.action.RuleActionBean;
import org.akaza.openclinica.domain.rule.action.ShowActionBean;
import org.akaza.openclinica.domain.rule.expression.Context;
import org.akaza.openclinica.domain.rule.expression.ExpressionBean;
import org.akaza.openclinica.domain.rule.expression.ExpressionObjectWrapper;
import org.akaza.openclinica.domain.rule.expression.ExpressionProcessor;
import org.akaza.openclinica.domain.rule.expression.ExpressionProcessorFactory;
import org.akaza.openclinica.service.rule.expression.ExpressionService;
import org.akaza.openclinica.validator.rule.action.InsertActionValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.DataBinder;
import org.springframework.validation.Errors;

import java.text.MessageFormat;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;

import javax.sql.DataSource;

/**
 * @author Krikor Krumlian
 * 
 */
public class RulesPostImportContainerService {

    protected final Logger logger = LoggerFactory.getLogger(getClass().getName());
    DataSource ds;
    private RuleDao ruleDao;
    private RuleSetDao ruleSetDao;
    private final OidGenerator oidGenerator;
    private StudyBean currentStudy;
    private UserAccountBean userAccount;

    private ExpressionService expressionService;
    private InsertActionValidator insertActionValidator;
    ResourceBundle respage;

    public RulesPostImportContainerService(DataSource ds, StudyBean currentStudy) {
        oidGenerator = new GenericOidGenerator();
        this.ds = ds;
        this.currentStudy = currentStudy;
    }

    public RulesPostImportContainerService(DataSource ds) {
        oidGenerator = new GenericOidGenerator();
        this.ds = ds;
    }

    public RulesPostImportContainer validateRuleSetRuleDefs(RulesPostImportContainer importContainer, RuleSetRuleBean ruleSetRuleForm) {
        RuleSetBean ruleSetBean = ruleSetRuleForm.getRuleSetBean();
        RuleSetRuleBean ruleSetRuleBean = ruleSetRuleForm;
        AuditableBeanWrapper<RuleSetBean> ruleSetBeanWrapper = new AuditableBeanWrapper<RuleSetBean>(ruleSetBean);
        ruleSetBeanWrapper.getAuditableBean().setStudy(currentStudy);
        if (isRuleSetExpressionValid(ruleSetBeanWrapper)) {
            RuleSetBean persistentRuleSetBean = getRuleSetDao().findByExpressionAndStudy(ruleSetBean,currentStudy.getId());

            if (persistentRuleSetBean != null) {
                List<RuleSetRuleBean> importedRuleSetRules = ruleSetBeanWrapper.getAuditableBean().getRuleSetRules();
                persistentRuleSetBean.setUpdaterAndDate(getUserAccount());
                ruleSetBeanWrapper.setAuditableBean(persistentRuleSetBean);
                for (RuleSetRuleBean persistentruleSetRuleBean : persistentRuleSetBean.getRuleSetRules()) {
                    if (persistentruleSetRuleBean.getStatus() != Status.DELETED && ruleSetRuleBean.equals(persistentruleSetRuleBean)) {
                        persistentruleSetRuleBean.setRuleSetRuleBeanImportStatus(RuleSetRuleBeanImportStatus.EXACT_DOUBLE);
                        // TODO : DO SOMETHING HERE
                        // itr.remove();
                        break;
                    } else if (persistentruleSetRuleBean.getStatus() != Status.DELETED && ruleSetRuleBean.getRuleBean() != null
                        && ruleSetRuleBean.getRuleBean().equals(persistentruleSetRuleBean.getRuleBean())) {
                        // persistentruleSetRuleBean.setActions(ruleSetRuleBean.getActions());
                        persistentruleSetRuleBean.setRuleSetRuleBeanImportStatus(RuleSetRuleBeanImportStatus.TO_BE_REMOVED);
                        // itr.remove();
                        break;
                    }
                    ruleSetRuleBean.setRuleSetRuleBeanImportStatus(RuleSetRuleBeanImportStatus.LINE);
                }
                ruleSetBeanWrapper.getAuditableBean().addRuleSetRules(importedRuleSetRules);
                // ruleSetBeanWrapper.getAuditableBean().setId(persistentRuleSetBean.getId());
            } else {
                ruleSetBeanWrapper.getAuditableBean().setOwner(getUserAccount());
                ruleSetBeanWrapper.getAuditableBean().setStudyEventDefinition(
                        getExpressionService().getStudyEventDefinitionFromExpression(ruleSetBean.getTarget().getValue()));
                ruleSetBeanWrapper.getAuditableBean().setCrf(getExpressionService().getCRFFromExpression(ruleSetBean.getTarget().getValue()));
                ruleSetBeanWrapper.getAuditableBean().setCrfVersion(getExpressionService().getCRFVersionFromExpression(ruleSetBean.getTarget().getValue()));
                ruleSetBeanWrapper.getAuditableBean().setItem(getExpressionService().getItemBeanFromExpression(ruleSetBean.getTarget().getValue()));
                ruleSetBeanWrapper.getAuditableBean().setItemGroup(getExpressionService().getItemGroupExpression(ruleSetBean.getTarget().getValue()));
            }
            isRuleSetRuleValid(importContainer, ruleSetBeanWrapper);
        }
        putRuleSetInCorrectContainer(ruleSetBeanWrapper, importContainer);
        logger.info("# of Valid RuleSetDefs : " + importContainer.getValidRuleSetDefs().size());
        logger.info("# of InValid RuleSetDefs : " + importContainer.getInValidRuleSetDefs().size());
        logger.info("# of Overwritable RuleSetDefs : " + importContainer.getDuplicateRuleSetDefs().size());
        return importContainer;
    }

    public RulesPostImportContainer validateRuleSetDefs(RulesPostImportContainer importContainer) {
        for (RuleSetBean ruleSetBean : importContainer.getRuleSets()) {
            AuditableBeanWrapper<RuleSetBean> ruleSetBeanWrapper = new AuditableBeanWrapper<RuleSetBean>(ruleSetBean);
            ruleSetBeanWrapper.getAuditableBean().setStudy(currentStudy);
            if (isRuleSetExpressionValid(ruleSetBeanWrapper)) {
                RuleSetBean persistentRuleSetBean = getRuleSetDao().findByExpressionAndStudy(ruleSetBean,currentStudy.getId());

                if (persistentRuleSetBean != null) {
                    List<RuleSetRuleBean> importedRuleSetRules = ruleSetBeanWrapper.getAuditableBean().getRuleSetRules();
                    persistentRuleSetBean.setUpdaterAndDate(getUserAccount());
                    ruleSetBeanWrapper.setAuditableBean(persistentRuleSetBean);
                    Iterator<RuleSetRuleBean> itr = importedRuleSetRules.iterator();
                    while (itr.hasNext()) {
                        RuleSetRuleBean ruleSetRuleBean = itr.next();
                        ruleSetRuleBean.setRuleBean(getRuleDao().findByOid(ruleSetRuleBean.getOid(), persistentRuleSetBean.getStudyId()));
                        // ruleSetRuleBean.setRuleSetBean(ruleSetBeanWrapper.getAuditableBean());
                        for (RuleSetRuleBean persistentruleSetRuleBean : persistentRuleSetBean.getRuleSetRules()) {
                            if (persistentruleSetRuleBean.getStatus() != Status.DELETED && ruleSetRuleBean.equals(persistentruleSetRuleBean)) {
                                persistentruleSetRuleBean.setRuleSetRuleBeanImportStatus(RuleSetRuleBeanImportStatus.EXACT_DOUBLE);
                                itr.remove();
                                break;
                            } else if (persistentruleSetRuleBean.getStatus() != Status.DELETED && ruleSetRuleBean.getRuleBean() != null
                                && ruleSetRuleBean.getRuleBean().equals(persistentruleSetRuleBean.getRuleBean())) {
                                // persistentruleSetRuleBean.setActions(ruleSetRuleBean.getActions());
                                persistentruleSetRuleBean.setRuleSetRuleBeanImportStatus(RuleSetRuleBeanImportStatus.TO_BE_REMOVED);
                                // itr.remove();
                                break;
                            }
                            ruleSetRuleBean.setRuleSetRuleBeanImportStatus(RuleSetRuleBeanImportStatus.LINE);
                        }
                    }
                    ruleSetBeanWrapper.getAuditableBean().addRuleSetRules(importedRuleSetRules);
                    // ruleSetBeanWrapper.getAuditableBean().setId(persistentRuleSetBean.getId());
                } else {
                    if (importContainer.getValidRuleSetExpressionValues().contains(ruleSetBeanWrapper.getAuditableBean().getTarget().getValue())) {
                        ruleSetBeanWrapper.error(createError("OCRERR_0031"));
                    }
                    ruleSetBeanWrapper.getAuditableBean().setOwner(getUserAccount());
                    ruleSetBeanWrapper.getAuditableBean().setStudyEventDefinition(
                            getExpressionService().getStudyEventDefinitionFromExpression(ruleSetBean.getTarget().getValue()));
                    ruleSetBeanWrapper.getAuditableBean().setCrf(getExpressionService().getCRFFromExpression(ruleSetBean.getTarget().getValue()));
                    ruleSetBeanWrapper.getAuditableBean().setCrfVersion(getExpressionService().getCRFVersionFromExpression(ruleSetBean.getTarget().getValue()));
                    ruleSetBeanWrapper.getAuditableBean().setItem(getExpressionService().getItemBeanFromExpression(ruleSetBean.getTarget().getValue()));
                    ruleSetBeanWrapper.getAuditableBean().setItemGroup(getExpressionService().getItemGroupExpression(ruleSetBean.getTarget().getValue()));
                }
                isRuleSetRuleValid(importContainer, ruleSetBeanWrapper);
            }
            putRuleSetInCorrectContainer(ruleSetBeanWrapper, importContainer);
        }
        logger.info("# of Valid RuleSetDefs : " + importContainer.getValidRuleSetDefs().size());
        logger.info("# of InValid RuleSetDefs : " + importContainer.getInValidRuleSetDefs().size());
        logger.info("# of Overwritable RuleSetDefs : " + importContainer.getDuplicateRuleSetDefs().size());
        return importContainer;
    }

    public RulesPostImportContainer validateRuleDefs(RulesPostImportContainer importContainer) {
        for (RuleBean ruleBean : importContainer.getRuleDefs()) {
            AuditableBeanWrapper<RuleBean> ruleBeanWrapper = new AuditableBeanWrapper<RuleBean>(ruleBean);
            ruleBeanWrapper.getAuditableBean().setStudy(currentStudy);
            // Remove illegal characters from expression value
            ruleBeanWrapper.getAuditableBean().getExpression()
                    .setValue(ruleBeanWrapper.getAuditableBean().getExpression().getValue().trim().replaceAll("(\n|\t|\r)", " "));

            if (isRuleOidValid(ruleBeanWrapper) && isRuleExpressionValid(ruleBeanWrapper, null)) {
                RuleBean persistentRuleBean = getRuleDao().findByOid(ruleBeanWrapper.getAuditableBean());
                if (persistentRuleBean != null) {
                    String name = ruleBeanWrapper.getAuditableBean().getName();
                    String expressionValue = ruleBeanWrapper.getAuditableBean().getExpression().getValue();
                    String expressionContextName = ruleBeanWrapper.getAuditableBean().getExpression().getContextName();
                    String description = ruleBeanWrapper.getAuditableBean().getDescription();
                    Context context = expressionContextName != null ? Context.getByName(expressionContextName) : Context.OC_RULES_V1;
                    persistentRuleBean.setUpdaterAndDate(getUserAccount());
                    ruleBeanWrapper.setAuditableBean(persistentRuleBean);
                    ruleBeanWrapper.getAuditableBean().setName(name);
                    ruleBeanWrapper.getAuditableBean().setDescription(description);
                    ruleBeanWrapper.getAuditableBean().getExpression().setValue(expressionValue);
                    ruleBeanWrapper.getAuditableBean().getExpression().setContext(context);
                    doesPersistentRuleBeanBelongToCurrentStudy(ruleBeanWrapper);
                    // ruleBeanWrapper.getAuditableBean().setId(persistentRuleBean.getId());
                    // ruleBeanWrapper.getAuditableBean().getExpression().setId(persistentRuleBean.getExpression().getId());
                } else {
                    ruleBeanWrapper.getAuditableBean().setOwner(getUserAccount());
                }
            }
            putRuleInCorrectContainer(ruleBeanWrapper, importContainer);
        }
        logger.info("# of Valid RuleDefs : {} , # of InValid RuleDefs : {} , # of Overwritable RuleDefs : {}", new Object[] {
            importContainer.getValidRuleDefs().size(), importContainer.getInValidRuleDefs().size(), importContainer.getDuplicateRuleDefs().size() });
        return importContainer;
    }

    private void putRuleSetInCorrectContainer(AuditableBeanWrapper<RuleSetBean> ruleSetBeanWrapper, RulesPostImportContainer importContainer) {
        if (!ruleSetBeanWrapper.isSavable()) {
            importContainer.getInValidRuleSetDefs().add(ruleSetBeanWrapper);
        } else if (getExpressionService().getEventDefinitionCRF(ruleSetBeanWrapper.getAuditableBean().getTarget().getValue()) != null
            && getExpressionService().getEventDefinitionCRF(ruleSetBeanWrapper.getAuditableBean().getTarget().getValue()).getStatus().isDeleted()) {
            importContainer.getInValidRuleSetDefs().add(ruleSetBeanWrapper);
        } else if (ruleSetBeanWrapper.getAuditableBean().getId() == null) {
            importContainer.getValidRuleSetDefs().add(ruleSetBeanWrapper);
            importContainer.getValidRuleSetExpressionValues().add(ruleSetBeanWrapper.getAuditableBean().getTarget().getValue());
        } else if (ruleSetBeanWrapper.getAuditableBean().getId() != null) {
            importContainer.getDuplicateRuleSetDefs().add(ruleSetBeanWrapper);
        }
    }

    private void putRuleInCorrectContainer(AuditableBeanWrapper<RuleBean> ruleBeanWrapper, RulesPostImportContainer importContainer) {
        if (!ruleBeanWrapper.isSavable()) {
            importContainer.getInValidRuleDefs().add(ruleBeanWrapper);
            importContainer.getInValidRules().put(ruleBeanWrapper.getAuditableBean().getOid(), ruleBeanWrapper);
        } else if (ruleBeanWrapper.getAuditableBean().getId() == null) {
            importContainer.getValidRuleDefs().add(ruleBeanWrapper);
            importContainer.getValidRules().put(ruleBeanWrapper.getAuditableBean().getOid(), ruleBeanWrapper);
        } else if (ruleBeanWrapper.getAuditableBean().getId() != null) {
            importContainer.getDuplicateRuleDefs().add(ruleBeanWrapper);
            importContainer.getValidRules().put(ruleBeanWrapper.getAuditableBean().getOid(), ruleBeanWrapper);
        }
    }

    /**
     * If the RuleSet contains any RuleSetRule object with an invalid RuleRef OID (OID that is not in DB or in the Valid Rule Lists) , Then add an error to the
     * ruleSetBeanWrapper, which in terms will make the RuleSet inValid.
     * 
     * @param importContainer
     * @param ruleSetBeanWrapper
     */
    private void isRuleSetRuleValid(RulesPostImportContainer importContainer, AuditableBeanWrapper<RuleSetBean> ruleSetBeanWrapper) {
        for (RuleSetRuleBean ruleSetRuleBean : ruleSetBeanWrapper.getAuditableBean().getRuleSetRules()) {
            String ruleDefOid = ruleSetRuleBean.getOid();
            if (ruleSetRuleBean.getId() == null || ruleSetRuleBean.getRuleSetRuleBeanImportStatus() == RuleSetRuleBeanImportStatus.EXACT_DOUBLE) {
                EventDefinitionCRFBean eventDefinitionCRFBean =
                    getExpressionService().getEventDefinitionCRF(ruleSetBeanWrapper.getAuditableBean().getTarget().getValue());
                if (eventDefinitionCRFBean != null && eventDefinitionCRFBean.getStatus().isDeleted()) {
                    ruleSetBeanWrapper.error(createError("OCRERR_0026"));
                }
                if (importContainer.getInValidRules().get(ruleDefOid) != null || importContainer.getValidRules().get(ruleDefOid) == null
                    && getRuleDao().findByOid(ruleDefOid, ruleSetBeanWrapper.getAuditableBean().getStudyId()) == null) {
                    ruleSetBeanWrapper.error(createError("OCRERR_0025"));
                }
                if (importContainer.getValidRules().get(ruleDefOid) != null) {
                    AuditableBeanWrapper<RuleBean> r = importContainer.getValidRules().get(ruleDefOid);
                    if (!isRuleExpressionValid(r, ruleSetBeanWrapper.getAuditableBean()))
                        ruleSetBeanWrapper.error(createError("OCRERR_0027"));
                }
                if (importContainer.getValidRules().get(ruleDefOid) == null) {
                    RuleBean rule = getRuleDao().findByOid(ruleDefOid, ruleSetBeanWrapper.getAuditableBean().getStudyId());
                    AuditableBeanWrapper<RuleBean> r = new AuditableBeanWrapper<RuleBean>(rule);
                    if (rule == null || !isRuleExpressionValid(r, ruleSetBeanWrapper.getAuditableBean()))
                        ruleSetBeanWrapper.error(createError("OCRERR_0027"));
                }

                if (ruleSetRuleBean.getActions().size() == 0) {
                    ruleSetBeanWrapper.error(createError("OCRERR_0027"));
                }

                for (RuleActionBean ruleActionBean : ruleSetRuleBean.getActions()) {
                    isRuleActionValid(ruleActionBean, ruleSetBeanWrapper, eventDefinitionCRFBean);
                }

            }
        }
    }

    private void isRuleActionValid(RuleActionBean ruleActionBean, AuditableBeanWrapper<RuleSetBean> ruleSetBeanWrapper,
            EventDefinitionCRFBean eventDefinitionCRFBean) {
        if (ruleActionBean instanceof ShowActionBean) {
            List<PropertyBean> properties = ((ShowActionBean) ruleActionBean).getProperties();
            if (ruleActionBean.getRuleActionRun().getBatch() == true || ruleActionBean.getRuleActionRun().getImportDataEntry() == true) {
                ruleSetBeanWrapper.error("ShowAction " + ((ShowActionBean) ruleActionBean).toString()
                    + " is not Valid. You cannot have ImportDataEntry=\"true\" Batch=\"true\". ");
            }
            for (PropertyBean propertyBean : properties) {
                String result = getExpressionService().checkValidityOfItemOrItemGroupOidInCrf(propertyBean.getOid(), ruleSetBeanWrapper.getAuditableBean());
                // String result = getExpressionService().isExpressionValid(oid, ruleSetBeanWrapper.getAuditableBean(), 2) ? "OK" : "";
                if (!result.equals("OK")) {
                    ruleSetBeanWrapper.error("ShowAction OID " + result + " is not Valid. ");
                }
            }
        }
        if (ruleActionBean instanceof HideActionBean) {
            List<PropertyBean> properties = ((HideActionBean) ruleActionBean).getProperties();
            if (ruleActionBean.getRuleActionRun().getBatch() == true || ruleActionBean.getRuleActionRun().getImportDataEntry() == true) {
                ruleSetBeanWrapper.error("HideAction " + ((HideActionBean) ruleActionBean).toString()
                    + " is not Valid. You cannot have ImportDataEntry=\"true\" Batch=\"true\". ");
            }
            for (PropertyBean propertyBean : properties) {
                String result = getExpressionService().checkValidityOfItemOrItemGroupOidInCrf(propertyBean.getOid(), ruleSetBeanWrapper.getAuditableBean());
                // String result = getExpressionService().isExpressionValid(oid, ruleSetBeanWrapper.getAuditableBean(), 2) ? "OK" : "";
                if (!result.equals("OK")) {
                    ruleSetBeanWrapper.error("HideAction OID " + result + " is not Valid. ");
                }
            }
        }
        if (ruleActionBean instanceof InsertActionBean) {
            if (ruleActionBean.getRuleActionRun().getBatch() == true || ruleActionBean.getRuleActionRun().getImportDataEntry() == true) {
                ruleSetBeanWrapper.error("InsertAction " + ((InsertActionBean) ruleActionBean).toString() + " is not Valid. ");
            }
            DataBinder dataBinder = new DataBinder(ruleActionBean);
            Errors errors = dataBinder.getBindingResult();
            InsertActionValidator insertActionValidator = getInsertActionValidator();
            insertActionValidator.setEventDefinitionCRFBean(eventDefinitionCRFBean);
            insertActionValidator.setRuleSetBean(ruleSetBeanWrapper.getAuditableBean());
            insertActionValidator.setExpressionService(expressionService);
            insertActionValidator.validate(ruleActionBean, errors);
            if (errors.hasErrors()) {
                ruleSetBeanWrapper.error("InsertAction is not valid: " + errors.getAllErrors().get(0).getDefaultMessage());
            }
        }
        if (ruleActionBean instanceof EmailActionBean) {
            if (ruleActionBean.getRuleActionRun().getImportDataEntry() == true) {
                ruleSetBeanWrapper.error("EmailAction " + ((EmailActionBean) ruleActionBean).toString()
                    + " is not Valid.You cannot have ImportDataEntry=\"true\". ");
            }
        }
    }

    private String createError(String key) {
        MessageFormat mf = new MessageFormat("");
        mf.applyPattern(respage.getString(key));
        Object[] arguments = {};
        return key + ": " + mf.format(arguments);
    }

    private boolean isRuleExpressionValid(AuditableBeanWrapper<RuleBean> ruleBeanWrapper, RuleSetBean ruleSet) {
        boolean isValid = true;
        ExpressionBean expressionBean = isExpressionValid(ruleBeanWrapper.getAuditableBean().getExpression(), ruleBeanWrapper);
        ExpressionObjectWrapper eow = new ExpressionObjectWrapper(ds, currentStudy, expressionBean, ruleSet);
        ExpressionProcessor ep = ExpressionProcessorFactory.createExpressionProcessor(eow);
        ep.setRespage(respage);
        String errorString = ep.isRuleExpressionValid();
        if (errorString != null) {
            ruleBeanWrapper.error(errorString);
            isValid = false;
        }
        return isValid;
    }

    private boolean isRuleSetExpressionValid(AuditableBeanWrapper<RuleSetBean> beanWrapper) {
        boolean isValid = true;
        ExpressionBean expressionBean = isExpressionValid(beanWrapper.getAuditableBean().getTarget(), beanWrapper);
        ExpressionObjectWrapper eow = new ExpressionObjectWrapper(ds, currentStudy, expressionBean);
        ExpressionProcessor ep = ExpressionProcessorFactory.createExpressionProcessor(eow);
        ep.setRespage(respage);
        String errorString = ep.isRuleAssignmentExpressionValid();
        if (errorString != null) {
            beanWrapper.error(errorString);
            isValid = false;
        }
        return isValid;
    }

    private ExpressionBean isExpressionValid(ExpressionBean expressionBean, AuditableBeanWrapper<?> beanWrapper) {

        if (expressionBean.getContextName() == null && expressionBean.getContext() == null) {
            expressionBean.setContext(Context.OC_RULES_V1);
        }
        if (expressionBean.getContextName() != null && expressionBean.getContext() == null) {
            beanWrapper.warning(createError("OCRERR_0029"));
            expressionBean.setContext(Context.OC_RULES_V1);
        }
        return expressionBean;
    }

    private boolean isRuleOidValid(AuditableBeanWrapper<RuleBean> ruleBeanWrapper) {
        boolean isValid = true;
        try {
            oidGenerator.validate(ruleBeanWrapper.getAuditableBean().getOid());
        } catch (Exception e) {
            ruleBeanWrapper.error(createError("OCRERR_0028"));
            isValid = false;
        }
        return isValid;
    }

    private boolean doesPersistentRuleBeanBelongToCurrentStudy(AuditableBeanWrapper<RuleBean> ruleBeanWrapper) {
        boolean isValid = true;
        if (ruleBeanWrapper.getAuditableBean().getRuleSetRules().size() > 0) {
            int studyId = ruleBeanWrapper.getAuditableBean().getRuleSetRules().get(0).getRuleSetBean().getStudyId();
            if (studyId != currentStudy.getId()) {
                ruleBeanWrapper.error(createError("OCRERR_0030"));
                isValid = false;
            }
        }
        return isValid;
    }

    /**
     * @return the ruleDao
     */
    public RuleDao getRuleDao() {
        return ruleDao;
    }

    /**
     * @param ruleDao
     *            the ruleDao to set
     */
    public void setRuleDao(RuleDao ruleDao) {
        this.ruleDao = ruleDao;
    }

    /**
     * @return the ruleSetDao
     */
    public RuleSetDao getRuleSetDao() {
        return ruleSetDao;
    }

    /**
     * @param ruleSetDao
     *            the ruleSetDao to set
     */
    public void setRuleSetDao(RuleSetDao ruleSetDao) {
        this.ruleSetDao = ruleSetDao;
    }

    /**
     * @return the currentStudy
     */
    public StudyBean getCurrentStudy() {
        return currentStudy;
    }

    /**
     * @param currentStudy
     *            the currentStudy to set
     */
    public void setCurrentStudy(StudyBean currentStudy) {
        this.currentStudy = currentStudy;
    }

    public InsertActionValidator getInsertActionValidator() {
        return insertActionValidator;
    }

    public void setInsertActionValidator(InsertActionValidator insertActionValidator) {
        this.insertActionValidator = insertActionValidator;
    }

    /**
     * @return the respage
     */
    public ResourceBundle getRespage() {
        return respage;
    }

    /**
     * @param respage
     */
    public void setRespage(ResourceBundle respage) {
        this.respage = respage;
    }

    /**
     * @return userAccount
     */
    public UserAccountBean getUserAccount() {
        return userAccount;
    }

    /**
     * @param userAccount
     */
    public void setUserAccount(UserAccountBean userAccount) {
        this.userAccount = userAccount;
    }

    private ExpressionService getExpressionService() {
        expressionService =
            this.expressionService != null ? expressionService : new ExpressionService(new ExpressionObjectWrapper(ds, currentStudy, null, null));
        expressionService.setExpressionWrapper(new ExpressionObjectWrapper(ds, currentStudy, null, null));

        return expressionService;
    }
}
