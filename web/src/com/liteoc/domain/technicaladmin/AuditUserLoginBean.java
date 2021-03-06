/* 
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).
 * For details see: http://www.openclinica.org/license
 *
 * Copyright 2003-2008 Akaza Research 
 */
package com.liteoc.domain.technicaladmin;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import com.liteoc.bean.login.UserAccountBean;
import com.liteoc.domain.AbstractMutableDomainObject;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * <p>
 * Audit User Login
 * </p>
 * 
 * @author Krikor Krumlian
 */
@Entity
@Table(name = "audit_user_login")
@GenericGenerator(name = "id-generator", strategy = "native", parameters = { @Parameter(name = "sequence", value = "audit_user_login_id_seq") })
public class AuditUserLoginBean extends AbstractMutableDomainObject {

    private String userName;
    private UserAccountBean userAccount;
    private Date loginAttemptDate;
    private LoginStatus loginStatus;

    private Integer userAccountId;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Transient
    public UserAccountBean getUserAccount() {
        return userAccount;
    }

    public void setUserAccount(UserAccountBean userAccount) {
        if (this.userAccount != null) {
            this.userAccountId = userAccount.getId();
        }
        this.userAccount = userAccount;
    }

    public Date getLoginAttemptDate() {
        return loginAttemptDate;
    }

    public void setLoginAttemptDate(Date loginAttemptDate) {
        this.loginAttemptDate = loginAttemptDate;
    }

    public Integer getUserAccountId() {
        return userAccountId;
    }

    public void setUserAccountId(Integer userAccountId) {
        this.userAccountId = userAccountId;
    }

    @Type(type = "loginStatus")
    @Column(name = "login_status_code")
    public LoginStatus getLoginStatus() {
        return loginStatus;
    }

    public void setLoginStatus(LoginStatus loginStatus) {
        this.loginStatus = loginStatus;
    }

}