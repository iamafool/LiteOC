/* OpenClinica is distributed under the GNU Lesser General Public License (GNU
 * LGPL).
 *
 * For details see: http://www.openclinica.org/license copyright 2003-2005 Akaza
 * Research
 *
 */
package com.liteoc.bean.extract.odm;

import org.apache.commons.lang.StringEscapeUtils;

import com.liteoc.bean.odmbeans.AuditLogBean;
import com.liteoc.bean.odmbeans.AuditLogsBean;
import com.liteoc.bean.odmbeans.ChildNoteBean;
import com.liteoc.bean.odmbeans.DiscrepancyNoteBean;
import com.liteoc.bean.odmbeans.DiscrepancyNotesBean;
import com.liteoc.bean.odmbeans.OdmClinicalDataBean;
import com.liteoc.bean.submit.crfdata.ExportFormDataBean;
import com.liteoc.bean.submit.crfdata.ExportStudyEventDataBean;
import com.liteoc.bean.submit.crfdata.ExportSubjectDataBean;
import com.liteoc.bean.submit.crfdata.ImportItemDataBean;
import com.liteoc.bean.submit.crfdata.ImportItemGroupDataBean;
import com.liteoc.bean.submit.crfdata.SubjectGroupDataBean;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Create ODM XML ClinicalData Element for a study.
 * 
 * @author ywang (May, 2008)
 */

public class ClinicalDataReportBean extends OdmXmlReportBean {
    private OdmClinicalDataBean clinicalData;

    public ClinicalDataReportBean(OdmClinicalDataBean clinicaldata) {
        super();
        this.clinicalData = clinicaldata;
    }

    /**
     * has not been implemented yet
     */
    @Override
    public void createOdmXml(boolean isDataset) {
        // this.addHeading();
        // this.addRootStartLine();
        // addNodeClinicalData();
        // this.addRootEndLine();
    }

