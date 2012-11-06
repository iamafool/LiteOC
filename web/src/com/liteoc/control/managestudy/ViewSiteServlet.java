/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package com.liteoc.control.managestudy;



import com.liteoc.bean.admin.CRFBean;
import com.liteoc.bean.core.Role;
import com.liteoc.bean.managestudy.EventDefinitionCRFBean;
import com.liteoc.bean.managestudy.StudyBean;
import com.liteoc.bean.managestudy.StudyEventDefinitionBean;
import com.liteoc.bean.submit.CRFVersionBean;
import com.liteoc.control.core.SecureController;
import com.liteoc.core.form.StringUtil;
import com.liteoc.dao.admin.CRFDAO;
import com.liteoc.dao.managestudy.EventDefinitionCRFDAO;
import com.liteoc.dao.managestudy.StudyDAO;
import com.liteoc.dao.managestudy.StudyEventDefinitionDAO;
import com.liteoc.dao.service.StudyParameterValueDAO;
import com.liteoc.dao.submit.CRFVersionDAO;
import com.liteoc.domain.SourceDataVerification;
import com.liteoc.view.Page;
import com.liteoc.web.InsufficientPermissionException;

import java.util.ArrayList;

/**
 * @author jxu
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class ViewSiteServlet extends SecureController {
    /**
     * Checks whether the user has the correct privilege
     */
    @Override
    public void mayProceed() throws InsufficientPermissionException {
        if (ub.isSysAdmin()) {
            return;
        }
        if (currentRole.getRole().equals(Role.STUDYDIRECTOR) || currentRole.getRole().equals(Role.COORDINATOR)) {
            return;
        }
        int siteId = request.getParameter("id") == null ? 0 : Integer.valueOf(request.getParameter("id"));
        if (currentStudy.getId() == siteId) {
            return;
        }
        addPageMessage(respage.getString("no_have_correct_privilege_current_study") + " " + respage.getString("change_study_contact_sysadmin"));
        throw new InsufficientPermissionException(Page.MENU_SERVLET, resexception.getString("not_director"), "1");

    }

    @Override
    public void processRequest() throws Exception {

        StudyDAO sdao = new StudyDAO(sm.getDataSource());
        String idString = "";
        if (request.getAttribute("siteId") == null) {
            idString = request.getParameter("id");
        } else {
            idString = request.getAttribute("siteId").toString();
        }
        logger.info("site id:" + idString);
        if (StringUtil.isBlank(idString)) {
            addPageMessage(respage.getString("please_choose_a_site_to_edit"));
            forwardPage(Page.SITE_LIST_SERVLET);
        } else {
            int siteId = Integer.valueOf(idString.trim()).intValue();
            StudyBean study = (StudyBean) sdao.findByPK(siteId);

            checkRoleByUserAndStudy(ub, study.getParentStudyId(), study.getId());
            // if (currentStudy.getId() != study.getId()) {

            ArrayList configs = new ArrayList();
            StudyParameterValueDAO spvdao = new StudyParameterValueDAO(sm.getDataSource());
            configs = spvdao.findParamConfigByStudy(study);
            study.setStudyParameters(configs);

            // }

            String parentStudyName = "";
            if (study.getParentStudyId() > 0) {
                StudyBean parent = (StudyBean) sdao.findByPK(study.getParentStudyId());
                parentStudyName = parent.getName();
            }
            request.setAttribute("parentName", parentStudyName);
            request.setAttribute("siteToView", study);
            request.setAttribute("idToSort", request.getAttribute("idToSort"));
            viewSiteEventDefinitions(study);

            forwardPage(Page.VIEW_SITE);
        }
    }

    private void viewSiteEventDefinitions(StudyBean siteToView) {
        int siteId = siteToView.getId();
        ArrayList<StudyEventDefinitionBean> seds = new ArrayList<StudyEventDefinitionBean>();
        StudyEventDefinitionDAO sedDao = new StudyEventDefinitionDAO(sm.getDataSource());
        EventDefinitionCRFDAO edcdao = new EventDefinitionCRFDAO(sm.getDataSource());
        CRFVersionDAO cvdao = new CRFVersionDAO(sm.getDataSource());
        CRFDAO cdao = new CRFDAO(sm.getDataSource());
        seds = sedDao.findAllByStudy(siteToView);
        int start = 0;
        for (StudyEventDefinitionBean sed : seds) {
            int defId = sed.getId();
            ArrayList<EventDefinitionCRFBean> edcs =
                (ArrayList<EventDefinitionCRFBean>) edcdao.findAllByDefinitionAndSiteIdAndParentStudyId(defId, siteId, siteToView.getParentStudyId());
            ArrayList<EventDefinitionCRFBean> defCrfs = new ArrayList<EventDefinitionCRFBean>();
            for (EventDefinitionCRFBean edcBean : edcs) {
                int edcStatusId = edcBean.getStatus().getId();
                CRFBean crf = (CRFBean) cdao.findByPK(edcBean.getCrfId());
                int crfStatusId = crf.getStatusId();
                ArrayList<CRFVersionBean> versions = (ArrayList<CRFVersionBean>) cvdao.findAllActiveByCRF(edcBean.getCrfId());
                edcBean.setVersions(versions);
                edcBean.setCrfName(crf.getName());
                CRFVersionBean defaultVersion = (CRFVersionBean) cvdao.findByPK(edcBean.getDefaultVersionId());
                edcBean.setDefaultVersionName(defaultVersion.getName());
                String sversionIds = edcBean.getSelectedVersionIds();
                ArrayList<Integer> idList = new ArrayList<Integer>();
                String idNames = "";
                if (sversionIds.length() > 0) {
                    String[] ids = sversionIds.split("\\,");
                    for (String id : ids) {
                        idList.add(Integer.valueOf(id));
                        for (CRFVersionBean v : versions) {
                            if (v.getId() == Integer.valueOf(id)) {
                                idNames += v.getName() + ",";
                                break;
                            }
                        }
                    }
                    idNames = idNames.substring(0, idNames.length() - 1);
                }
                edcBean.setSelectedVersionIdList(idList);
                edcBean.setSelectedVersionNames(idNames);
                defCrfs.add(edcBean);
                ++start;
            }
            sed.setCrfs(defCrfs);
            sed.setCrfNum(defCrfs.size());
        }
        request.setAttribute("definitions", seds);
        ArrayList<String> sdvOptions = new ArrayList<String>();
        sdvOptions.add(SourceDataVerification.AllREQUIRED.toString());
        sdvOptions.add(SourceDataVerification.PARTIALREQUIRED.toString());
        sdvOptions.add(SourceDataVerification.NOTREQUIRED.toString());
        sdvOptions.add(SourceDataVerification.NOTAPPLICABLE.toString());
        request.setAttribute("sdvOptions", sdvOptions);

    }

}
