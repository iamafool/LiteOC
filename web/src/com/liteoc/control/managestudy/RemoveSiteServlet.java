/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package com.liteoc.control.managestudy;


import com.liteoc.bean.core.Role;
import com.liteoc.bean.core.Status;
import com.liteoc.bean.extract.DatasetBean;
import com.liteoc.bean.login.StudyUserRoleBean;
import com.liteoc.bean.managestudy.StudyBean;
import com.liteoc.bean.managestudy.StudyEventBean;
import com.liteoc.bean.managestudy.StudyGroupBean;
import com.liteoc.bean.managestudy.StudySubjectBean;
import com.liteoc.bean.submit.EventCRFBean;
import com.liteoc.bean.submit.ItemDataBean;
import com.liteoc.bean.submit.SubjectGroupMapBean;
import com.liteoc.control.core.SecureController;
import com.liteoc.core.form.StringUtil;
import com.liteoc.dao.extract.DatasetDAO;
import com.liteoc.dao.login.UserAccountDAO;
import com.liteoc.dao.managestudy.EventDefinitionCRFDAO;
import com.liteoc.dao.managestudy.StudyDAO;
import com.liteoc.dao.managestudy.StudyEventDAO;
import com.liteoc.dao.managestudy.StudyEventDefinitionDAO;
import com.liteoc.dao.managestudy.StudyGroupDAO;
import com.liteoc.dao.managestudy.StudySubjectDAO;
import com.liteoc.dao.submit.EventCRFDAO;
import com.liteoc.dao.submit.ItemDataDAO;
import com.liteoc.dao.submit.SubjectGroupMapDAO;
import com.liteoc.view.Page;
import com.liteoc.web.InsufficientPermissionException;

import java.util.ArrayList;
import java.util.Date;

/**
 * @author jxu
 * 
 *         Removes a site from a study
 */
public class RemoveSiteServlet extends SecureController {

    /**
     *
     */
    @Override
    public void mayProceed() throws InsufficientPermissionException {
        checkStudyLocked(Page.SITE_LIST_SERVLET, respage.getString("current_study_locked"));
        if (ub.isSysAdmin()) {
            return;
        }

        if (currentRole.getRole().equals(Role.STUDYDIRECTOR) || currentRole.getRole().equals(Role.COORDINATOR)) {
            return;
        }

        addPageMessage(respage.getString("no_have_correct_privilege_current_study") + respage.getString("change_study_contact_sysadmin"));
        throw new InsufficientPermissionException(Page.SITE_LIST_SERVLET, resexception.getString("not_study_director"), "1");

    }