    public void addNodeClinicalData(boolean header, boolean footer) {
        String ODMVersion = this.getODMVersion();
        // when collecting data, only item with value has been collected.
        StringBuffer xml = this.getXmlOutput();
        String indent = this.getIndent();
        String nls = System.getProperty("line.separator");
        if (header) {
            xml.append(indent + "<ClinicalData StudyOID=\"" + StringEscapeUtils.escapeXml(clinicalData.getStudyOID()) + "\" MetaDataVersionOID=\""
                + StringEscapeUtils.escapeXml(this.clinicalData.getMetaDataVersionOID()) + "\">");
            xml.append(nls);
        }
        ArrayList<ExportSubjectDataBean> subs = (ArrayList<ExportSubjectDataBean>) this.clinicalData.getExportSubjectData();
        for (ExportSubjectDataBean sub : subs) {
            xml.append(indent + indent + "<SubjectData SubjectKey=\"" + StringEscapeUtils.escapeXml(sub.getSubjectOID()));
            if ("oc1.2".equalsIgnoreCase(ODMVersion) || "oc1.3".equalsIgnoreCase(ODMVersion)) {
                xml.append("\" OpenClinica:StudySubjectID=\"" + StringEscapeUtils.escapeXml(sub.getStudySubjectId()));
                String uniqueIdentifier = sub.getUniqueIdentifier();
                if (uniqueIdentifier != null && uniqueIdentifier.length() > 0) {
                    xml.append("\" OpenClinica:UniqueIdentifier=\"" + StringEscapeUtils.escapeXml(uniqueIdentifier));
                }
                String status = sub.getStatus();
                if (status != null && status.length() > 0) {
                    xml.append("\" OpenClinica:Status=\"" + StringEscapeUtils.escapeXml(status));
                }
                String secondaryId = sub.getSecondaryId();
                if (secondaryId != null && secondaryId.length() > 0) {
                    xml.append("\"  OpenClinica:SecondaryID=\"" + StringEscapeUtils.escapeXml(secondaryId));
                }
                Integer year = sub.getYearOfBirth();
                if (year != null) {
                    xml.append("\" OpenClinica:YearOfBirth=\"" + sub.getYearOfBirth());
                } else {
                    if (sub.getDateOfBirth() != null) {
                        xml.append("\" OpenClinica:DateOfBirth=\"" + sub.getDateOfBirth());
                    }
                }
                String gender = sub.getSubjectGender();
                if (gender != null && gender.length() > 0) {
                    xml.append("\" OpenClinica:Sex=\"" + StringEscapeUtils.escapeXml(gender));
                }
            }
            xml.append("\">");
            xml.append(nls);
            //
            ArrayList<ExportStudyEventDataBean> ses = (ArrayList<ExportStudyEventDataBean>) sub.getExportStudyEventData();
            for (ExportStudyEventDataBean se : ses) {
                xml.append(indent + indent + indent + "<StudyEventData StudyEventOID=\"" + StringEscapeUtils.escapeXml(se.getStudyEventOID()));
                if ("oc1.2".equalsIgnoreCase(ODMVersion) || "oc1.3".equalsIgnoreCase(ODMVersion)) {
                    String location = se.getLocation();
                    if (location != null && location.length() > 0) {
                        xml.append("\" OpenClinica:StudyEventLocation=\"" + StringEscapeUtils.escapeXml(location));
                    }
                    String startDate = se.getStartDate();
                    if (startDate != null && startDate.length() > 0) {
                        xml.append("\" OpenClinica:StartDate=\"" + startDate);
                    }
                    String endDate = se.getEndDate();
                    if (endDate != null && endDate.length() > 0) {
                        xml.append("\" OpenClinica:EndDate=\"" + endDate);
                    }
                    String status = se.getStatus();
                    if (status != null && status.length() > 0) {
                        xml.append("\" OpenClinica:Status=\"" + StringEscapeUtils.escapeXml(status));
                    }
                    if (se.getAgeAtEvent() != null) {
                        xml.append("\" OpenClinica:SubjectAgeAtEvent=\"" + se.getAgeAtEvent());
                    }
                }
                xml.append('"');
                if (!"-1".equals(se.getStudyEventRepeatKey())) {
                    xml.append(" StudyEventRepeatKey=\"" + se.getStudyEventRepeatKey() + "\"");
                }
                xml.append('>');
                xml.append(nls);
                //
                ArrayList<ExportFormDataBean> forms = se.getExportFormData();
                for (ExportFormDataBean form : forms) {
                    xml.append(indent + indent + indent + indent + "<FormData FormOID=\"" + StringEscapeUtils.escapeXml(form.getFormOID()));
                    if ("oc1.2".equalsIgnoreCase(ODMVersion) || "oc1.3".equalsIgnoreCase(ODMVersion)) {
                        String crfVersion = form.getCrfVersion();
                        if (crfVersion != null && crfVersion.length() > 0) {
                            xml.append("\" OpenClinica:Version=\"" + StringEscapeUtils.escapeXml(crfVersion));
                        }
                        String interviewerName = form.getInterviewerName();
                        if (interviewerName != null && interviewerName.length() > 0) {
                            xml.append("\" OpenClinica:InterviewerName=\"" + StringEscapeUtils.escapeXml(interviewerName));
                        }
                        if (form.getInterviewDate() != null) {
                            xml.append("\" OpenClinica:InterviewDate=\"" + form.getInterviewDate());
                        }
                        String status = form.getStatus();
                        if (status != null && status.length() > 0) {
                            xml.append("\" OpenClinica:Status=\"" + StringEscapeUtils.escapeXml(status));
                        }
                    }
                    xml.append("\">");
                    xml.append(nls);
                    //
                    ArrayList<ImportItemGroupDataBean> igs = form.getItemGroupData();
                    for (ImportItemGroupDataBean ig : igs) {
                        xml.append(indent + indent + indent + indent + indent + "<ItemGroupData ItemGroupOID=\""
                            + StringEscapeUtils.escapeXml(ig.getItemGroupOID()) + "\" ");
                        if (!"-1".equals(ig.getItemGroupRepeatKey())) {
                            xml.append("ItemGroupRepeatKey=\"" + ig.getItemGroupRepeatKey() + "\" ");
                        }
                        xml.append("TransactionType=\"Insert\">");
                        xml.append(nls);
                        ArrayList<ImportItemDataBean> items = ig.getItemData();
                        for (ImportItemDataBean item : items) {
                            boolean printValue = true;
                            xml.append(indent + indent + indent + indent + indent + indent + "<ItemData ItemOID=\""
                                    + StringEscapeUtils.escapeXml(item.getItemOID()) + "\" ");
                            if ("Yes".equals(item.getIsNull())) {
                                xml.append("IsNull=\"Yes\"");
                                if(!item.isHasValueWithNull()) {
                                    printValue = false;
                                }
                                if ("oc1.2".equalsIgnoreCase(ODMVersion) || "oc1.3".equalsIgnoreCase(ODMVersion)) {
                                    xml.append(" OpenClinica:ReasonForNull=\"" + StringEscapeUtils.escapeXml(item.getReasonForNull()) + "\" ");
                                    if(!printValue) {
                                        xml.append("/>");
                                        xml.append(nls);
                                    }
                                }
                            } 
                            if(printValue) {
                                Boolean hasElm = false;
                                xml.append("Value=\"" + StringEscapeUtils.escapeXml(item.getValue()) + "\"");

                                String muRefOid = item.getMeasurementUnitRef().getElementDefOID();
                                if (muRefOid != null && muRefOid.length() > 0) {
                                    if (!hasElm) {
                                        xml.append('>');
                                        xml.append(nls);
                                        hasElm = true;
                                    }
                                    xml.append(indent + indent + indent + indent + indent + indent + indent + "<MeasurementUnitRef MeasurementUnitOID=\""
                                        + StringEscapeUtils.escapeXml(muRefOid) + "\"/>");
                                    xml.append(nls);
                                }
                                //

                                if ("oc1.2".equalsIgnoreCase(ODMVersion) || "oc1.3".equalsIgnoreCase(ODMVersion)) {
                                    if (item.getAuditLogs() != null && item.getAuditLogs().getAuditLogs().size() > 0) {
                                        if (!hasElm) {
                                            xml.append('>');
                                            xml.append(nls);
                                            hasElm = true;
                                        }
                                        this.addAuditLogs(item.getAuditLogs(), indent + indent + indent + indent + indent + indent + indent);
                                    }
                                    //
                                    if (item.getDiscrepancyNotes() != null && item.getDiscrepancyNotes().getDiscrepancyNotes().size() > 0) {
                                        if (!hasElm) {
                                            xml.append('>');
                                            xml.append(nls);
                                            hasElm = true;
                                        }
                                        this.addDiscrepancyNotes(item.getDiscrepancyNotes(), indent + indent + indent + indent + indent + indent + indent);
                                    }
                                }
                                if (hasElm) {
                                    xml.append(indent + indent + indent + indent + indent + indent + "</ItemData>");
                                    xml.append(nls);
                                    hasElm = false;
                                } else {
                                    xml.append("/>");
                                    xml.append(nls);
                                }
                            }
                        }
                        xml.append(indent + indent + indent + indent + indent + "</ItemGroupData>");
                        xml.append(nls);
                    }
                    //
                    if ("oc1.2".equalsIgnoreCase(ODMVersion) || "oc1.3".equalsIgnoreCase(ODMVersion)) {
                        if (form.getAuditLogs() != null && form.getAuditLogs().getAuditLogs().size() > 0) {
                            this.addAuditLogs(form.getAuditLogs(), indent + indent + indent + indent + indent);
                        }
                        //
                        if (form.getDiscrepancyNotes() != null && form.getDiscrepancyNotes().getDiscrepancyNotes().size() > 0) {
                            this.addDiscrepancyNotes(form.getDiscrepancyNotes(), indent + indent + indent + indent + indent);
                        }
                    }
                    xml.append(indent + indent + indent + indent + "</FormData>");
                    xml.append(nls);
                }
                //
                if ("oc1.2".equalsIgnoreCase(ODMVersion) || "oc1.3".equalsIgnoreCase(ODMVersion)) {
                    if (se.getAuditLogs() != null && se.getAuditLogs().getAuditLogs().size() > 0) {
                        this.addAuditLogs(se.getAuditLogs(), indent + indent + indent + indent);
                    }
                    //
                    if (se.getDiscrepancyNotes() != null && se.getDiscrepancyNotes().getDiscrepancyNotes().size() > 0) {
                        this.addDiscrepancyNotes(se.getDiscrepancyNotes(), indent + indent + indent + indent);
                    }
                }
                xml.append(indent + indent + indent + "</StudyEventData>");
                xml.append(nls);
            }
            if ("oc1.2".equalsIgnoreCase(ODMVersion) || "oc1.3".equalsIgnoreCase(ODMVersion)) {
                ArrayList<SubjectGroupDataBean> sgddata = (ArrayList<SubjectGroupDataBean>) sub.getSubjectGroupData();
                if (sgddata.size() > 0) {
                    for (SubjectGroupDataBean sgd : sgddata) {
                        String cid =
                            sgd.getStudyGroupClassId() != null ? "OpenClinica:StudyGroupClassID=\"" + StringEscapeUtils.escapeXml(sgd.getStudyGroupClassId())
                                + "\" " : "";
                        if (cid.length() > 0) {
                            String cn =
                                sgd.getStudyGroupClassName() != null ? "OpenClinica:StudyGroupClassName=\""
                                    + StringEscapeUtils.escapeXml(sgd.getStudyGroupClassName()) + "\" " : "";
                            String gn =
                                sgd.getStudyGroupName() != null ? "OpenClinica:StudyGroupName=\"" + StringEscapeUtils.escapeXml(sgd.getStudyGroupName())
                                    + "\" " : "";
                            xml.append(indent + indent + indent + "<OpenClinica:SubjectGroupData " + cid + cn + gn);
                        }
                        xml.append(" />");
                        xml.append(nls);
                    }
                }
                //
                if (sub.getAuditLogs() != null && sub.getAuditLogs().getAuditLogs().size() > 0) {
                    this.addAuditLogs(sub.getAuditLogs(), indent + indent + indent);
                }
                //
                if (sub.getDiscrepancyNotes() != null && sub.getDiscrepancyNotes().getDiscrepancyNotes().size() > 0) {
                    this.addDiscrepancyNotes(sub.getDiscrepancyNotes(), indent + indent + indent);
                }
            }
            xml.append(indent + indent + "</SubjectData>");
            xml.append(nls);
        }
        if (footer) {
            xml.append(indent + "</ClinicalData>");
            xml.append(nls);
        }
    }
    
