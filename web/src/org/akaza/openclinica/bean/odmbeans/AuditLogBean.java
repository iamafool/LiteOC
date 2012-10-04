/*
 * OpenClinica is distributed under the GNU Lesser General Public License (GNU
 * LGPL).
 *
 * For details see: http://www.openclinica.org/license copyright 2003-2010 Akaza
 * Research
 *
 */

package org.akaza.openclinica.bean.odmbeans;

import java.util.Date;


/**
 *
 * @author ywang (March, 2010)
 *
 */
public class AuditLogBean extends ElementOIDBean {
    private String userId;
    private Date datetimeStamp;
    private String type;
    private String reasonForChange;
    private String oldValue;
    private String newValue;
    
    public String getUserId() {
        return userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }
    public Date getDatetimeStamp() {
        return datetimeStamp;
    }
    public void setDatetimeStamp(Date datetimeStamp) {
        this.datetimeStamp = datetimeStamp;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public String getReasonForChange() {
        return reasonForChange;
    }
    public void setReasonForChange(String reasonForChange) {
        this.reasonForChange = reasonForChange;
    }
    public String getOldValue() {
        return oldValue;
    }
    public void setOldValue(String oldValue) {
        this.oldValue = oldValue;
    }
    public String getNewValue() {
        return newValue;
    }
    public void setNewValue(String newValue) {
        this.newValue = newValue;
    }
}