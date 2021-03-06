/* 
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).
 * For details see: http://www.openclinica.org/license
 *
 * Copyright 2003-2008 Akaza Research 
 */
package com.liteoc.domain.rule;

import org.apache.commons.collections.FactoryUtils;
import org.apache.commons.collections.list.LazyList;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import com.liteoc.domain.AbstractAuditableMutableDomainObject;
import com.liteoc.domain.rule.action.DiscrepancyNoteActionBean;
import com.liteoc.domain.rule.action.EmailActionBean;
import com.liteoc.domain.rule.action.HideActionBean;
import com.liteoc.domain.rule.action.InsertActionBean;
import com.liteoc.domain.rule.action.RuleActionBean;
import com.liteoc.domain.rule.action.ShowActionBean;
import com.liteoc.domain.rule.action.RuleActionRunBean.Phase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "rule_set_rule")
@GenericGenerator(name = "id-generator", strategy = "native", parameters = { @Parameter(name = "sequence", value = "rule_set_rule_id_seq") })
public class RuleSetRuleBean extends AbstractAuditableMutableDomainObject {

    RuleSetBean ruleSetBean;
    RuleBean ruleBean;
    List<RuleActionBean> actions;
    private List<DiscrepancyNoteActionBean> lazyDiscrepancyNoteActions = LazyList.decorate(new ArrayList<DiscrepancyNoteActionBean>(),
            FactoryUtils.instantiateFactory(DiscrepancyNoteActionBean.class));
    private List<EmailActionBean> lazyEmailActions = LazyList
            .decorate(new ArrayList<EmailActionBean>(), FactoryUtils.instantiateFactory(EmailActionBean.class));
    private List<ShowActionBean> lazyShowActions = LazyList.decorate(new ArrayList<ShowActionBean>(), FactoryUtils.instantiateFactory(ShowActionBean.class));
    private List<HideActionBean> lazyHideActions = LazyList.decorate(new ArrayList<HideActionBean>(), FactoryUtils.instantiateFactory(HideActionBean.class));
    private List<InsertActionBean> lazyInsertActions = LazyList.decorate(new ArrayList<InsertActionBean>(),
            FactoryUtils.instantiateFactory(InsertActionBean.class));

    // Transient
    String oid;
    RuleSetRuleBeanImportStatus ruleSetRuleBeanImportStatus;

    public enum RuleSetRuleBeanImportStatus {
        EXACT_DOUBLE, TO_BE_REMOVED, LINE
    }

    @Transient
    public void formToModel() {
        actions = new ArrayList<RuleActionBean>();
        actions.addAll(lazyDiscrepancyNoteActions);
        actions.addAll(lazyEmailActions);
        actions.addAll(lazyShowActions);
        actions.addAll(lazyHideActions);
    }

    @Transient
    public HashMap<String, ArrayList<RuleActionBean>> getAllActionsWithEvaluatesToAsKey() {
        HashMap<String, ArrayList<RuleActionBean>> h = new HashMap<String, ArrayList<RuleActionBean>>();
        for (RuleActionBean action : actions) {
            String key = action.getExpressionEvaluatesTo().toString();
            if (h.containsKey(key)) {
                h.get(key).add(action);
            } else {
                ArrayList<RuleActionBean> a = new ArrayList<RuleActionBean>();
                a.add(action);
                h.put(key, a);
            }
        }
        return h;
    }

    @Transient
    public HashMap<String, ArrayList<RuleActionBean>> getAllActionsWithEvaluatesToAsKey(String actionEvaluatesTo) {
        HashMap<String, ArrayList<RuleActionBean>> h = new HashMap<String, ArrayList<RuleActionBean>>();
        for (RuleActionBean action : actions) {
            String key = action.getExpressionEvaluatesTo().toString();
            if (actionEvaluatesTo == null || actionEvaluatesTo.equals(key)) {
                if (h.containsKey(key)) {
                    h.get(key).add(action);
                } else {
                    ArrayList<RuleActionBean> a = new ArrayList<RuleActionBean>();
                    a.add(action);
                    h.put(key, a);
                }
            }
        }
        return h;
    }

    @Transient
    public HashMap<String, ArrayList<String>> getActionsAsKeyPair(String actionEvaluatesTo) {
        HashMap<String, ArrayList<String>> h = new HashMap<String, ArrayList<String>>();
        for (RuleActionBean action : actions) {
            String key = action.getExpressionEvaluatesTo().toString();
            if (actionEvaluatesTo.equals(key)) {
                if (h.containsKey(key)) {
                    h.get(key).add(action.getSummary());
                } else {
                    ArrayList<String> a = new ArrayList<String>();
                    a.add(action.getSummary());
                    h.put(key, a);
                }
            }
        }
        return h;
    }