    protected void addAuditLogs(AuditLogsBean auditLogs, String currentIndent) {
        if (auditLogs != null) {
            ArrayList<AuditLogBean> audits = auditLogs.getAuditLogs();
            if (audits != null && audits.size() > 0) {
                StringBuffer xml = this.getXmlOutput();
                String indent = this.getIndent();
                String nls = System.getProperty("line.separator");
                xml.append(currentIndent + "<OpenClinica:AuditLogs EntityID=\"" + auditLogs.getEntityID() + "\">");
                xml.append(nls);
                for (AuditLogBean audit : audits) {
                    this.addOneAuditLog(audit, currentIndent + indent);
                }
                xml.append(currentIndent + "</OpenClinica:AuditLogs>");
                xml.append(nls);
            }
        }
    }

    /*
    protected void addAuditLogs(AuditLogsBean auditLogs, String currentIndent) {
        if (auditLogs != null) {
            ArrayList<AuditLogBean> audits = auditLogs.getAuditLogs();
            if (audits != null && audits.size() > 0) {
                StringBuffer xml = this.getXmlOutput();
                String indent = this.getIndent();
                String nls = System.getProperty("line.separator");
                xml.append(currentIndent + "<OpenClinica:AuditLogs OpenClinica:EntityID=\"" + auditLogs.getEntityID() + "\">");
                xml.append(nls);
                for (AuditLogBean audit : audits) {
                    this.addOneAuditLog(audit, currentIndent + indent);
                }
                xml.append(currentIndent + "</OpenClinica:AuditLogs>");
                xml.append(nls);
            }
        }
    }
    */

