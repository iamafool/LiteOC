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
 * Builds on top of PrintCRFServlet
 * 
 * @author Krikor Krumlian
 */
public class PrintCRFByIdServlet extends PrintCRFServlet {

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
        
        StudyBean currentStudy =    (StudyBean) request.getSession().getAttribute("study");
        StudyDAO studyDao = new StudyDAO(getDataSource());
        currentStudy = (StudyBean) studyDao.findByPK(1);
        CRFVersionDAO crfVersionDao = new CRFVersionDAO(getDataSource());
        if (request.getParameter("id") == null) {
            forwardPage(Page.LOGIN, request, response);
        }
        CRFVersionBean crfVersion = crfVersionDao.findByOid(request.getParameter("id"));
        request.setAttribute("study", currentStudy);
        if (crfVersion != null) {
            request.setAttribute("id", String.valueOf(crfVersion.getId()));
            super.processRequest(request, response);
        } else {
            forwardPage(Page.LOGIN, request, response);
        }
    }
}