    @Override
    public void processRequest() throws Exception {
        StudyDAO sdao = new StudyDAO(sm.getDataSource());
        String idString = request.getParameter("id");
        logger.info("site id:" + idString);

        int siteId = Integer.valueOf(idString.trim()).intValue();
        StudyBean study = (StudyBean) sdao.findByPK(siteId);
        if (currentStudy.getId() != study.getParentStudyId()) {
            addPageMessage(respage.getString("no_have_correct_privilege_current_study")
                    + " " + respage.getString("change_active_study_or_contact"));
            forwardPage(Page.MENU_SERVLET);
            return;
        }

        // find all user and roles
        UserAccountDAO udao = new UserAccountDAO(sm.getDataSource());
        ArrayList userRoles = udao.findAllByStudyId(siteId);

        // find all subjects
        StudySubjectDAO ssdao = new StudySubjectDAO(sm.getDataSource());
        ArrayList subjects = ssdao.findAllByStudy(study);

        // find all events
        StudyEventDefinitionDAO sefdao = new StudyEventDefinitionDAO(sm.getDataSource());
        ArrayList definitions = sefdao.findAllByStudy(study);

        String action = request.getParameter("action");
        if (StringUtil.isBlank(idString)) {
            addPageMessage(respage.getString("please_choose_a_site_to_remove"));
            forwardPage(Page.SITE_LIST_SERVLET);
        } else {
            if ("confirm".equalsIgnoreCase(action)) {
                request.setAttribute("siteToRemove", study);

                request.setAttribute("userRolesToRemove", userRoles);

                request.setAttribute("subjectsToRemove", subjects);

                forwardPage(Page.REMOVE_SITE);
            } else {
                logger.info("submit to remove the site");
                // change all statuses to unavailable
                StudyDAO studao = new StudyDAO(sm.getDataSource());
                study.setOldStatus(study.getStatus());
                study.setStatus(Status.DELETED);
                study.setUpdater(ub);
                study.setUpdatedDate(new Date());
                studao.update(study);

                // remove all users and roles
                for (int i = 0; i < userRoles.size(); i++) {
                    StudyUserRoleBean role = (StudyUserRoleBean) userRoles.get(i);
                    if (!role.getStatus().equals(Status.DELETED)) {
                        role.setStatus(Status.AUTO_DELETED);
                        role.setUpdater(ub);
                        role.setUpdatedDate(new Date());
                        // YW << So study_user_role table status_id field can be
                        // updated
                        udao.updateStudyUserRole(role, role.getUserName());
                    }
                    // YW 06-18-2007 >>
                }

                // YW << bug fix that current active study has been deleted
                if (study.getId() == currentStudy.getId()) {
                    currentStudy.setStatus(Status.DELETED);
                    // currentRole.setRole(Role.INVALID);
                    currentRole.setStatus(Status.DELETED);
                }
                // YW 06-18-2007 >>

                // remove all subjects
                for (int i = 0; i < subjects.size(); i++) {
                    StudySubjectBean subject = (StudySubjectBean) subjects.get(i);

                }

                // remove all study_group
                StudyGroupDAO sgdao = new StudyGroupDAO(sm.getDataSource());
                SubjectGroupMapDAO sgmdao = new SubjectGroupMapDAO(sm.getDataSource());
                ArrayList groups = sgdao.findAllByStudy(study);
                for (int i = 0; i < groups.size(); i++) {
                    StudyGroupBean group = (StudyGroupBean) groups.get(i);
                    if (!group.getStatus().equals(Status.DELETED)) {
                        group.setStatus(Status.AUTO_DELETED);
                        group.setUpdater(ub);
                        group.setUpdatedDate(new Date());
                        sgdao.update(group);
                        // all subject_group_map
                        ArrayList subjectGroupMaps = sgmdao.findAllByStudyGroupId(group.getId());
                        for (int j = 0; j < subjectGroupMaps.size(); j++) {
                            SubjectGroupMapBean sgMap = (SubjectGroupMapBean) subjectGroupMaps.get(j);
                            if (!sgMap.getStatus().equals(Status.DELETED)) {
                                sgMap.setStatus(Status.AUTO_DELETED);
                                sgMap.setUpdater(ub);
                                sgMap.setUpdatedDate(new Date());
                                sgmdao.update(sgMap);
                            }
                        }
                    }
                }

                // remove all events
                EventDefinitionCRFDAO edcdao = new EventDefinitionCRFDAO(sm.getDataSource());
                StudyEventDAO sedao = new StudyEventDAO(sm.getDataSource());
                for (int i = 0; i < subjects.size(); i++) {
                    StudySubjectBean subject = (StudySubjectBean) subjects.get(i);

                    if (!subject.getStatus().equals(Status.DELETED)) {
                        subject.setStatus(Status.AUTO_DELETED);
                        subject.setUpdater(ub);
                        subject.setUpdatedDate(new Date());
                        ssdao.update(subject);

                        ArrayList events = sedao.findAllByStudySubject(subject);
                        EventCRFDAO ecdao = new EventCRFDAO(sm.getDataSource());

                        for (int j = 0; j < events.size(); j++) {
                            StudyEventBean event = (StudyEventBean) events.get(j);
                            if (!event.getStatus().equals(Status.DELETED)) {
                                event.setStatus(Status.AUTO_DELETED);
                                event.setUpdater(ub);
                                event.setUpdatedDate(new Date());
                                sedao.update(event);

                                ArrayList eventCRFs = ecdao.findAllByStudyEvent(event);

                                ItemDataDAO iddao = new ItemDataDAO(sm.getDataSource());
                                for (int k = 0; k < eventCRFs.size(); k++) {
                                    EventCRFBean eventCRF = (EventCRFBean) eventCRFs.get(k);
                                    if (!eventCRF.getStatus().equals(Status.DELETED)) {
                                        eventCRF.setOldStatus(eventCRF.getStatus());
                                        eventCRF.setStatus(Status.AUTO_DELETED);
                                        eventCRF.setUpdater(ub);
                                        eventCRF.setUpdatedDate(new Date());
                                        ecdao.update(eventCRF);

                                        ArrayList itemDatas = iddao.findAllByEventCRFId(eventCRF.getId());
                                        for (int a = 0; a < itemDatas.size(); a++) {
                                            ItemDataBean item = (ItemDataBean) itemDatas.get(a);
                                            if (!item.getStatus().equals(Status.DELETED)) {
                                                item.setOldStatus(item.getStatus());
                                                item.setStatus(Status.AUTO_DELETED);
                                                item.setUpdater(ub);
                                                item.setUpdatedDate(new Date());
                                                iddao.update(item);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }// for subjects

                DatasetDAO datadao = new DatasetDAO(sm.getDataSource());
                ArrayList dataset = datadao.findAllByStudyId(study.getId());
                for (int i = 0; i < dataset.size(); i++) {
                    DatasetBean data = (DatasetBean) dataset.get(i);
                    if (!data.getStatus().equals(Status.DELETED)) {
                        data.setStatus(Status.AUTO_DELETED);
                        data.setUpdater(ub);
                        data.setUpdatedDate(new Date());
                        datadao.update(data);
                    }
                }

                addPageMessage(respage.getString("this_site_has_been_removed_succesfully"));

                String fromListSite = (String) session.getAttribute("fromListSite");
                if (fromListSite != null && fromListSite.equals("yes") && currentRole.getRole().equals(Role.STUDYDIRECTOR)) {
                    session.removeAttribute("fromListSite");
                    forwardPage(Page.SITE_LIST_SERVLET);
                } else {
                    session.removeAttribute("fromListSite");
                    if (currentRole.getRole().equals(Role.ADMIN)) {
                        forwardPage(Page.STUDY_LIST_SERVLET);
                    } else {
                        forwardPage(Page.SITE_LIST_SERVLET);
                    }
                }

            }
        }

    }

}