    protected void addOneAuditLog(AuditLogBean audit, String currentIndent) {
        if (audit != null) {
            StringBuffer xml = this.getXmlOutput();
            String indent = this.getIndent();
            String nls = System.getProperty("line.separator");
            String i = audit.getOid();
            String u = audit.getUserId();
            Date d = audit.getDatetimeStamp();
            String t = audit.getType();
            String r = audit.getReasonForChange();
            String o = audit.getOldValue();
            String n = audit.getNewValue();
            Boolean p = i.length() > 0 || u.length() > 0 || d != null || t.length() > 0 || r.length() > 0 || o.length() > 0 || n.length() > 0 ? true : false;
            if (p) {
                xml.append(currentIndent + "<OpenClinica:AuditLog ");
                if (i.length() > 0) {
                    xml.append("ID=\"" + StringEscapeUtils.escapeXml(i) + "\" ");
                }
                if (u.length() > 0) {
                    xml.append("UserID=\"" + StringEscapeUtils.escapeXml(u) + "\" ");
                }
                if (d != null) {
                    xml.append("DateTimeStamp=\"" + new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(d) + "\" ");
                }
                if (t.length() > 0) {
                    xml.append(nls);
                    xml.append(currentIndent + "                      Type=\"" + t + "\" ");
                }
                if (r.length() > 0) {
                    xml.append(nls);
                    xml.append(currentIndent + "                      ReasonForChange=\"" + StringEscapeUtils.escapeXml(r) + "\" ");
                }
                if (o.length() > 0) {
                    xml.append(nls);
                    xml.append(currentIndent + "                      OldValue=\"" + StringEscapeUtils.escapeXml(o) + "\" ");
                }
                if (n.length() > 0) {
                    xml.append(nls);
                    xml.append(currentIndent + "                      NewValue=\"" + StringEscapeUtils.escapeXml(n) + "\"");
                }
                xml.append("/>");
                xml.append(nls);
            }
        }
    }
    
