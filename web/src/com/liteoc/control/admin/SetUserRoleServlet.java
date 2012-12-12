/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package com.liteoc.control.admin;


import com.liteoc.bean.core.Role;
import com.liteoc.bean.core.Status;
import com.liteoc.bean.login.StudyUserRoleBean;
import com.liteoc.bean.login.UserAccountBean;
import com.liteoc.bean.managestudy.StudyBean;
import com.liteoc.control.core.SecureController;
import com.liteoc.control.form.FormProcessor;
import com.liteoc.dao.login.UserAccountDAO;
import com.liteoc.dao.managestudy.StudyDAO;
import com.liteoc.view.Page;
import com.liteoc.web.InsufficientPermissionException;

import java.util.*;

/**
 * @author jxu
 *
 * Modified by ywang, 11-19-2007.
 *
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class SetUserRoleServlet extends SecureController {
    /**
     *
     */
    @Override
    public void mayProceed() throws InsufficientPermissionException {
        if (ub.isSysAdmin()) {
            return;
        }

        addPageMessage(respage.getString("no_have_correct_privilege_current_study") + respage.getString("change_study_contact_sysadmin"));
        throw new InsufficientPermissionException(Page.LIST_USER_ACCOUNTS_SERVLET, resexception.getString("not_admin"), "1");

    }

    @Override
    public void processRequest() throws Exception {
        UserAccountDAO udao = new UserAccountDAO(sm.getDataSource());
        StudyDAO sdao = new StudyDAO(sm.getDataSource());
        FormProcessor fp = new FormProcessor(request);
        int userId = fp.getInt("userId");
        if (userId == 0) {
            addPageMessage(respage.getString("please_choose_a_user_to_set_role_for"));
            forwardPage(Page.LIST_USER_ACCOUNTS_SERVLET);
        } else {
            String action = request.getParameter("action");
            UserAccountBean user = (UserAccountBean) udao.findByPK(userId);
            ArrayList studies = (ArrayList) sdao.findAll();
            ArrayList studiesHaveRole = (ArrayList) sdao.findAllByUser(user.getName());
            studies.removeAll(studiesHaveRole);
            HashSet<StudyBean> studiesNotHaveRole = new HashSet<StudyBean>();
            HashSet<StudyBean> sitesNotHaveRole = new HashSet<StudyBean>();
            for (int i = 0; i < studies.size(); i++) {
                StudyBean study1 = (StudyBean) studies.get(i);

                // TODO: implement equal() according to id
                boolean hasStudy = false;
                for (int j = 0; j < studiesHaveRole.size(); j++) {
                    StudyBean study2 = (StudyBean) studiesHaveRole.get(j);
                    if (study2.getId() == study1.getId()) {
                        hasStudy = true;
                        break;
                    }
                }
                if (!hasStudy) {
                    // YW 11-19-2007 <<
                    if (study1.getParentStudyId() > 0) {
                        sitesNotHaveRole.add(study1);
                    } else {
                        studiesNotHaveRole.add(study1);
                    }
                    // YW >>
                }
            }

            Map roleMap = new LinkedHashMap();
            for (Iterator it = getRoles().iterator(); it.hasNext();) {
                Role role = (Role) it.next();
                roleMap.put(role.getId(), role.getDescription());
            }

            Boolean changeRoles = request.getParameter("changeRoles") == null ? false : Boolean.parseBoolean(request.getParameter("changeRoles"));
            int studyId = fp.getInt("studyId");
            if (changeRoles) {
                StudyBean study = (StudyBean) sdao.findByPK(studyId);
                roleMap = new LinkedHashMap();
                ResourceBundle resterm = com.liteoc.i18n.util.ResourceBundleProvider.getTermsBundle();

                if (study.getParentStudyId() > 0) {
                    for (Iterator it = getRoles().iterator(); it.hasNext();) {
                        Role role = (Role) it.next();
                        switch (role.getId()) {
//                        case 2: roleMap.put(role.getId(), resterm.getString("site_Study_Coordinator").trim());
//                            break;
//                        case 3: roleMap.put(role.getId(), resterm.getString("site_Study_Director").trim());
//                            break;
                            case 4: roleMap.put(role.getId(), resterm.getString("site_investigator").trim());
                                break;
                            case 5: roleMap.put(role.getId(), resterm.getString("site_Data_Entry_Person").trim());
                                break;
                            case 6: roleMap.put(role.getId(), resterm.getString("site_monitor").trim());
                                break;
                        default:
                            // logger.info("No role matched when setting role description");
                        }
                    }
                } else {
                    for (Iterator it = getRoles().iterator(); it.hasNext();) {
                        Role role = (Role) it.next();
                        switch (role.getId()) {
                            case 2: roleMap.put(role.getId(), resterm.getString("Study_Coordinator").trim());
                                break;
                            case 3: roleMap.put(role.getId(), resterm.getString("Study_Director").trim());
                                break;
                            case 4: roleMap.put(role.getId(), resterm.getString("Investigator").trim());
                                break;
                            case 5: roleMap.put(role.getId(), resterm.getString("Data_Entry_Person").trim());
                                break;
                            case 6: roleMap.put(role.getId(), resterm.getString("Monitor").trim());
                                break;
                        default:
                            // logger.info("No role matched when setting role description");
                        }
                    }
                }
            } else {
                if (currentStudy.getParentStudyId() > 0) {
                    roleMap.remove(Role.COORDINATOR.getId());
                    roleMap.remove(Role.STUDYDIRECTOR.getId());
                }
            }
            request.setAttribute("roles", roleMap);
            request.setAttribute("studyId", studyId);
            if ("confirm".equalsIgnoreCase(action) || changeRoles) {
                // YW 11-19-2007 << re-order studiesNotHaveRole so that sites
                // under their studies;
                ArrayList finalStudiesNotHaveRole = new ArrayList();
                Iterator iter_study = studiesNotHaveRole.iterator();
                while (iter_study.hasNext()) {
                    StudyBean s = (StudyBean) iter_study.next();
                    finalStudiesNotHaveRole.add(s);
                    Iterator iter_site = sitesNotHaveRole.iterator();
                    while (iter_site.hasNext()) {
                        StudyBean site = (StudyBean) iter_site.next();
                        if (site.getParentStudyId() == s.getId()) {
                            finalStudiesNotHaveRole.add(site);
                        }
                    }
                }
                // YW >>
                request.setAttribute("user", user);
                request.setAttribute("studies", finalStudiesNotHaveRole);
                StudyUserRoleBean uRole = new StudyUserRoleBean();
                uRole.setFirstName(user.getFirstName());
                uRole.setLastName(user.getLastName());
                uRole.setUserName(user.getName());
                request.setAttribute("uRole", uRole);

//                ArrayList roles = Role.toArrayList();
//                roles.remove(Role.ADMIN); // admin is not a user role, only used for tomcat
//                if (currentStudy.getParentStudyId() > 0) {
//                    roles.remove(Role.COORDINATOR);
//                    roles.remove(Role.STUDYDIRECTOR);
//                }
//                request.setAttribute("roles", roles);

                forwardPage(Page.SET_USER_ROLE);
            } else {
                // set role
                String userName = fp.getString("name");
                studyId = fp.getInt("studyId");
                StudyBean userStudy = (StudyBean) sdao.findByPK(studyId);
                int roleId = fp.getInt("roleId");
                // new user role
                StudyUserRoleBean sur = new StudyUserRoleBean();
                sur.setName(userName);
                sur.setRole(Role.get(roleId));
                sur.setStudyId(studyId);
                sur.setStudyName(userStudy.getName());
                sur.setStatus(Status.AVAILABLE);
                sur.setOwner(ub);
                sur.setCreatedDate(new Date());

                if (studyId > 0) {
                    udao.createStudyUserRole(user, sur);

                    addPageMessage(respage.getString("action_success"));
                }

                forwardPage(Page.LIST_USER_ACCOUNTS_SERVLET);

            }

        }
    }

    @Override
    protected String getAdminServlet() {
        return SecureController.ADMIN_SERVLET_CODE;
    }

    private ArrayList getRoles() {
        ArrayList roles = Role.toArrayList();
        roles.remove(Role.ADMIN);

        return roles;
    }


}
