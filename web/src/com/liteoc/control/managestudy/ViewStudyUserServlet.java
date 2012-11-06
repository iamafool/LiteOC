/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package com.liteoc.control.managestudy;


import com.liteoc.bean.core.Role;
import com.liteoc.bean.login.StudyUserRoleBean;
import com.liteoc.bean.login.UserAccountBean;
import com.liteoc.bean.managestudy.StudyBean;
import com.liteoc.control.core.SecureController;
import com.liteoc.core.form.StringUtil;
import com.liteoc.dao.login.UserAccountDAO;
import com.liteoc.dao.managestudy.StudyDAO;
import com.liteoc.i18n.util.ResourceBundleProvider;
import com.liteoc.view.Page;
import com.liteoc.web.InsufficientPermissionException;

/**
 * @author jxu
 *
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class ViewStudyUserServlet extends SecureController {
    /**
     *
     */
    @Override
    public void mayProceed() throws InsufficientPermissionException {
        if (ub.isSysAdmin()) {
            return;
        }

        if (currentRole.getRole().equals(Role.STUDYDIRECTOR) || currentRole.getRole().equals(Role.COORDINATOR)) {
            return;
        }

        addPageMessage(respage.getString("no_have_correct_privilege_current_study") + " " + respage.getString("change_study_contact_sysadmin"));
        throw new InsufficientPermissionException(Page.LIST_USER_IN_STUDY_SERVLET, resexception.getString("not_study_director"), "1");

    }

    @Override
    public void processRequest() throws Exception {
        UserAccountDAO udao = new UserAccountDAO(sm.getDataSource());
        String name = request.getParameter("name");
        String studyIdString = request.getParameter("studyId");
       
     //if(request.getParameter("submit")!=null)
     {     
    	 
    	 if (StringUtil.isBlank(name) || StringUtil.isBlank(studyIdString)) {
            addPageMessage(respage.getString("please_choose_a_user_to_view"));
            forwardPage(Page.LIST_USER_IN_STUDY_SERVLET);
     	} else {
            int studyId = Integer.valueOf(studyIdString.trim()).intValue();
            UserAccountBean user = (UserAccountBean) udao.findByUserName(name);

            request.setAttribute("user", user);

            StudyUserRoleBean uRole = udao.findRoleByUserNameAndStudyId(name, studyId);
            request.setAttribute("uRole", uRole);

            StudyDAO sdao = new StudyDAO(sm.getDataSource());
            StudyBean study = (StudyBean) sdao.findByPK(studyId);
            request.setAttribute("uStudy", study);
            request.setAttribute("siteRoleMap", Role.siteRoleMap);
            // BWP 12/7/07 >>To provide the view with the correct date format
            // pattern, locale sensitive
            String pattn = "";
            pattn = ResourceBundleProvider.getFormatBundle().getString("date_format_string");
            request.setAttribute("dateFormatPattern", pattn);
            request.setAttribute("action","");
            forwardPage(Page.VIEW_USER_IN_STUDY);

        }
     }
    }

}