    /*
    protected void addOneAuditLog(AuditLogBean audit, String currentIndent) {
        if (audit != null) {
            StringBuffer xml = this.getXmlOutput();
            String indent = this.getIndent();
            String nls = System.getProperty("line.separator");
            String i = audit.getOid();
            String u = audit.getUserId();
            Date d = audit.getDatetimeStamp();
            String t = audit.getType();
            String r = audit.getReasonForChange();
            String o = audit.getOldValue();
            String n = audit.getNewValue();
            Boolean p = i.length() > 0 || u.length() > 0 || d != null || t.length() > 0 || r.length() > 0 || o.length() > 0 || n.length() > 0 ? true : false;
            if (p) {
                xml.append(currentIndent + "<OpenClinica:AuditLog ");
                if (i.length() > 0) {
                    xml.append("OpenClinica:ID=\"" + StringEscapeUtils.escapeXml(i) + "\" ");
                }
                if (u.length() > 0) {
                    xml.append("OpenClinica:UserID=\"" + StringEscapeUtils.escapeXml(u) + "\" ");
                }
                if (d != null) {
                    xml.append("OpenClinica:DateTimeStamp=\"" + new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(d) + "\" ");
                }
                if (t.length() > 0) {
                    xml.append(nls);
                    xml.append(currentIndent + "                      OpenClinica:Type=\"" + t + "\" ");
                }
                if (r.length() > 0) {
                    xml.append(nls);
                    xml.append(currentIndent + "                      OpenClinica:ReasonForChange=\"" + StringEscapeUtils.escapeXml(r) + "\" ");
                }
                if (o.length() > 0) {
                    xml.append(nls);
                    xml.append(currentIndent + "                      OpenClinica:OldValue=\"" + StringEscapeUtils.escapeXml(o) + "\" ");
                }
                if (n.length() > 0) {
                    xml.append(nls);
                    xml.append(currentIndent + "                      OpenClinica:NewValue=\"" + StringEscapeUtils.escapeXml(n) + "\"");
                }
                xml.append("/>");
                xml.append(nls);
            }
        }
    }
    */

    protected void addDiscrepancyNotes(DiscrepancyNotesBean DNs, String currentIndent) {
        if (DNs != null) {
            ArrayList<DiscrepancyNoteBean> dns = DNs.getDiscrepancyNotes();
            if (dns != null && dns.size() > 0) {
                StringBuffer xml = this.getXmlOutput();
                String indent = this.getIndent();
                String nls = System.getProperty("line.separator");
                xml.append(currentIndent + "<OpenClinica:DiscrepancyNotes EntityID=\"" + DNs.getEntityID() + "\">");
                xml.append(nls);
                for (DiscrepancyNoteBean dn : dns) {
                    this.addOneDN(dn, currentIndent + indent);
                }
                xml.append(currentIndent + "</OpenClinica:DiscrepancyNotes>");
                xml.append(nls);
            }
        }
    }
    
