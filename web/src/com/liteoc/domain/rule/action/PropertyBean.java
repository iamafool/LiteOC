package com.liteoc.domain.rule.action;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import com.liteoc.domain.AbstractMutableDomainObject;
import com.liteoc.domain.rule.expression.ExpressionBean;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "rule_action_property")
@GenericGenerator(name = "id-generator", strategy = "native", parameters = { @Parameter(name = "sequence", value = "rule_action_property_id_seq") })
public class PropertyBean extends AbstractMutableDomainObject {

    private String oid;
    private String value;
    private ExpressionBean valueExpression;

    @Column(name = "oc_oid")
    public String getOid() {
        return oid;
    }

    public void setOid(String oid) {
        this.oid = oid;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "rule_expression_id")
    public ExpressionBean getValueExpression() {
        return valueExpression;
    }

    public void setValueExpression(ExpressionBean valueExpression) {
        this.valueExpression = valueExpression;
    }

    @Override
    public String toString() {
        return "PropertyBean [oid=" + oid + ", value=" + value + ", valueExpression=" + valueExpression + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((oid == null) ? 0 : oid.hashCode());
        result = prime * result + ((value == null) ? 0 : value.hashCode());
        result = prime * result + ((valueExpression == null) ? 0 : valueExpression.hashCode());
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
        PropertyBean other = (PropertyBean) obj;
        if (oid == null) {
            if (other.oid != null)
                return false;
        } else if (!oid.equals(other.oid))
            return false;
        if (value == null) {
            if (other.value != null)
                return false;
        } else if (!value.equals(other.value))
            return false;
        if (valueExpression == null) {
            if (other.valueExpression != null)
                return false;
        } else if (!valueExpression.equals(other.valueExpression))
            return false;
        return true;
    }

}
