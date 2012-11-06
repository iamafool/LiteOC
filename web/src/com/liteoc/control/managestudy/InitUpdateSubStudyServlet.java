/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package com.liteoc.control.managestudy;


import com.liteoc.bean.admin.CRFBean;
import com.liteoc.bean.core.Role;
import com.liteoc.bean.core.Status;
import com.liteoc.bean.managestudy.EventDefinitionCRFBean;
import com.liteoc.bean.managestudy.StudyBean;
import com.liteoc.bean.managestudy.StudyEventDefinitionBean;
import com.liteoc.bean.service.StudyParameterValueBean;
import com.liteoc.bean.service.StudyParamsConfig;
import com.liteoc.bean.submit.CRFVersionBean;
import com.liteoc.control.core.SecureController;
import com.liteoc.control.form.FormProcessor;
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
 * @version CVS: $Id: InitUpdateSubStudyServlet.java 9834 2007-09-05 22:28:31Z
 *          jxu $
 */
public class InitUpdateSubStudyServlet extends SecureController {

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
        throw new InsufficientPermissionException(Page.MENU_SERVLET, resexception.getString("not_study_director"), "1");

    }

    @Override
    public void processRequest() throws Exception {

        String userName = request.getRemoteUser();
        StudyDAO sdao = new StudyDAO(sm.getDataSource());
        String idString = request.getParameter("id");
        logger.info("study id:" + idString);
        if (StringUtil.isBlank(idString)) {
            addPageMessage(respage.getString("please_choose_a_study_to_edit"));
            forwardPage(Page.STUDY_LIST_SERVLET);
        } else {
            int studyId = Integer.valueOf(idString.trim()).intValue();
            StudyBean study = (StudyBean) sdao.findByPK(studyId);

            checkRoleByUserAndStudy(ub, study.getParentStudyId(), study.getId());
            
            String parentStudyName = "";
            StudyBean parent = new StudyBean();
            if (study.getParentStudyId() > 0) {
                parent = (StudyBean) sdao.findByPK(study.getParentStudyId());
                parentStudyName = parent.getName();
                // at this time, this feature is only available for site
                createEventDefinitions(parent);
            }

            if (currentStudy.getId() != study.getId()) {
                ArrayList parentConfigs = currentStudy.getStudyParameters();
                // logger.info("parentConfigs size:" + parentConfigs.size());
                ArrayList configs = new ArrayList();
                StudyParameterValueDAO spvdao = new StudyParameterValueDAO(sm.getDataSource());
                for (int i = 0; i < parentConfigs.size(); i++) {
                    StudyParamsConfig scg = (StudyParamsConfig) parentConfigs.get(i);
                    if (scg != null) {
                        // find the one that sub study can change
                        if (scg.getValue().getId() > 0 && scg.getParameter().isOverridable()) {
                            // logger.info("parameter:" +
                            // scg.getParameter().getHandle());
                            // logger.info("value:" +
                            // scg.getValue().getValue());
                            StudyParameterValueBean spvb = spvdao.findByHandleAndStudy(study.getId(), scg.getParameter().getHandle());
                            if (spvb.getId() > 0) {
                                // the sub study itself has the parameter
                                scg.setValue(spvb);
                            }
                            configs.add(scg);
                        }
                    }

                }

                study.setStudyParameters(configs);
            }
            request.setAttribute("parentStudy", parent);
            session.setAttribute("parentName", parentStudyName);
            session.setAttribute("newStudy", study);
            request.setAttribute("facRecruitStatusMap", CreateStudyServlet.facRecruitStatusMap);
            request.setAttribute("statuses", Status.toStudyUpdateMembersList());

            FormProcessor fp = new FormProcessor(request);
            logger.info("start date:" + study.getDatePlannedEnd());
            if (study.getDatePlannedEnd() != null) {
                fp.addPresetValue(UpdateSubStudyServlet.INPUT_END_DATE, local_df.format(study.getDatePlannedEnd()));
            }
            if (study.getDatePlannedStart() != null) {
                fp.addPresetValue(UpdateSubStudyServlet.INPUT_START_DATE, local_df.format(study.getDatePlannedStart()));
            }
            setPresetValues(fp.getPresetValues());
            if (study.getProtocolDateVerification() != null) {
                fp.addPresetValue(UpdateSubStudyServlet.INPUT_VER_DATE, local_df.format(study.getProtocolDateVerification()));
            }

            forwardPage(Page.UPDATE_SUB_STUDY);
        }

    }

    private void createEventDefinitions(StudyBean parentStudy) {
        FormProcessor fp = new FormProcessor(request);
        int siteId = Integer.valueOf(request.getParameter("id").trim());
        ArrayList<StudyEventDefinitionBean> seds = new ArrayList<StudyEventDefinitionBean>();
        StudyEventDefinitionDAO sedDao = new StudyEventDefinitionDAO(sm.getDataSource());
        EventDefinitionCRFDAO edcdao = new EventDefinitionCRFDAO(sm.getDataSource());
        CRFVersionDAO cvdao = new CRFVersionDAO(sm.getDataSource());
        CRFDAO cdao = new CRFDAO(sm.getDataSource());
        seds = sedDao.findAllByStudy(parentStudy);
        int start = 0;
        for (StudyEventDefinitionBean sed : seds) {
            int defId = sed.getId();
            ArrayList<EventDefinitionCRFBean> edcs =
                (ArrayList<EventDefinitionCRFBean>) edcdao.findAllByDefinitionAndSiteIdAndParentStudyId(defId, siteId, parentStudy.getId());
            ArrayList<EventDefinitionCRFBean> defCrfs = new ArrayList<EventDefinitionCRFBean>();
            // sed.setCrfNum(edcs.size());
            for (EventDefinitionCRFBean edcBean : edcs) {
                int edcStatusId = edcBean.getStatus().getId();
                CRFBean crf = (CRFBean) cdao.findByPK(edcBean.getCrfId());
                int crfStatusId = crf.getStatusId();
                if (edcStatusId == 5 || edcStatusId == 7 || crfStatusId == 5 || crfStatusId == 7) {
                } else {
                    ArrayList<CRFVersionBean> versions = (ArrayList<CRFVersionBean>) cvdao.findAllActiveByCRF(edcBean.getCrfId());
                    edcBean.setVersions(versions);
                    edcBean.setCrfName(crf.getName());
                    CRFVersionBean defaultVersion = (CRFVersionBean) cvdao.findByPK(edcBean.getDefaultVersionId());
                    edcBean.setDefaultVersionName(defaultVersion.getName());
                    String sversionIds = edcBean.getSelectedVersionIds();
                    ArrayList<Integer> idList = new ArrayList<Integer>();
                    if (sversionIds.length() > 0) {
                        String[] ids = sversionIds.split("\\,");
                        for (String id : ids) {
                            idList.add(Integer.valueOf(id));
                        }
                    }
                    edcBean.setSelectedVersionIdList(idList);
                    defCrfs.add(edcBean);
                    ++start;
                }
            }
            logger.debug("definitionCrfs size=" + defCrfs.size() + " total size=" + edcs.size());
            sed.setCrfs(defCrfs);
            sed.setCrfNum(defCrfs.size());
        }
        // not sure if request is better, since not sure if there is another
        // process using this.
        session.setAttribute("definitions", seds);
        session.setAttribute("sdvOptions", this.setSDVOptions());
    }

    private ArrayList<String> setSDVOptions() {
        ArrayList<String> sdvOptions = new ArrayList<String>();
        sdvOptions.add(SourceDataVerification.AllREQUIRED.toString());
        sdvOptions.add(SourceDataVerification.PARTIALREQUIRED.toString());
        sdvOptions.add(SourceDataVerification.NOTREQUIRED.toString());
        sdvOptions.add(SourceDataVerification.NOTAPPLICABLE.toString());
        return sdvOptions;
    }

    @Override
    protected String getAdminServlet() {
        if (ub.isSysAdmin()) {
            return SecureController.ADMIN_SERVLET_CODE;
        } else {
            return "";
        }
    }

}
