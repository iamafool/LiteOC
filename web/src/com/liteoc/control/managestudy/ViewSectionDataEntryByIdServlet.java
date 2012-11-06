/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2008-2009 Akaza Research
 */
package com.liteoc.control.managestudy;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import com.liteoc.bean.managestudy.StudyBean;
import com.liteoc.bean.submit.CRFVersionBean;
import com.liteoc.dao.managestudy.StudyDAO;
import com.liteoc.dao.submit.CRFVersionDAO;
import com.liteoc.view.Page;
import com.liteoc.web.InsufficientPermissionException;

/**
 * Builds on top of ViewSectionDataEntryServlet, Doesn't add much other than using OIDs to get to the View Screen.
 * 
 * @author Krikor Krumlian
 */
public class ViewSectionDataEntryByIdServlet extends ViewSectionDataEntryServlet {

    private static final long serialVersionUID = 1L;

    /*
     * (non-Javadoc)
     * @see com.liteoc.control.managestudy.ViewSectionDataEntryServlet#mayProceed()
     */
    @Override
    public void mayProceed(HttpServletRequest request, HttpServletResponse response) throws InsufficientPermissionException {
    }

    /*
     * (non-Javadoc)
     * @see com.liteoc.control.managestudy.ViewSectionDataEntryServlet#processRequest()
     */
    @Override
    public void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        StudyDAO studyDao = new StudyDAO(getDataSource());
       
        StudyBean  currentStudy = (StudyBean) studyDao.findByPK(1);
        CRFVersionDAO crfVersionDao = new CRFVersionDAO(getDataSource());
        if (request.getParameter("id") == null) {
            forwardPage(Page.LOGIN, request, response);
        }
        request.setAttribute("study", currentStudy);
        CRFVersionBean crfVersion = crfVersionDao.findByOid(request.getParameter("id"));
        if (crfVersion != null) {
            request.setAttribute("crfVersionId", String.valueOf(crfVersion.getId()));
            request.setAttribute("crfId", String.valueOf(crfVersion.getCrfId()));
            super.processRequest(request, response);
        } else {
            forwardPage(Page.LOGIN, request, response);
        }
    }
}
