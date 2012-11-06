/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package com.liteoc.control.managestudy;


import com.liteoc.bean.admin.CRFBean;
import com.liteoc.bean.core.Role;
import com.liteoc.bean.managestudy.StudyEventDefinitionBean;
import com.liteoc.bean.submit.CRFVersionBean;
import com.liteoc.bean.submit.DisplayTableOfContentsBean;
import com.liteoc.bean.submit.EventCRFBean;
import com.liteoc.bean.submit.SectionBean;
import com.liteoc.control.core.SecureController;
import com.liteoc.control.form.FormProcessor;
import com.liteoc.control.submit.TableOfContentsServlet;
import com.liteoc.dao.admin.CRFDAO;
import com.liteoc.dao.submit.CRFVersionDAO;
import com.liteoc.dao.submit.SectionDAO;
import com.liteoc.view.Page;
import com.liteoc.web.InsufficientPermissionException;

import java.util.ArrayList;
import java.util.HashMap;

import javax.sql.DataSource;

/**
 * To view the table of content of an event CRF
 *
 * @author jxu
 */
public class ViewTableOfContentServlet extends SecureController {
    /**
     * Checks whether the user has the correct privilege
     */
    @Override
    public void mayProceed() throws InsufficientPermissionException {
        if (ub.isSysAdmin()) {
            return;
        }
        if (currentRole.getRole().equals(Role.STUDYDIRECTOR) || currentRole.getRole().equals(Role.COORDINATOR)
            || currentRole.getRole().equals(Role.INVESTIGATOR) || currentRole.getRole().equals(Role.RESEARCHASSISTANT)) {
            return;
        }

        addPageMessage(respage.getString("no_have_correct_privilege_current_study") + " " + respage.getString("change_study_contact_sysadmin"));
        throw new InsufficientPermissionException(Page.MENU_SERVLET, resexception.getString("not_director"), "1");

    }

    @Override
    public void processRequest() throws Exception {
        FormProcessor fp = new FormProcessor(request);
        int crfVersionId = fp.getInt("crfVersionId");
        // YW <<
        int sedId = fp.getInt("sedId");
        request.setAttribute("sedId", new Integer(sedId) + "");
        // YW >>
        DisplayTableOfContentsBean displayBean = getDisplayBean(sm.getDataSource(), crfVersionId);
        request.setAttribute("toc", displayBean);
        forwardPage(Page.VIEW_TABLE_OF_CONTENT);
    }

    public static DisplayTableOfContentsBean getDisplayBean(DataSource ds, int crfVersionId) {
        DisplayTableOfContentsBean answer = new DisplayTableOfContentsBean();

        SectionDAO sdao = new SectionDAO(ds);
        ArrayList sections = getSections(crfVersionId, ds);
        answer.setSections(sections);

        CRFVersionDAO cvdao = new CRFVersionDAO(ds);
        CRFVersionBean cvb = (CRFVersionBean) cvdao.findByPK(crfVersionId);
        answer.setCrfVersion(cvb);

        CRFDAO cdao = new CRFDAO(ds);
        CRFBean cb = (CRFBean) cdao.findByPK(cvb.getCrfId());
        answer.setCrf(cb);

        answer.setEventCRF(new EventCRFBean());

        answer.setStudyEventDefinition(new StudyEventDefinitionBean());

        return answer;
    }

    public static ArrayList getSections(int crfVersionId, DataSource ds) {
        SectionDAO sdao = new SectionDAO(ds);

        HashMap numItemsBySectionId = sdao.getNumItemsBySectionId();
        ArrayList sections = sdao.findAllByCRFVersionId(crfVersionId);

        for (int i = 0; i < sections.size(); i++) {
            SectionBean sb = (SectionBean) sections.get(i);

            int sectionId = sb.getId();
            Integer key = new Integer(sectionId);
            sb.setNumItems(TableOfContentsServlet.getIntById(numItemsBySectionId, key));
            sections.set(i, sb);
        }

        return sections;
    }

}
