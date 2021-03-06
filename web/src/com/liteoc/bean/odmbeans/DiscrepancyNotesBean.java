/*
 * OpenClinica is distributed under the GNU Lesser General Public License (GNU
 * LGPL).
 *
 * For details see: http://www.openclinica.org/license copyright 2003-2010 Akaza
 * Research
 *
 */

package com.liteoc.bean.odmbeans;

import java.util.ArrayList;


/**
 *
 * @author ywang (May, 2010)
 *
 */
public class DiscrepancyNotesBean {
    private String entityID;
    private ArrayList<DiscrepancyNoteBean> dns = new ArrayList<DiscrepancyNoteBean>();
    
    public String getEntityID() {
        return entityID;
    }
    public void setEntityID(String entityID) {
        this.entityID = entityID;
    }
    public ArrayList<DiscrepancyNoteBean> getDiscrepancyNotes() {
        return dns;
    }
    public void setAuditLogs(ArrayList<DiscrepancyNoteBean> dns) {
        this.dns = dns;
    }
}