    /**
     * Run the rule and pass in the result. Will return all actions that match the result.
     * 
     * @param actionEvaluatesTo
     * @return
     */
    @Transient
    public List<RuleActionBean> getActions(String ruleEvaluatedTo) {
        List<RuleActionBean> ruleActions = new ArrayList<RuleActionBean>();
        for (RuleActionBean action : actions) {
            String key = action.getExpressionEvaluatesTo().toString();
            if (ruleEvaluatedTo.equals(key)) {
                ruleActions.add(action);
            }
        }
        return ruleActions;
    }

    /**
     * Run the rule and pass in the result. Will return all actions that match the result.
     * 
     * @param actionEvaluatesTo
     * @return
     */
    @Transient
    public List<RuleActionBean> getActions(String ruleEvaluatedTo, Phase phase) {
        List<RuleActionBean> ruleActions = new ArrayList<RuleActionBean>();
        for (RuleActionBean action : actions) {
            String key = action.getExpressionEvaluatesTo().toString();
            if (ruleEvaluatedTo.equals(key) && action.getRuleActionRun().canRun(phase)) {
                ruleActions.add(action);
            }
        }
        return ruleActions;
    }

    @Transient
    public void addAction(RuleActionBean ruleAction) {
        if (actions == null) {
            actions = new ArrayList<RuleActionBean>();
        }
        actions.add(ruleAction);
    }

    // getters & setters
    @ManyToOne
    // @JoinColumn(name = "rule_set_id", nullable = false)
    @JoinColumn(name = "rule_set_id", nullable = false, updatable = false, insertable = false)
    public RuleSetBean getRuleSetBean() {
        return ruleSetBean;
    }

    public void setRuleSetBean(RuleSetBean ruleSetBean) {
        this.ruleSetBean = ruleSetBean;
    }

    @ManyToOne
    @JoinColumn(name = "rule_id", nullable = false)
    public RuleBean getRuleBean() {
        return ruleBean;
    }

    public void setRuleBean(RuleBean ruleBean) {
        this.ruleBean = ruleBean;
    }

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "rule_set_rule_id", nullable = false)
    public List<RuleActionBean> getActions() {
        return actions;
    }

    public void setActions(List<RuleActionBean> actions) {
        this.actions = actions;
    }

    @Transient
    public String getOriginalOid() {
        return oid;
    }

    @Transient
    public String getOid() {
        return this.oid == null && getRuleBean() != null ? getRuleBean().getOid() : oid;
    }

    public void setOid(String oid) {
        this.oid = oid;
    }

    @Transient
    public RuleSetRuleBeanImportStatus getRuleSetRuleBeanImportStatus() {
        return ruleSetRuleBeanImportStatus;
    }

    public void setRuleSetRuleBeanImportStatus(RuleSetRuleBeanImportStatus ruleSetRuleBeanImportStatus) {
        this.ruleSetRuleBeanImportStatus = ruleSetRuleBeanImportStatus;
    }

    @Transient
    public List<DiscrepancyNoteActionBean> getLazyDiscrepancyNoteActions() {
        return lazyDiscrepancyNoteActions;
    }

    public void setLazyDiscrepancyNoteActions(List<DiscrepancyNoteActionBean> lazyDiscrepancyNoteActions) {
        this.lazyDiscrepancyNoteActions = lazyDiscrepancyNoteActions;
    }

    @Transient
    public List<EmailActionBean> getLazyEmailActions() {
        return lazyEmailActions;
    }

    public void setLazyEmailActions(List<EmailActionBean> lazyEmailActions) {
        this.lazyEmailActions = lazyEmailActions;
    }

    @Transient
    public List<ShowActionBean> getLazyShowActions() {
        return lazyShowActions;
    }

    public void setLazyShowActions(List<ShowActionBean> lazyShowActions) {
        this.lazyShowActions = lazyShowActions;
    }

    @Transient
    public List<HideActionBean> getLazyHideActions() {
        return lazyHideActions;
    }

    public void setLazyHideActions(List<HideActionBean> lazyHideActions) {
        this.lazyHideActions = lazyHideActions;
    }

    @Transient
    public List<InsertActionBean> getLazyInsertActions() {
        return lazyInsertActions;
    }

    public void setLazyInsertActions(List<InsertActionBean> lazyInsertActions) {
        this.lazyInsertActions = lazyInsertActions;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (actions == null ? 0 : actions.hashCode());
        result = prime * result + (ruleBean == null ? 0 : ruleBean.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        RuleSetRuleBean other = (RuleSetRuleBean) obj;
        if (actions == null) {
            if (other.actions != null)
                return false;
        } else {// if (!actions.equals(other.actions))
            if (actions.size() != other.actions.size())
                return false;
            for (RuleActionBean ruleActionBean : other.actions) {
                if (!actions.contains(ruleActionBean))
                    return false;
            }
        }
        if (ruleBean == null) {
            if (other.ruleBean != null)
                return false;
        } else if (!ruleBean.equals(other.ruleBean))
            return false;
        return true;
    }

}