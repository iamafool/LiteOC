/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package com.liteoc.control.managestudy;

import java.util.ArrayList;


import com.liteoc.bean.admin.CRFBean;
import com.liteoc.bean.core.Role;
import com.liteoc.bean.core.Status;
import com.liteoc.bean.submit.CRFVersionBean;
import com.liteoc.control.core.SecureController;
import com.liteoc.control.form.FormProcessor;
import com.liteoc.core.form.StringUtil;
import com.liteoc.dao.admin.CRFDAO;
import com.liteoc.dao.submit.CRFVersionDAO;
import com.liteoc.dao.submit.EventCRFDAO;
import com.liteoc.view.Page;
import com.liteoc.web.InsufficientPermissionException;

public class UnlockCRFVersionServlet extends SecureController {
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

       addPageMessage(respage.getString("no_have_correct_privilege_current_study") + respage.getString("change_study_contact_sysadmin"));
       throw new InsufficientPermissionException(Page.MENU_SERVLET, resexception.getString("not_study_director"), "1");

   }
   
   @Override
   public void processRequest() throws Exception {
       FormProcessor fp = new FormProcessor(request);
       
       int crfVersionId = fp.getInt("id");
       String action = fp.getString("action");
       
       // checks which module the requests are from
       String module = fp.getString(MODULE);
       request.setAttribute(MODULE, module);
       
       if(crfVersionId ==0) {
           addPageMessage(respage.getString("no_have_correct_privilege_current_study"));
           forwardPage(Page.CRF_LIST_SERVLET);
           return;
       }
       
       CRFVersionDAO cvdao = new CRFVersionDAO(sm.getDataSource());
       CRFDAO cdao = new CRFDAO (sm.getDataSource());
       
       CRFVersionBean version = (CRFVersionBean)cvdao.findByPK(crfVersionId);
       CRFBean crf = (CRFBean)cdao.findByPK(version.getCrfId());
       
       EventCRFDAO ecdao = new EventCRFDAO(sm.getDataSource());
       ArrayList eventCRFs = ecdao.findAllStudySubjectByCRFVersion(crfVersionId);
       
       if (StringUtil.isBlank(action)) {
           request.setAttribute("crfVersionToUnlock", version);
           request.setAttribute("crf", crf);
           request.setAttribute("eventSubjectsUsingVersion", eventCRFs);
           forwardPage(Page.CONFIRM_UNLOCKING_CRF_VERSION);
           
       } else if ("confirm".equalsIgnoreCase(action)) {
           version.setStatus(Status.AVAILABLE);
           version.setUpdater(ub);
           cvdao.update(version);
           addPageMessage(respage.getString("crf_version_unarchived_successfully"));
           forwardPage(Page.CRF_LIST_SERVLET);
       }
   }

}