    /*
    protected void addDiscrepancyNotes(DiscrepancyNotesBean DNs, String currentIndent) {
        if (DNs != null) {
            ArrayList<DiscrepancyNoteBean> dns = DNs.getDiscrepancyNotes();
            if (dns != null && dns.size() > 0) {
                StringBuffer xml = this.getXmlOutput();
                String indent = this.getIndent();
                String nls = System.getProperty("line.separator");
                xml.append(currentIndent + "<OpenClinica:DiscrepancyNotes OpenClinica:EntityID=\"" + DNs.getEntityID() + "\">");
                xml.append(nls);
                for (DiscrepancyNoteBean dn : dns) {
                    this.addOneDN(dn, currentIndent + indent);
                }
                xml.append(currentIndent + "</OpenClinica:DiscrepancyNotes>");
                xml.append(nls);
            }
        }
    }
    */

    protected void addOneDN(DiscrepancyNoteBean dn, String currentIndent) {
        StringBuffer xml = this.getXmlOutput();
        String indent = this.getIndent();
        String nls = System.getProperty("line.separator");
        //Boolean p = s.length()>0||i.length()>0||d.toString().length()>0||n>0 ? true : false;
        xml.append(currentIndent + "<OpenClinica:DiscrepancyNote ");
        if (dn.getOid() != null) {
            String i = dn.getOid();
            if (i.length() > 0) {
                xml.append("ID=\"" + StringEscapeUtils.escapeXml(i) + "\" ");
            }
        }
        if (dn.getStatus() != null) {
            String s = dn.getStatus();
            if (s.length() > 0) {
                xml.append("Status=\"" + s + "\" ");
            }
        }
        if (dn.getNoteType() != null) {
            String s = dn.getNoteType();
            if (s.length() > 0) {
                xml.append("NoteType=\"" + s + "\" ");
            }
        }
        if (dn.getDateUpdated() != null) {
            Date d = dn.getDateUpdated();
            if (d.toString().length() > 0) {
                xml.append("DateUpdated=\"" + new SimpleDateFormat("yyyy-MM-dd").format(d) + "\" ");
            }
        }
        int n = dn.getNumberOfChildNotes();
        if (n > 0) {
            xml.append("NumberOfChildNotes=\"" + dn.getNumberOfChildNotes() + "\"");
        }
        xml.append('>');
        xml.append(nls);
        if (dn.getChildNotes() != null && dn.getChildNotes().size() > 0) {
            for (ChildNoteBean cn : dn.getChildNotes()) {
                xml.append(currentIndent + indent + "<OpenClinica:ChildNote ");
                if (cn.getStatus() != null) {
                    String s = cn.getStatus();
                    if (s.length() > 0) {
                        xml.append("Status=\"" + s + "\" ");
                    }
                }
                if (cn.getDateCreated() != null) {
                    Date d = cn.getDateCreated();
                    if (d.toString().length() > 0) {
                        xml.append("DateCreated=\"" + new SimpleDateFormat("yyyy-MM-dd").format(d) + "\"");
                    }
                }
                xml.append('>');
                xml.append(nls);
                if (cn.getDescription() != null) {
                    String dc = cn.getDescription();
                    if (dc.length() > 0) {
                        xml.append(currentIndent + indent + indent + "<OpenClinica:Description>" + StringEscapeUtils.escapeXml(dc)
                            + "</OpenClinica:Description>");
                        xml.append(nls);
                    }
                }
                if (cn.getDetailedNote() != null) {
                    String nt = cn.getDetailedNote();
                    if (nt.length() > 0) {
                        xml.append(currentIndent + indent + indent + "<OpenClinica:DetailedNote>" + StringEscapeUtils.escapeXml(nt)
                            + "</OpenClinica:DetailedNote>");
                        xml.append(nls);
                    }
                }
                if (cn.getUserRef() != null) {
                    String uid = cn.getUserRef().getElementDefOID();
                    if (uid.length() > 0) {
                        xml.append(currentIndent + indent + indent + "<UserRef UserOID=\"" + StringEscapeUtils.escapeXml(uid) + "\"/>");
                        xml.append(nls);
                    }
                }
                xml.append(currentIndent + indent + "</OpenClinica:ChildNote>");
                xml.append(nls);
            }
        }
        xml.append(currentIndent + "</OpenClinica:DiscrepancyNote>");
        xml.append(nls);
    }

    
    /*
    protected void addOneDN(DiscrepancyNoteBean dn, String currentIndent) {
        StringBuffer xml = this.getXmlOutput();
        String indent = this.getIndent();
        String nls = System.getProperty("line.separator");
        //Boolean p = s.length()>0||i.length()>0||d.toString().length()>0||n>0 ? true : false;
        xml.append(currentIndent + "<OpenClinica:DiscrepancyNote ");
        if (dn.getOid() != null) {
            String i = dn.getOid();
            if (i.length() > 0) {
                xml.append("OpenClinica:ID=\"" + StringEscapeUtils.escapeXml(i) + "\" ");
            }
        }
        if (dn.getStatus() != null) {
            String s = dn.getStatus();
            if (s.length() > 0) {
                xml.append("OpenClinica:Status=\"" + s + "\" ");
            }
        }
        if (dn.getNoteType() != null) {
            String s = dn.getNoteType();
            if (s.length() > 0) {
                xml.append("OpenClinica:NoteType=\"" + s + "\" ");
            }
        }
        if (dn.getDateUpdated() != null) {
            Date d = dn.getDateUpdated();
            if (d.toString().length() > 0) {
                xml.append("OpenClinica:DateUpdated=\"" + new SimpleDateFormat("yyyy-MM-dd").format(d) + "\" ");
            }
        }
        int n = dn.getNumberOfChildNotes();
        if (n > 0) {
            xml.append("OpenClinica:NumberOfChildNotes=\"" + dn.getNumberOfChildNotes() + "\"");
        }
        xml.append(">");
        xml.append(nls);
        if (dn.getChildNotes() != null && dn.getChildNotes().size() > 0) {
            for (ChildNoteBean cn : dn.getChildNotes()) {
                xml.append(currentIndent + indent + "<OpenClinica:ChildNote ");
                if (cn.getStatus() != null) {
                    String s = cn.getStatus();
                    if (s.length() > 0) {
                        xml.append("OpenClinica:Status=\"" + s + "\" ");
                    }
                }
                if (cn.getDateCreated() != null) {
                    Date d = cn.getDateCreated();
                    if (d.toString().length() > 0) {
                        xml.append("OpenClinica:DateCreated=\"" + new SimpleDateFormat("yyyy-MM-dd").format(d) + "\"");
                    }
                }
                xml.append(">");
                xml.append(nls);
                if (cn.getDescription() != null) {
                    String dc = cn.getDescription();
                    if (dc.length() > 0) {
                        xml.append(currentIndent + indent + indent + "<OpenClinica:Description>" + StringEscapeUtils.escapeXml(dc)
                            + "</OpenClinica:Description>");
                        xml.append(nls);
                    }
                }
                if (cn.getDetailedNote() != null) {
                    String nt = cn.getDetailedNote();
                    if (nt.length() > 0) {
                        xml.append(currentIndent + indent + indent + "<OpenClinica:DetailedNote>" + StringEscapeUtils.escapeXml(nt)
                            + "</OpenClinica:DetailedNote>");
                        xml.append(nls);
                    }
                }
                if (cn.getUserRef() != null) {
                    String uid = cn.getUserRef().getElementDefOID();
                    if (uid.length() > 0) {
                        xml.append(currentIndent + indent + indent + "<UserRef UserOID=\"" + StringEscapeUtils.escapeXml(uid) + "\"/>");
                        xml.append(nls);
                    }
                }
                xml.append(currentIndent + indent + "</OpenClinica:ChildNote>");
                xml.append(nls);
            }
        }
        xml.append(currentIndent + "</OpenClinica:DiscrepancyNote>");
        xml.append(nls);
    }
    */

    public void setClinicalData(OdmClinicalDataBean clinicaldata) {
        this.clinicalData = clinicaldata;
    }

    public OdmClinicalDataBean getClinicalData() {
        return this.clinicalData;
    }
}