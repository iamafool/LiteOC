/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package com.liteoc.control.managestudy;



import com.liteoc.bean.core.GroupClassType;
import com.liteoc.bean.core.Role;
import com.liteoc.bean.managestudy.StudyBean;
import com.liteoc.bean.managestudy.StudyGroupBean;
import com.liteoc.bean.managestudy.StudyGroupClassBean;
import com.liteoc.control.core.SecureController;
import com.liteoc.control.form.FormProcessor;
import com.liteoc.dao.managestudy.StudyDAO;
import com.liteoc.dao.managestudy.StudyGroupClassDAO;
import com.liteoc.dao.managestudy.StudyGroupDAO;
import com.liteoc.dao.submit.SubjectGroupMapDAO;
import com.liteoc.view.Page;
import com.liteoc.web.InsufficientPermissionException;

import java.util.ArrayList;

/**
 * @author jxu, modified by ywang
 *
 * Views details of a Subject Group Class
 */
public class ViewSubjectGroupClassServlet extends SecureController {
    @Override
    public void mayProceed() throws InsufficientPermissionException {
        if (ub.isSysAdmin()) {
            return;
        }
        if (currentRole.getRole().equals(Role.STUDYDIRECTOR) || currentRole.getRole().equals(Role.COORDINATOR)) {
            return;
        }
        addPageMessage(respage.getString("no_have_correct_privilege_current_study") + "\n" + respage.getString("change_study_contact_sysadmin"));
        throw new InsufficientPermissionException(Page.SUBJECT_GROUP_CLASS_LIST_SERVLET, resexception.getString("not_study_director"), "1");

    }

    @Override
    public void processRequest() throws Exception {
        String action = request.getParameter("action");
        FormProcessor fp = new FormProcessor(request);
        int classId = fp.getInt("id");

        if (classId == 0) {

            addPageMessage(respage.getString("please_choose_a_subject_group_class_to_view"));
            forwardPage(Page.SUBJECT_GROUP_CLASS_LIST_SERVLET);
        } else {
            StudyGroupClassDAO sgcdao = new StudyGroupClassDAO(sm.getDataSource());
            StudyGroupDAO sgdao = new StudyGroupDAO(sm.getDataSource());
            SubjectGroupMapDAO sgmdao = new SubjectGroupMapDAO(sm.getDataSource());
            StudyDAO studyDao = new StudyDAO(sm.getDataSource());

            StudyGroupClassBean sgcb = (StudyGroupClassBean) sgcdao.findByPK(classId);
            StudyBean study = (StudyBean)studyDao.findByPK(sgcb.getStudyId());

            checkRoleByUserAndStudy(ub, sgcb.getStudyId(), study.getParentStudyId());

            // YW 09-19-2007 <<
            sgcb.setGroupClassTypeName(GroupClassType.get(sgcb.getGroupClassTypeId()).getName());
            // YW >>

            ArrayList groups = sgdao.findAllByGroupClass(sgcb);
            ArrayList studyGroups = new ArrayList();

            for (int i = 0; i < groups.size(); i++) {
                StudyGroupBean sg = (StudyGroupBean) groups.get(i);
                ArrayList subjectMaps = sgmdao.findAllByStudyGroupClassAndGroup(sgcb.getId(), sg.getId());
                sg.setSubjectMaps(subjectMaps);
                // YW<<
                studyGroups.add(sg);
                // YW>>
            }

            request.setAttribute("group", sgcb);
            // request.setAttribute("studyGroups", groups);
            request.setAttribute("studyGroups", studyGroups);
            forwardPage(Page.VIEW_SUBJECT_GROUP_CLASS);
        }
    }

}
